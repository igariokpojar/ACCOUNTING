package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.entity.Product;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.NotEnoughProductException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import com.cydeo.service.SecurityService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final InvoiceService invoiceService;
    private final MapperUtil mapperUtil;
    private final ProductService productService;


    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, @Lazy InvoiceService invoiceService, MapperUtil mapperUtil, ProductService productService, SecurityService securityService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceService = invoiceService;
        this.mapperUtil = mapperUtil;
        this.productService = productService;
        this.securityService = securityService;
    }

    @Override
    public InvoiceProductDto findInvoiceProductById(long invoiceProductId) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(invoiceProductId).orElseThrow();
        return mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
    }

    @Override
    public List<InvoiceProductDto> getInvoiceProductsOfInvoice(Long invoiceId) {
        List<InvoiceProduct> list = invoiceProductRepository.findAllByInvoice_Id(invoiceId);
        return list.stream().map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .map(invoiceProductDto -> {
                    BigDecimal price = invoiceProductDto.getPrice();
                    BigDecimal tax = BigDecimal.valueOf(invoiceProductDto.getTax()).divide(BigDecimal.valueOf(100));
                    BigDecimal totalPriceWithOutTax = price.multiply(BigDecimal.valueOf(invoiceProductDto.getQuantity()));
                    BigDecimal totalWithTax = totalPriceWithOutTax.multiply(tax).add(totalPriceWithOutTax).setScale(2, RoundingMode.HALF_UP);
                    invoiceProductDto.setTotal(totalWithTax);
                    return invoiceProductDto;
                }).collect(Collectors.toList());
    }

    @Override
    public void save(Long invoiceId, InvoiceProductDto invoiceProductDto) {
        InvoiceDto invoiceDto = mapperUtil.convert(invoiceService.findInvoiceById(invoiceId), new InvoiceDto());
        invoiceProductDto.setInvoice(invoiceDto);
        if (invoiceProductDto.getProfitLoss() == null) {
            invoiceProductDto.setProfitLoss(BigDecimal.ZERO);
        }
        invoiceProductRepository.save(mapperUtil.convert(invoiceProductDto, new InvoiceProduct()));
    }

    @Override
    public void delete(Long invoiceProductId) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findInvoiceProductById(invoiceProductId);
        invoiceProduct.setIsDeleted(true);
        invoiceProductRepository.save(invoiceProduct);
    }

    @Transactional
    @Override
    public void completeApprovalProcedures(Long invoiceId, InvoiceType type) throws NotEnoughProductException {
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAllByInvoice_Id(invoiceId);
        if (type == InvoiceType.SALES) {
            for (InvoiceProduct salesInvoiceProduct : invoiceProductList) {
                //If there is enough stock
                if (salesInvoiceProduct.getProduct().getQuantityInStock() >= salesInvoiceProduct.getQuantity()) {
                    //decrease the product quantity based on the quantity we are selling
                    updateQuantityOfProduct(salesInvoiceProduct, type);
                    //assign quantity to remainingQty (currently null), will be updated when doing profit/loss calculation
                    salesInvoiceProduct.setRemainingQuantity(salesInvoiceProduct.getQuantity());
                    //updating
                    invoiceProductRepository.save(salesInvoiceProduct);
                    //calculate profit/loss and update remaining quantity values
                    setProfitLossOfInvoiceProductsForSalesInvoice(salesInvoiceProduct);
                } else {
                   // delete(salesInvoiceProduct.getId());
                    throw new NotEnoughProductException("This sale cannot be completed due to insufficient quantity of product");
                }
            }
        } else {
            for (InvoiceProduct purchaseInvoiceProduct : invoiceProductList) {
                //increase the product quantity based on the amount purchased
                updateQuantityOfProduct(purchaseInvoiceProduct, type);
                purchaseInvoiceProduct.setRemainingQuantity(purchaseInvoiceProduct.getQuantity());
                //updating
                invoiceProductRepository.save(purchaseInvoiceProduct);
            }
        }
    }

    private final SecurityService securityService;

    private void setProfitLossOfInvoiceProductsForSalesInvoice(InvoiceProduct toBeSoldProduct) {
        List<InvoiceProduct> purchasedProductList = findNotSoldProduct(toBeSoldProduct.getProduct());
        for (InvoiceProduct purchasedProduct : purchasedProductList) {
            if (toBeSoldProduct.getRemainingQuantity() <= purchasedProduct.getRemainingQuantity()) {

                BigDecimal costTotalForQty = purchasedProduct.getPrice().multiply(
                        BigDecimal.valueOf(toBeSoldProduct.getRemainingQuantity() * (purchasedProduct.getTax() + 100) / 100d));

                BigDecimal salesTotalForQty = toBeSoldProduct.getPrice().multiply(
                        BigDecimal.valueOf(toBeSoldProduct.getRemainingQuantity() * (toBeSoldProduct.getTax() + 100) / 100d));

                BigDecimal profitLoss = toBeSoldProduct.getProfitLoss().add(salesTotalForQty.subtract(costTotalForQty));
                purchasedProduct.setRemainingQuantity(purchasedProduct.getRemainingQuantity() - toBeSoldProduct.getRemainingQuantity());
                toBeSoldProduct.setRemainingQuantity(0);
                toBeSoldProduct.setProfitLoss(profitLoss);

                invoiceProductRepository.save(purchasedProduct);
                invoiceProductRepository.save(toBeSoldProduct);
                break;

            } else {
                BigDecimal costForQty = purchasedProduct.getPrice().multiply(
                        BigDecimal.valueOf(purchasedProduct.getRemainingQuantity() * (purchasedProduct.getTax() + 100) / 100d));

                BigDecimal salesTotalForQty = toBeSoldProduct.getPrice().multiply(
                        BigDecimal.valueOf(purchasedProduct.getRemainingQuantity() * (toBeSoldProduct.getTax() + 100) / 100d));

                BigDecimal profitLoss = toBeSoldProduct.getProfitLoss().add(salesTotalForQty.subtract(costForQty));
                toBeSoldProduct.setRemainingQuantity(toBeSoldProduct.getRemainingQuantity() - purchasedProduct.getRemainingQuantity());
                purchasedProduct.setRemainingQuantity(0);
                toBeSoldProduct.setProfitLoss(profitLoss);

                invoiceProductRepository.save(purchasedProduct);
                invoiceProductRepository.save(toBeSoldProduct);
            }
        }
    }

    @Override
    public List<InvoiceProduct> findNotSoldProduct(Product product) {
        return invoiceProductRepository
                .findInvoiceProductsByInvoiceInvoiceTypeAndProductAndRemainingQuantityNotOrderByIdAsc(InvoiceType.PURCHASE, product, 0);
    }

    private void updateQuantityOfProduct(InvoiceProduct invoiceProduct, InvoiceType type) {
        ProductDto productDto = mapperUtil.convert(invoiceProduct.getProduct(), new ProductDto());
        if (type.equals(InvoiceType.SALES)) {// increasing quantity in stock
            productDto.setQuantityInStock(productDto.getQuantityInStock() - invoiceProduct.getQuantity());
        } else { //decrease quantity in stock
            productDto.setQuantityInStock(productDto.getQuantityInStock() + invoiceProduct.getQuantity());
        }
        //calling productService to update database
        productService.update(productDto.getId(), productDto);
    }


    @Override
    public boolean checkProductQuantityBeforeAddingToInvoice(InvoiceProductDto salesInvoiceProduct, Long invoiceId) {

        Integer contOfProductAlreadyInInvoice =
                getInvoiceProductsOfInvoice(invoiceId).stream()
                        .filter(invoiceProductDto -> invoiceProductDto.getProduct().getId() == salesInvoiceProduct.getProduct().getId())
                        .map(invoiceProductDto -> {
                            int quantity = 0;
                            if (invoiceProductDto.getQuantity() != null) {
                                quantity += invoiceProductDto.getQuantity();
                            }
                            return quantity;
                        }).reduce(0, Integer::sum);

        if (salesInvoiceProduct.getQuantity() != null) {
            if (salesInvoiceProduct.getQuantity() + contOfProductAlreadyInInvoice > salesInvoiceProduct.getProduct().getQuantityInStock()) {
                return false;
            }
            return salesInvoiceProduct.getProduct().getQuantityInStock() >= salesInvoiceProduct.getQuantity();
        }
        return true;
    }

    @Override
    public List<InvoiceProductDto> findAllInvoiceProductsByProductId(Long productId) {
        return invoiceProductRepository.findAllInvoiceProductByProductId(productId).stream()
                .map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());

    }

    @Override
    public Boolean stockCheckBeforeApproval(Long invoiceId){
    InvoiceDto invoice= invoiceService.findInvoiceById(invoiceId);
    List<InvoiceProductDto> invoiceProductDtoList= invoice.getInvoiceProducts();
    boolean enoughStock =true;
        for (InvoiceProductDto invoiceProductDto : invoiceProductDtoList) {
        if (!(invoiceProductDto.getProduct().getQuantityInStock() >= invoiceProductDto.getQuantity())) {
            enoughStock = false;
        }
        }
        return enoughStock;
    }

    @Override
    public List<InvoiceProductDto> getAllByInvoiceStatusApprovedForCompany() {

     Company company = mapperUtil.convert( securityService.getLoggedInUser().getCompany(),new Company());

        return invoiceProductRepository
                .findAllByInvoice_InvoiceStatusAndInvoice_CompanyOrderByInvoice_DateDesc( InvoiceStatus.APPROVED,company).stream()
                .map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .peek(invoiceProductDto -> {
                    if(invoiceProductDto.getInvoice().getInvoiceType().equals(InvoiceType.SALES)){
                        invoiceProductDto.setQuantity(-invoiceProductDto.getQuantity());
                    }
                })
                .collect(Collectors.toList());
    }



}
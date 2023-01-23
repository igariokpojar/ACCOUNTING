package com.cydeo.service.impl;

import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Product;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.ProductService;
import com.cydeo.service.SecurityService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SecurityService securityService;
    private final MapperUtil mapperUtil;
    private final InvoiceProductService invoiceProductService;

    public ProductServiceImpl(ProductRepository productRepository, SecurityService securityService, MapperUtil mapperUtil, @Lazy InvoiceProductService invoiceProductService) {
        this.productRepository = productRepository;
        this.securityService = securityService;
        this.mapperUtil = mapperUtil;
        this.invoiceProductService = invoiceProductService;
    }

    @Override
    public ProductDto findProductById(Long productId) {
     Product product = productRepository.findById(productId).get();

        return mapperUtil.convert(product, new ProductDto());
    }

    @Override
    public List<ProductDto> getAllProducts() {
        Company company = mapperUtil.convert(securityService.getLoggedInUser().getCompany(), new Company());

        return productRepository.findAllByCategoryCompany(company).stream()
                .sorted(Comparator.comparing((Product product) -> product.getCategory().getDescription())
                        .thenComparing(Product::getName))
                .map(each-> mapperUtil.convert(each, new ProductDto()))
                .collect(Collectors.toList());
    }


    @Override
    public ProductDto save(ProductDto productDto) {

        Product product = mapperUtil.convert(productDto, new Product());
        product.setQuantityInStock(0);
        return mapperUtil.convert(productRepository.save(product), new ProductDto());

    }

    @Override
    public ProductDto update(Long productId, ProductDto productDto) {

       productDto.setId(productId);
       Product product = productRepository.findById(productId)
               .orElseThrow(()-> new NoSuchElementException("Product "+ productDto.getName() + " not found"));
       final int quantityInStock = productDto.getQuantityInStock() == null? product.getQuantityInStock():productDto.getQuantityInStock();
       productDto.setQuantityInStock(quantityInStock);

       product = productRepository.save(mapperUtil.convert(productDto, new Product()));

        return mapperUtil.convert(product, productDto);
    }

    @Override
    public void delete(Long productId) {
        Product product = productRepository.findById(productId).get();

        if (invoiceProductService.findAllInvoiceProductsByProductId(productId).size() == 0 && product.getQuantityInStock() == 0){
            product.setIsDeleted(true);

        }else {
            System.out.println("YOU CAN NOT DELETE THIS PRODUCT");
        }

        productRepository.save(product);

    }

    @Override
    public List<ProductDto> findAllProductsWithCategoryId(Long categoryId) {
        List<Product> productsList = productRepository.findByCategoryId(categoryId);
        return productsList.stream().map(product -> mapperUtil.convert(product, new ProductDto())).collect(Collectors.toList());
    }


    @Override
    public boolean isProductNameExist(ProductDto productDto, Long id) {

        if (id != null){

            Product product = productRepository.findById(id).get();

            if (product.getName().equals(productDto.getName())) {
                return false;
            }
        }

            return productRepository.existsByName(productDto.getName());
        }


    @Override
    public List<ProductDto> findAllProductsInStock() {

        Company company = mapperUtil.convert(securityService.getLoggedInUser().getCompany(), new Company());

        return productRepository.findAllByCategoryCompany(company).stream()
                .filter(product -> product.getQuantityInStock() > 0)
                .sorted(Comparator.comparing((Product product) -> product.getCategory().getDescription())
                        .thenComparing(Product::getName))
                .map(each-> mapperUtil.convert(each, new ProductDto()))
                .collect(Collectors.toList());
    }


}

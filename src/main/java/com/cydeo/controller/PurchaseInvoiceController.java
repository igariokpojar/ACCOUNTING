package com.cydeo.controller;

import com.cydeo.annotation.ExecutionTime;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/purchaseInvoices")
public class PurchaseInvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ProductService productService;
    private final ClientVendorService clientVendorService;
    private final EmailSenderService emailSenderService;

    public PurchaseInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ProductService productService, ClientVendorService clientVendorService, EmailSenderService emailSenderService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.productService = productService;
        this.clientVendorService = clientVendorService;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("/update/{invoiceId}")
    public String navigateToPurchaseInvoiceUpdate(@PathVariable("invoiceId") Long invoiceId, Model model) throws Exception {
        model.addAttribute("invoice", invoiceService.findInvoiceById(invoiceId));
        model.addAttribute("invoiceProducts", invoiceProductService.getInvoiceProductsOfInvoice(invoiceId));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("vendors", clientVendorService.getAllClientVendorsOfCompany(ClientVendorType.VENDOR));
        model.addAttribute("products",productService.getAllProducts());
        return "/invoice/purchase-invoice-update";
    }

    @PostMapping("/update/{invoiceId}")
    public String updatePurchaseInvoice(@PathVariable("invoiceId") Long invoiceId, InvoiceDto invoiceDto) {
        invoiceService.update(invoiceId, invoiceDto);
        return "redirect:/purchaseInvoices/update/"+invoiceId;
    }

    @GetMapping("/delete/{id}")
    public String deletePurchaseInvoice(@PathVariable("id") Long id) {
        invoiceService.delete(id);
        return "redirect:/purchaseInvoices/list";
    }
    @ExecutionTime
    @GetMapping("/list")
    public String navigateToPurchaseInvoiceList(Model model) throws Exception {
        model.addAttribute("invoices", invoiceService.getAllInvoicesOfCompany(InvoiceType.PURCHASE));
        return "/invoice/purchase-invoice-list";
    }

    @GetMapping("/create")
    public String navigateToPurchaseInvoiceCreate(Model model) throws Exception {
        model.addAttribute("newPurchaseInvoice", invoiceService.getNewInvoice(InvoiceType.PURCHASE));
          model.addAttribute("vendors", clientVendorService.getAllClientVendorsOfCompany(ClientVendorType.VENDOR));
        return "/invoice/purchase-invoice-create";
    }

    @PostMapping("/create")
    public String createNewPurchaseInvoice(@Valid @ModelAttribute("newPurchaseInvoice") InvoiceDto newPurchaseInvoice, BindingResult result, Model model) throws Exception {

        if (result.hasErrors()) {
            model.addAttribute("vendors", clientVendorService.getAllClientVendorsOfCompany(ClientVendorType.VENDOR));
            return "/invoice/purchase-invoice-create";
        }
        var invoice = invoiceService.save(newPurchaseInvoice, InvoiceType.PURCHASE);
        return "redirect:/purchaseInvoices/update/"+invoice.getId();
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addInvoiceProductToPurchaseInvoice(@PathVariable("invoiceId") Long invoiceId,  @Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDto newInvoiceProduct, BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("invoice", invoiceService.findInvoiceById(invoiceId));
            model.addAttribute("invoiceProducts", invoiceProductService.getInvoiceProductsOfInvoice(invoiceId));
            model.addAttribute("vendors", clientVendorService.getAllClientVendorsOfCompany(ClientVendorType.VENDOR));
            model.addAttribute("products",productService.getAllProducts());
            return "/invoice/purchase-invoice-update";
        }
        invoiceProductService.save(invoiceId, newInvoiceProduct);
        return "redirect:/purchaseInvoices/update/"+invoiceId;
    }

    @GetMapping("removeInvoiceProduct/{invoiceId}/{invoiceProductId}")
    public String removeInvoiceProductFromPurchaseInvoice(@PathVariable("invoiceId") Long invoiceId, @PathVariable("invoiceProductId") Long invoiceProductId) {
        invoiceProductService.delete(invoiceProductId);
        return "redirect:/purchaseInvoices/update/"+invoiceId;
    }

    @GetMapping("/print/{invoiceId}")
    public String print(@PathVariable("invoiceId") long invoiceId, Model model) {
        InvoiceDto invoice = invoiceService.printInvoice(invoiceId);
        model.addAttribute("company", invoice.getCompany());
        model.addAttribute("invoice", invoice);
        model.addAttribute("invoiceProducts", invoice.getInvoiceProducts());
        return "invoice/invoice_print";
    }

    @GetMapping("/print/{invoiceId}/sent")
    public String sentEmail(@PathVariable("invoiceId") long invoiceId, Model model) throws MessagingException, IOException {
        InvoiceDto invoice = invoiceService.printInvoice(invoiceId);
        model.addAttribute("company", invoice.getCompany());
        model.addAttribute("invoice", invoice);
        model.addAttribute("invoiceProducts", invoice.getInvoiceProducts());

        FileSystemResource file = new FileSystemResource(ResourceUtils.getFile("src/main/resources/templates/invoice/invoice_print.html"));
        emailSenderService.sendEmailAttach("Notification", "<h1>Invoice " + invoice.getInvoiceNo() +" in amount of $" + invoice.getTotal() + " has been approved </h1>", file.getPath());

        return "invoice/invoice_print";
    }

    @GetMapping("/approve/{invoiceId}")
    public String approve(@PathVariable("invoiceId") long invoiceId) throws Exception {
        invoiceService.approve(invoiceId);
        return "redirect:/purchaseInvoices/list";
    }
}

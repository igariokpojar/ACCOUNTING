package com.cydeo.controller;

import com.cydeo.dto.ProductDto;
import com.cydeo.enums.ProductUnit;
import com.cydeo.service.CategoryService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public ProductController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }


    @GetMapping("/list")
    public String listAllProducts(Model model){
        model.addAttribute("products", productService.getAllProducts());

        return "product/product-list";
    }

    @GetMapping("/create")
    public String navigateToProductCreate(Model model) throws Exception {
        model.addAttribute("newProduct", new ProductDto());

        return "product/product-create";

    }

    @PostMapping("/create")
    public String createNewProduct(@Valid @ModelAttribute("newProduct") ProductDto productDto, BindingResult bindingResult, Model model){

        if (productService.isProductNameExist(productDto, productDto.getId())){
            bindingResult.rejectValue("name", " ", "product name already exist");
        }

        if (bindingResult.hasErrors()){
            return "product/product-create";
        }

        productService.save(productDto);

        return "redirect:/products/list";
    }

    @GetMapping("/update/{productId}")
    public String navigateToProductUpdate(@PathVariable(value = "productId") Long productId, Model model) throws Exception{

        model.addAttribute("product", productService.findProductById(productId));

        return "/product/product-update";
    }

    @PostMapping("/update/{productId}")
    public String updateProduct(@PathVariable(value = "productId") Long productId, @Valid @ModelAttribute("product") ProductDto productDto, BindingResult bindingResult, Model model) throws Exception{

        productDto.setId(productId);

        if (productService.isProductNameExist(productDto, productId)){
            bindingResult.rejectValue("name", " ", "product name already exist");
        }

        if (bindingResult.hasErrors()){
            return "product/product-update";
        }

        productService.update(productId, productDto);

        return "redirect:/products/list";
    }

    @GetMapping("/delete/{productId}")
    public String deleteProduct(@PathVariable(value = "productId") Long productId, Model model){

        productService.delete(productId);
        return "redirect:/products/list";
    }

    @ModelAttribute
    public void commonAttributes(Model model) throws Exception {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));
        model.addAttribute("title", "Cydeo Accounting-Product");
    }


}

package com.example.khatifarm.controller;

import com.example.khatifarm.model.Admin;
import com.example.khatifarm.model.Product;
import com.example.khatifarm.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;


    public ProductController(
            ProductService productService) {

        this.productService =
                productService;
    }


    // =====================================================
    // PRODUCTS BY CATEGORY
    // =====================================================

    @GetMapping("/category/{category}")
    public String productsByCategory(
            @PathVariable String category,
            Model model) {


        List<Product> products =
                productService
                        .getProductsByCategory(
                                category
                        );


        model.addAttribute(
                "products",
                products
        );


        return "products";
    }


    // =====================================================
    // ALL APPROVED PRODUCTS
    // =====================================================

    @GetMapping
    public String getAllProducts(
            Model model) {


        model.addAttribute(
                "products",
                productService
                        .getApprovedProducts()
        );


        return "products";
    }


    // =====================================================
    // SEARCH + GRADE + PRICE
    // =====================================================

    @GetMapping("/search")
    public String searchProducts(

            @RequestParam(
                    required = false
            )
            String name,

            @RequestParam(
                    required = false
            )
            String grade,

            @RequestParam(
                    required = false
            )
            String priceRange,

            Model model) {


        // -----------------------------------------------
        // SEARCH PRODUCTS
        // -----------------------------------------------

        List<Product> products =
                productService
                        .searchAndFilterProducts(
                                name,
                                grade,
                                priceRange
                        );


        // -----------------------------------------------
        // SEND PRODUCTS TO HTML
        // -----------------------------------------------

        model.addAttribute(
                "products",
                products
        );


        // -----------------------------------------------
        // KEEP SEARCH VALUE
        // -----------------------------------------------

        model.addAttribute(
                "searchName",
                name
        );


        // -----------------------------------------------
        // KEEP SELECTED GRADE
        // -----------------------------------------------

        model.addAttribute(
                "selectedGrade",
                grade
        );


        // -----------------------------------------------
        // KEEP SELECTED PRICE
        // -----------------------------------------------

        model.addAttribute(
                "selectedPrice",
                priceRange
        );


        return "products";
    }


    // =====================================================
    // OLD QUALITY + PRICE FILTER
    // =====================================================
    //
    // This route is kept so that existing code
    // does not break.
    //
    // The new products.html does NOT use this route.
    //
    // =====================================================

    @GetMapping("/filter")
    public String filterProducts(

            @RequestParam int qualityScore,

            @RequestParam double price,

            Model model) {


        List<Product> products =
                productService.filterProducts(
                        qualityScore,
                        price
                );


        model.addAttribute(
                "products",
                products
        );


        return "products";
    }


    // =====================================================
    // PRODUCT DETAILS
    // =====================================================

    @GetMapping("/{id}")
    public String getProductById(

            @PathVariable String id,

            Model model) {


        Product product =
                productService
                        .getProductById(id);


        if (product == null) {

            return "redirect:/products";
        }


        model.addAttribute(
                "product",
                product
        );


        return "product-details";
    }


    // =====================================================
    // ADMIN → ADD PRODUCT PAGE
    // =====================================================

    @GetMapping("/add")
    public String showAddProductForm(

            Model model,

            HttpSession session) {


        Admin admin =
                (Admin) session.getAttribute(
                        "loggedInAdmin"
                );


        if (admin == null) {

            return "redirect:/admin/login";
        }


        model.addAttribute(
                "product",
                new Product()
        );


        return "add-product";
    }


    // =====================================================
    // ADMIN → SAVE PRODUCT
    // =====================================================

    @PostMapping("/save")
    public String saveProduct(

            @ModelAttribute Product product,

            HttpSession session) {


        Admin admin =
                (Admin) session.getAttribute(
                        "loggedInAdmin"
                );


        if (admin == null) {

            return "redirect:/admin/login";
        }


        productService.saveProduct(
                product
        );


        return "redirect:/products";
    }


    // =====================================================
    // ADMIN → EDIT PRODUCT PAGE
    // =====================================================

    @GetMapping("/edit/{id}")
    public String showEditProductForm(

            @PathVariable String id,

            Model model,

            HttpSession session) {


        Admin admin =
                (Admin) session.getAttribute(
                        "loggedInAdmin"
                );


        if (admin == null) {

            return "redirect:/admin/login";
        }


        Product product =
                productService
                        .getProductById(id);


        if (product == null) {

            return "redirect:/products";
        }


        model.addAttribute(
                "product",
                product
        );


        return "edit-product";
    }


    // =====================================================
    // ADMIN → UPDATE PRODUCT
    // =====================================================

    @PostMapping("/update/{id}")
    public String updateProduct(

            @PathVariable String id,

            @ModelAttribute Product product,

            HttpSession session) {


        Admin admin =
                (Admin) session.getAttribute(
                        "loggedInAdmin"
                );


        if (admin == null) {

            return "redirect:/admin/login";
        }


        productService.updateProduct(
                id,
                product
        );


        return "redirect:/products/" + id;
    }


    // =====================================================
    // ADMIN → DELETE PRODUCT
    // =====================================================

    @GetMapping("/delete/{id}")
    public String deleteProduct(

            @PathVariable String id,

            HttpSession session) {


        Admin admin =
                (Admin) session.getAttribute(
                        "loggedInAdmin"
                );


        if (admin == null) {

            return "redirect:/admin/login";
        }


        productService.deleteProduct(
                id
        );


        return "redirect:/products";
    }
}
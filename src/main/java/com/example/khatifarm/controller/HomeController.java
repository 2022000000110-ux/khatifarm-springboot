package com.example.khatifarm.controller;

import com.example.khatifarm.model.Admin;
import com.example.khatifarm.model.Customer;
import com.example.khatifarm.model.Product;
import com.example.khatifarm.model.Seller;
import com.example.khatifarm.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(
            Model model,
            HttpSession session) {

        // =========================
        // APPROVED PRODUCTS
        // =========================

        List<Product> products =
                productService.getApprovedProducts();

        model.addAttribute(
                "products",
                products
        );


        // =========================
        // CHECK LOGIN STATUS
        // =========================

        Admin admin =
                (Admin) session.getAttribute(
                        "loggedInAdmin"
                );

        Seller seller =
                (Seller) session.getAttribute(
                        "loggedInSeller"
                );

        Customer customer =
                (Customer) session.getAttribute(
                        "loggedInCustomer"
                );


        // =========================
        // SEND LOGIN INFORMATION
        // TO HOME PAGE
        // =========================

        model.addAttribute(
                "loggedInAdmin",
                admin
        );

        model.addAttribute(
                "loggedInSeller",
                seller
        );

        model.addAttribute(
                "loggedInCustomer",
                customer
        );


        return "index";
    }
}
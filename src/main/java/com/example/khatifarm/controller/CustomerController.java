package com.example.khatifarm.controller;

import com.example.khatifarm.model.Customer;
import com.example.khatifarm.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    // =========================
    // CUSTOMER REGISTER PAGE
    // =========================

    @GetMapping("/register")
    public String showRegisterPage(
            Model model,
            HttpSession session) {

        // Already logged in
        Customer loggedInCustomer =
                (Customer) session.getAttribute(
                        "loggedInCustomer"
                );

        if (loggedInCustomer != null) {
            return "redirect:/customer/dashboard";
        }

        model.addAttribute(
                "customer",
                new Customer()
        );

        return "customer-register";
    }


    // =========================
    // CUSTOMER REGISTER
    // =========================

    @PostMapping("/register")
    public String registerCustomer(
            @ModelAttribute Customer customer,
            Model model) {

        Customer existingCustomer =
                customerService.findByEmail(
                        customer.getEmail()
                );

        if (existingCustomer != null) {

            model.addAttribute(
                    "error",
                    "Email already registered!"
            );

            return "customer-register";
        }

        customerService.registerCustomer(customer);

        return "redirect:/customer/login";
    }


    // =========================
    // CUSTOMER LOGIN PAGE
    // =========================

    @GetMapping("/login")
    public String showLoginPage(
            Model model,
            HttpSession session) {

        // Already logged in
        Customer loggedInCustomer =
                (Customer) session.getAttribute(
                        "loggedInCustomer"
                );

        if (loggedInCustomer != null) {
            return "redirect:/customer/dashboard";
        }

        model.addAttribute(
                "customer",
                new Customer()
        );

        return "customer-login";
    }


    // =========================
    // CUSTOMER LOGIN
    // =========================

    @PostMapping("/login")
    public String loginCustomer(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        Customer customer =
                customerService.findByEmail(email);

        if (customer != null &&
                customer.getPassword().equals(password)) {

            // =========================
            // SAVE CUSTOMER IN SESSION
            // =========================

            session.setAttribute(
                    "loggedInCustomer",
                    customer
            );

            return "redirect:/customer/dashboard";
        }

        model.addAttribute(
                "error",
                "Invalid email or password!"
        );

        return "customer-login";
    }


    // =========================
    // CUSTOMER DASHBOARD
    // =========================

    @GetMapping("/dashboard")
    public String customerDashboard(
            Model model,
            HttpSession session) {

        Customer customer =
                (Customer) session.getAttribute(
                        "loggedInCustomer"
                );

        // Customer is not logged in
        if (customer == null) {
            return "redirect:/customer/login";
        }

        model.addAttribute(
                "customer",
                customer
        );

        return "customer-dashboard";
    }


    // =========================
    // CUSTOMER LOGOUT
    // =========================

    @GetMapping("/logout")
    public String logout(
            HttpSession session) {

        // Remove ONLY customer login
        session.removeAttribute(
                "loggedInCustomer"
        );

        return "redirect:/customer/login";
    }

}
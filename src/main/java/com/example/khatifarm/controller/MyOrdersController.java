package com.example.khatifarm.controller;

import com.example.khatifarm.model.Customer;
import com.example.khatifarm.model.Order;
import com.example.khatifarm.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/my-orders")
public class MyOrdersController {

    private final OrderService orderService;

    public MyOrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================
    // MY ORDERS
    // =========================

    @GetMapping
    public String myOrders(
            HttpSession session,
            Model model) {

        Customer customer =
                (Customer) session.getAttribute("loggedInCustomer");

        // Customer login না থাকলে
        if (customer == null) {
            return "redirect:/customer/login";
        }

        List<Order> orders =
                orderService.getOrdersByEmail(customer.getEmail());

        model.addAttribute("orders", orders);
        model.addAttribute("customer", customer);

        return "my-orders";
    }


    // =========================
    // VIEW ORDER DETAILS
    // =========================

    @GetMapping("/view/{id}")
    public String viewOrder(
            @PathVariable String id,
            Model model,
            HttpSession session) {

        Customer customer =
                (Customer) session.getAttribute("loggedInCustomer");

        if (customer == null) {
            return "redirect:/customer/login";
        }

        Order order =
                orderService.getOrderById(id);

        if (order == null) {
            return "redirect:/my-orders";
        }

        // Security check:
        // অন্য customer's order দেখা যাবে না
        if (!order.getEmail().equals(customer.getEmail())) {
            return "redirect:/my-orders";
        }

        model.addAttribute("order", order);

        return "customer-order-details";
    }
}
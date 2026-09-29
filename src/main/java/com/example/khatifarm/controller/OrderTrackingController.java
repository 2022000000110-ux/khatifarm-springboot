package com.example.khatifarm.controller;

import com.example.khatifarm.model.Order;
import com.example.khatifarm.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/track-order")
public class OrderTrackingController {

    private final OrderService orderService;

    public OrderTrackingController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================
    // TRACK ORDER PAGE
    // =========================

    @GetMapping
    public String trackingPage() {
        return "track-order";
    }


    // =========================
    // SEARCH ORDER
    // =========================

    @PostMapping("/search")
    public String searchOrder(
            @RequestParam String orderId,
            @RequestParam String phone,
            Model model) {

        Order order = orderService.getOrderById(orderId);

        if (order == null) {

            model.addAttribute(
                    "error",
                    "Order not found!"
            );

            return "track-order";
        }


        // Verify phone number

        if (!order.getPhone().equals(phone)) {

            model.addAttribute(
                    "error",
                    "Order ID and phone number do not match!"
            );

            return "track-order";
        }


        model.addAttribute(
                "order",
                order
        );

        return "track-order";
    }
}
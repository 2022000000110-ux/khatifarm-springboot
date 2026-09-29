package com.example.khatifarm.controller;

import com.example.khatifarm.model.Order;
import com.example.khatifarm.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderSuccessController {

    private final OrderService orderService;

    public OrderSuccessController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order-success/{id}")
    public String orderSuccess(
            @PathVariable String id,
            Model model) {

        Order order =
                orderService.getOrderById(id);

        if (order == null) {
            return "redirect:/products";
        }

        model.addAttribute("order", order);

        return "order-success";
    }
}
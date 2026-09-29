package com.example.khatifarm.controller;

import com.example.khatifarm.model.CartItem;
import com.example.khatifarm.model.Customer;
import com.example.khatifarm.model.Order;
import com.example.khatifarm.service.OrderService;
import com.example.khatifarm.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CheckoutController {

    private final OrderService orderService;
    private final ProductService productService;

    // =========================
    // CONSTRUCTOR
    // =========================

    public CheckoutController(
            OrderService orderService,
            ProductService productService) {

        this.orderService = orderService;
        this.productService = productService;
    }


    // =========================
    // CHECKOUT PAGE
    // =========================

    @GetMapping("/checkout")
    public String checkout(
            HttpSession session,
            Model model) {

        // Get cart from session
        Object cartObject = session.getAttribute("cart");

        List<CartItem> cart;

        if (cartObject == null) {
            cart = new ArrayList<>();
        } else {
            cart = (List<CartItem>) cartObject;
        }

        // Cart empty হলে cart page এ পাঠাবে
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        // =========================
        // CHECK CURRENT STOCK
        // =========================

        for (CartItem item : cart) {

            if (!productService.hasEnoughStock(
                    item.getProductId(),
                    item.getQuantity())) {

                return "redirect:/cart?error=stock";
            }
        }

        // =========================
        // CALCULATE TOTAL
        // =========================

        double total = 0;

        for (CartItem item : cart) {
            total += item.getSubtotal();
        }

        // =========================
        // CHECK LOGGED-IN CUSTOMER
        // =========================

        Customer customer =
                (Customer) session.getAttribute("loggedInCustomer");

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("customer", customer);

        return "checkout";
    }


    // =========================
    // PLACE ORDER
    // =========================

    @PostMapping("/checkout/place-order")
    public String placeOrder(
            @RequestParam String customerName,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String address,
            @RequestParam String paymentMethod,
            HttpSession session) {

        // =========================
        // GET CART
        // =========================

        Object cartObject = session.getAttribute("cart");

        if (cartObject == null) {
            return "redirect:/cart";
        }

        List<CartItem> cart =
                (List<CartItem>) cartObject;

        if (cart.isEmpty()) {
            return "redirect:/cart";
        }


        // =========================
        // STOCK VALIDATION
        // =========================

        for (CartItem item : cart) {

            boolean enoughStock =
                    productService.hasEnoughStock(
                            item.getProductId(),
                            item.getQuantity()
                    );

            if (!enoughStock) {

                return "redirect:/cart?error=stock";
            }
        }


        // =========================
        // CALCULATE TOTAL
        // =========================

        double total = 0;

        for (CartItem item : cart) {
            total += item.getSubtotal();
        }


        // =========================
        // CREATE ORDER
        // =========================

        Order order = new Order();

        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setEmail(email);
        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);

        order.setItems(new ArrayList<>(cart));

        order.setTotal(total);

        order.setStatus("PENDING");


        // =========================
        // SAVE ORDER
        // =========================

        Order savedOrder =
                orderService.saveOrder(order);


        // =========================
        // REDUCE PRODUCT STOCK
        // =========================

        for (CartItem item : cart) {

            productService.reduceStock(
                    item.getProductId(),
                    item.getQuantity()
            );
        }


        // =========================
        // CLEAR CART
        // =========================

        session.removeAttribute("cart");


        // =========================
        // ORDER SUCCESS PAGE
        // =========================

        return "redirect:/order-success/"
                + savedOrder.getId();
    }
}
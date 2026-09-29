package com.example.khatifarm.service;

import com.example.khatifarm.model.Order;
import com.example.khatifarm.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    // =========================
    // SAVE ORDER
    // =========================

    public Order saveOrder(Order order) {

        return orderRepository.save(order);
    }


    // =========================
    // GET ALL ORDERS
    // =========================

    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }


    // =========================
    // GET ORDER BY ID
    // =========================

    public Order getOrderById(String id) {

        return orderRepository
                .findById(id)
                .orElse(null);
    }


    // =========================
    // GET ORDERS BY PHONE
    // =========================

    public List<Order> getOrdersByPhone(String phone) {

        return orderRepository.findByPhone(phone);
    }


    // =========================
    // GET ORDERS BY EMAIL
    // =========================

    public List<Order> getOrdersByEmail(String email) {

        return orderRepository.findByEmail(email);
    }


    // =========================
    // GET ORDERS BY SELLER
    // =========================

    public List<Order> getOrdersBySeller(String sellerName) {

        return orderRepository
                .findByItemsSellerName(sellerName);
    }

}
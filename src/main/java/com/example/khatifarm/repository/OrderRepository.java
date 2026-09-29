package com.example.khatifarm.repository;

import com.example.khatifarm.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {

    // =========================
    // CUSTOMER ORDERS
    // =========================

    List<Order> findByPhone(String phone);

    List<Order> findByEmail(String email);


    // =========================
    // SELLER ORDERS
    // =========================

    List<Order> findByItemsSellerName(String sellerName);

}
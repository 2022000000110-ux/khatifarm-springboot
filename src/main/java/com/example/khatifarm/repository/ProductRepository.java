package com.example.khatifarm.repository;

import com.example.khatifarm.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository
        extends MongoRepository<Product, String> {


    // =========================
    // FIND BY SELLER
    // =========================

    List<Product> findBySellerName(
            String sellerName
    );


    // =========================
    // FIND BY PRODUCT NAME
    // =========================

    List<Product> findByNameContainingIgnoreCase(
            String name
    );


    // =========================
    // FIND BY CATEGORY
    // =========================

    List<Product> findByCategoryIgnoreCase(
            String category
    );


    // =========================
    // FIND BY STATUS
    // =========================

    List<Product> findByStatus(
            String status
    );


    // =========================
    // SEARCH NAME + STATUS
    // =========================

    List<Product> findByNameContainingIgnoreCaseAndStatus(
            String name,
            String status
    );


    // =========================
    // CATEGORY + STATUS
    // =========================

    List<Product> findByCategoryIgnoreCaseAndStatus(
            String category,
            String status
    );


    // =========================
    // GRADE + STATUS
    // =========================

    List<Product> findByGradeAndStatus(
            String grade,
            String status
    );


    // =========================
    // STATUS + MAX PRICE
    // =========================

    List<Product> findByStatusAndPriceLessThanEqual(
            String status,
            double price
    );


    // =========================
    // GRADE + STATUS + MAX PRICE
    // =========================

    List<Product> findByGradeAndStatusAndPriceLessThanEqual(
            String grade,
            String status,
            double price
    );


    // =========================
    // OLD QUALITY + PRICE FILTER
    // =========================

    List<Product>
    findByStatusAndQualityScoreGreaterThanEqualAndPriceLessThanEqual(
            String status,
            int qualityScore,
            double price
    );
}
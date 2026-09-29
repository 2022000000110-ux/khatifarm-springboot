package com.example.khatifarm.repository;

import com.example.khatifarm.model.Seller;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SellerRepository extends MongoRepository<Seller, String> {

    Seller findByEmail(String email);
}
package com.example.khatifarm.service;

import com.example.khatifarm.model.Seller;
import com.example.khatifarm.repository.SellerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public Seller registerSeller(Seller seller) {
        return sellerRepository.save(seller);
    }

    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public Seller getSellerById(String id) {
        return sellerRepository.findById(id).orElse(null);
    }

    public Seller findByEmail(String email) {
        return sellerRepository.findByEmail(email);
    }
}
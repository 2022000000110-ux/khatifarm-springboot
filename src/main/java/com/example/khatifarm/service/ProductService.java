package com.example.khatifarm.service;

import com.example.khatifarm.model.Product;
import com.example.khatifarm.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;


    public ProductService(
            ProductRepository productRepository) {

        this.productRepository =
                productRepository;
    }


    // =====================================================
    // SAVE PRODUCT
    // =====================================================

    public Product saveProduct(
            Product product) {

        return productRepository.save(product);
    }


    // =====================================================
    // GET ALL PRODUCTS
    // =====================================================

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }


    // =====================================================
    // GET PRODUCT BY ID
    // =====================================================

    public Product getProductById(
            String id) {

        return productRepository
                .findById(id)
                .orElse(null);
    }


    // =====================================================
    // UPDATE PRODUCT
    // =====================================================

    public Product updateProduct(
            String id,
            Product updatedProduct) {

        Product existingProduct =
                productRepository
                        .findById(id)
                        .orElse(null);


        if (existingProduct != null) {

            existingProduct.setName(
                    updatedProduct.getName()
            );

            existingProduct.setCategory(
                    updatedProduct.getCategory()
            );

            existingProduct.setSellerName(
                    updatedProduct.getSellerName()
            );

            existingProduct.setSellerType(
                    updatedProduct.getSellerType()
            );

            existingProduct.setOrigin(
                    updatedProduct.getOrigin()
            );

            existingProduct.setImageUrl(
                    updatedProduct.getImageUrl()
            );

            existingProduct.setPrice(
                    updatedProduct.getPrice()
            );

            existingProduct.setStock(
                    updatedProduct.getStock()
            );

            existingProduct.setQualityScore(
                    updatedProduct.getQualityScore()
            );

            existingProduct.setGrade(
                    updatedProduct.getGrade()
            );

            existingProduct.setStatus(
                    updatedProduct.getStatus()
            );


            return productRepository.save(
                    existingProduct
            );
        }


        return null;
    }


    // =====================================================
    // DELETE PRODUCT
    // =====================================================

    public void deleteProduct(
            String id) {

        productRepository.deleteById(id);
    }


    // =====================================================
    // PRODUCTS BY SELLER
    // =====================================================

    public List<Product> getProductsBySeller(
            String sellerName) {

        return productRepository
                .findBySellerName(sellerName);
    }


    // =====================================================
    // SEARCH PRODUCTS BY NAME
    // =====================================================

    public List<Product> searchProducts(
            String name) {

        return productRepository
                .findByNameContainingIgnoreCaseAndStatus(
                        name,
                        "APPROVED"
                );
    }


    // =====================================================
    // PRODUCTS BY CATEGORY
    // =====================================================

    public List<Product> getProductsByCategory(
            String category) {

        return productRepository
                .findByCategoryIgnoreCaseAndStatus(
                        category,
                        "APPROVED"
                );
    }


    // =====================================================
    // GET APPROVED PRODUCTS
    // =====================================================

    public List<Product> getApprovedProducts() {

        return productRepository
                .findByStatus("APPROVED");
    }


    // =====================================================
    // NEW SEARCH + GRADE + PRICE FILTER
    // =====================================================

    public List<Product> searchAndFilterProducts(
            String name,
            String grade,
            String priceRange) {


        // -------------------------------------------------
        // CHECK NAME
        // -------------------------------------------------

        boolean hasName =
                name != null
                        && !name.isBlank();


        // -------------------------------------------------
        // CHECK GRADE
        // -------------------------------------------------

        boolean hasGrade =
                grade != null
                        && !grade.isBlank()
                        && !grade.equalsIgnoreCase("ALL");


        // -------------------------------------------------
        // CHECK PRICE
        // -------------------------------------------------

        boolean hasPrice =
                priceRange != null
                        && !priceRange.isBlank()
                        && !priceRange.equalsIgnoreCase("ALL");


        // =================================================
        // NAME + GRADE + PRICE
        // =================================================

        if (hasName && hasGrade && hasPrice) {

            double maxPrice =
                    getMaxPrice(priceRange);


            return productRepository
                    .findByGradeAndStatusAndPriceLessThanEqual(
                            grade,
                            "APPROVED",
                            maxPrice
                    )
                    .stream()
                    .filter(product ->
                            product.getName() != null
                                    &&
                                    product.getName()
                                            .toLowerCase()
                                            .contains(
                                                    name.toLowerCase()
                                            )
                    )
                    .toList();
        }


        // =================================================
        // GRADE + PRICE
        // =================================================

        if (hasGrade && hasPrice) {

            double maxPrice =
                    getMaxPrice(priceRange);


            return productRepository
                    .findByGradeAndStatusAndPriceLessThanEqual(
                            grade,
                            "APPROVED",
                            maxPrice
                    );
        }


        // =================================================
        // NAME + GRADE
        // =================================================

        if (hasName && hasGrade) {

            return productRepository
                    .findByGradeAndStatus(
                            grade,
                            "APPROVED"
                    )
                    .stream()
                    .filter(product ->
                            product.getName() != null
                                    &&
                                    product.getName()
                                            .toLowerCase()
                                            .contains(
                                                    name.toLowerCase()
                                            )
                    )
                    .toList();
        }


        // =================================================
        // NAME + PRICE
        // =================================================

        if (hasName && hasPrice) {

            double maxPrice =
                    getMaxPrice(priceRange);


            return productRepository
                    .findByStatusAndPriceLessThanEqual(
                            "APPROVED",
                            maxPrice
                    )
                    .stream()
                    .filter(product ->
                            product.getName() != null
                                    &&
                                    product.getName()
                                            .toLowerCase()
                                            .contains(
                                                    name.toLowerCase()
                                            )
                    )
                    .toList();
        }


        // =================================================
        // NAME ONLY
        // =================================================

        if (hasName) {

            return searchProducts(name);
        }


        // =================================================
        // GRADE ONLY
        // =================================================

        if (hasGrade) {

            return productRepository
                    .findByGradeAndStatus(
                            grade,
                            "APPROVED"
                    );
        }


        // =================================================
        // PRICE ONLY
        // =================================================

        if (hasPrice) {

            double maxPrice =
                    getMaxPrice(priceRange);


            return productRepository
                    .findByStatusAndPriceLessThanEqual(
                            "APPROVED",
                            maxPrice
                    );
        }


        // =================================================
        // NO FILTER
        // =================================================

        return getApprovedProducts();
    }


    // =====================================================
    // PRICE RANGE → MAXIMUM PRICE
    // =====================================================

    private double getMaxPrice(
            String priceRange) {


        switch (priceRange) {

            case "UNDER_500":

                return 500;


            case "500_1000":

                return 1000;


            case "1000_2000":

                return 2000;


            case "ABOVE_2000":

                return Double.MAX_VALUE;


            default:

                return Double.MAX_VALUE;
        }
    }


    // =====================================================
    // OLD QUALITY + PRICE FILTER
    // =====================================================

    public List<Product> filterProducts(
            int qualityScore,
            double price) {

        return productRepository
                .findByStatusAndQualityScoreGreaterThanEqualAndPriceLessThanEqual(
                        "APPROVED",
                        qualityScore,
                        price
                );
    }


    // =====================================================
    // CHECK STOCK
    // =====================================================

    public boolean hasEnoughStock(
            String productId,
            int quantity) {


        Product product =
                productRepository
                        .findById(productId)
                        .orElse(null);


        if (product == null) {

            return false;
        }


        return product.getStock() >= quantity;
    }


    // =====================================================
    // REDUCE STOCK
    // =====================================================

    public boolean reduceStock(
            String productId,
            int quantity) {


        Product product =
                productRepository
                        .findById(productId)
                        .orElse(null);


        if (product == null) {

            return false;
        }


        if (product.getStock() < quantity) {

            return false;
        }


        product.setStock(
                product.getStock() - quantity
        );


        productRepository.save(product);


        return true;
    }
}
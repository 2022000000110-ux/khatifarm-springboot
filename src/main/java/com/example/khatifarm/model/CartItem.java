package com.example.khatifarm.model;

public class CartItem {

    private String productId;
    private String productName;
    private String imageUrl;
    private double price;
    private int quantity;
    private int qualityScore;
    private String grade;

    // NEW
    private String sellerName;
    private String sellerType;

    public CartItem() {
    }

    public CartItem(
            String productId,
            String productName,
            String imageUrl,
            double price,
            int quantity,
            int qualityScore,
            String grade,
            String sellerName,
            String sellerType) {

        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.qualityScore = qualityScore;
        this.grade = grade;

        // NEW
        this.sellerName = sellerName;
        this.sellerType = sellerType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    // =========================
    // SELLER INFORMATION
    // =========================

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerType() {
        return sellerType;
    }

    public void setSellerType(String sellerType) {
        this.sellerType = sellerType;
    }

    // =========================
    // SUBTOTAL
    // =========================

    public double getSubtotal() {
        return price * quantity;
    }
}
package com.example.khatifarm.controller;

import com.example.khatifarm.model.Admin;
import com.example.khatifarm.model.Order;
import com.example.khatifarm.model.Product;
import com.example.khatifarm.model.Seller;
import com.example.khatifarm.service.FileUploadService;
import com.example.khatifarm.service.OrderService;
import com.example.khatifarm.service.ProductService;
import com.example.khatifarm.service.SellerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final SellerService sellerService;
    private final OrderService orderService;
    private final FileUploadService fileUploadService;

    public AdminController(
            ProductService productService,
            SellerService sellerService,
            OrderService orderService,
            FileUploadService fileUploadService) {

        this.productService = productService;
        this.sellerService = sellerService;
        this.orderService = orderService;
        this.fileUploadService = fileUploadService;
    }


    // =========================================================
    // ADMIN LOGIN PAGE
    // =========================================================

    @GetMapping("/login")
    public String loginPage(
            Model model,
            HttpSession session) {

        Admin loggedInAdmin =
                (Admin) session.getAttribute("loggedInAdmin");

        if (loggedInAdmin != null) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute(
                "admin",
                new Admin()
        );

        return "admin-login";
    }


    // =========================================================
    // ADMIN LOGIN
    // =========================================================

    @PostMapping("/login")
    public String loginAdmin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (username.equals("admin")
                && password.equals("admin123")) {

            session.setAttribute(
                    "loggedInAdmin",
                    new Admin(username, password)
            );

            return "redirect:/admin/dashboard";
        }

        model.addAttribute(
                "error",
                "Invalid admin username or password!"
        );

        return "admin-login";
    }


    // =========================================================
    // ADMIN DASHBOARD
    // =========================================================

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        var products =
                productService.getAllProducts();

        var orders =
                orderService.getAllOrders();


        model.addAttribute(
                "products",
                products
        );


        // -----------------------------------------------------
        // PRODUCT COUNTS
        // -----------------------------------------------------

        model.addAttribute(
                "pendingCount",
                products.stream()
                        .filter(p ->
                                "PENDING".equals(p.getStatus()))
                        .count()
        );

        model.addAttribute(
                "approvedCount",
                products.stream()
                        .filter(p ->
                                "APPROVED".equals(p.getStatus()))
                        .count()
        );

        model.addAttribute(
                "rejectedCount",
                products.stream()
                        .filter(p ->
                                "REJECTED".equals(p.getStatus()))
                        .count()
        );


        // -----------------------------------------------------
        // ORDER COUNTS
        // -----------------------------------------------------

        model.addAttribute(
                "totalOrders",
                orders.size()
        );

        model.addAttribute(
                "pendingOrders",
                orders.stream()
                        .filter(o ->
                                "PENDING".equals(o.getStatus()))
                        .count()
        );

        model.addAttribute(
                "processingOrders",
                orders.stream()
                        .filter(o ->
                                "PROCESSING".equals(o.getStatus()))
                        .count()
        );

        model.addAttribute(
                "shippedOrders",
                orders.stream()
                        .filter(o ->
                                "SHIPPED".equals(o.getStatus()))
                        .count()
        );

        model.addAttribute(
                "deliveredOrders",
                orders.stream()
                        .filter(o ->
                                "DELIVERED".equals(o.getStatus()))
                        .count()
        );


        return "admin-dashboard";
    }


    // =========================================================
    // REVIEW SELLER PRODUCT
    // =========================================================

    @GetMapping("/products/review/{id}")
    public String reviewProduct(
            @PathVariable String id,
            Model model,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Product product =
                productService.getProductById(id);

        if (product == null) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute(
                "product",
                product
        );

        return "admin-review-product";
    }


    // =========================================================
    // APPROVE PRODUCT
    // =========================================================

    @PostMapping("/products/approve/{id}")
    public String approveProduct(
            @PathVariable String id,
            @RequestParam int qualityScore,
            @RequestParam String grade,
            @RequestParam double approvedPrice,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Product product =
                productService.getProductById(id);

        if (product != null) {

            // Quality Score: 0–100
            int safeScore =
                    Math.max(
                            0,
                            Math.min(
                                    100,
                                    qualityScore
                            )
                    );

            product.setQualityScore(
                    safeScore
            );

            product.setGrade(
                    grade
            );

            product.setPrice(
                    approvedPrice
            );

            product.setStatus(
                    "APPROVED"
            );

            productService.saveProduct(
                    product
            );
        }

        return "redirect:/admin/dashboard";
    }


    // =========================================================
    // REJECT PRODUCT
    // =========================================================

    @PostMapping("/products/reject/{id}")
    public String rejectProduct(
            @PathVariable String id,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Product product =
                productService.getProductById(id);

        if (product != null) {

            product.setStatus(
                    "REJECTED"
            );

            productService.saveProduct(
                    product
            );
        }

        return "redirect:/admin/dashboard";
    }


    // =========================================================
    // SHOW ADD KHATIFARM PRODUCT PAGE
    // =========================================================

    @GetMapping("/products/add")
    public String showAddOwnProductPage(
            Model model,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute(
                "product",
                new Product()
        );

        return "admin-add-product";
    }


    // =========================================================
    // SAVE KHATIFARM OWN PRODUCT
    // =========================================================

    @PostMapping("/products/save")
    public String saveOwnProduct(
            @ModelAttribute Product product,
            @RequestParam("imageFile") MultipartFile imageFile,
            HttpSession session,
            Model model) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        try {

            // -------------------------------------------------
            // IMAGE UPLOAD
            // -------------------------------------------------

            String imagePath =
                    fileUploadService.uploadImage(
                            imageFile
                    );

            if (imagePath != null) {

                product.setImageUrl(
                        imagePath
                );
            }


            // -------------------------------------------------
            // KHATIFARM PRODUCT INFORMATION
            // -------------------------------------------------

            product.setId(null);

            product.setSellerName(
                    "KhatiFarm"
            );

            product.setSellerType(
                    "KHATIFARM"
            );


            // -------------------------------------------------
            // OWN PRODUCTS ARE APPROVED
            // -------------------------------------------------

            product.setStatus(
                    "APPROVED"
            );


            // -------------------------------------------------
            // QUALITY SCORE
            // -------------------------------------------------

            int safeScore =
                    Math.max(
                            0,
                            Math.min(
                                    100,
                                    product.getQualityScore()
                            )
                    );

            product.setQualityScore(
                    safeScore
            );


            // -------------------------------------------------
            // DEFAULT GRADE
            // -------------------------------------------------

            if (product.getGrade() == null
                    || product.getGrade().isBlank()) {

                product.setGrade(
                        "A"
                );
            }


            // -------------------------------------------------
            // SAVE
            // -------------------------------------------------

            productService.saveProduct(
                    product
            );


            return "redirect:/admin/dashboard";


        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Image upload failed: "
                            + e.getMessage()
            );

            return "admin-add-product";
        }
    }


    // =========================================================
    // SHOW EDIT PRODUCT PAGE
    // =========================================================

    @GetMapping("/products/edit/{id}")
    public String showEditProductPage(
            @PathVariable String id,
            Model model,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Product product =
                productService.getProductById(id);

        if (product == null) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute(
                "product",
                product
        );

        return "admin-edit-product";
    }


    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @PostMapping("/products/update/{id}")
    public String updateProduct(
            @PathVariable String id,
            @ModelAttribute Product updatedProduct,
            @RequestParam(
                    value = "imageFile",
                    required = false
            )
            MultipartFile imageFile,
            HttpSession session,
            Model model) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }


        // -----------------------------------------------------
        // FIND EXISTING PRODUCT
        // -----------------------------------------------------

        Product existingProduct =
                productService.getProductById(id);

        if (existingProduct == null) {
            return "redirect:/admin/dashboard";
        }


        try {

            // -------------------------------------------------
            // BASIC INFORMATION
            // -------------------------------------------------

            existingProduct.setName(
                    updatedProduct.getName()
            );

            existingProduct.setCategory(
                    updatedProduct.getCategory()
            );

            existingProduct.setOrigin(
                    updatedProduct.getOrigin()
            );


            // -------------------------------------------------
            // DESCRIPTION
            // -------------------------------------------------

            existingProduct.setDescription(
                    updatedProduct.getDescription()
            );


            // -------------------------------------------------
            // BENEFITS
            // -------------------------------------------------

            existingProduct.setBenefits(
                    updatedProduct.getBenefits()
            );


            // -------------------------------------------------
            // PRICE
            // -------------------------------------------------

            existingProduct.setPrice(
                    updatedProduct.getPrice()
            );


            // -------------------------------------------------
            // STOCK
            // -------------------------------------------------

            existingProduct.setStock(
                    updatedProduct.getStock()
            );


            // -------------------------------------------------
            // QUALITY SCORE
            // -------------------------------------------------

            int safeScore =
                    Math.max(
                            0,
                            Math.min(
                                    100,
                                    updatedProduct.getQualityScore()
                            )
                    );

            existingProduct.setQualityScore(
                    safeScore
            );


            // -------------------------------------------------
            // GRADE
            // -------------------------------------------------

            existingProduct.setGrade(
                    updatedProduct.getGrade()
            );


            // -------------------------------------------------
            // STATUS
            // -------------------------------------------------

            existingProduct.setStatus(
                    updatedProduct.getStatus()
            );


            // -------------------------------------------------
            // REPLACE IMAGE ONLY IF NEW IMAGE SELECTED
            // -------------------------------------------------

            if (imageFile != null
                    && !imageFile.isEmpty()) {

                String imagePath =
                        fileUploadService.uploadImage(
                                imageFile
                        );

                if (imagePath != null) {

                    existingProduct.setImageUrl(
                            imagePath
                    );
                }
            }


            // -------------------------------------------------
            // SAVE UPDATED PRODUCT
            // -------------------------------------------------

            productService.saveProduct(
                    existingProduct
            );


            return "redirect:/admin/dashboard";


        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Product update failed: "
                            + e.getMessage()
            );

            model.addAttribute(
                    "product",
                    existingProduct
            );

            return "admin-edit-product";
        }
    }


    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(
            @PathVariable String id,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        productService.deleteProduct(
                id
        );

        return "redirect:/admin/dashboard";
    }


    // =========================================================
    // VIEW ALL SELLERS
    // =========================================================

    @GetMapping("/sellers")
    public String viewAllSellers(
            Model model,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute(
                "sellers",
                sellerService.getAllSellers()
        );

        return "admin-sellers";
    }


    // =========================================================
    // VIEW ALL ORDERS
    // =========================================================

    @GetMapping("/orders")
    public String orders(
            Model model,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute(
                "orders",
                orderService.getAllOrders()
        );

        return "admin-orders";
    }


    // =========================================================
    // VIEW ORDER DETAILS
    // =========================================================

    @GetMapping("/orders/view/{id}")
    public String viewOrder(
            @PathVariable String id,
            Model model,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Order order =
                orderService.getOrderById(id);

        if (order == null) {
            return "redirect:/admin/orders";
        }

        model.addAttribute(
                "order",
                order
        );

        return "admin-order-details";
    }


    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    @PostMapping("/orders/update/{id}")
    public String updateOrderStatus(
            @PathVariable String id,
            @RequestParam String status,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Order order =
                orderService.getOrderById(id);

        if (order != null) {

            order.setStatus(
                    status
            );

            orderService.saveOrder(
                    order
            );
        }

        return "redirect:/admin/orders/view/" + id;
    }


    // =========================================================
    // ADMIN LOGOUT
    // =========================================================

    @GetMapping("/logout")
    public String logout(
            HttpSession session) {

        session.removeAttribute(
                "loggedInAdmin"
        );

        return "redirect:/admin/login";
    }


    // =========================================================
    // LOGIN CHECK
    // =========================================================

    private boolean isLoggedIn(
            HttpSession session) {

        return session.getAttribute(
                "loggedInAdmin"
        ) != null;
    }
}
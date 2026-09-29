package com.example.khatifarm.controller;

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

import java.util.List;

@Controller
@RequestMapping("/seller")
public class SellerController {

    private final SellerService sellerService;
    private final ProductService productService;
    private final OrderService orderService;
    private final FileUploadService fileUploadService;

    public SellerController(
            SellerService sellerService,
            ProductService productService,
            OrderService orderService,
            FileUploadService fileUploadService) {

        this.sellerService = sellerService;
        this.productService = productService;
        this.orderService = orderService;
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute(
                "seller",
                new Seller()
        );

        return "seller-register";
    }

    @PostMapping("/register")
    public String registerSeller(
            @ModelAttribute Seller seller) {

        sellerService.registerSeller(seller);

        return "redirect:/seller/login";
    }

    @GetMapping("/login")
    public String showLoginPage(
            Model model,
            HttpSession session) {

        Seller loggedInSeller =
                (Seller) session.getAttribute(
                        "loggedInSeller"
                );

        if (loggedInSeller != null) {
            return "redirect:/seller/dashboard";
        }

        model.addAttribute(
                "seller",
                new Seller()
        );

        return "seller-login";
    }

    @PostMapping("/login")
    public String loginSeller(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        Seller seller =
                sellerService.findByEmail(email);

        if (seller != null
                && seller.getPassword().equals(password)) {

            session.setAttribute(
                    "loggedInSeller",
                    seller
            );

            return "redirect:/seller/dashboard";
        }

        model.addAttribute(
                "error",
                "Invalid email or password!"
        );

        return "seller-login";
    }

    @GetMapping("/dashboard")
    public String sellerDashboard(
            Model model,
            HttpSession session) {

        Seller seller =
                getLoggedInSeller(session);

        if (seller == null) {
            return "redirect:/seller/login";
        }

        model.addAttribute(
                "seller",
                seller
        );

        return "seller-dashboard";
    }

    @GetMapping("/products/add")
    public String showAddProductPage(
            Model model,
            HttpSession session) {

        Seller seller =
                getLoggedInSeller(session);

        if (seller == null) {
            return "redirect:/seller/login";
        }

        model.addAttribute(
                "product",
                new Product()
        );

        return "seller-add-product";
    }

    @PostMapping("/products/save")
    public String saveSellerProduct(
            @ModelAttribute Product product,
            @RequestParam("imageFile") MultipartFile imageFile,
            HttpSession session,
            Model model) {

        Seller seller =
                getLoggedInSeller(session);

        if (seller == null) {
            return "redirect:/seller/login";
        }

        try {

            String imagePath =
                    fileUploadService.uploadImage(imageFile);

            if (imagePath != null) {
                product.setImageUrl(imagePath);
            }

            product.setSellerName(
                    seller.getName()
            );

            product.setSellerType(
                    "SELLER"
            );

            product.setQualityScore(0);
            product.setGrade("PENDING");
            product.setStatus("PENDING");

            productService.saveProduct(product);

            return "redirect:/seller/products";

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Image upload failed: " + e.getMessage()
            );

            return "seller-add-product";
        }
    }

    @GetMapping("/products")
    public String sellerProducts(
            Model model,
            HttpSession session) {

        Seller seller =
                getLoggedInSeller(session);

        if (seller == null) {
            return "redirect:/seller/login";
        }

        model.addAttribute(
                "products",
                productService.getProductsBySeller(
                        seller.getName()
                )
        );

        model.addAttribute(
                "sellerName",
                seller.getName()
        );

        return "seller-products";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditProductPage(
            @PathVariable String id,
            Model model,
            HttpSession session) {

        Seller seller =
                getLoggedInSeller(session);

        if (seller == null) {
            return "redirect:/seller/login";
        }

        Product product =
                productService.getProductById(id);

        if (product == null) {
            return "redirect:/seller/products";
        }

        if (!seller.getName().equals(
                product.getSellerName())) {

            return "redirect:/seller/products";
        }

        model.addAttribute(
                "product",
                product
        );

        return "seller-edit-product";
    }

    @PostMapping("/products/update/{id}")
    public String updateSellerProduct(
            @PathVariable String id,
            @ModelAttribute Product updatedProduct,
            @RequestParam(
                    value = "imageFile",
                    required = false
            )
            MultipartFile imageFile,
            HttpSession session,
            Model model) {

        Seller seller =
                getLoggedInSeller(session);

        if (seller == null) {
            return "redirect:/seller/login";
        }

        Product existingProduct =
                productService.getProductById(id);

        if (existingProduct == null) {
            return "redirect:/seller/products";
        }

        if (!seller.getName().equals(
                existingProduct.getSellerName())) {

            return "redirect:/seller/products";
        }

        try {

            existingProduct.setName(
                    updatedProduct.getName()
            );

            existingProduct.setCategory(
                    updatedProduct.getCategory()
            );

            existingProduct.setOrigin(
                    updatedProduct.getOrigin()
            );

            existingProduct.setDescription(
                    updatedProduct.getDescription()
            );

            existingProduct.setBenefits(
                    updatedProduct.getBenefits()
            );

            existingProduct.setPrice(
                    updatedProduct.getPrice()
            );

            existingProduct.setStock(
                    updatedProduct.getStock()
            );

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

            existingProduct.setSellerName(
                    seller.getName()
            );

            existingProduct.setSellerType(
                    "SELLER"
            );

            // Any seller edit requires a fresh admin review.
            existingProduct.setQualityScore(0);
            existingProduct.setGrade("PENDING");
            existingProduct.setStatus("PENDING");

            productService.saveProduct(
                    existingProduct
            );

            return "redirect:/seller/products";

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Product update failed: " + e.getMessage()
            );

            model.addAttribute(
                    "product",
                    existingProduct
            );

            return "seller-edit-product";
        }
    }

    @GetMapping("/orders")
    public String sellerOrders(
            Model model,
            HttpSession session) {

        Seller seller =
                getLoggedInSeller(session);

        if (seller == null) {
            return "redirect:/seller/login";
        }

        List<Order> orders =
                orderService.getOrdersBySeller(
                        seller.getName()
                );

        model.addAttribute(
                "orders",
                orders
        );

        model.addAttribute(
                "seller",
                seller
        );

        return "seller-orders";
    }

    @GetMapping("/logout")
    public String logout(
            HttpSession session) {

        // Keep the current project behavior.
        session.invalidate();

        return "redirect:/seller/login";
    }

    private Seller getLoggedInSeller(
            HttpSession session) {

        return (Seller) session.getAttribute(
                "loggedInSeller"
        );
    }
}

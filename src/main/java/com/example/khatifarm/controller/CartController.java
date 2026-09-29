package com.example.khatifarm.controller;

import com.example.khatifarm.model.CartItem;
import com.example.khatifarm.model.Product;
import com.example.khatifarm.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductService productService;

    public CartController(ProductService productService) {
        this.productService = productService;
    }


    // =========================================================
    // SHOW CART
    // URL: /cart
    // =========================================================
    @GetMapping
    public String showCart(
            HttpSession session,
            Model model) {

        List<CartItem> cart = getCartFromSession(session);

        double total = calculateTotal(cart);

        // Support existing cart.html
        model.addAttribute("cartItems", cart);

        // Also provide cart for compatibility
        model.addAttribute("cart", cart);

        model.addAttribute("total", total);

        return "cart";
    }


    // =========================================================
    // ADD TO CART
    //
    // URL:
    // /cart/add/{id}
    //
    // Example:
    // /cart/add/12345
    // =========================================================
    @GetMapping("/add/{id}")
    public String addToCart(
            @PathVariable("id") String id,
            HttpSession session) {

        System.out.println("=================================");
        System.out.println("ADD TO CART REQUEST");
        System.out.println("Product ID = " + id);
        System.out.println("=================================");


        // -----------------------------------------------------
        // Find product
        // -----------------------------------------------------
        Product product =
                productService.getProductById(id);


        // -----------------------------------------------------
        // Product not found
        // -----------------------------------------------------
        if (product == null) {

            System.out.println(
                    "ERROR: Product not found: " + id
            );

            return "redirect:/products";
        }


        System.out.println(
                "Product found: " + product.getName()
        );


        // -----------------------------------------------------
        // Stock check
        // -----------------------------------------------------
        if (product.getStock() <= 0) {

            System.out.println(
                    "ERROR: Product is out of stock"
            );

            return "redirect:/products/" + id;
        }


        // -----------------------------------------------------
        // Get existing cart
        // -----------------------------------------------------
        List<CartItem> cart =
                getCartFromSession(session);


        System.out.println(
                "Cart before adding = "
                        + cart.size()
        );


        // -----------------------------------------------------
        // Check whether product already exists
        // -----------------------------------------------------
        boolean found = false;


        for (CartItem item : cart) {

            if (item.getProductId() != null
                    && item.getProductId().equals(id)) {

                // ---------------------------------------------
                // Product already exists
                // Increase quantity
                // ---------------------------------------------

                int newQuantity =
                        item.getQuantity() + 1;


                // Do not exceed stock
                if (newQuantity > product.getStock()) {

                    newQuantity =
                            product.getStock();
                }


                item.setQuantity(
                        newQuantity
                );


                found = true;

                System.out.println(
                        "Existing product quantity updated: "
                                + newQuantity
                );

                break;
            }
        }


        // -----------------------------------------------------
        // Product not found in cart
        // Add new item
        // -----------------------------------------------------
        if (!found) {

            CartItem cartItem =
                    createCartItem(product);

            cart.add(cartItem);

            System.out.println(
                    "New product added to cart"
            );
        }


        // -----------------------------------------------------
        // Save cart in session
        // -----------------------------------------------------
        session.setAttribute(
                "cart",
                cart
        );


        // Compatibility with possible old code
        session.setAttribute(
                "cartItems",
                cart
        );


        System.out.println(
                "Cart after adding = "
                        + cart.size()
        );


        // -----------------------------------------------------
        // NORMAL ADD TO CART
        // Go to cart page
        // -----------------------------------------------------
        return "redirect:/cart";
    }


    // =========================================================
    // BUY NOW
    //
    // URL:
    // /cart/add/{id}?buyNow=true
    // =========================================================
    @GetMapping(
            value = "/add/{id}",
            params = "buyNow"
    )
    public String buyNow(
            @PathVariable("id") String id,
            @RequestParam(
                    value = "buyNow",
                    defaultValue = "false"
            ) boolean buyNow,
            HttpSession session) {


        // -----------------------------------------------------
        // If buyNow is false
        // normal add-to-cart behavior
        // -----------------------------------------------------
        if (!buyNow) {

            return addToCart(
                    id,
                    session
            );
        }


        // -----------------------------------------------------
        // Find product
        // -----------------------------------------------------
        Product product =
                productService.getProductById(id);


        if (product == null) {
            return "redirect:/products";
        }


        // -----------------------------------------------------
        // Check stock
        // -----------------------------------------------------
        if (product.getStock() <= 0) {
            return "redirect:/products/" + id;
        }


        // -----------------------------------------------------
        // Create fresh cart
        // -----------------------------------------------------
        List<CartItem> cart =
                new ArrayList<>();


        // -----------------------------------------------------
        // Add selected product
        // -----------------------------------------------------
        CartItem cartItem =
                createCartItem(product);

        cart.add(cartItem);


        // -----------------------------------------------------
        // Save new cart
        // -----------------------------------------------------
        session.setAttribute(
                "cart",
                cart
        );


        session.setAttribute(
                "cartItems",
                cart
        );


        // -----------------------------------------------------
        // Go directly to checkout
        // -----------------------------------------------------
        return "redirect:/checkout";
    }


    // =========================================================
    // REMOVE PRODUCT
    //
    // URL:
    // /cart/remove/{id}
    // =========================================================
    @GetMapping("/remove/{id}")
    public String removeFromCart(
            @PathVariable("id") String id,
            HttpSession session) {


        List<CartItem> cart =
                getCartFromSession(session);


        cart.removeIf(item ->
                item.getProductId() != null
                        && item.getProductId().equals(id)
        );


        saveCart(
                session,
                cart
        );


        return "redirect:/cart";
    }


    // =========================================================
    // CLEAR CART
    //
    // URL:
    // /cart/clear
    // =========================================================
    @GetMapping("/clear")
    public String clearCart(
            HttpSession session) {


        session.removeAttribute("cart");

        session.removeAttribute("cartItems");


        return "redirect:/cart";
    }


    // =========================================================
    // UPDATE QUANTITY
    //
    // URL:
    // /cart/update/{id}
    // Method: POST
    // =========================================================
    @PostMapping("/update/{id}")
    public String updateQuantity(
            @PathVariable("id") String id,
            @RequestParam("quantity") int quantity,
            HttpSession session) {


        Product product =
                productService.getProductById(id);


        if (product == null) {
            return "redirect:/cart";
        }


        List<CartItem> cart =
                getCartFromSession(session);


        // -----------------------------------------------------
        // Quantity minimum
        // -----------------------------------------------------
        if (quantity < 1) {
            quantity = 1;
        }


        // -----------------------------------------------------
        // Quantity maximum = available stock
        // -----------------------------------------------------
        if (quantity > product.getStock()) {

            quantity =
                    product.getStock();
        }


        // -----------------------------------------------------
        // If stock is zero remove item
        // -----------------------------------------------------
        if (product.getStock() <= 0) {

            cart.removeIf(item ->
                    item.getProductId() != null
                            && item.getProductId().equals(id)
            );

            saveCart(
                    session,
                    cart
            );

            return "redirect:/cart";
        }


        // -----------------------------------------------------
        // Update item
        // -----------------------------------------------------
        for (CartItem item : cart) {

            if (item.getProductId() != null
                    && item.getProductId().equals(id)) {

                item.setQuantity(
                        quantity
                );

                break;
            }
        }


        saveCart(
                session,
                cart
        );


        return "redirect:/cart";
    }


    // =========================================================
    // HELPER:
    // GET CART FROM SESSION
    // =========================================================
    @SuppressWarnings("unchecked")
    private List<CartItem> getCartFromSession(
            HttpSession session) {


        Object cartObject =
                session.getAttribute("cart");


        // -----------------------------------------------------
        // New cart
        // -----------------------------------------------------
        if (cartObject == null) {

            Object oldCartObject =
                    session.getAttribute("cartItems");


            if (oldCartObject != null) {

                List<CartItem> oldCart =
                        (List<CartItem>) oldCartObject;

                session.setAttribute(
                        "cart",
                        oldCart
                );

                return oldCart;
            }


            List<CartItem> newCart =
                    new ArrayList<>();


            session.setAttribute(
                    "cart",
                    newCart
            );


            session.setAttribute(
                    "cartItems",
                    newCart
            );


            return newCart;
        }


        return (List<CartItem>) cartObject;
    }


    // =========================================================
    // HELPER:
    // SAVE CART
    // =========================================================
    private void saveCart(
            HttpSession session,
            List<CartItem> cart) {


        session.setAttribute(
                "cart",
                cart
        );


        // Compatibility
        session.setAttribute(
                "cartItems",
                cart
        );
    }


    // =========================================================
    // HELPER:
    // CREATE CART ITEM
    // =========================================================
    private CartItem createCartItem(
            Product product) {


        CartItem item =
                new CartItem();


        // -----------------------------------------------------
        // Product information
        // -----------------------------------------------------
        item.setProductId(
                product.getId()
        );


        item.setProductName(
                product.getName()
        );


        item.setImageUrl(
                product.getImageUrl()
        );


        // -----------------------------------------------------
        // Price
        // -----------------------------------------------------
        item.setPrice(
                product.getPrice()
        );


        // -----------------------------------------------------
        // Initial quantity
        // -----------------------------------------------------
        item.setQuantity(1);


        // -----------------------------------------------------
        // Quality information
        // -----------------------------------------------------
        item.setQualityScore(
                product.getQualityScore()
        );


        item.setGrade(
                product.getGrade()
        );


        // -----------------------------------------------------
        // Seller information
        // -----------------------------------------------------
        item.setSellerName(
                product.getSellerName()
        );


        item.setSellerType(
                product.getSellerType()
        );


        return item;
    }


    // =========================================================
    // HELPER:
    // CALCULATE TOTAL
    // =========================================================
    private double calculateTotal(
            List<CartItem> cart) {


        double total = 0;


        for (CartItem item : cart) {

            total += item.getSubtotal();
        }


        return total;
    }
}


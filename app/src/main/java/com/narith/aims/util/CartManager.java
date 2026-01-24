package com.narith.aims.util;

import com.narith.aims.model.Product;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Product> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    // កែប្រែ៖ បើមានទំនិញហើយ គ្រាន់តែថែមចំនួន (Qty)
    public void addToCart(Product product) {
        for (Product p : cartItems) {
            if (p.getId() == product.getId()) {
                p.setCartQuantity(p.getCartQuantity() + 1);
                return;
            }
        }
        product.setCartQuantity(1); // ចាប់ផ្តើមពី 1
        cartItems.add(product);
    }

    // មុខងារបន្ថយចំនួន
    public void decreaseQuantity(Product product) {
        if (product.getCartQuantity() > 1) {
            product.setCartQuantity(product.getCartQuantity() - 1);
        } else {
            cartItems.remove(product); // បើនៅសល់ 0 លុបចេញតែម្តង
        }
    }

    // មុខងារលុបចេញ
    public void removeItem(Product product) {
        cartItems.remove(product);
    }

    public List<Product> getCartItems() { return cartItems; }

    public void clearCart() { cartItems.clear(); }

    // កែប្រែ៖ គណនាតម្លៃសរុបតាមចំនួន (Price * Qty)
    public double getTotalPrice() {
        double total = 0;
        for (Product p : cartItems) {
            total += (p.getPrice() * p.getCartQuantity());
        }
        return total;
    }
}
package com.narith.aims.utils;

import com.narith.aims.model.Product;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Product> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Product product) {
        // ឆែកមើលថាបើមានក្នុងកន្ត្រកហើយ ឱ្យវាថែមតែចំនួន (Quantity)
        for (Product item : cartItems) {
            if (item.getId() == product.getId()) {
                item.setCartQuantity(item.getCartQuantity() + 1);
                return;
            }
        }
        // បើមិនទាន់មាន ថែមចូលថ្មី
        product.setCartQuantity(1);
        cartItems.add(product);
    }

    public void decreaseQuantity(Product product) {
        for (Product item : cartItems) {
            if (item.getId() == product.getId()) {
                if (item.getCartQuantity() > 1) {
                    item.setCartQuantity(item.getCartQuantity() - 1);
                } else {
                    cartItems.remove(item);
                }
                break;
            }
        }
    }

    public void removeItem(Product product) {
        cartItems.removeIf(item -> item.getId() == product.getId());
    }

    public List<Product> getCartItems() {
        return cartItems;
    }

    public double getTotalPrice() {
        double total = 0;
        for (Product item : cartItems) {
            total += item.getPrice() * item.getCartQuantity();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }
}
package com.narith.aims.util;

import com.narith.aims.model.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * PRODUCT FILTER UTILITY
 * Advanced filtering and sorting for product lists
 * Supports multiple filter criteria and sort options
 */
public class ProductFilter {

    public enum SortOption {
        NAME_ASC,
        NAME_DESC,
        PRICE_ASC,
        PRICE_DESC,
        QUANTITY_ASC,
        QUANTITY_DESC,
        DATE_NEWEST,
        DATE_OLDEST,
        STATUS
    }

    public enum FilterCategory {
        ALL,
        LOW_STOCK,
        OUT_OF_STOCK,
        NEW_ITEMS,
        EXPIRING_SOON,
        EXPIRED
    }

    /**
     * Filter products by category
     */
    public static List<Product> filterByCategory(List<Product> products, String category) {
        if (category == null || category.equals("All")) {
            return new ArrayList<>(products);
        }

        List<Product> filtered = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                filtered.add(product);
            }
        }
        return filtered;
    }

    /**
     * Filter products by status
     */
    public static List<Product> filterByStatus(List<Product> products, FilterCategory filter) {
        List<Product> filtered = new ArrayList<>();

        for (Product product : products) {
            switch (filter) {
                case ALL:
                    filtered.add(product);
                    break;
                case LOW_STOCK:
                    if (product.getQuantity() <= product.getReorderPoint() && product.getQuantity() > 0) {
                        filtered.add(product);
                    }
                    break;
                case OUT_OF_STOCK:
                    if (product.getQuantity() == 0) {
                        filtered.add(product);
                    }
                    break;
                case NEW_ITEMS:
                    if (product.isNew()) {
                        filtered.add(product);
                    }
                    break;
                case EXPIRING_SOON:
                    if (product.isExpiringSoon()) {
                        filtered.add(product);
                    }
                    break;
                case EXPIRED:
                    if (product.isExpired()) {
                        filtered.add(product);
                    }
                    break;
            }
        }
        return filtered;
    }

    /**
     * Filter products by price range
     */
    public static List<Product> filterByPriceRange(List<Product> products, double minPrice, double maxPrice) {
        List<Product> filtered = new ArrayList<>();
        for (Product product : products) {
            if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                filtered.add(product);
            }
        }
        return filtered;
    }

    /**
     * Search products by keyword (name, SKU, category)
     */
    public static List<Product> searchProducts(List<Product> products, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(products);
        }

        String searchTerm = keyword.toLowerCase().trim();
        List<Product> results = new ArrayList<>();

        for (Product product : products) {
            if (product.getName().toLowerCase().contains(searchTerm) ||
                product.getSku().toLowerCase().contains(searchTerm) ||
                product.getCategory().toLowerCase().contains(searchTerm)) {
                results.add(product);
            }
        }
        return results;
    }

    /**
     * Sort products by specified option
     */
    public static List<Product> sortProducts(List<Product> products, SortOption sortOption) {
        List<Product> sorted = new ArrayList<>(products);

        switch (sortOption) {
            case NAME_ASC:
                Collections.sort(sorted, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                break;
            case NAME_DESC:
                Collections.sort(sorted, (p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()));
                break;
            case PRICE_ASC:
                Collections.sort(sorted, Comparator.comparingDouble(Product::getPrice));
                break;
            case PRICE_DESC:
                Collections.sort(sorted, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case QUANTITY_ASC:
                Collections.sort(sorted, Comparator.comparingInt(Product::getQuantity));
                break;
            case QUANTITY_DESC:
                Collections.sort(sorted, (p1, p2) -> Integer.compare(p2.getQuantity(), p1.getQuantity()));
                break;
            case DATE_NEWEST:
                Collections.sort(sorted, (p1, p2) -> p2.getDateAdded().compareTo(p1.getDateAdded()));
                break;
            case DATE_OLDEST:
                Collections.sort(sorted, (p1, p2) -> p1.getDateAdded().compareTo(p2.getDateAdded()));
                break;
            case STATUS:
                Collections.sort(sorted, (p1, p2) -> {
                    int priority1 = getStatusPriority(p1.getStatus());
                    int priority2 = getStatusPriority(p2.getStatus());
                    return Integer.compare(priority1, priority2);
                });
                break;
        }

        return sorted;
    }

    /**
     * Get priority for status sorting (lower number = higher priority)
     */
    private static int getStatusPriority(String status) {
        switch (status) {
            case "EXPIRED": return 1;
            case "EXPIRING_SOON": return 2;
            case "NEW": return 3;
            case "OLD": return 4;
            default: return 5;
        }
    }

    /**
     * Apply multiple filters and sort
     */
    public static List<Product> applyFiltersAndSort(List<Product> products, 
                                                     String category,
                                                     FilterCategory statusFilter,
                                                     String searchKeyword,
                                                     SortOption sortOption) {
        List<Product> result = new ArrayList<>(products);

        // Apply filters
        if (category != null && !category.equals("All")) {
            result = filterByCategory(result, category);
        }

        if (statusFilter != FilterCategory.ALL) {
            result = filterByStatus(result, statusFilter);
        }

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            result = searchProducts(result, searchKeyword);
        }

        // Apply sort
        if (sortOption != null) {
            result = sortProducts(result, sortOption);
        }

        return result;
    }
}

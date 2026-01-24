package com.narith.aims.util;

public class DiscountCalculator {
    
    /**
     * Calculate discount percentage based on purchase amount
     * Updated tiered discount system
     */
    public static int getDiscountPercentage(double amount) {
        if (amount >= 1000) return 50;
        if (amount >= 400) return 35;
        if (amount >= 350) return 30;
        if (amount >= 300) return 25;
        if (amount >= 200) return 25;
        if (amount >= 150) return 20;
        if (amount >= 100) return 15;
        if (amount >= 50) return 5;
        return 0;
    }
    
    /**
     * Calculate discount amount
     */
    public static double calculateDiscount(double subtotal) {
        int percentage = getDiscountPercentage(subtotal);
        return subtotal * percentage / 100.0;
    }
    
    /**
     * Calculate final total after discount
     */
    public static double calculateFinalTotal(double subtotal) {
        double discount = calculateDiscount(subtotal);
        return subtotal - discount;
    }
    
    /**
     * Get discount description
     */
    public static String getDiscountDescription(double amount) {
        int percentage = getDiscountPercentage(amount);
        if (percentage == 0) {
            return "No discount. Spend $50+ to get 5% off!";
        }
        
        String tier = "";
        if (amount >= 1000) tier = "$1000+";
        else if (amount >= 400) tier = "$400-$999";
        else if (amount >= 350) tier = "$350-$399";
        else if (amount >= 300) tier = "$300-$349";
        else if (amount >= 200) tier = "$200-$299";
        else if (amount >= 150) tier = "$150-$199";
        else if (amount >= 100) tier = "$100-$149";
        else if (amount >= 50) tier = "$50-$99";
        
        return percentage + "% off for purchases " + tier;
    }
    
    /**
     * Get next discount tier info
     */
    public static String getNextTierInfo(double amount) {
        if (amount >= 1000) return "Maximum discount reached! 🎉";
        if (amount >= 400) return "Spend $" + String.format("%.2f", 1000 - amount) + " more to reach $1000 tier (50% off)";
        if (amount >= 350) return "Spend $" + String.format("%.2f", 400 - amount) + " more to reach $400 tier (35% off)";
        if (amount >= 300) return "Spend $" + String.format("%.2f", 350 - amount) + " more to reach $350 tier (30% off)";
        if (amount >= 200) return "Spend $" + String.format("%.2f", 300 - amount) + " more to reach $300 tier (25% off)";
        if (amount >= 150) return "Spend $" + String.format("%.2f", 200 - amount) + " more to reach $200 tier (25% off)";
        if (amount >= 100) return "Spend $" + String.format("%.2f", 150 - amount) + " more to reach $150 tier (20% off)";
        if (amount >= 50) return "Spend $" + String.format("%.2f", 100 - amount) + " more to reach $100 tier (15% off)";
        return "Spend $" + String.format("%.2f", 50 - amount) + " more to get 5% off!";
    }
    
    /**
     * Cashback bonus: $100 for 3rd purchase of the day
     */
    public static final double CASHBACK_AMOUNT = 100.0;
    public static final int CASHBACK_THRESHOLD = 3;
}

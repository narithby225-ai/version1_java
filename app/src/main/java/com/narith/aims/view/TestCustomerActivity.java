package com.narith.aims.view;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.narith.aims.R;
import com.narith.aims.database.DatabaseHelper;
import com.narith.aims.model.Product;
import com.narith.aims.utils.CartManager;
import java.util.ArrayList;
import java.util.List;

/**
 * TEST CUSTOMER ACTIVITY
 * Admin-only feature to test sales/purchase flow without switching accounts
 * Includes transaction history viewer
 */
public class TestCustomerActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvProducts, rvTestTransactions;
    private ProductAdapter productAdapter;
    private TestTransactionAdapter transactionAdapter;
    private List<Product> productList;
    private List<TestTransaction> testTransactionList;
    private TabLayout tabLayout;
    private Button btnViewCart, btnClearTestData;
    private TextView tvCartCount, tvTestInfo;
    private LinearLayout layoutTestHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_customer);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        setupTabs();
        loadProducts();
        loadTestTransactions();
        updateCartBadge();
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        rvProducts = findViewById(R.id.rvProducts);
        rvTestTransactions = findViewById(R.id.rvTestTransactions);
        layoutTestHistory = findViewById(R.id.layoutTestHistory);
        btnViewCart = findViewById(R.id.btnViewCart);
        btnClearTestData = findViewById(R.id.btnClearTestData);
        tvCartCount = findViewById(R.id.tvCartCount);
        tvTestInfo = findViewById(R.id.tvTestInfo);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvTestTransactions.setLayoutManager(new LinearLayoutManager(this));

        tvTestInfo.setText("Quick sales and customer management");

        btnViewCart.setOnClickListener(v -> viewTestCart());
        btnClearTestData.setOnClickListener(v -> confirmClearTestData());
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("🛍️ Products"));
        tabLayout.addTab(tabLayout.newTab().setText("📋 Sales History"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // Show Products tab
                    rvProducts.setVisibility(android.view.View.VISIBLE);
                    layoutTestHistory.setVisibility(android.view.View.GONE);
                } else {
                    // Show Sales History tab
                    rvProducts.setVisibility(android.view.View.GONE);
                    layoutTestHistory.setVisibility(android.view.View.VISIBLE);
                    loadTestTransactions();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Refresh data when tab is reselected
                if (tab.getPosition() == 0) {
                    loadProducts();
                } else {
                    loadTestTransactions();
                }
            }
        });
    }

    private void loadProducts() {
        productList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS, null);

        if (cursor.moveToFirst()) {
            do {
                productList.add(new Product(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getInt(4), cursor.getInt(5),
                    cursor.getDouble(6), cursor.getString(7), cursor.getString(8),
                    cursor.getString(9)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        productAdapter = new ProductAdapter(this, productList, "user");
        productAdapter.setOnProductActionListener(new ProductAdapter.OnProductActionListener() {
            @Override
            public void onBuyClick(Product product) {
                addToTestCart(product);
            }
            @Override public void onEditClick(Product product) {}
            @Override public void onDeleteClick(Product product) {}
        });
        rvProducts.setAdapter(productAdapter);
    }

    private void addToTestCart(Product product) {
        if (product.getQuantity() <= 0) {
            Toast.makeText(this, "❌ Out of stock!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if product already in cart
        boolean found = false;
        for (Product p : CartManager.getInstance().getCartItems()) {
            if (p.getId() == product.getId()) {
                int newQty = p.getCartQuantity() + 1;
                if (newQty > product.getQuantity()) {
                    Toast.makeText(this, 
                        "❌ Cannot add more! Stock: " + product.getQuantity(),
                        Toast.LENGTH_SHORT).show();
                    return;
                }
                p.setCartQuantity(newQty);
                found = true;
                break;
            }
        }
        
        if (!found) {
            // Create a copy of the product for the cart
            Product cartProduct = new Product(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getCategory(),
                product.getQuantity(),
                product.getReorderPoint(),
                product.getPrice(),
                product.getImageUri(),
                product.getDateAdded(),
                product.getExpiryDate()
            );
            cartProduct.setCartQuantity(1);
            CartManager.getInstance().addToCart(cartProduct);
        }
        
        // Just show a quick toast, don't open cart
        Toast.makeText(this, "✅ Added to cart", Toast.LENGTH_SHORT).show();
        updateCartBadge();
    }

    private void updateCartBadge() {
        int totalQty = 0;
        for (Product p : CartManager.getInstance().getCartItems()) {
            totalQty += p.getCartQuantity();
        }
        tvCartCount.setText(String.valueOf(totalQty));
        tvCartCount.setVisibility(totalQty > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void viewTestCart() {
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            Toast.makeText(this, "🛒 Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create custom dialog with cart items and payment form
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_test_checkout, null);
        
        RecyclerView rvCartItems = dialogView.findViewById(R.id.rvCartItems);
        TextView tvTotalAmount = dialogView.findViewById(R.id.tvTotalAmount);
        TextView tvFinalTotal = dialogView.findViewById(R.id.tvFinalTotal);
        TextView tvDiscountDetails = dialogView.findViewById(R.id.tvDiscountDetails);
        TextView tvDiscountAmount = dialogView.findViewById(R.id.tvDiscountAmount);
        TextView tvCashbackDetails = dialogView.findViewById(R.id.tvCashbackDetails);
        com.google.android.material.card.MaterialCardView cardDiscountInfo = dialogView.findViewById(R.id.cardDiscountInfo);
        com.google.android.material.card.MaterialCardView cardCashbackInfo = dialogView.findViewById(R.id.cardCashbackInfo);
        com.google.android.material.textfield.TextInputEditText etCustomerName = dialogView.findViewById(R.id.etCustomerName);
        com.google.android.material.textfield.TextInputEditText etCustomerPhone = dialogView.findViewById(R.id.etCustomerPhone);
        Button btnCompletePurchase = dialogView.findViewById(R.id.btnCompletePurchase);
        Button btnClearCart = dialogView.findViewById(R.id.btnClearCart);
        
        // Setup cart items recycler view
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        CartItemAdapter cartAdapter = new CartItemAdapter(this, CartManager.getInstance().getCartItems(), 
            new CartItemAdapter.OnCartItemChangeListener() {
                @Override
                public void onQuantityChanged() {
                    updateTotalInDialog(tvTotalAmount, tvFinalTotal, tvDiscountDetails, tvDiscountAmount, 
                        tvCashbackDetails, cardDiscountInfo, cardCashbackInfo, etCustomerPhone);
                    updateCartBadge();
                }
                
                @Override
                public void onItemRemoved() {
                    updateTotalInDialog(tvTotalAmount, tvFinalTotal, tvDiscountDetails, tvDiscountAmount, 
                        tvCashbackDetails, cardDiscountInfo, cardCashbackInfo, etCustomerPhone);
                    updateCartBadge();
                    if (CartManager.getInstance().getCartItems().isEmpty()) {
                        Toast.makeText(TestCustomerActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        rvCartItems.setAdapter(cartAdapter);
        
        // Update total
        updateTotalInDialog(tvTotalAmount, tvFinalTotal, tvDiscountDetails, tvDiscountAmount, 
            tvCashbackDetails, cardDiscountInfo, cardCashbackInfo, etCustomerPhone);
        
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create();
        
        // Listen to phone number changes to check for cashback
        etCustomerPhone.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotalInDialog(tvTotalAmount, tvFinalTotal, tvDiscountDetails, tvDiscountAmount, 
                    tvCashbackDetails, cardDiscountInfo, cardCashbackInfo, etCustomerPhone);
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        
        btnClearCart.setOnClickListener(v -> {
            CartManager.getInstance().clearCart();
            updateCartBadge();
            dialog.dismiss();
            Toast.makeText(this, "🗑️ Cart cleared", Toast.LENGTH_SHORT).show();
        });
        
        btnCompletePurchase.setOnClickListener(v -> {
            String customerName = etCustomerName.getText().toString().trim();
            String customerPhone = etCustomerPhone.getText().toString().trim();
            
            if (customerName.isEmpty()) {
                etCustomerName.setError("Required");
                return;
            }
            
            if (customerPhone.isEmpty()) {
                etCustomerPhone.setError("Required");
                return;
            }
            
            dialog.dismiss();
            completeTestSale(customerName, customerPhone);
        });
        
        dialog.show();
    }
    
    private void updateTotalInDialog(TextView tvTotalAmount, TextView tvFinalTotal, 
            TextView tvDiscountDetails, TextView tvDiscountAmount, TextView tvCashbackDetails,
            com.google.android.material.card.MaterialCardView cardDiscountInfo,
            com.google.android.material.card.MaterialCardView cardCashbackInfo,
            com.google.android.material.textfield.TextInputEditText etCustomerPhone) {
        
        double subtotal = 0;
        int totalItems = 0;
        for (Product p : CartManager.getInstance().getCartItems()) {
            subtotal += p.getPrice() * p.getCartQuantity();
            totalItems += p.getCartQuantity();
        }
        
        // Calculate discount
        int discountPercent = com.narith.aims.util.DiscountCalculator.getDiscountPercentage(subtotal);
        double discountAmount = com.narith.aims.util.DiscountCalculator.calculateDiscount(subtotal);
        double finalTotal = subtotal - discountAmount;
        
        // Check for cashback (3rd purchase today)
        boolean hasCashback = false;
        if (etCustomerPhone != null && etCustomerPhone.getText().toString().trim().length() > 0) {
            int purchaseCount = getTodayPurchaseCount(etCustomerPhone.getText().toString().trim());
            if (purchaseCount >= 2) { // This will be the 3rd purchase
                hasCashback = true;
                finalTotal += com.narith.aims.util.DiscountCalculator.CASHBACK_AMOUNT;
            }
        }
        
        // Update UI
        tvTotalAmount.setText(String.format("Subtotal: %d items = $%.2f", totalItems, subtotal));
        
        if (discountPercent > 0) {
            cardDiscountInfo.setVisibility(View.VISIBLE);
            tvDiscountDetails.setText(com.narith.aims.util.DiscountCalculator.getDiscountDescription(subtotal));
            tvDiscountAmount.setText(String.format("Discount (%d%%): -$%.2f", discountPercent, discountAmount));
        } else {
            cardDiscountInfo.setVisibility(View.GONE);
        }
        
        if (hasCashback) {
            cardCashbackInfo.setVisibility(View.VISIBLE);
            tvCashbackDetails.setText("🎉 3rd purchase today! +$100 cashback applied");
        } else {
            cardCashbackInfo.setVisibility(View.GONE);
        }
        
        tvFinalTotal.setText(String.format("Total: $%.2f", finalTotal));
    }
    
    private int getTodayPurchaseCount(String phone) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String today = DatabaseHelper.getCurrentDate();
        
        Cursor cursor = db.rawQuery(
            "SELECT COUNT(DISTINCT " + DatabaseHelper.COLUMN_SALE_TRANSACTION_ID + ") " +
            "FROM " + DatabaseHelper.TABLE_SALES + " " +
            "WHERE " + DatabaseHelper.COLUMN_SALE_CUSTOMER_PHONE + " = ? " +
            "AND " + DatabaseHelper.COLUMN_SALE_DATE + " = ?",
            new String[]{phone, today}
        );
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private void completeTestSale(String customerName, String customerPhone) {
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            Toast.makeText(this, "❌ Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate stock availability
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (Product product : CartManager.getInstance().getCartItems()) {
            Cursor cursor = db.rawQuery(
                "SELECT " + DatabaseHelper.COLUMN_QUANTITY + 
                " FROM " + DatabaseHelper.TABLE_PRODUCTS + 
                " WHERE " + DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(product.getId())}
            );
            
            if (cursor.moveToFirst()) {
                int currentStock = cursor.getInt(0);
                if (currentStock < product.getCartQuantity()) {
                    cursor.close();
                    Toast.makeText(this, 
                        "❌ Insufficient stock for " + product.getName() + 
                        "\nAvailable: " + currentStock + ", Requested: " + product.getCartQuantity(),
                        Toast.LENGTH_LONG).show();
                    return;
                }
            }
            cursor.close();
        }

        String today = DatabaseHelper.getCurrentDate();
        String transactionId = "TXN" + System.currentTimeMillis(); // Unique transaction ID
        
        // Calculate totals
        double subtotal = 0;
        int totalItems = 0;
        for (Product p : CartManager.getInstance().getCartItems()) {
            subtotal += p.getPrice() * p.getCartQuantity();
            totalItems += p.getCartQuantity();
        }
        
        // Apply discount
        int discountPercent = com.narith.aims.util.DiscountCalculator.getDiscountPercentage(subtotal);
        double discountAmount = com.narith.aims.util.DiscountCalculator.calculateDiscount(subtotal);
        double finalTotal = subtotal - discountAmount;
        
        // Check for cashback
        int purchaseCount = getTodayPurchaseCount(customerPhone);
        boolean hasCashback = purchaseCount >= 2; // This will be the 3rd
        if (hasCashback) {
            finalTotal += com.narith.aims.util.DiscountCalculator.CASHBACK_AMOUNT;
        }

        db.beginTransaction();
        try {
            for (Product product : CartManager.getInstance().getCartItems()) {
                double itemSubtotal = product.getPrice() * product.getCartQuantity();

                // Update stock
                db.execSQL("UPDATE " + DatabaseHelper.TABLE_PRODUCTS +
                        " SET " + DatabaseHelper.COLUMN_QUANTITY + " = " + 
                        DatabaseHelper.COLUMN_QUANTITY + " - ?" +
                        " WHERE " + DatabaseHelper.COLUMN_ID + " = ?",
                        new Object[]{product.getCartQuantity(), product.getId()});

                // Record sale with transaction ID
                ContentValues saleValues = new ContentValues();
                saleValues.put(DatabaseHelper.COLUMN_SALE_TRANSACTION_ID, transactionId);
                saleValues.put(DatabaseHelper.COLUMN_SALE_PROD_NAME, product.getName());
                saleValues.put(DatabaseHelper.COLUMN_SALE_QTY, product.getCartQuantity());
                saleValues.put(DatabaseHelper.COLUMN_SALE_TOTAL, itemSubtotal);
                saleValues.put(DatabaseHelper.COLUMN_SALE_DATE, today);
                saleValues.put(DatabaseHelper.COLUMN_SALE_CUSTOMER_NAME, customerName);
                saleValues.put(DatabaseHelper.COLUMN_SALE_CUSTOMER_PHONE, customerPhone);
                db.insert(DatabaseHelper.TABLE_SALES, null, saleValues);
            }

            db.setTransactionSuccessful();
            
            // Build success message
            StringBuilder message = new StringBuilder();
            message.append("Transaction ID: ").append(transactionId).append("\n\n");
            message.append("Customer: ").append(customerName).append("\n");
            message.append("Phone: ").append(customerPhone).append("\n\n");
            message.append("Items: ").append(totalItems).append("\n");
            message.append("Subtotal: $").append(String.format("%.2f", subtotal)).append("\n");
            
            if (discountPercent > 0) {
                message.append("\n🎉 Discount (").append(discountPercent).append("%): -$")
                       .append(String.format("%.2f", discountAmount)).append("\n");
            }
            
            if (hasCashback) {
                message.append("\n💰 Cashback Bonus: +$")
                       .append(String.format("%.2f", com.narith.aims.util.DiscountCalculator.CASHBACK_AMOUNT))
                       .append("\n(3rd purchase today!)").append("\n");
            }
            
            message.append("\nFinal Total: $").append(String.format("%.2f", finalTotal)).append("\n\n");
            message.append("Thank you for your purchase!");
            
            // Show success dialog
            new AlertDialog.Builder(this)
                .setTitle("✅ Purchase Complete!")
                .setMessage(message.toString())
                .setPositiveButton("OK", (dialog, which) -> {
                    CartManager.getInstance().clearCart();
                    updateCartBadge();
                    loadProducts(); // Refresh to show updated stock
                    
                    // Switch to history tab to show the new transaction
                    tabLayout.getTabAt(1).select();
                })
                .setCancelable(false)
                .show();

        } catch (Exception e) {
            Toast.makeText(this, "❌ Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private void loadTestTransactions() {
        testTransactionList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Load grouped transactions
        Cursor cursor = db.rawQuery(
            "SELECT " + DatabaseHelper.COLUMN_SALE_TRANSACTION_ID + ", " +
            DatabaseHelper.COLUMN_SALE_DATE + ", " +
            DatabaseHelper.COLUMN_SALE_CUSTOMER_NAME + ", " +
            DatabaseHelper.COLUMN_SALE_CUSTOMER_PHONE + ", " +
            "GROUP_CONCAT(" + DatabaseHelper.COLUMN_SALE_PROD_NAME + ", ', ') as products, " +
            "SUM(" + DatabaseHelper.COLUMN_SALE_QTY + ") as total_qty, " +
            "SUM(" + DatabaseHelper.COLUMN_SALE_TOTAL + ") as total_amount, " +
            "MIN(" + DatabaseHelper.COLUMN_SALE_ID + ") as first_id " +
            "FROM " + DatabaseHelper.TABLE_SALES + " " +
            "GROUP BY " + DatabaseHelper.COLUMN_SALE_TRANSACTION_ID + " " +
            "ORDER BY first_id DESC",
            null
        );

        if (cursor.moveToFirst()) {
            do {
                TestTransaction transaction = new TestTransaction(
                    cursor.getInt(7),        // first_id
                    cursor.getString(1),     // date
                    cursor.getString(4),     // products (concatenated)
                    cursor.getInt(5),        // total_qty
                    cursor.getDouble(6),     // total_amount
                    cursor.getString(2),     // customer name
                    cursor.getString(3)      // customer phone
                );
                testTransactionList.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();

        transactionAdapter = new TestTransactionAdapter(this, testTransactionList);
        transactionAdapter.setOnTransactionDeleteListener((transactionId, position) -> {
            confirmDeleteTransaction(transactionId, position);
        });
        rvTestTransactions.setAdapter(transactionAdapter);

        // Update info text
        if (testTransactionList.isEmpty()) {
            tvTestInfo.setText("📋 No sales history yet");
        } else {
            double totalRevenue = 0;
            int totalQty = 0;
            for (TestTransaction t : testTransactionList) {
                totalRevenue += t.getTotal();
                totalQty += t.getQuantity();
            }
            tvTestInfo.setText(String.format("📋 Sales: %d transactions | %d items | $%.2f revenue", 
                testTransactionList.size(), totalQty, totalRevenue));
        }
    }

    private void confirmDeleteTransaction(int transactionId, int position) {
        TestTransaction transaction = testTransactionList.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Delete Test Transaction?")
                .setMessage(
                    "Product: " + transaction.getProductName() + "\n" +
                    "Qty: " + transaction.getQuantity() + "\n" +
                    "Total: $" + String.format("%.2f", transaction.getTotal()) + "\n\n" +
                    "Note: Stock will NOT be restored."
                )
                .setPositiveButton("Delete", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    int deleted = db.delete(
                        DatabaseHelper.TABLE_SALES,
                        DatabaseHelper.COLUMN_SALE_ID + " = ?",
                        new String[]{String.valueOf(transactionId)}
                    );
                    
                    if (deleted > 0) {
                        testTransactionList.remove(position);
                        transactionAdapter.notifyItemRemoved(position);
                        Toast.makeText(this, "✅ Transaction deleted", Toast.LENGTH_SHORT).show();
                        loadTestTransactions(); // Refresh to update stats
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmClearTestData() {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Clear All Sales History")
                .setMessage("This will delete ALL sales transactions.\n\n" +
                           "Note: Stock quantities will NOT be restored.\n\n" +
                           "Continue?")
                .setPositiveButton("Yes, Clear All", (dialog, which) -> clearTestData())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearTestData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        int deleted = db.delete(DatabaseHelper.TABLE_SALES, null, null);

        Toast.makeText(this, 
            "🗑️ Deleted " + deleted + " sales transactions", 
            Toast.LENGTH_SHORT).show();
        
        loadTestTransactions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
        updateCartBadge();
    }

    // Inner class for test transaction data
    public static class TestTransaction {
        private int id;
        private String date;
        private String productName;
        private int quantity;
        private double total;
        private String customerName;
        private String customerPhone;

        public TestTransaction(int id, String date, String productName, 
                             int quantity, double total, String customerName, String customerPhone) {
            this.id = id;
            this.date = date;
            this.productName = productName;
            this.quantity = quantity;
            this.total = total;
            this.customerName = customerName;
            this.customerPhone = customerPhone;
        }

        public int getId() { return id; }
        public String getDate() { return date; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getTotal() { return total; }
        public String getCustomerName() { return customerName; }
        public String getCustomerPhone() { return customerPhone; }
    }
}

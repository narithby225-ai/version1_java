package com.narith.aims.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.narith.aims.R;
import com.narith.aims.database.DatabaseHelper;
import com.narith.aims.model.Product;
import com.narith.aims.utils.CartManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private DatabaseHelper dbHelper;
    private String userRole = "admin";

    private TextView tvTotalProducts, tvLowStock, tvTotalValue;
    private TextInputEditText etSearch;
    private MaterialButton btnManageCategory, btnViewReport;
    private LineChart revenueChart; // សម្រាប់បង្ហាញក្រាហ្វិកចំណូល

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ១. ទទួល Role ពី Login
        if (getIntent().hasExtra("USER_ROLE")) {
            userRole = getIntent().getStringExtra("USER_ROLE");
        }

        dbHelper = new DatabaseHelper(this);

        // ២. រៀបចំ UI និង Logic
        initializeViews();
        setupSearchFunction();

        // ៣. បង្ហាញទិន្នន័យ
        checkAndInsertDummyData();
        loadProducts();
    }

    private void initializeViews() {
        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvLowStock = findViewById(R.id.tvLowStock);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        etSearch = findViewById(R.id.etSearch);
        btnManageCategory = findViewById(R.id.btnManageCategory);
        btnViewReport = findViewById(R.id.btnViewReport);
        revenueChart = findViewById(R.id.revenueChart);

        recyclerView = findViewById(R.id.rvProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup user info display
        setupUserInfo();

        // កំណត់ប៊ូតុងបន្ថែមទំនិញ (FAB)
        FloatingActionButton fabAdd = findViewById(R.id.btnAddProduct);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, AddProductActivity.class)));
        }

        // កំណត់ការចុចលើប៊ូតុង Manage Categories
        if (btnManageCategory != null) {
            btnManageCategory.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, CategoryActivity.class)));
        }

        // កំណត់ការចុចលើប៊ូតុង View Reports (Premium)
        if (btnViewReport != null) {
            btnViewReport.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, ReportActivity.class)));
        }

        // កំណត់ការចុចលើប៊ូតុង Stock Management
        MaterialButton btnStockManagement = findViewById(R.id.btnStockManagement);
        if (btnStockManagement != null) {
            btnStockManagement.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, StockManagementActivity.class)));
        }

        // កំណត់ការចុចលើប៊ូតុង Test Customer (Admin only)
        MaterialButton btnTestCustomer = findViewById(R.id.btnTestCustomer);
        if (btnTestCustomer != null) {
            if ("admin".equals(userRole)) {
                btnTestCustomer.setVisibility(View.VISIBLE);
                btnTestCustomer.setOnClickListener(v ->
                        startActivity(new Intent(MainActivity.this, TestCustomerActivity.class)));
            } else {
                btnTestCustomer.setVisibility(View.GONE);
            }
        }

        // ចុចលើ Card Low Stock ដើម្បីមើលបញ្ជីឈ្មោះទំនិញ
        View cardLowStock = findViewById(R.id.cardLowStock);
        if (cardLowStock != null) {
            cardLowStock.setOnClickListener(v -> showLowStockDetailsDialog());
        }

        updateDashboard();
        setupRevenueChart();
    }
    
    private void setupUserInfo() {
        // Get username from intent
        String username = getIntent().getStringExtra("USERNAME");
        if (username == null) username = "User";
        
        // Display username
        TextView tvUsername = findViewById(R.id.tvUsername);
        if (tvUsername != null) {
            tvUsername.setText(username);
        }
        
        // Display user avatar (first letter)
        TextView tvUserAvatar = findViewById(R.id.tvUserAvatar);
        if (tvUserAvatar != null) {
            tvUserAvatar.setText(username.substring(0, 1).toUpperCase());
        }
        
        // Display role
        TextView tvUserRole = findViewById(R.id.tvUserRole);
        if (tvUserRole != null) {
            if ("admin".equals(userRole)) {
                tvUserRole.setText("Administrator");
                tvUserRole.setBackgroundResource(R.drawable.bg_gradient_red);
            } else {
                tvUserRole.setText("Sales User");
                tvUserRole.setBackgroundResource(R.drawable.bg_gradient_blue);
            }
        }
        
        // Setup logout button
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutConfirmation());
        }
    }
    
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes, Logout", (dialog, which) -> {
                // Clear any saved session data if needed
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                
                // Go back to login screen
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    /**
     * រៀបចំក្រាហ្វិកបង្ហាញចំណូល (Line Chart) ពី Database
     */
    private void setupRevenueChart() {
        if (revenueChart == null) return;

        List<Entry> entries = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query ទាញយកចំណូលសរុបបំបែកតាមថ្ងៃ (៧ ថ្ងៃចុងក្រោយ)
        String query = "SELECT " + DatabaseHelper.COLUMN_SALE_DATE + ", SUM(" + DatabaseHelper.COLUMN_SALE_TOTAL + ") " +
                "FROM " + DatabaseHelper.TABLE_SALES +
                " GROUP BY " + DatabaseHelper.COLUMN_SALE_DATE +
                " ORDER BY " + DatabaseHelper.COLUMN_SALE_DATE + " ASC LIMIT 7";

        Cursor cursor = db.rawQuery(query, null);
        int index = 0;
        if (cursor.moveToFirst()) {
            do {
                entries.add(new Entry(index++, (float) cursor.getDouble(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // ប្រសិនបើគ្មានទិន្នន័យ ដាក់ទិន្នន័យគំរូឱ្យមើលឃើញរាងក្រាហ្វស្អាត
        if (entries.isEmpty()) {
            entries.add(new Entry(0, 5f)); entries.add(new Entry(1, 15f));
            entries.add(new Entry(2, 10f)); entries.add(new Entry(3, 25f));
            entries.add(new Entry(4, 20f));
        }

        LineDataSet dataSet = new LineDataSet(entries, "ចំណូលប្រចាំថ្ងៃ ($)");
        dataSet.setColor(Color.parseColor("#6200EE")); // ពណ៌ស្វាយ PRO
        dataSet.setCircleColor(Color.parseColor("#6200EE"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#E1BEE7"));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // ធ្វើឱ្យខ្សែកោងទន់ស្អាត
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        revenueChart.setData(lineData);
        revenueChart.getDescription().setEnabled(false);
        revenueChart.getXAxis().setDrawGridLines(false);
        revenueChart.getAxisRight().setEnabled(false);
        revenueChart.animateY(1000); // Animation ពេលបើក App
        revenueChart.invalidate();
    }

    private void setupSearchFunction() {
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (adapter != null) adapter.getFilter().filter(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void updateDashboard() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // ១. ចំនួនទំនិញសរុប
        Cursor cursorTotal = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_PRODUCTS, null);
        if (cursorTotal.moveToFirst()) tvTotalProducts.setText(String.valueOf(cursorTotal.getInt(0)));
        cursorTotal.close();

        // ២. ទំនិញជិតអស់ស្តុក (<= 5)
        Cursor cursorLow = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_PRODUCTS +
                " WHERE " + DatabaseHelper.COLUMN_QUANTITY + " <= 5", null);
        if (cursorLow.moveToFirst()) tvLowStock.setText(String.valueOf(cursorLow.getInt(0)));
        cursorLow.close();

        // ៣. តម្លៃស្តុកសរុប (Quantity * Price)
        Cursor cursorValue = db.rawQuery("SELECT SUM(" + DatabaseHelper.COLUMN_QUANTITY + " * " +
                DatabaseHelper.COLUMN_PRICE + ") FROM " + DatabaseHelper.TABLE_PRODUCTS, null);
        if (cursorValue.moveToFirst()) tvTotalValue.setText("$ " + String.format("%.2f", cursorValue.getDouble(0)));
        cursorValue.close();
    }

    private void showLowStockDetailsDialog() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StringBuilder details = new StringBuilder();
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COLUMN_NAME + ", " +
                DatabaseHelper.COLUMN_QUANTITY + " FROM " + DatabaseHelper.TABLE_PRODUCTS +
                " WHERE " + DatabaseHelper.COLUMN_QUANTITY + " <= 5", null);

        if (cursor.moveToFirst()) {
            do {
                details.append("📍 ").append(cursor.getString(0))
                        .append(" : សល់ត្រឹម ").append(cursor.getInt(1)).append("\n\n");
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (details.length() == 0) {
            Toast.makeText(this, "ស្តុកទំនិញទាំងអស់នៅមានច្រើន!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("📦 បញ្ជីទំនិញជិតអស់ស្តុក")
                .setMessage(details.toString())
                .setPositiveButton("យល់ព្រម", null).show();
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

        adapter = new ProductAdapter(this, productList, userRole);
        adapter.setOnProductActionListener(new ProductAdapter.OnProductActionListener() {
            @Override
            public void onBuyClick(Product product) {
                if (product.getQuantity() > 0) {
                    CartManager.getInstance().addToCart(product);
                    Toast.makeText(MainActivity.this, "បានបន្ថែមទៅកន្ត្រក!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "ទំនិញនេះអស់ពីស្តុកហើយ!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onEditClick(Product product) {
                Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                intent.putExtra("PRODUCT_ID", product.getId());
                startActivity(intent);
            }
            @Override public void onDeleteClick(Product product) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("លុបទំនិញ")
                        .setMessage("តើអ្នកប្រាកដថាចង់លុបទំនិញនេះមែនទេ?")
                        .setPositiveButton("លុប", (dialog, which) -> deleteProduct(product.getId()))
                        .setNegativeButton("បោះបង់", null).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void deleteProduct(int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_PRODUCTS, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(productId)});
        loadProducts();
        updateDashboard();
        setupRevenueChart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
        updateDashboard();
        setupRevenueChart();
    }

    private void checkAndInsertDummyData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + DatabaseHelper.TABLE_PRODUCTS, null);
        cursor.moveToFirst();
        if (cursor.getInt(0) == 0) {
            SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
            String today = DatabaseHelper.getCurrentDate();
            
            // Insert sample products with premium images
            insertProduct(writableDb, "Nike Football", "SKU001", "Football", 4, 5, 25.0, 
                "drawable://sample_product_4", today, "2026-12-31");
            insertProduct(writableDb, "Adidas Jersey", "SKU002", "Jersey", 50, 5, 45.0, 
                "drawable://sample_product_2", today, "2028-12-31");
            insertProduct(writableDb, "Samsung Phone", "SKU003", "Electronics", 15, 3, 299.99, 
                "drawable://sample_product_1", today, "2027-06-30");
            insertProduct(writableDb, "Organic Snacks", "SKU004", "Food", 100, 20, 5.99, 
                "drawable://sample_product_3", today, "2026-03-15");
            insertProduct(writableDb, "Programming Book", "SKU005", "Books", 25, 5, 39.99, 
                "drawable://sample_product_5", today, "2030-12-31");
        }
        cursor.close();
    }

    private void insertProduct(SQLiteDatabase db, String name, String sku, String cat, int qty, int reorder, double price, String imageUri, String dateAdded, String expiry) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COLUMN_NAME, name);
        v.put(DatabaseHelper.COLUMN_SKU, sku);
        v.put(DatabaseHelper.COLUMN_CATEGORY, cat);
        v.put(DatabaseHelper.COLUMN_QUANTITY, qty);
        v.put(DatabaseHelper.COLUMN_REORDER_POINT, reorder);
        v.put(DatabaseHelper.COLUMN_PRICE, price);
        v.put(DatabaseHelper.COLUMN_IMAGE, imageUri);
        v.put(DatabaseHelper.COLUMN_DATE_ADDED, dateAdded);
        v.put(DatabaseHelper.COLUMN_EXPIRY_DATE, expiry);
        db.insert(DatabaseHelper.TABLE_PRODUCTS, null, v);
    }
}
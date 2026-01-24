package com.narith.aims.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.narith.aims.R;
import com.narith.aims.database.DatabaseHelper;
import com.narith.aims.model.Product;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkAndInsertDummyData();
        loadProducts();

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddProductActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {
        productList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS, null);

        if (cursor.moveToFirst()) {
            do {
                // កែសម្រួលត្រង់នេះ៖ អានរូបភាពជា String (cursor.getString)
                Product product = new Product(
                        cursor.getString(1), // Name
                        cursor.getString(2), // SKU
                        cursor.getString(3), // Category
                        cursor.getInt(4),    // Qty
                        cursor.getInt(5),    // Reorder
                        cursor.getDouble(6), // Price
                        cursor.getString(7)  // <--- កែពី getInt(7) មក getString(7)
                );
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new ProductAdapter(productList);

        adapter.setOnQuantityChangeListener(new ProductAdapter.OnQuantityChangeListener() {
            @Override
            public void onAddClick(Product product) {
                updateStockDirectly(product, 1);
            }

            @Override
            public void onMinusClick(Product product) {
                if (product.getQuantity() > 0) {
                    updateStockDirectly(product, -1);
                } else {
                    Toast.makeText(MainActivity.this, "Stock cannot be negative!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void updateStockDirectly(Product product, int changeAmount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + DatabaseHelper.TABLE_PRODUCTS +
                " SET " + DatabaseHelper.COLUMN_QUANTITY + " = " + DatabaseHelper.COLUMN_QUANTITY + " + ?" +
                " WHERE " + DatabaseHelper.COLUMN_SKU + " = ?", new Object[]{changeAmount, product.getSku()});

        loadProducts();
    }

    private void checkAndInsertDummyData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + DatabaseHelper.TABLE_PRODUCTS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) {
            SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
            // ដាក់ "" (String ទទេ) សម្រាប់រូបភាពព្រោះយើងមិនទាន់មានរូបពិត
            insertProduct(writableDb, "Nike Football", "SKU001", "Pro League", 15, 5, 25.00, "");
            insertProduct(writableDb, "Adidas Jersey", "SKU002", "Apparel", 50, 10, 45.99, "");
            insertProduct(writableDb, "Puma Cleats", "SKU003", "Footwear", 8, 2, 89.50, "");
        }
    }

    // កែ Function នេះអោយទទួល String imageUri ជំនួស int imageResId
    private void insertProduct(SQLiteDatabase db, String name, String sku, String cat, int qty, int reorder, double price, String imageUri) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_SKU, sku);
        values.put(DatabaseHelper.COLUMN_CATEGORY, cat);
        values.put(DatabaseHelper.COLUMN_QUANTITY, qty);
        values.put(DatabaseHelper.COLUMN_REORDER_POINT, reorder);
        values.put(DatabaseHelper.COLUMN_PRICE, price);
        values.put(DatabaseHelper.COLUMN_IMAGE, imageUri); // ដាក់ String ចូល

        db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values);
    }
}
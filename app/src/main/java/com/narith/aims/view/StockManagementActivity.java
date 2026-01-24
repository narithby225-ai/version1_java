package com.narith.aims.view;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.narith.aims.R;
import com.narith.aims.database.DatabaseHelper;
import com.narith.aims.model.Product;
import com.narith.aims.model.StockTransaction;
import java.util.ArrayList;
import java.util.List;

public class StockManagementActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Spinner spProduct;
    private TextInputEditText etQuantity, etUnitPrice, etNote;
    private RadioGroup rgTransactionType;
    private Button btnSubmit;
    private RecyclerView rvTransactions;
    private StockTransactionAdapter adapter;
    private List<StockTransaction> transactionList;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_management);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        loadProducts();
        loadTransactions();

        btnSubmit.setOnClickListener(v -> processStockTransaction());
    }

    private void initializeViews() {
        spProduct = findViewById(R.id.spProduct);
        etQuantity = findViewById(R.id.etQuantity);
        etUnitPrice = findViewById(R.id.etUnitPrice);
        etNote = findViewById(R.id.etNote);
        rgTransactionType = findViewById(R.id.rgTransactionType);
        btnSubmit = findViewById(R.id.btnSubmit);
        rvTransactions = findViewById(R.id.rvTransactions);
        
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadProducts() {
        productList = new ArrayList<>();
        List<String> productNames = new ArrayList<>();
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS, null);
        
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getInt(4), cursor.getInt(5),
                    cursor.getDouble(6), cursor.getString(7), cursor.getString(8),
                    cursor.getString(9)
                );
                productList.add(product);
                productNames.add(product.getName() + " (SKU: " + product.getSku() + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, productNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProduct.setAdapter(adapter);
    }

    private void loadTransactions() {
        transactionList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT t.*, p." + DatabaseHelper.COLUMN_NAME + 
                      " FROM " + DatabaseHelper.TABLE_STOCK_TRANSACTIONS + " t " +
                      " LEFT JOIN " + DatabaseHelper.TABLE_PRODUCTS + " p " +
                      " ON t." + DatabaseHelper.COLUMN_TRANS_PRODUCT_ID + " = p." + DatabaseHelper.COLUMN_ID +
                      " ORDER BY t." + DatabaseHelper.COLUMN_TRANS_DATE + " DESC LIMIT 50";
        
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                StockTransaction transaction = new StockTransaction(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(9),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getDouble(4),
                    cursor.getDouble(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8)
                );
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        adapter = new StockTransactionAdapter(this, transactionList);
        rvTransactions.setAdapter(adapter);
    }

    private void processStockTransaction() {
        if (productList.isEmpty()) {
            Toast.makeText(this, "សូមបន្ថែមទំនិញមុនសិន!", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPosition = spProduct.getSelectedItemPosition();
        Product selectedProduct = productList.get(selectedPosition);
        
        String qtyStr = etQuantity.getText().toString().trim();
        String priceStr = etUnitPrice.getText().toString().trim();
        String note = etNote.getText().toString().trim();
        
        if (qtyStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "សូមបំពេញចំនួន និងតម្លៃ!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            int quantity = Integer.parseInt(qtyStr);
            double unitPrice = Double.parseDouble(priceStr);
            double totalAmount = quantity * unitPrice;
            
            int selectedTypeId = rgTransactionType.getCheckedRadioButtonId();
            String transType = "";
            int qtyChange = 0;
            
            if (selectedTypeId == R.id.rbImport) {
                transType = "IMPORT";
                qtyChange = quantity;
            } else if (selectedTypeId == R.id.rbExport) {
                transType = "EXPORT";
                qtyChange = -quantity;
            } else if (selectedTypeId == R.id.rbPurchase) {
                transType = "PURCHASE";
                qtyChange = quantity;
            }
            
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            
            try {
                // បញ្ចូលប្រតិបត្តិការ
                ContentValues transValues = new ContentValues();
                transValues.put(DatabaseHelper.COLUMN_TRANS_PRODUCT_ID, selectedProduct.getId());
                transValues.put(DatabaseHelper.COLUMN_TRANS_TYPE, transType);
                transValues.put(DatabaseHelper.COLUMN_TRANS_QTY, quantity);
                transValues.put(DatabaseHelper.COLUMN_TRANS_PRICE, unitPrice);
                transValues.put(DatabaseHelper.COLUMN_TRANS_TOTAL, totalAmount);
                transValues.put(DatabaseHelper.COLUMN_TRANS_DATE, DatabaseHelper.getCurrentDateTime());
                transValues.put(DatabaseHelper.COLUMN_TRANS_NOTE, note);
                transValues.put(DatabaseHelper.COLUMN_TRANS_USER, "admin");
                
                db.insert(DatabaseHelper.TABLE_STOCK_TRANSACTIONS, null, transValues);
                
                // បច្ចុប្បន្នភាពចំនួនស្តុក
                db.execSQL("UPDATE " + DatabaseHelper.TABLE_PRODUCTS +
                          " SET " + DatabaseHelper.COLUMN_QUANTITY + " = " + 
                          DatabaseHelper.COLUMN_QUANTITY + " + ? " +
                          " WHERE " + DatabaseHelper.COLUMN_ID + " = ?",
                          new Object[]{qtyChange, selectedProduct.getId()});
                
                // បច្ចុប្បន្នភាពតម្លៃប្រសិនបើមានការផ្លាស់ប្តូរ
                if (transType.equals("PURCHASE") || transType.equals("IMPORT")) {
                    ContentValues priceUpdate = new ContentValues();
                    priceUpdate.put(DatabaseHelper.COLUMN_PRICE, unitPrice);
                    db.update(DatabaseHelper.TABLE_PRODUCTS, priceUpdate, 
                             DatabaseHelper.COLUMN_ID + "=?", 
                             new String[]{String.valueOf(selectedProduct.getId())});
                }
                
                db.setTransactionSuccessful();
                Toast.makeText(this, "ប្រតិបត្តិការជោគជ័យ!", Toast.LENGTH_SHORT).show();
                
                etQuantity.setText("");
                etUnitPrice.setText("");
                etNote.setText("");
                
                loadTransactions();
                
            } finally {
                db.endTransaction();
            }
            
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

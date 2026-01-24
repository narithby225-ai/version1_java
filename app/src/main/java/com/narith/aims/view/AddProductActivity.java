package com.narith.aims.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.narith.aims.R;
import com.narith.aims.database.DatabaseHelper;
import com.narith.aims.model.Product;
import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private TextInputEditText etName, etSKU, etPrice, etQty, etReorder, etExpiryDate;
    private Spinner spCategory;
    private Button btnSave;
    private ImageButton btnScan;
    private FrameLayout btnSelectImage;
    private ImageView ivProductPreview;
    private LinearLayout layoutPlaceholder;
    private DatabaseHelper dbHelper;
    private Uri selectedImageUri = null;
    private String existingImageUri = "";
    private String selectedSampleImage = ""; // For sample images

    private boolean isEditMode = false;
    private int productId = -1;

    // ១. Launcher សម្រាប់រើសរូបភាពពី Gallery
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    selectedSampleImage = ""; // Clear sample selection
                    ivProductPreview.setImageURI(uri);
                    ivProductPreview.setVisibility(View.VISIBLE);
                    if (layoutPlaceholder != null) {
                        layoutPlaceholder.setVisibility(View.GONE);
                    }
                }
            }
    );

    // ២. Launcher សម្រាប់ទទួលលទ្ធផលពី Scanner (ដោះស្រាយ Error ScannerActivity)
    private final ActivityResultLauncher<Intent> scanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String scannedSKU = result.getData().getStringExtra("SCAN_RESULT");
                    if(etSKU != null) etSKU.setText(scannedSKU);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        loadCategoriesToSpinner();

        // ពិនិត្យមើលថាតើ Activity នេះត្រូវបានហៅមកដើម្បី Edit ឬ Add ថ្មី
        if (getIntent().hasExtra("PRODUCT_ID")) {
            isEditMode = true;
            productId = getIntent().getIntExtra("PRODUCT_ID", -1);
            btnSave.setText("UPDATE PRODUCT");
            loadProductData(productId);
        }

        // កំណត់ការចុចលើប៊ូតុងរើសរូបភាព
        if(btnSelectImage != null) {
            btnSelectImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        }

        // កំណត់ការចុចលើប៊ូតុងស្កេន (ហៅ ScannerActivity)
        if(btnScan != null) {
            btnScan.setOnClickListener(v -> {
                Intent intent = new Intent(this, ScannerActivity.class);
                scanLauncher.launch(intent);
            });
        }

        btnSave.setOnClickListener(v -> saveProductToDatabase());
    }

    private void initializeViews() {
        etName = findViewById(R.id.etName);
        etSKU = findViewById(R.id.etSku);
        etPrice = findViewById(R.id.etPrice);
        etQty = findViewById(R.id.etQty);
        etReorder = findViewById(R.id.etReorder);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        spCategory = findViewById(R.id.spCategory);
        btnScan = findViewById(R.id.btn_scan);
        btnSave = findViewById(R.id.btnSave);
        btnSelectImage = findViewById(R.id.btn_select_image);
        ivProductPreview = findViewById(R.id.iv_product_preview);
        layoutPlaceholder = findViewById(R.id.layout_placeholder);
        
        setupSampleImageListeners();
    }

    private void setupSampleImageListeners() {
        // Sample image 1 - Electronics
        findViewById(R.id.sample_image_1).setOnClickListener(v -> {
            selectSampleImage("sample_product_1", R.drawable.sample_product_1);
        });
        
        // Sample image 2 - Clothing
        findViewById(R.id.sample_image_2).setOnClickListener(v -> {
            selectSampleImage("sample_product_2", R.drawable.sample_product_2);
        });
        
        // Sample image 3 - Food
        findViewById(R.id.sample_image_3).setOnClickListener(v -> {
            selectSampleImage("sample_product_3", R.drawable.sample_product_3);
        });
        
        // Sample image 4 - Sports
        findViewById(R.id.sample_image_4).setOnClickListener(v -> {
            selectSampleImage("sample_product_4", R.drawable.sample_product_4);
        });
        
        // Sample image 5 - Books
        findViewById(R.id.sample_image_5).setOnClickListener(v -> {
            selectSampleImage("sample_product_5", R.drawable.sample_product_5);
        });
    }

    private void selectSampleImage(String imageName, int drawableId) {
        selectedSampleImage = imageName;
        selectedImageUri = null; // Clear any selected URI
        ivProductPreview.setImageResource(drawableId);
        ivProductPreview.setVisibility(View.VISIBLE);
        if (layoutPlaceholder != null) {
            layoutPlaceholder.setVisibility(View.GONE);
        }
        Toast.makeText(this, "Sample image selected", Toast.LENGTH_SHORT).show();
    }

    private void loadCategoriesToSpinner() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.CAT_NAME + " FROM " + DatabaseHelper.TABLE_CATEGORIES, null);
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (categories.isEmpty()) { categories.add("General"); }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
    }

    private void loadProductData(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE " + DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            etName.setText(cursor.getString(1));
            etSKU.setText(cursor.getString(2));
            etQty.setText(String.valueOf(cursor.getInt(4)));
            etReorder.setText(String.valueOf(cursor.getInt(5)));
            etPrice.setText(String.valueOf(cursor.getDouble(6)));

            String currentCat = cursor.getString(3);
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spCategory.getAdapter();
            if (adapter != null) {
                int position = adapter.getPosition(currentCat);
                if (position >= 0) spCategory.setSelection(position);
            }

            existingImageUri = cursor.getString(7);
            if (existingImageUri != null && !existingImageUri.isEmpty()) {
                if (existingImageUri.startsWith("drawable://")) {
                    // Load sample image
                    String drawableName = existingImageUri.replace("drawable://", "");
                    int drawableId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
                    if (drawableId != 0) {
                        ivProductPreview.setImageResource(drawableId);
                        ivProductPreview.setVisibility(View.VISIBLE);
                        if (layoutPlaceholder != null) {
                            layoutPlaceholder.setVisibility(View.GONE);
                        }
                        selectedSampleImage = drawableName;
                    }
                } else {
                    // Load URI image
                    ivProductPreview.setImageURI(Uri.parse(existingImageUri));
                    ivProductPreview.setVisibility(View.VISIBLE);
                    if (layoutPlaceholder != null) {
                        layoutPlaceholder.setVisibility(View.GONE);
                    }
                }
            }
            
            // កំណត់កាលបរិច្ឆេទផុតកំណត់
            String expiryDate = cursor.getString(9);
            if (expiryDate != null && !expiryDate.isEmpty()) {
                etExpiryDate.setText(expiryDate);
            }
        }
        cursor.close();
    }

    private void saveProductToDatabase() {
        String name = etName.getText().toString().trim();
        String sku = etSKU.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String qtyStr = etQty.getText().toString().trim();
        String reorderStr = etReorder.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();

        if (name.isEmpty() || sku.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            Toast.makeText(this, "សូមបំពេញព័ត៌មានដែលចាំបាច់!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int qty = Integer.parseInt(qtyStr);
            int reorder = (reorderStr.isEmpty()) ? 5 : Integer.parseInt(reorderStr);
            String category = spCategory.getSelectedItem().toString();
            
            // កំណត់កាលបរិច្ឆេទផុតកំណត់លំនាំដើម
            if (expiryDate.isEmpty()) {
                expiryDate = "2030-12-31";
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, name);
            values.put(DatabaseHelper.COLUMN_SKU, sku);
            values.put(DatabaseHelper.COLUMN_CATEGORY, category);
            values.put(DatabaseHelper.COLUMN_QUANTITY, qty);
            values.put(DatabaseHelper.COLUMN_REORDER_POINT, reorder);
            values.put(DatabaseHelper.COLUMN_PRICE, price);
            values.put(DatabaseHelper.COLUMN_EXPIRY_DATE, expiryDate);

            if (selectedImageUri != null) {
                values.put(DatabaseHelper.COLUMN_IMAGE, selectedImageUri.toString());
            } else if (!selectedSampleImage.isEmpty()) {
                values.put(DatabaseHelper.COLUMN_IMAGE, "drawable://" + selectedSampleImage);
            } else if (isEditMode) {
                values.put(DatabaseHelper.COLUMN_IMAGE, existingImageUri);
            }

            if (isEditMode) {
                db.update(DatabaseHelper.TABLE_PRODUCTS, values, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(productId)});
                Toast.makeText(this, "បានកែប្រែទំនិញជោគជ័យ!", Toast.LENGTH_SHORT).show();
            } else {
                values.put(DatabaseHelper.COLUMN_DATE_ADDED, DatabaseHelper.getCurrentDate());
                long result = db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values);
                if (result != -1) Toast.makeText(this, "បានបន្ថែមទំនិញថ្មី!", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "Error: SKU នេះមានរួចហើយ!", Toast.LENGTH_SHORT).show();
            }
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "សូមពិនិត្យមើលតម្លៃដែលអ្នកបានបញ្ចូល!", Toast.LENGTH_SHORT).show();
        }
    }
}
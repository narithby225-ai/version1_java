package com.narith.aims.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout; // សម្រាប់ចុចលើប្រអប់រូបភាព
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.narith.aims.R;
import com.narith.aims.database.DatabaseHelper;

public class AddProductActivity extends AppCompatActivity {

    private TextInputEditText etName, etSKU, etCategory, etPrice, etQty, etReorder;
    private Button btnSave;
    private ImageButton btnScan;
    private FrameLayout btnSelectImage; // កន្លែងចុចរើសរូប
    private ImageView ivProductPreview; // កន្លែងបង្ហាញរូប
    private DatabaseHelper dbHelper;
    private Uri selectedImageUri = null; // រូបដែលបានរើស

    // 1. Launcher សម្រាប់រើសរូបពី Gallery
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivProductPreview.setImageURI(uri); // បង្ហាញរូបភ្លាមៗ
                    ivProductPreview.setVisibility(View.VISIBLE);
                }
            }
    );

    private final ActivityResultLauncher<Intent> scanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String scannedSKU = result.getData().getStringExtra("SCAN_RESULT");
                    etSKU.setText(scannedSKU);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.et_product_name);
        etSKU = findViewById(R.id.et_product_sku);
        etCategory = findViewById(R.id.et_product_category);
        etPrice = findViewById(R.id.et_product_price);
        etQty = findViewById(R.id.et_product_qty);
        etReorder = findViewById(R.id.et_product_reorder);

        btnScan = findViewById(R.id.btn_scan);
        btnSave = findViewById(R.id.btn_save_product);

        // ភ្ជាប់ View សម្រាប់រូបភាព (ត្រូវនឹង XML Premium)
        btnSelectImage = findViewById(R.id.btn_select_image);
        ivProductPreview = findViewById(R.id.iv_product_preview);

        // ចុចលើប្រអប់រូបភាព -> បើក Gallery
        btnSelectImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScannerActivity.class);
            scanLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> saveProductToDatabase());
    }

    private void saveProductToDatabase() {
        // ... (Validation ដូចមុន) ...
        if (etName.getText().toString().isEmpty()) return;

        try {
            String name = etName.getText().toString().trim();
            String sku = etSKU.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            double price = Double.parseDouble(etPrice.getText().toString());
            int qty = Integer.parseInt(etQty.getText().toString());
            int reorder = Integer.parseInt(etReorder.getText().toString());

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.COLUMN_NAME, name);
            values.put(DatabaseHelper.COLUMN_SKU, sku);
            values.put(DatabaseHelper.COLUMN_CATEGORY, category);
            values.put(DatabaseHelper.COLUMN_QUANTITY, qty);
            values.put(DatabaseHelper.COLUMN_REORDER_POINT, reorder);
            values.put(DatabaseHelper.COLUMN_PRICE, price);

            // រក្សាទុកទីតាំងរូបភាព (String)
            if (selectedImageUri != null) {
                values.put(DatabaseHelper.COLUMN_IMAGE, selectedImageUri.toString());
            } else {
                values.put(DatabaseHelper.COLUMN_IMAGE, ""); // គ្មានរូប
            }

            long newRowId = db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Check numbers!", Toast.LENGTH_SHORT).show();
        }
    }
}
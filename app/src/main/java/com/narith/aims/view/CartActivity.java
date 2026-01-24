package com.narith.aims.view;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.narith.aims.R;
import com.narith.aims.database.DatabaseHelper;
import com.narith.aims.model.Product;
import com.narith.aims.utils.CartManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCartItems;
    private CartAdapter adapter;
    private TextView tvCartTotal;
    private Button btnCheckout;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new DatabaseHelper(this); //

        // ភ្ជាប់ View ជាមួយ ID ក្នុង XML
        rvCartItems = findViewById(R.id.lvCartItems);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvCartItems.setLayoutManager(new LinearLayoutManager(this));

        setupCartList();

        btnCheckout.setOnClickListener(v -> processCheckout());
    }

    private void setupCartList() {
        List<Product> cartItems = CartManager.getInstance().getCartItems(); //

        // បង្កើត Adapter ដោយបញ្ជូន Callback ដើម្បី Update តម្លៃសរុបភ្លាមៗ (Real-time)
        adapter = new CartAdapter(this, cartItems, this::updateTotal);
        rvCartItems.setAdapter(adapter);

        updateTotal();
    }

    private void updateTotal() {
        double total = CartManager.getInstance().getTotalPrice(); //
        tvCartTotal.setText("$ " + String.format("%.2f", total));
    }

    private void processCheckout() {
        List<Product> cartItems = CartManager.getInstance().getCartItems(); //
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "កន្ត្រកទំនិញទទេ!", Toast.LENGTH_SHORT).show();
            return;
        }

        double finalTotal = CartManager.getInstance().getTotalPrice(); //
        SQLiteDatabase db = dbHelper.getWritableDatabase(); //
        db.beginTransaction(); //

        try {
            for (Product item : cartItems) {
                // ១. បន្ថែមទិន្នន័យចូលក្នុងតារាង Sales (របាយការណ៍លក់)
                ContentValues saleValues = new ContentValues();
                saleValues.put(DatabaseHelper.COLUMN_SALE_DATE, DatabaseHelper.getCurrentDate()); //
                saleValues.put(DatabaseHelper.COLUMN_SALE_PROD_NAME, item.getName()); //
                saleValues.put(DatabaseHelper.COLUMN_SALE_QTY, item.getCartQuantity()); //
                saleValues.put(DatabaseHelper.COLUMN_SALE_TOTAL, item.getPrice() * item.getCartQuantity()); //
                saleValues.put(DatabaseHelper.COLUMN_SALE_CUSTOMER_NAME, "Guest Customer"); //
                db.insert(DatabaseHelper.TABLE_SALES, null, saleValues); //

                // ២. កាត់ចំនួនចេញពីស្តុកទំនិញក្នុងតារាង Products
                db.execSQL("UPDATE " + DatabaseHelper.TABLE_PRODUCTS +
                        " SET " + DatabaseHelper.COLUMN_QUANTITY + " = " + DatabaseHelper.COLUMN_QUANTITY + " - " + item.getCartQuantity() +
                        " WHERE " + DatabaseHelper.COLUMN_ID + " = ?", new Object[]{item.getId()}); //
            }
            db.setTransactionSuccessful(); //

            // មុខងារ Premium: បង្កើតវិក្កយបត្រ PDF ភ្លាមៗ
            generateReceiptPdf(cartItems, finalTotal);

            Toast.makeText(this, "ការទូទាត់ប្រាក់ជោគជ័យ និងបានចេញវិក្កយបត្រ!", Toast.LENGTH_SHORT).show();

            CartManager.getInstance().clearCart(); //
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "កំហុសបច្ចេកទេស៖ " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction(); //
        }
    }

    private void generateReceiptPdf(List<Product> items, double total) {
        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setFakeBoldText(true);
        paint.setTextSize(14f);
        canvas.drawText("AIMS PREMIUM STORE", 80, 40, paint);

        paint.setFakeBoldText(false);
        paint.setTextSize(10f);
        canvas.drawText("កាលបរិច្ឆេទ: " + DatabaseHelper.getCurrentDate(), 20, 70, paint); //
        canvas.drawText("--------------------------------------------------", 20, 90, paint);

        int y = 110;
        for (Product p : items) {
            canvas.drawText(p.getName() + " x" + p.getCartQuantity(), 20, y, paint); //
            canvas.drawText("$" + String.format("%.2f", (p.getPrice() * p.getCartQuantity())), 220, y, paint); //
            y += 20;
        }

        canvas.drawText("--------------------------------------------------", 20, y, paint);
        paint.setFakeBoldText(true);
        canvas.drawText("សរុបរួម: $" + String.format("%.2f", total), 150, y + 30, paint); //

        document.finishPage(page);

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Receipt_" + System.currentTimeMillis() + ".pdf");
        try {
            document.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
    }
}
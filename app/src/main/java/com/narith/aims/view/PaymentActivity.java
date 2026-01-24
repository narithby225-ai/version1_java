package com.narith.aims.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.narith.aims.R;
import com.narith.aims.database.DatabaseHelper;
import com.narith.aims.model.Product;
import com.narith.aims.utils.CartManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * PAYMENT ACTIVITY - គ្រប់គ្រងការបង់ប្រាក់ និងកត់ត្រាទិន្នន័យអតិថិជនចូលក្នុង Form របាយការណ៍
 */
public class PaymentActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvOrderDetails, tvFinalAmount;
    private EditText etCustomerName, etCustomerPhone;
    private Button btnConfirm, btnCancel, btnShare, btnSavePdf;
    private String invoiceText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        dbHelper = new DatabaseHelper(this);

        // --- ១. ភ្ជាប់ UI Components ជាមួយ XML ---
        initializeViews();

        // --- ២. បង្កើតទិន្នន័យវិក្កយបត្របឋម ---
        generateInvoice();

        // --- ៣. កំណត់សកម្មភាពប៊ូតុង (Listeners) ---
        setupClickListeners();
    }

    private void initializeViews() {
        tvOrderDetails = findViewById(R.id.tvOrderDetails);
        tvFinalAmount = findViewById(R.id.tvFinalAmount);
        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerPhone = findViewById(R.id.etCustomerPhone);
        btnConfirm = findViewById(R.id.btnConfirmPayment);
        btnCancel = findViewById(R.id.btnCancel);
        btnShare = findViewById(R.id.btnShareInvoice);
        btnSavePdf = findViewById(R.id.btnSavePdf);
    }

    private void setupClickListeners() {
        btnShare.setOnClickListener(v -> shareInvoice());
        btnSavePdf.setOnClickListener(v -> createPdfInvoice());
        btnConfirm.setOnClickListener(v -> processFinalPayment());
        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * រៀបចំអត្ថបទសម្រាប់បង្ហាញក្នុង App និងសម្រាប់ Share/PDF
     */
    private void generateInvoice() {
        StringBuilder details = new StringBuilder();
        StringBuilder shareDetails = new StringBuilder();
        double total = 0;

        shareDetails.append("--- AIMS INVOICE ---\n\n");

        for (Product p : CartManager.getInstance().getCartItems()) {
            double subTotal = p.getPrice() * p.getCartQuantity();

            details.append(p.getName()).append(" x").append(p.getCartQuantity())
                    .append("\n$").append(String.format("%.2f", p.getPrice()))
                    .append(" each\t\tSub: $").append(String.format("%.2f", subTotal))
                    .append("\n\n");

            shareDetails.append(p.getName()).append(" x").append(p.getCartQuantity())
                    .append(" = $").append(String.format("%.2f", subTotal)).append("\n");

            total += subTotal;
        }

        invoiceText = shareDetails.append("\nTOTAL: $").append(String.format("%.2f", total))
                .append("\n\nThank you for your purchase!")
                .toString();

        tvOrderDetails.setText(details.toString());
        tvFinalAmount.setText(String.format("$ %.2f", total));
    }

    /**
     * បង្កើតឯកសារ PDF ដែលមានព័ត៌មានអតិថិជន
     */
    private void createPdfInvoice() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setTextSize(14f);
        paint.setFakeBoldText(true);
        canvas.drawText("AIMS INVOICE", 100, 40, paint);

        paint.setFakeBoldText(false);
        paint.setTextSize(10f);
        int y = 80;

        String custName = etCustomerName.getText().toString().trim();
        if(!custName.isEmpty()){
            canvas.drawText("Customer: " + custName, 20, y, paint);
            y += 20;
        }

        for (String line : invoiceText.split("\n")) {
            canvas.drawText(line, 20, y, paint);
            y += 20;
        }

        pdfDocument.finishPage(page);
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Invoice_" + System.currentTimeMillis() + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF Saved: " + file.getName(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
    }

    /**
     * ដំណើរការកាត់ស្តុក និងរក្សាទុកទិន្នន័យចូលក្នុង Table Sales (Form របាយការណ៍)
     */
    private void processFinalPayment() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String today = DatabaseHelper.getCurrentDate();

        String customerName = etCustomerName.getText().toString().trim();
        String customerPhone = etCustomerPhone.getText().toString().trim();

        if (customerName.isEmpty()) customerName = "General Customer";

        db.beginTransaction();
        try {
            for (Product product : CartManager.getInstance().getCartItems()) {
                // ១. បច្ចុប្បន្នភាពចំនួនស្តុក
                db.execSQL("UPDATE " + DatabaseHelper.TABLE_PRODUCTS +
                                " SET " + DatabaseHelper.COLUMN_QUANTITY + " = " + DatabaseHelper.COLUMN_QUANTITY + " - ?" +
                                " WHERE " + DatabaseHelper.COLUMN_ID + " = ?",
                        new Object[]{product.getCartQuantity(), product.getId()});

                // ២. បញ្ចូលទិន្នន័យទៅក្នុងតារាងលក់ (Sales Form)
                ContentValues saleValues = new ContentValues();
                saleValues.put(DatabaseHelper.COLUMN_SALE_PROD_NAME, product.getName());
                saleValues.put(DatabaseHelper.COLUMN_SALE_QTY, product.getCartQuantity());
                saleValues.put(DatabaseHelper.COLUMN_SALE_TOTAL, product.getPrice() * product.getCartQuantity());
                saleValues.put(DatabaseHelper.COLUMN_SALE_DATE, today);
                saleValues.put(DatabaseHelper.COLUMN_SALE_CUSTOMER_NAME, customerName);
                saleValues.put(DatabaseHelper.COLUMN_SALE_CUSTOMER_PHONE, customerPhone);

                db.insert(DatabaseHelper.TABLE_SALES, null, saleValues);
            }
            db.setTransactionSuccessful();

            CartManager.getInstance().clearCart();
            Toast.makeText(this, "Payment Success!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
        }
    }

    private void shareInvoice() {
        if (invoiceText.isEmpty()) return;
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, invoiceText);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share via"));
    }
}
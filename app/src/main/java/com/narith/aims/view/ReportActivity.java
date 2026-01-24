package com.narith.aims.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import com.narith.aims.R;
import com.narith.aims.database.DatabaseHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvTotalRevenue;
    private ListView lvSalesReport;
    private SearchView searchViewReport;
    private Button btnSaveReportPdf, btnSelectDate, btnMonthlySummary;

    private ReportAdapter adapter;
    private List<String> salesList;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        dbHelper = new DatabaseHelper(this);
        selectedDate = DatabaseHelper.getCurrentDate(); // យកកាលបរិច្ឆេទបច្ចុប្បន្នជាលំនាំដើម

        initializeViews();
        loadReportByDate(selectedDate);
        setupEventListeners();
    }

    private void initializeViews() {
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        lvSalesReport = findViewById(R.id.lvSalesReport);
        searchViewReport = findViewById(R.id.searchViewReport);
        btnSaveReportPdf = findViewById(R.id.btnSaveReportPdf);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnMonthlySummary = findViewById(R.id.btnMonthlySummary);
    }

    private void setupEventListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnMonthlySummary.setOnClickListener(v -> showMonthlySummary());

        // មុខងារស្វែងរកក្នុងរបាយការណ៍ (ស្វែងរកឈ្មោះអតិថិជន ឬទំនិញ)
        searchViewReport.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                if (adapter != null) adapter.getFilter().filter(newText);
                return true;
            }
        });

        // ចុចលើជួរនីមួយៗដើម្បីផ្ដល់ជម្រើសតេទៅកាន់អតិថិជន
        lvSalesReport.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            makeCallFromEntry(selectedItem);
        });

        btnSaveReportPdf.setOnClickListener(v -> createReportPdf());
    }

    private void loadReportByDate(String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        salesList = new ArrayList<>();
        double totalRevenue = 0;

        // SQL Query ប្រមូលផ្តុំទំនិញតាមអតិថិជនម្នាក់ៗក្នុងថ្ងៃតែមួយ
        String query = "SELECT " +
                DatabaseHelper.COLUMN_SALE_CUSTOMER_NAME + ", " +
                DatabaseHelper.COLUMN_SALE_CUSTOMER_PHONE + ", " +
                "GROUP_CONCAT('- ' || " + DatabaseHelper.COLUMN_SALE_PROD_NAME + " || ' x' || " + DatabaseHelper.COLUMN_SALE_QTY + " || ' : $' || " + DatabaseHelper.COLUMN_SALE_TOTAL + ", '\n') as items_list, " +
                "SUM(" + DatabaseHelper.COLUMN_SALE_TOTAL + ") as grand_total " +
                "FROM " + DatabaseHelper.TABLE_SALES +
                " WHERE " + DatabaseHelper.COLUMN_SALE_DATE + " = ?" +
                " GROUP BY " + DatabaseHelper.COLUMN_SALE_CUSTOMER_NAME + ", " + DatabaseHelper.COLUMN_SALE_CUSTOMER_PHONE;

        try {
            Cursor cursor = db.rawQuery(query, new String[]{date});
            if (cursor.moveToFirst()) {
                do {
                    String custName = cursor.getString(0);
                    String custPhone = cursor.getString(1);
                    String itemsList = cursor.getString(2);
                    double grandTotal = cursor.getDouble(3);

                    String entry = "👤 អតិថិជន: " + (custName != null && !custName.isEmpty() ? custName : "ភ្ញៀវទូទៅ") +
                            " (" + (custPhone != null && !custPhone.isEmpty() ? custPhone : "---") + ")\n" +
                            (itemsList != null ? itemsList : "") + "\n" +
                            "💰 ចំណាយសរុប: $" + String.format("%.2f", grandTotal);

                    salesList.add(entry);
                    totalRevenue += grandTotal;
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) { e.printStackTrace(); }

        tvTotalRevenue.setText(String.format("$ %.2f", totalRevenue));
        adapter = new ReportAdapter(this, salesList);
        lvSalesReport.setAdapter(adapter);
    }

    // Adapter សម្រាប់បង្ហាញទិន្នន័យក្នុង ListView ឱ្យមានសោភ័ណភាព
    private class ReportAdapter extends ArrayAdapter<String> {
        public ReportAdapter(Context context, List<String> data) {
            super(context, R.layout.list_item_report, data);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_report, parent, false);
            }
            String fullEntry = getItem(position);
            String[] parts = fullEntry.split("\n");

            TextView tvName = convertView.findViewById(R.id.tvReportCustName);
            TextView tvItems = convertView.findViewById(R.id.tvReportItems);
            TextView tvTotal = convertView.findViewById(R.id.tvReportGrandTotal);

            if (parts.length >= 1) tvName.setText(parts[0]);
            StringBuilder items = new StringBuilder();
            for (int i = 1; i < parts.length - 1; i++) { items.append(parts[i]).append("\n"); }
            tvItems.setText(items.toString().trim());
            if (parts.length > 1) tvTotal.setText(parts[parts.length - 1]);

            return convertView;
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, (month + 1), dayOfMonth);
            btnSelectDate.setText("ថ្ងៃទី: " + selectedDate);
            loadReportByDate(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showMonthlySummary() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String currentMonth = selectedDate.substring(0, 7);
        String query = "SELECT SUM(" + DatabaseHelper.COLUMN_SALE_TOTAL + "), SUM(" + DatabaseHelper.COLUMN_SALE_QTY + ") " +
                "FROM " + DatabaseHelper.TABLE_SALES + " WHERE " + DatabaseHelper.COLUMN_SALE_DATE + " LIKE ?";

        Cursor cursor = db.rawQuery(query, new String[]{currentMonth + "%"});
        if (cursor.moveToFirst()) {
            double monthlyTotal = cursor.getDouble(0);
            int totalQty = cursor.getInt(1);
            new AlertDialog.Builder(this)
                    .setTitle("សេចក្ដីសង្ខេបប្រចាំខែ (" + currentMonth + ")")
                    .setMessage("• ចំណូលសរុប: $" + String.format("%.2f", monthlyTotal) + "\n• ចំនួនទំនិញលក់ចេញ: " + totalQty + " មុខ")
                    .setPositiveButton("យល់ព្រម", null).show();
        }
        cursor.close();
    }

    private void makeCallFromEntry(String entry) {
        int start = entry.indexOf("(") + 1;
        int end = entry.indexOf(")");
        if (start > 0 && end > start) {
            String phoneNumber = entry.substring(start, end).trim();
            if (!phoneNumber.equals("---")) {
                new AlertDialog.Builder(this)
                        .setTitle("តេទៅអតិថិជន")
                        .setMessage("តើអ្នកចង់តេទៅកាន់លេខ " + phoneNumber + " មែនទេ?")
                        .setPositiveButton("តេ", (dialog, which) -> {
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
                        }).setNegativeButton("បោះបង់", null).show();
            }
        }
    }

    private void createReportPdf() {
        if (salesList.isEmpty()) {
            Toast.makeText(this, "មិនមានទិន្នន័យសម្រាប់ចេញរបាយការណ៍ទេ!", Toast.LENGTH_SHORT).show();
            return;
        }
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(400, 800, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setTextSize(18f); paint.setFakeBoldText(true);
        canvas.drawText("AIMS SALES REPORT", 100, 50, paint);
        paint.setTextSize(12f); paint.setFakeBoldText(false);
        canvas.drawText("Date: " + selectedDate, 20, 90, paint);
        canvas.drawText("Revenue: " + tvTotalRevenue.getText().toString(), 20, 110, paint);

        int y = 150;
        for (String item : salesList) {
            for (String line : item.split("\n")) {
                canvas.drawText(line, 20, y, paint);
                y += 20;
            }
            y += 10;
            if (y > 750) break; // ការពារកុំឱ្យលើសទំព័រ
        }

        pdfDocument.finishPage(page);
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Report_" + selectedDate + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "រក្សាទុក PDF រួចរាល់ក្នុង Documents!", Toast.LENGTH_LONG).show();
        } catch (IOException e) { e.printStackTrace(); }
        pdfDocument.close();
    }
}
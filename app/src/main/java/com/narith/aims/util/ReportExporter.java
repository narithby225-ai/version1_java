package com.narith.aims.util;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * REPORT EXPORTER UTILITY
 * Handles exporting data to CSV and PDF formats
 * Provides methods for inventory reports, sales reports, and transaction history
 */
public class ReportExporter {

    /**
     * Export inventory data to CSV format
     * @param context Application context
     * @param data List of data rows (each row is a String array)
     * @param headers Column headers
     * @param fileName Output file name
     * @return File path if successful, null otherwise
     */
    public static String exportToCSV(Context context, List<String[]> data, String[] headers, String fileName) {
        try {
            // Create AIMS directory in Downloads
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File aimsDir = new File(downloadsDir, "AIMS_Reports");
            if (!aimsDir.exists()) {
                aimsDir.mkdirs();
            }

            // Create file with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File file = new File(aimsDir, fileName + "_" + timestamp + ".csv");

            FileWriter writer = new FileWriter(file);

            // Write headers
            writer.append(String.join(",", headers));
            writer.append("\n");

            // Write data rows
            for (String[] row : data) {
                writer.append(String.join(",", row));
                writer.append("\n");
            }

            writer.flush();
            writer.close();

            return file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error exporting CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * Export inventory report
     */
    public static String exportInventoryReport(Context context, List<String[]> products) {
        String[] headers = {"ID", "Name", "SKU", "Category", "Quantity", "Price", "Status", "Date Added"};
        return exportToCSV(context, products, headers, "Inventory_Report");
    }

    /**
     * Export sales report
     */
    public static String exportSalesReport(Context context, List<String[]> sales) {
        String[] headers = {"Transaction ID", "Date", "Customer", "Phone", "Products", "Quantity", "Total", "Discount", "Final Amount"};
        return exportToCSV(context, sales, headers, "Sales_Report");
    }

    /**
     * Export low stock report
     */
    public static String exportLowStockReport(Context context, List<String[]> lowStockItems) {
        String[] headers = {"ID", "Name", "SKU", "Current Stock", "Reorder Point", "Status"};
        return exportToCSV(context, lowStockItems, headers, "Low_Stock_Report");
    }

    /**
     * Export transaction history
     */
    public static String exportTransactionHistory(Context context, List<String[]> transactions) {
        String[] headers = {"ID", "Date", "Type", "Product", "Quantity", "Price", "Total", "User", "Notes"};
        return exportToCSV(context, transactions, headers, "Transaction_History");
    }

    /**
     * Get formatted date string
     */
    public static String getFormattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    /**
     * Escape CSV special characters
     */
    public static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

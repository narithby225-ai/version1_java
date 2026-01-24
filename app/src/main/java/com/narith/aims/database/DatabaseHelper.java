package com.narith.aims.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Inventory.db";
    // លេខ 13 ជួយ Force Upgrade ដើម្បីបង្កើត Table ឱ្យត្រូវតាម Structure ថ្មី
    private static final int DATABASE_VERSION = 13;

    // --- តារាងទំនិញ (Products Table) ---
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SKU = "sku";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_REORDER_POINT = "reorder_point";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE = "image_res";
    public static final String COLUMN_DATE_ADDED = "date_added";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    // --- តារាងលក់ (Sales Table) ---
    public static final String TABLE_SALES = "sales";
    public static final String COLUMN_SALE_ID = "sale_id";
    public static final String COLUMN_SALE_TRANSACTION_ID = "transaction_id"; // Group multiple items
    public static final String COLUMN_SALE_DATE = "sale_date";
    public static final String COLUMN_SALE_PROD_NAME = "product_name";
    public static final String COLUMN_SALE_QTY = "quantity";
    public static final String COLUMN_SALE_TOTAL = "total_price";
    public static final String COLUMN_SALE_CUSTOMER_NAME = "customer_name";
    public static final String COLUMN_SALE_CUSTOMER_PHONE = "customer_phone";

    // --- តារាងប្រតិបត្តិការស្តុក (Stock Transactions Table) ---
    public static final String TABLE_STOCK_TRANSACTIONS = "stock_transactions";
    public static final String COLUMN_TRANS_ID = "trans_id";
    public static final String COLUMN_TRANS_PRODUCT_ID = "product_id";
    public static final String COLUMN_TRANS_TYPE = "trans_type"; // IMPORT, EXPORT, SALE, PURCHASE
    public static final String COLUMN_TRANS_QTY = "quantity";
    public static final String COLUMN_TRANS_PRICE = "unit_price";
    public static final String COLUMN_TRANS_TOTAL = "total_amount";
    public static final String COLUMN_TRANS_DATE = "trans_date";
    public static final String COLUMN_TRANS_NOTE = "note";
    public static final String COLUMN_TRANS_USER = "user_name";

    // --- តារាងប្រភេទ (Categories Table) ---
    public static final String TABLE_CATEGORIES = "categories";
    public static final String CAT_ID = "id";
    public static final String CAT_NAME = "name";

    // --- តារាងអ្នកប្រើប្រាស់ (Users Table) ---
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // បង្កើតតារាង Products
        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_SKU + " TEXT UNIQUE,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_QUANTITY + " INTEGER,"
                + COLUMN_REORDER_POINT + " INTEGER,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_IMAGE + " TEXT,"
                + COLUMN_DATE_ADDED + " TEXT,"
                + COLUMN_EXPIRY_DATE + " TEXT" + ")");

        // បង្កើតតារាង Sales
        db.execSQL("CREATE TABLE " + TABLE_SALES + "("
                + COLUMN_SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SALE_TRANSACTION_ID + " TEXT,"
                + COLUMN_SALE_DATE + " TEXT,"
                + COLUMN_SALE_PROD_NAME + " TEXT,"
                + COLUMN_SALE_QTY + " INTEGER,"
                + COLUMN_SALE_TOTAL + " REAL,"
                + COLUMN_SALE_CUSTOMER_NAME + " TEXT,"
                + COLUMN_SALE_CUSTOMER_PHONE + " TEXT" + ")");

        // បង្កើតតារាង Categories
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + "("
                + CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CAT_NAME + " TEXT UNIQUE" + ")");

        // បង្កើតតារាង Users
        db.execSQL("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ROLE + " TEXT" + ")");

        // បង្កើតតារាង Stock Transactions
        db.execSQL("CREATE TABLE " + TABLE_STOCK_TRANSACTIONS + "("
                + COLUMN_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TRANS_PRODUCT_ID + " INTEGER,"
                + COLUMN_TRANS_TYPE + " TEXT,"
                + COLUMN_TRANS_QTY + " INTEGER,"
                + COLUMN_TRANS_PRICE + " REAL,"
                + COLUMN_TRANS_TOTAL + " REAL,"
                + COLUMN_TRANS_DATE + " TEXT,"
                + COLUMN_TRANS_NOTE + " TEXT,"
                + COLUMN_TRANS_USER + " TEXT" + ")");

        // បញ្ចូលគណនីគំរូសម្រាប់សាកល្បង
        db.execSQL("INSERT INTO " + TABLE_USERS + " (username, password, role) VALUES ('admin', '1234', 'admin')");
        db.execSQL("INSERT INTO " + TABLE_USERS + " (username, password, role) VALUES ('user', '1234', 'user')");

        // បញ្ចូលប្រភេទដំបូងៗមួយចំនួន
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" + CAT_NAME + ") VALUES ('គ្រឿងអេឡិចត្រូនិក')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" + CAT_NAME + ") VALUES ('គ្រឿងបន្លាស់')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // លុបតារាងចាស់ៗចោលទាំងអស់
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK_TRANSACTIONS);
        // បង្កើតឡើងវិញថ្មីស្រឡាង
        onCreate(db);
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    /**
     * គណនាចំនួនថ្ងៃរវាងកាលបរិច្ឆេទពីរ
     */
    public static long getDaysDifference(String date1, String date2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            long diff = d2.getTime() - d1.getTime();
            return diff / (1000 * 60 * 60 * 24);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * ពិនិត្យស្ថានភាពទំនិញ (New, Expiring Soon, Old)
     */
    public static String getProductStatus(String dateAdded, String expiryDate) {
        String today = getCurrentDate();
        long daysFromAdded = getDaysDifference(dateAdded, today);
        long daysToExpiry = getDaysDifference(today, expiryDate);

        if (daysFromAdded <= 1) {
            return "NEW"; // ទំនិញថ្មីក្នុងរយៈពេល 24 ម៉ោង
        } else if (daysToExpiry <= 5 && daysToExpiry >= 0) {
            return "EXPIRING_SOON"; // ជិតផុតកំណត់ ≤ 5 ថ្ងៃ
        } else if (daysToExpiry < 0) {
            return "EXPIRED"; // ផុតកំណត់ហើយ
        } else if (daysFromAdded >= 6) {
            return "OLD"; // ទំនិញចាស់ ≥ 6 ថ្ងៃ
        }
        return "NORMAL";
    }
}
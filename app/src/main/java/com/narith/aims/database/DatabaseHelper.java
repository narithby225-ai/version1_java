package com.narith.aims.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Inventory.db";
    // សំខាន់៖ យើងដំឡើង Version ពី 1 ទៅ 2 ដើម្បីឱ្យវាលុប Table ចាស់ចោល
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SKU = "sku";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_REORDER_POINT = "reorder_point";

    // --- Column ថ្មីសម្រាប់ Sport Store ---
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE = "image_res";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_SKU + " TEXT UNIQUE,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_QUANTITY + " INTEGER,"
                + COLUMN_REORDER_POINT + " INTEGER,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_IMAGE + " TEXT"  // <--- កែត្រង់នេះ៖ ពី INTEGER ទៅ TEXT
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // លុប Table ចាស់ចោល រួចបង្កើតថ្មីដែលមាន Column គ្រប់គ្រាន់
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }
}
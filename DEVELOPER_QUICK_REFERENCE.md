# 👨‍💻 Developer Quick Reference Guide

## Quick Commands

### Build & Run
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Run app
adb shell am start -n com.narith.aims/.view.LoginActivity
```

### Database Operations
```bash
# Access database
adb shell
cd /data/data/com.narith.aims/databases/
sqlite3 Inventory.db

# View tables
.tables

# Query products
SELECT * FROM products;

# View transactions
SELECT * FROM stock_transactions ORDER BY trans_date DESC LIMIT 10;
```

## Code Snippets

### 1. Add New Product Programmatically
```java
SQLiteDatabase db = dbHelper.getWritableDatabase();
ContentValues values = new ContentValues();
values.put(DatabaseHelper.COLUMN_NAME, "Product Name");
values.put(DatabaseHelper.COLUMN_SKU, "SKU123");
values.put(DatabaseHelper.COLUMN_CATEGORY, "Category");
values.put(DatabaseHelper.COLUMN_QUANTITY, 100);
values.put(DatabaseHelper.COLUMN_REORDER_POINT, 10);
values.put(DatabaseHelper.COLUMN_PRICE, 29.99);
values.put(DatabaseHelper.COLUMN_DATE_ADDED, DatabaseHelper.getCurrentDate());
values.put(DatabaseHelper.COLUMN_EXPIRY_DATE, "2027-12-31");
long id = db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values);
```

### 2. Create Stock Transaction
```java
ContentValues transValues = new ContentValues();
transValues.put(DatabaseHelper.COLUMN_TRANS_PRODUCT_ID, productId);
transValues.put(DatabaseHelper.COLUMN_TRANS_TYPE, "IMPORT");
transValues.put(DatabaseHelper.COLUMN_TRANS_QTY, 50);
transValues.put(DatabaseHelper.COLUMN_TRANS_PRICE, 25.00);
transValues.put(DatabaseHelper.COLUMN_TRANS_TOTAL, 1250.00);
transValues.put(DatabaseHelper.COLUMN_TRANS_DATE, DatabaseHelper.getCurrentDateTime());
transValues.put(DatabaseHelper.COLUMN_TRANS_NOTE, "Supplier delivery");
transValues.put(DatabaseHelper.COLUMN_TRANS_USER, "admin");
db.insert(DatabaseHelper.TABLE_STOCK_TRANSACTIONS, null, transValues);
```

### 3. Update Product Quantity
```java
db.execSQL("UPDATE " + DatabaseHelper.TABLE_PRODUCTS +
          " SET " + DatabaseHelper.COLUMN_QUANTITY + " = " + 
          DatabaseHelper.COLUMN_QUANTITY + " + ? " +
          " WHERE " + DatabaseHelper.COLUMN_ID + " = ?",
          new Object[]{quantityChange, productId});
```

### 4. Get Product Status
```java
String status = DatabaseHelper.getProductStatus(
    product.getDateAdded(), 
    product.getExpiryDate()
);

// Or use Product methods
if (product.isNew()) {
    // Handle new product
} else if (product.isExpiringSoon()) {
    // Handle expiring product
}
```

### 5. Query Products by Status
```java
String query = "SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS + 
               " WHERE julianday('" + DatabaseHelper.getCurrentDate() + 
               "') - julianday(" + DatabaseHelper.COLUMN_DATE_ADDED + ") <= 1";
Cursor cursor = db.rawQuery(query, null);
```

### 6. Add to Cart
```java
CartManager.getInstance().addToCart(product);
```

### 7. Process Sale
```java
SQLiteDatabase db = dbHelper.getWritableDatabase();
db.beginTransaction();
try {
    for (Product product : CartManager.getInstance().getCartItems()) {
        // Update stock
        db.execSQL("UPDATE " + DatabaseHelper.TABLE_PRODUCTS +
                  " SET " + DatabaseHelper.COLUMN_QUANTITY + " = " + 
                  DatabaseHelper.COLUMN_QUANTITY + " - ?" +
                  " WHERE " + DatabaseHelper.COLUMN_ID + " = ?",
                  new Object[]{product.getCartQuantity(), product.getId()});
        
        // Record sale
        ContentValues saleValues = new ContentValues();
        saleValues.put(DatabaseHelper.COLUMN_SALE_PROD_NAME, product.getName());
        saleValues.put(DatabaseHelper.COLUMN_SALE_QTY, product.getCartQuantity());
        saleValues.put(DatabaseHelper.COLUMN_SALE_TOTAL, 
                      product.getPrice() * product.getCartQuantity());
        saleValues.put(DatabaseHelper.COLUMN_SALE_DATE, DatabaseHelper.getCurrentDate());
        saleValues.put(DatabaseHelper.COLUMN_SALE_CUSTOMER_NAME, customerName);
        saleValues.put(DatabaseHelper.COLUMN_SALE_CUSTOMER_PHONE, customerPhone);
        db.insert(DatabaseHelper.TABLE_SALES, null, saleValues);
    }
    db.setTransactionSuccessful();
} finally {
    db.endTransaction();
}
CartManager.getInstance().clearCart();
```

## Common Queries

### Get Low Stock Products
```sql
SELECT * FROM products 
WHERE quantity <= reorder_point 
ORDER BY quantity ASC;
```

### Get Expiring Products
```sql
SELECT * FROM products 
WHERE julianday(expiry_date) - julianday('now') <= 5 
  AND julianday(expiry_date) - julianday('now') >= 0
ORDER BY expiry_date ASC;
```

### Get New Products
```sql
SELECT * FROM products 
WHERE julianday('now') - julianday(date_added) <= 1
ORDER BY date_added DESC;
```

### Get Sales by Date Range
```sql
SELECT * FROM sales 
WHERE sale_date BETWEEN '2026-02-01' AND '2026-02-28'
ORDER BY sale_date DESC;
```

### Get Transaction History for Product
```sql
SELECT t.*, p.name 
FROM stock_transactions t
JOIN products p ON t.product_id = p.id
WHERE t.product_id = ?
ORDER BY t.trans_date DESC;
```

### Calculate Total Inventory Value
```sql
SELECT SUM(quantity * price) as total_value 
FROM products;
```

### Get Top Selling Products
```sql
SELECT product_name, SUM(quantity) as total_sold, SUM(total_price) as revenue
FROM sales
GROUP BY product_name
ORDER BY total_sold DESC
LIMIT 10;
```

## Database Schema Quick Reference

### products
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER | Primary key |
| name | TEXT | Product name |
| sku | TEXT | Unique SKU |
| category | TEXT | Category name |
| quantity | INTEGER | Stock quantity |
| reorder_point | INTEGER | Low stock alert |
| price | REAL | Unit price |
| image_res | TEXT | Image URI |
| date_added | TEXT | Entry date |
| expiry_date | TEXT | Expiry date |

### stock_transactions
| Column | Type | Description |
|--------|------|-------------|
| trans_id | INTEGER | Primary key |
| product_id | INTEGER | Product FK |
| trans_type | TEXT | IMPORT/EXPORT/PURCHASE |
| quantity | INTEGER | Transaction qty |
| unit_price | REAL | Price per unit |
| total_amount | REAL | Total value |
| trans_date | TEXT | Transaction datetime |
| note | TEXT | Optional note |
| user_name | TEXT | User who created |

### sales
| Column | Type | Description |
|--------|------|-------------|
| sale_id | INTEGER | Primary key |
| sale_date | TEXT | Sale date |
| product_name | TEXT | Product sold |
| quantity | INTEGER | Quantity sold |
| total_price | REAL | Total amount |
| customer_name | TEXT | Customer name |
| customer_phone | TEXT | Customer phone |

## Status Constants

```java
public static final String STATUS_NEW = "NEW";
public static final String STATUS_OLD = "OLD";
public static final String STATUS_EXPIRING_SOON = "EXPIRING_SOON";
public static final String STATUS_EXPIRED = "EXPIRED";
public static final String STATUS_NORMAL = "NORMAL";

public static final String TRANS_IMPORT = "IMPORT";
public static final String TRANS_EXPORT = "EXPORT";
public static final String TRANS_PURCHASE = "PURCHASE";
public static final String TRANS_SALE = "SALE";

public static final String ROLE_ADMIN = "admin";
public static final String ROLE_USER = "user";
```

## Intent Extras

```java
// Pass product ID for editing
intent.putExtra("PRODUCT_ID", productId);

// Pass user role
intent.putExtra("USER_ROLE", userRole);

// Get scan result
String scannedSKU = intent.getStringExtra("SCAN_RESULT");
```

## Common Patterns

### RecyclerView Setup
```java
recyclerView = findViewById(R.id.rvProducts);
recyclerView.setLayoutManager(new LinearLayoutManager(this));
adapter = new ProductAdapter(this, productList, userRole);
recyclerView.setAdapter(adapter);
```

### Spinner Setup
```java
ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
    android.R.layout.simple_spinner_item, items);
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
spinner.setAdapter(adapter);
```

### Image Loading with Glide
```java
Glide.with(context)
    .load(imageUri)
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .into(imageView);
```

### Date Formatting
```java
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
String dateString = sdf.format(new Date());
```

## Testing Utilities

### Reset Database
```java
context.deleteDatabase("Inventory.db");
DatabaseHelper dbHelper = new DatabaseHelper(context);
SQLiteDatabase db = dbHelper.getWritableDatabase();
```

### Insert Test Data
```java
private void insertTestProducts() {
    String[] products = {"Product A", "Product B", "Product C"};
    for (String name : products) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_SKU, "SKU" + System.currentTimeMillis());
        values.put(DatabaseHelper.COLUMN_CATEGORY, "Test");
        values.put(DatabaseHelper.COLUMN_QUANTITY, 50);
        values.put(DatabaseHelper.COLUMN_REORDER_POINT, 10);
        values.put(DatabaseHelper.COLUMN_PRICE, 25.00);
        values.put(DatabaseHelper.COLUMN_DATE_ADDED, DatabaseHelper.getCurrentDate());
        values.put(DatabaseHelper.COLUMN_EXPIRY_DATE, "2027-12-31");
        db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values);
    }
}
```

## Debugging Tips

### Enable SQL Logging
```java
SQLiteDatabase.enableWriteAheadLogging();
```

### Log Database Queries
```java
Cursor cursor = db.rawQuery(query, null);
Log.d("DB_QUERY", "Query: " + query);
Log.d("DB_RESULT", "Count: " + cursor.getCount());
```

### Check Cursor Data
```java
if (cursor.moveToFirst()) {
    do {
        Log.d("DB_ROW", DatabaseUtils.dumpCurrentRowToString(cursor));
    } while (cursor.moveToNext());
}
```

## Performance Tips

### Use Transactions for Bulk Operations
```java
db.beginTransaction();
try {
    // Multiple operations
    db.setTransactionSuccessful();
} finally {
    db.endTransaction();
}
```

### Close Cursors
```java
Cursor cursor = db.query(...);
try {
    // Use cursor
} finally {
    cursor.close();
}
```

### Use Prepared Statements
```java
SQLiteStatement stmt = db.compileStatement(
    "INSERT INTO products (name, sku) VALUES (?, ?)"
);
stmt.bindString(1, name);
stmt.bindString(2, sku);
stmt.executeInsert();
```

## Common Issues & Solutions

### Issue: Database locked
**Solution**: Ensure all cursors are closed and use transactions properly

### Issue: Images not loading
**Solution**: Check storage permissions and URI validity

### Issue: Status not updating
**Solution**: Verify date format is YYYY-MM-DD

### Issue: Cart not persisting
**Solution**: CartManager is in-memory only, implement persistence if needed

### Issue: Duplicate SKU error
**Solution**: SKU column has UNIQUE constraint, check before insert

## Useful ADB Commands

```bash
# View logs
adb logcat | grep "AIMS"

# Clear app data
adb shell pm clear com.narith.aims

# Take screenshot
adb shell screencap /sdcard/screen.png
adb pull /sdcard/screen.png

# Install APK
adb install -r app-debug.apk

# Uninstall app
adb uninstall com.narith.aims
```

## File Locations

```
app/src/main/java/com/narith/aims/
├── BaseApplication.java
├── MainActivity.java
├── adapter/
│   ├── ProductAdapter.java
│   └── TransactionAdapter.java
├── database/
│   └── DatabaseHelper.java
├── model/
│   ├── Product.java
│   ├── StockTransaction.java
│   ├── Category.java
│   ├── Transaction.java
│   └── User.java
├── util/
│   ├── CartManager.java
│   └── SimpleBarChart.java
└── view/
    ├── AddProductActivity.java
    ├── StockManagementActivity.java
    ├── CartActivity.java
    ├── PaymentActivity.java
    ├── CategoryActivity.java
    ├── ReportActivity.java
    ├── LoginActivity.java
    └── ScannerActivity.java
```

## Quick Checklist for New Features

- [ ] Create model class (if needed)
- [ ] Update DatabaseHelper (add columns/tables)
- [ ] Create/update activity
- [ ] Create/update adapter (if list view)
- [ ] Create XML layout
- [ ] Add to AndroidManifest.xml
- [ ] Update navigation/intents
- [ ] Add error handling
- [ ] Test thoroughly
- [ ] Update documentation

---

**Keep this reference handy for quick lookups during development!**

# AIMS Developer Guide

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Project Structure](#project-structure)
3. [Key Components](#key-components)
4. [Database Design](#database-design)
5. [Adding New Features](#adding-new-features)
6. [Testing Guidelines](#testing-guidelines)
7. [Code Style](#code-style)

---

## Architecture Overview

AIMS follows the **MVC (Model-View-Controller)** architectural pattern:

- **Model**: Data classes (`Product`, `Transaction`, `User`, `Category`)
- **View**: Activities and XML layouts
- **Controller**: Business logic in Activities and Utility classes

### Design Patterns Used
1. **Singleton**: `CartManager` for global cart state
2. **Adapter Pattern**: RecyclerView adapters for list displays
3. **Observer Pattern**: Database changes trigger UI updates
4. **Factory Pattern**: Product creation and initialization

---

## Project Structure

```
app/src/main/
├── java/com/narith/aims/
│   ├── model/                    # Data Models
│   │   ├── Product.java          # Product entity
│   │   ├── Transaction.java      # Transaction entity
│   │   ├── StockTransaction.java # Stock movement entity
│   │   ├── User.java             # User entity
│   │   └── Category.java         # Category entity
│   │
│   ├── view/                     # UI Layer
│   │   ├── SplashActivity.java   # Splash screen
│   │   ├── LoginActivity.java    # Authentication
│   │   ├── MainActivity.java     # Dashboard
│   │   ├── AddProductActivity.java
│   │   ├── CategoryActivity.java
│   │   ├── ReportActivity.java
│   │   ├── StockManagementActivity.java
│   │   ├── TestCustomerActivity.java  # Sales & Checkout
│   │   ├── ScannerActivity.java  # Barcode scanning
│   │   ├── PaymentActivity.java
│   │   └── adapters/
│   │       ├── ProductAdapter.java
│   │       ├── TransactionAdapter.java
│   │       ├── CartItemAdapter.java
│   │       └── TestTransactionAdapter.java
│   │
│   ├── database/                 # Data Layer
│   │   └── DatabaseHelper.java   # SQLite management
│   │
│   ├── util/                     # Utilities
│   │   ├── CartManager.java      # Shopping cart singleton
│   │   ├── DiscountCalculator.java  # Pricing logic
│   │   ├── ReportExporter.java   # CSV export
│   │   ├── ProductFilter.java    # Search/filter/sort
│   │   └── SimpleBarChart.java   # Chart rendering
│   │
│   └── BaseApplication.java      # Application class
│
└── res/
    ├── layout/                   # XML layouts
    ├── drawable/                 # Images & vectors
    ├── values/                   # Strings, colors, styles
    └── mipmap/                   # App icons
```

---

## Key Components

### 1. DatabaseHelper.java

**Purpose**: Manages SQLite database operations

**Key Methods**:
```java
// Database creation
public void onCreate(SQLiteDatabase db)

// Database upgrade
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)

// Utility methods
public static String getCurrentDate()
public static long getDaysDifference(String date1, String date2)
public static String getProductStatus(String dateAdded, String expiryDate)
```

**Database Version Management**:
- Current version: 13
- Increment version when schema changes
- `onUpgrade()` handles migration

### 2. Product.java (Model)

**Purpose**: Represents a product entity

**Properties**:
```java
private int id;
private String name, sku, category;
private int quantity, reorderPoint;
private double price;
private String imageUri, dateAdded, expiryDate;
private int cartQuantity;  // For shopping cart
```

**Key Methods**:
```java
public String getStatus()           // Returns: NEW, OLD, EXPIRING_SOON, EXPIRED
public boolean isNew()              // Product added within 24 hours
public boolean isExpiringSoon()     // Expires within 5 days
public boolean isExpired()          // Past expiry date
public long getDaysUntilExpiry()    // Days remaining
```

### 3. MainActivity.java

**Purpose**: Main dashboard and navigation hub

**Key Features**:
- Real-time statistics (total products, low stock, inventory value)
- Revenue chart (MPAndroidChart)
- Product list with search
- Navigation to all major features

**Important Methods**:
```java
private void updateDashboard()      // Refresh statistics
private void loadProducts()         // Load product list
private void setupRevenueChart()    // Initialize chart
private void setupUserInfo()        // Display user profile
private void showLogoutConfirmation()  // Handle logout
```

### 4. TestCustomerActivity.java

**Purpose**: Sales and checkout interface

**Key Features**:
- Product browsing
- Cart management
- Discount calculation
- Cashback rewards
- Transaction recording

**Important Methods**:
```java
private void addToTestCart(Product product)
private void viewTestCart()
private void updateTotalInDialog(...)  // Calculate totals with discounts
private void completeTestSale(String name, String phone)
private int getTodayPurchaseCount(String phone)  // For cashback
```

### 5. DiscountCalculator.java

**Purpose**: Handles pricing logic

**Discount Tiers**:
```java
public static int getDiscountPercentage(double amount) {
    if (amount >= 1000) return 50;
    if (amount >= 400) return 35;
    if (amount >= 350) return 30;
    // ... more tiers
    if (amount >= 50) return 5;
    return 0;
}
```

**Cashback Logic**:
```java
public static final double CASHBACK_AMOUNT = 100.0;
public static final int CASHBACK_THRESHOLD = 3;  // 3rd purchase
```

### 6. CartManager.java (Singleton)

**Purpose**: Global shopping cart state

**Pattern**: Singleton ensures one cart instance across app

```java
public class CartManager {
    private static CartManager instance;
    private List<Product> cartItems = new ArrayList<>();
    
    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }
    
    public void addToCart(Product product) { ... }
    public void clearCart() { ... }
    public List<Product> getCartItems() { ... }
}
```

---

## Database Design

### Schema Diagram

```sql
-- Products Table
CREATE TABLE products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    sku TEXT UNIQUE,
    category TEXT,
    quantity INTEGER,
    reorder_point INTEGER,
    price REAL,
    image_res TEXT,
    date_added TEXT,
    expiry_date TEXT
);

-- Sales Table (with transaction grouping)
CREATE TABLE sales (
    sale_id INTEGER PRIMARY KEY AUTOINCREMENT,
    transaction_id TEXT,           -- Groups multiple items
    sale_date TEXT,
    product_name TEXT,
    quantity INTEGER,
    total_price REAL,
    customer_name TEXT,
    customer_phone TEXT
);

-- Stock Transactions Table
CREATE TABLE stock_transactions (
    trans_id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER,
    trans_type TEXT,               -- IMPORT, EXPORT, PURCHASE, SALE
    quantity INTEGER,
    unit_price REAL,
    total_amount REAL,
    trans_date TEXT,
    note TEXT,
    user_name TEXT
);

-- Users Table
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE,
    password TEXT,
    role TEXT                      -- admin, user
);

-- Categories Table
CREATE TABLE categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE
);
```

### Relationships

- **Products ↔ Stock Transactions**: One-to-Many (product_id foreign key)
- **Sales**: Transaction ID groups multiple items from same purchase
- **Users**: Role-based access control

### Indexes (Recommended for Production)

```sql
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_sales_transaction ON sales(transaction_id);
CREATE INDEX idx_sales_phone ON sales(customer_phone);
CREATE INDEX idx_sales_date ON sales(sale_date);
```

---

## Adding New Features

### Example 1: Add a New Report Type

**Step 1**: Create database query
```java
// In DatabaseHelper.java
public Cursor getProductsByCategory(String category) {
    SQLiteDatabase db = this.getReadableDatabase();
    return db.rawQuery(
        "SELECT * FROM products WHERE category = ? ORDER BY name",
        new String[]{category}
    );
}
```

**Step 2**: Add export method
```java
// In ReportExporter.java
public static String exportCategoryReport(Context context, 
                                         List<String[]> data, 
                                         String category) {
    String[] headers = {"ID", "Name", "SKU", "Quantity", "Price"};
    return exportToCSV(context, data, headers, 
                      "Category_Report_" + category);
}
```

**Step 3**: Add UI button
```xml
<!-- In activity_report.xml -->
<Button
    android:id="@+id/btnCategoryReport"
    android:text="Export by Category"
    android:onClick="exportCategoryReport"/>
```

**Step 4**: Implement click handler
```java
// In ReportActivity.java
public void exportCategoryReport(View view) {
    // Get data from database
    // Format as String[][]
    // Call ReportExporter.exportCategoryReport()
    // Show success message
}
```

### Example 2: Add Product Image Upload

**Step 1**: Add permission
```xml
<!-- In AndroidManifest.xml -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

**Step 2**: Create image picker
```java
// In AddProductActivity.java
private ActivityResultLauncher<String> pickImageLauncher = 
    registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                ivProductPreview.setImageURI(uri);
            }
        }
    );

// Trigger picker
btnSelectImage.setOnClickListener(v -> 
    pickImageLauncher.launch("image/*")
);
```

**Step 3**: Save URI to database
```java
ContentValues values = new ContentValues();
values.put(DatabaseHelper.COLUMN_IMAGE, selectedImageUri.toString());
db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values);
```

---

## Testing Guidelines

### Manual Testing Checklist

#### Authentication
- [ ] Login with admin account
- [ ] Login with user account
- [ ] Invalid credentials show error
- [ ] Logout clears session

#### Product Management
- [ ] Add new product with all fields
- [ ] Add product with minimal fields
- [ ] Edit existing product
- [ ] Delete product
- [ ] Duplicate SKU shows error
- [ ] Empty required fields show validation

#### Stock Management
- [ ] Import stock increases quantity
- [ ] Export stock decreases quantity
- [ ] Purchase updates price
- [ ] Negative quantity prevented
- [ ] Transaction history recorded

#### Sales & Checkout
- [ ] Add items to cart
- [ ] Adjust quantities in cart
- [ ] Remove items from cart
- [ ] Discount applies correctly
- [ ] Cashback triggers on 3rd purchase
- [ ] Stock updates after sale
- [ ] Transaction groups items correctly

#### Reports
- [ ] Export inventory report
- [ ] Export sales report
- [ ] Export low stock report
- [ ] CSV file created successfully
- [ ] Data accuracy verified

#### Search & Filter
- [ ] Search by product name
- [ ] Search by SKU
- [ ] Filter by category
- [ ] Filter by status
- [ ] Sort by name, price, quantity
- [ ] Combined filters work

### Unit Testing (Recommended)

```java
// Example: Test discount calculation
@Test
public void testDiscountCalculation() {
    assertEquals(5, DiscountCalculator.getDiscountPercentage(75));
    assertEquals(15, DiscountCalculator.getDiscountPercentage(125));
    assertEquals(50, DiscountCalculator.getDiscountPercentage(1500));
}

// Example: Test product status
@Test
public void testProductStatus() {
    Product product = new Product(...);
    product.setDateAdded("2026-02-26");
    product.setExpiryDate("2026-03-01");
    assertEquals("EXPIRING_SOON", product.getStatus());
}
```

---

## Code Style

### Naming Conventions

**Classes**: PascalCase
```java
public class ProductAdapter { }
public class DatabaseHelper { }
```

**Methods**: camelCase
```java
public void loadProducts() { }
private void updateDashboard() { }
```

**Variables**: camelCase
```java
private int productCount;
private String userName;
```

**Constants**: UPPER_SNAKE_CASE
```java
public static final int DATABASE_VERSION = 13;
public static final String TABLE_PRODUCTS = "products";
```

### Comments

**Class-level documentation**:
```java
/**
 * PRODUCT ADAPTER
 * Displays product list in RecyclerView
 * Features: Search, Filter, Role-based UI
 */
public class ProductAdapter { }
```

**Method documentation**:
```java
/**
 * Calculate discount based on purchase amount
 * @param amount Purchase subtotal
 * @return Discount percentage (0-50)
 */
public static int getDiscountPercentage(double amount) { }
```

**Inline comments**:
```java
// Update stock quantity
db.execSQL("UPDATE products SET quantity = quantity - ?", 
          new Object[]{qty});

// Apply discount if eligible
if (subtotal >= 50) {
    discount = calculateDiscount(subtotal);
}
```

### Error Handling

**Always use try-catch for database operations**:
```java
db.beginTransaction();
try {
    // Database operations
    db.setTransactionSuccessful();
} catch (Exception e) {
    Log.e("TAG", "Error: " + e.getMessage());
    Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
} finally {
    db.endTransaction();
}
```

**Validate user input**:
```java
if (name.isEmpty()) {
    etName.setError("Required");
    return;
}

if (quantity < 0) {
    Toast.makeText(this, "Quantity cannot be negative", 
                  Toast.LENGTH_SHORT).show();
    return;
}
```

### Resource Management

**Always close cursors**:
```java
Cursor cursor = db.rawQuery("SELECT * FROM products", null);
try {
    if (cursor.moveToFirst()) {
        // Process data
    }
} finally {
    cursor.close();
}
```

---

## Performance Optimization

### Database Queries

**Use parameterized queries**:
```java
// Good
db.rawQuery("SELECT * FROM products WHERE id = ?", new String[]{id});

// Bad (SQL injection risk)
db.rawQuery("SELECT * FROM products WHERE id = " + id, null);
```

**Limit results when possible**:
```java
db.rawQuery("SELECT * FROM sales ORDER BY date DESC LIMIT 100", null);
```

**Use indexes for frequently queried columns**:
```sql
CREATE INDEX idx_products_category ON products(category);
```

### Memory Management

**Recycle bitmaps**:
```java
if (bitmap != null && !bitmap.isRecycled()) {
    bitmap.recycle();
}
```

**Use ViewHolder pattern in adapters**:
```java
static class ViewHolder extends RecyclerView.ViewHolder {
    TextView tvName, tvPrice;
    ImageView ivProduct;
    
    ViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        tvPrice = itemView.findViewById(R.id.tvPrice);
        ivProduct = itemView.findViewById(R.id.ivProduct);
    }
}
```

---

## Debugging Tips

### Enable Logging

```java
private static final String TAG = "MainActivity";

Log.d(TAG, "Loading products...");
Log.e(TAG, "Error: " + e.getMessage());
Log.i(TAG, "User logged in: " + username);
```

### Database Inspection

Use Android Studio's Database Inspector:
1. Run app on emulator/device
2. View → Tool Windows → App Inspection
3. Select "Database Inspector"
4. View tables and execute queries

### Common Issues

**Issue**: App crashes on startup
- Check AndroidManifest.xml for missing activities
- Verify all required permissions
- Check database version conflicts

**Issue**: RecyclerView not updating
- Call `adapter.notifyDataSetChanged()`
- Ensure data list is updated before notify
- Check if adapter is properly set

**Issue**: Images not loading
- Verify image URI is valid
- Check storage permissions
- Use Glide error handling

---

## Deployment Checklist

- [ ] Remove debug logs
- [ ] Update version code and name
- [ ] Test on multiple devices
- [ ] Test on different Android versions
- [ ] Verify all permissions
- [ ] Test with ProGuard enabled
- [ ] Generate signed APK
- [ ] Test signed APK
- [ ] Prepare store listing
- [ ] Create screenshots

---

## Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Material Design Guidelines](https://material.io/design)
- [SQLite Documentation](https://www.sqlite.org/docs.html)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- [ZXing Barcode](https://github.com/zxing/zxing)

---

**Last Updated**: February 2026  
**Maintainer**: Narith  
**Version**: 1.0.0

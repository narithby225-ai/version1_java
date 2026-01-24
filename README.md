# AIMS - Advanced Inventory Management System

## 📱 Project Overview

AIMS is a comprehensive Android inventory management application designed for retail businesses. It provides real-time stock tracking, sales management, customer loyalty programs, and detailed reporting capabilities.

### Key Features
- ✅ Role-based authentication (Admin/Staff)
- ✅ Real-time dashboard with analytics
- ✅ Complete CRUD operations for products
- ✅ Stock-In/Stock-Out transaction management
- ✅ Barcode scanning integration
- ✅ Low-stock alerts and reorder notifications
- ✅ Tiered discount system (5%-50% off)
- ✅ Customer cashback rewards ($100 for 3rd daily purchase)
- ✅ Sales history with transaction grouping
- ✅ Export reports (CSV format)
- ✅ Advanced search, filter, and sort
- ✅ Material Design UI with animations
- ✅ Product status tracking (NEW, EXPIRING, EXPIRED, OLD)

---

## 🚀 Installation Guide

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 21+ (Lollipop)
- Java JDK 8 or later
- Gradle 7.0+

### Setup Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/AIMS.git
   cd AIMS
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the AIMS folder
   - Click "OK"

3. **Sync Gradle**
   - Wait for Gradle sync to complete
   - If prompted, accept any SDK updates

4. **Build the Project**
   ```bash
   ./gradlew build
   ```

5. **Run on Device/Emulator**
   - Connect Android device or start emulator
   - Click "Run" button or press Shift+F10
   - Select target device

### Demo Accounts
- **Admin**: username: `admin`, password: `1234`
- **Staff**: username: `user`, password: `1234`

---

## 📚 User Manual

### 1. Login
- Launch the app
- Enter username and password
- Click "Sign In"
- Admin users have full access; Staff users have limited permissions

### 2. Dashboard
- View total products, low stock alerts, and total inventory value
- See revenue chart
- Access quick actions (Add Product, Categories, Reports, Stock Management)

### 3. Product Management

#### Add Product
1. Click the "+" floating action button
2. Fill in product details:
   - Name, SKU, Category
   - Quantity, Reorder Point, Price
   - Expiry Date
   - Product Image (camera or sample images)
3. Click "SAVE"

#### Edit Product
1. Click "Edit" button on product card
2. Modify details
3. Click "UPDATE PRODUCT"

#### Delete Product
1. Click "Delete" button on product card
2. Confirm deletion

### 4. Stock Management
1. Click "Stock Management" button
2. Select product
3. Choose transaction type:
   - **Import**: Add stock
   - **Export**: Remove stock
   - **Purchase**: Buy from supplier (updates price)
4. Enter quantity and notes
5. Click "RECORD TRANSACTION"

### 5. Sales & Checkout
1. Click "Sales & Checkout" button
2. Browse products and click "+" to add to cart
3. Click "CART" button when ready
4. Review cart items (adjust quantities with +/- buttons)
5. Enter customer name and phone number
6. View automatic discounts and cashback
7. Click "Pay Now"

### 6. Discount System
- **$50-$99**: 5% off
- **$100-$149**: 15% off
- **$150-$199**: 20% off
- **$200-$299**: 25% off
- **$300-$349**: 25% off
- **$350-$399**: 30% off
- **$400-$999**: 35% off
- **$1000+**: 50% off
- **Cashback**: $100 for 3rd purchase of the day (same phone number)

### 7. Reports
1. Click "View Reports" button
2. Select report type:
   - Sales Report
   - Inventory Report
   - Low Stock Report
3. Click "Export CSV" to save report

### 8. Search & Filter
1. Use search bar to find products by name, SKU, or category
2. Apply filters:
   - Category filter
   - Status filter (Low Stock, New, Expiring, etc.)
3. Sort by:
   - Name, Price, Quantity, Date, Status

---

## 👨‍💻 Developer Guide

### Architecture

The app follows **MVC (Model-View-Controller)** architecture:

```
app/
├── src/main/java/com/narith/aims/
│   ├── model/          # Data models (Product, Transaction, User, etc.)
│   ├── view/           # Activities and Adapters
│   ├── database/       # DatabaseHelper (SQLite)
│   ├── util/           # Utility classes (CartManager, DiscountCalculator, etc.)
│   └── BaseApplication.java
├── res/
│   ├── layout/         # XML layouts
│   ├── drawable/       # Images and vector graphics
│   └── values/         # Colors, strings, styles
└── AndroidManifest.xml
```

### Key Classes

#### 1. DatabaseHelper.java
- Manages SQLite database
- Handles schema creation and upgrades
- Provides utility methods for date calculations and product status

```java
// Database version
private static final int DATABASE_VERSION = 13;

// Tables: products, sales, categories, users, stock_transactions
```

#### 2. Product.java (Model)
```java
public class Product {
    private int id;
    private String name, sku, category;
    private int quantity, reorderPoint;
    private double price;
    private String imageUri, dateAdded, expiryDate;
    private int cartQuantity;
    
    // Methods: getStatus(), isNew(), isExpiringSoon(), etc.
}
```

#### 3. MainActivity.java
- Main dashboard
- Displays statistics and charts
- Handles navigation to other activities

#### 4. TestCustomerActivity.java
- Sales and checkout interface
- Cart management
- Discount and cashback calculation
- Transaction recording

#### 5. DiscountCalculator.java
- Calculates tiered discounts
- Provides discount descriptions
- Handles cashback logic

#### 6. CartManager.java (Singleton)
```java
public class CartManager {
    private static CartManager instance;
    private List<Product> cartItems;
    
    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }
}
```

### Database Schema

#### Products Table
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER | Primary key |
| name | TEXT | Product name |
| sku | TEXT | Unique SKU code |
| category | TEXT | Product category |
| quantity | INTEGER | Current stock |
| reorder_point | INTEGER | Low stock threshold |
| price | REAL | Unit price |
| image_res | TEXT | Image URI |
| date_added | TEXT | Date added (YYYY-MM-DD) |
| expiry_date | TEXT | Expiration date |

#### Sales Table
| Column | Type | Description |
|--------|------|-------------|
| sale_id | INTEGER | Primary key |
| transaction_id | TEXT | Groups multiple items |
| sale_date | TEXT | Transaction date |
| product_name | TEXT | Product sold |
| quantity | INTEGER | Quantity sold |
| total_price | REAL | Item subtotal |
| customer_name | TEXT | Customer name |
| customer_phone | TEXT | Customer phone |

#### Stock Transactions Table
| Column | Type | Description |
|--------|------|-------------|
| trans_id | INTEGER | Primary key |
| product_id | INTEGER | Foreign key to products |
| trans_type | TEXT | IMPORT/EXPORT/PURCHASE/SALE |
| quantity | INTEGER | Transaction quantity |
| unit_price | REAL | Price per unit |
| total_amount | REAL | Total transaction value |
| trans_date | TEXT | Transaction date |
| note | TEXT | Transaction notes |
| user_name | TEXT | User who performed action |

### Adding New Features

#### Example: Add a new report type

1. **Create data query in DatabaseHelper**
```java
public Cursor getCustomReport() {
    SQLiteDatabase db = this.getReadableDatabase();
    return db.rawQuery("SELECT * FROM products WHERE ...", null);
}
```

2. **Add export method in ReportExporter**
```java
public static String exportCustomReport(Context context, List<String[]> data) {
    String[] headers = {"Column1", "Column2", ...};
    return exportToCSV(context, data, headers, "Custom_Report");
}
```

3. **Add UI button in ReportActivity**
```xml
<Button
    android:id="@+id/btnCustomReport"
    android:text="Export Custom Report"
    android:onClick="exportCustomReport"/>
```

### Error Handling

The app implements comprehensive error handling:

- **Database errors**: Try-catch blocks with user-friendly messages
- **Input validation**: Checks for empty fields, invalid formats
- **Stock validation**: Prevents overselling
- **Transaction safety**: Uses SQLite transactions with rollback

### Testing

#### Manual Testing Checklist
- [ ] Login with admin and user accounts
- [ ] Add, edit, delete products
- [ ] Perform stock-in/stock-out transactions
- [ ] Complete sales with discounts
- [ ] Test cashback (3rd purchase)
- [ ] Export reports
- [ ] Search and filter products
- [ ] Test low-stock alerts
- [ ] Scan barcodes
- [ ] Test on different screen sizes

---

## 📊 Database Schema Diagram

```
┌─────────────────┐
│    PRODUCTS     │
├─────────────────┤
│ id (PK)         │
│ name            │
│ sku (UNIQUE)    │
│ category        │
│ quantity        │
│ reorder_point   │
│ price           │
│ image_res       │
│ date_added      │
│ expiry_date     │
└─────────────────┘
         │
         │ 1:N
         ▼
┌─────────────────┐
│STOCK_TRANSACTIONS│
├─────────────────┤
│ trans_id (PK)   │
│ product_id (FK) │
│ trans_type      │
│ quantity        │
│ unit_price      │
│ total_amount    │
│ trans_date      │
│ note            │
│ user_name       │
└─────────────────┘

┌─────────────────┐
│     SALES       │
├─────────────────┤
│ sale_id (PK)    │
│ transaction_id  │◄─── Groups items
│ sale_date       │
│ product_name    │
│ quantity        │
│ total_price     │
│ customer_name   │
│ customer_phone  │
└─────────────────┘

┌─────────────────┐
│     USERS       │
├─────────────────┤
│ id (PK)         │
│ username (UQ)   │
│ password        │
│ role            │
└─────────────────┘

┌─────────────────┐
│   CATEGORIES    │
├─────────────────┤
│ id (PK)         │
│ name (UNIQUE)   │
└─────────────────┘
```

---

## 🎨 UI/UX Design

### Design Principles
- **Material Design 3**: Modern, clean interface
- **Consistent Colors**: Purple primary, orange accents
- **Responsive Layout**: Works on phones and tablets
- **Clear Feedback**: Loading states, success/error messages
- **Intuitive Navigation**: Bottom navigation, FAB for quick actions

### Color Palette
- Primary: `#6200EE` (Purple)
- Secondary: `#FF9800` (Orange)
- Success: `#4CAF50` (Green)
- Error: `#E53935` (Red)
- Background: `#F5F5F5` (Light Gray)

### Typography
- Headers: Bold, 24-28sp
- Body: Regular, 14-16sp
- Captions: 12sp

---

## 📦 Dependencies

```gradle
dependencies {
    // AndroidX
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.0'
    implementation 'androidx.cardview:cardview:1.0.0'
    
    // Material Design
    implementation 'com.google.android.material:material:1.9.0'
    
    // Image Loading
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    implementation 'de.hdodenhof:circleimageview:3.1.0'
    
    // Charts
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
    
    // Barcode Scanning
    implementation 'com.google.zxing:core:3.5.1'
    implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
}
```

---

## 🔒 Security Considerations

- Passwords stored in plain text (for demo purposes only)
- **Production recommendation**: Use bcrypt or similar hashing
- Input validation on all user inputs
- SQL injection prevention through parameterized queries
- Role-based access control

---

## 🐛 Known Issues & Limitations

1. **Offline Only**: No cloud sync (can be added with Firebase)
2. **Basic Authentication**: No password recovery
3. **CSV Export Only**: PDF export not yet implemented
4. **Single Currency**: USD only
5. **No Multi-language**: English/Khmer only

---

## 🚧 Future Enhancements

- [ ] Firebase integration for cloud sync
- [ ] PDF report generation
- [ ] Multi-currency support
- [ ] Email reports
- [ ] Push notifications for low stock
- [ ] Supplier management
- [ ] Purchase orders
- [ ] Multi-location support
- [ ] Advanced analytics dashboard
- [ ] Backup/restore functionality

---

## 📄 License

This project is developed for educational purposes.
© 2026 Developed by Narith

---

## 📞 Support

For issues or questions:
- Email: support@aims.com
- GitHub Issues: [Create an issue](https://github.com/yourusername/AIMS/issues)

---

## 🙏 Acknowledgments

- Material Design by Google
- MPAndroidChart by PhilJay
- ZXing barcode library
- Glide image loading library

---

**Version**: 1.0.0  
**Last Updated**: February 2026  
**Minimum Android Version**: 5.0 (Lollipop)  
**Target Android Version**: 13 (Tiramisu)

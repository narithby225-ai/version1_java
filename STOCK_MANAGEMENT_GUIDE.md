# 📦 Complete Stock Management System Guide

## Overview
This Android application is a comprehensive stock management system designed to handle all operational arrangements for managing goods, including import/export, buying/selling, pricing, product categorization, and expiry tracking.

## 🎯 Key Features

### 1. Product Management
- **Add/Edit/Delete Products**: Complete CRUD operations for product inventory
- **Product Information**:
  - Name, SKU (barcode), Category
  - Price, Quantity, Reorder Point
  - Image upload support
  - Stock entry date (automatically tracked)
  - Expiry date

### 2. Stock Status Tracking
The system automatically categorizes products based on their age and expiry:

#### Product Status Categories:
- **🆕 NEW**: Products entered within the last 24 hours
- **📦 OLD**: Products that have been in stock for 6 days or more
- **⚠️ EXPIRING SOON**: Products with 5 days or less until expiry
- **❌ EXPIRED**: Products past their expiry date

### 3. Import/Export Management
Access via the "Stock Management" button on the main screen.

#### Transaction Types:
- **IMPORT**: Add stock from suppliers (increases inventory)
- **EXPORT**: Remove stock for distribution (decreases inventory)
- **PURCHASE**: Buy new stock (increases inventory + updates price)
- **SALE**: Sell to customers (handled through cart/payment flow)

#### Transaction Features:
- Select product from dropdown
- Enter quantity and unit price
- Add optional notes
- Automatic stock quantity updates
- Complete transaction history
- Price updates on purchase/import

### 4. Sales & Payment System
- **Cart Management**: Add products to cart with quantity controls
- **Customer Information**: Capture customer name and phone
- **Invoice Generation**: 
  - View invoice on screen
  - Share via messaging apps
  - Export to PDF
- **Payment Processing**: Test payment system (not real transactions)
- **Sales Recording**: All sales automatically recorded in database

### 5. Category Management
- Create and manage product categories
- Assign products to categories
- Filter products by category

### 6. Reporting & Analytics
- **Dashboard Metrics**:
  - Total products count
  - Low stock alerts (≤5 items)
  - Total inventory value
- **Revenue Chart**: 7-day revenue trend visualization
- **Transaction History**: Complete audit trail of all stock movements
- **Sales Reports**: Detailed sales data with customer information

### 7. User Roles
- **Admin**: Full access (add/edit/delete products, manage stock)
- **User**: Limited access (view products, make sales)

## 🗄️ Database Structure

### Tables:

#### 1. products
- id, name, sku, category
- quantity, reorder_point, price
- image_res, date_added, expiry_date

#### 2. stock_transactions
- trans_id, product_id, trans_type
- quantity, unit_price, total_amount
- trans_date, note, user_name

#### 3. sales
- sale_id, sale_date, product_name
- quantity, total_price
- customer_name, customer_phone

#### 4. categories
- id, name

#### 5. users
- id, username, password, role

## 🚀 How to Use

### Adding a New Product:
1. Click the "+" FAB button on main screen
2. Fill in product details:
   - Name, SKU, Category
   - Price, Quantity, Reorder Point
   - Expiry Date (format: YYYY-MM-DD)
3. Optionally add product image
4. Click "SAVE PRODUCT"

### Managing Stock (Import/Export):
1. Click "Stock Management" button
2. Select product from dropdown
3. Choose transaction type (Import/Export/Purchase)
4. Enter quantity and unit price
5. Add optional note
6. Click "Submit Transaction"

### Making a Sale:
1. Browse products on main screen
2. Click "Buy" button to add to cart
3. Navigate to cart (cart icon)
4. Review items and quantities
5. Proceed to payment
6. Enter customer information
7. Confirm payment

### Viewing Reports:
1. Click "View Reports" button
2. View sales history
3. Check revenue trends
4. Export or share reports

### Managing Categories:
1. Click "Manage Categories" button
2. Add new categories
3. Edit or delete existing categories

## 📊 Product Status Logic

### Status Calculation:
```
NEW: dateAdded within last 24 hours
EXPIRING_SOON: expiryDate - today ≤ 5 days
EXPIRED: expiryDate < today
OLD: dateAdded ≥ 6 days ago
NORMAL: Everything else
```

### Visual Indicators:
- NEW products show green badge with "🆕 NEW"
- EXPIRING SOON shows orange badge with days remaining
- EXPIRED shows grey badge with "❌ EXPIRED"
- OLD shows grey badge with "📦 OLD"

## 🔐 Test Accounts

### Admin Account:
- Username: `admin`
- Password: `1234`
- Access: Full system access

### User Account:
- Username: `user`
- Password: `1234`
- Access: Sales and viewing only

## 📱 Main Activities

1. **LoginActivity**: User authentication
2. **MainActivity**: Dashboard and product list
3. **AddProductActivity**: Add/edit products
4. **StockManagementActivity**: Import/export operations
5. **CartActivity**: Shopping cart
6. **PaymentActivity**: Payment processing
7. **CategoryActivity**: Category management
8. **ReportActivity**: Sales reports and analytics
9. **ScannerActivity**: Barcode scanning

## 🎨 UI Features

- Material Design components
- Card-based layouts
- Color-coded status indicators
- Search functionality
- Filterable product lists
- Revenue charts (MPAndroidChart)
- Image loading (Glide)

## 💾 Data Persistence

- SQLite database for local storage
- Automatic database version management
- Transaction support for data integrity
- Backup-friendly structure

## 🔄 Stock Flow

### Import Flow:
1. Supplier delivers goods
2. Admin creates IMPORT transaction
3. Stock quantity increases
4. Price can be updated
5. Transaction recorded

### Export Flow:
1. Goods shipped to customer/branch
2. Admin creates EXPORT transaction
3. Stock quantity decreases
4. Transaction recorded

### Sale Flow:
1. Customer selects products
2. Items added to cart
3. Payment processed
4. Stock automatically reduced
5. Sale recorded with customer info

## 📈 Business Intelligence

### Low Stock Alerts:
- Automatic detection when quantity ≤ reorder point
- Dashboard card shows count
- Click card to see detailed list

### Expiry Management:
- Visual warnings for expiring products
- Helps prevent waste
- Enables proactive discounting

### Revenue Tracking:
- Daily revenue aggregation
- 7-day trend visualization
- Helps identify sales patterns

## 🛠️ Technical Stack

- **Language**: Java
- **Database**: SQLite
- **UI**: Material Design Components
- **Charts**: MPAndroidChart
- **Images**: Glide
- **Architecture**: Activity-based with RecyclerView adapters

## 📝 Notes

- All dates use format: YYYY-MM-DD
- Prices in USD ($)
- Stock quantities are integers
- Transaction history limited to 50 recent entries
- Images stored as URI strings

## 🔮 Future Enhancements

Potential additions:
- Barcode scanning integration
- Multi-currency support
- Cloud backup
- Advanced analytics
- Supplier management
- Purchase orders
- Inventory forecasting
- Multi-location support

---

**Version**: 1.0  
**Database Version**: 12  
**Last Updated**: February 2026

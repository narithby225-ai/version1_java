# 🛒 Buying & Selling Goods - Complete Test Guide

## Overview
This guide walks you through testing the complete buying and selling workflow in the AIMS Stock Management System.

---

## 📋 Test Scenarios

### Scenario 1: Complete Sales Flow (Selling Goods)
### Scenario 2: Stock Purchase Flow (Buying Goods)
### Scenario 3: Import/Export Operations
### Scenario 4: Edge Cases & Validation

---

## 🎯 Scenario 1: SELLING GOODS (Customer Purchase)

### Prerequisites
- App is running
- Logged in as admin or user
- At least one product exists with quantity > 0

### Step-by-Step Test

#### Step 1: Login
```
1. Launch app
2. Enter username: admin
3. Enter password: 1234
4. Click LOGIN
✅ Expected: Navigate to Main Dashboard
```

#### Step 2: View Available Products
```
1. Scroll down to "Inventory List"
2. Observe product cards
✅ Expected: See products with:
   - Product name
   - Category
   - Price
   - Stock quantity
   - Status badge (NEW/OLD/EXPIRING)
   - BUY button (if user role)
   - EDIT/DELETE buttons (if admin role)
```

#### Step 3: Add Products to Cart
```
1. Find a product with stock > 0
2. Click the "BUY" button (shopping cart icon)
✅ Expected: Toast message "បានបន្ថែមទៅកន្ត្រក!" (Added to cart!)

3. Click BUY on 2-3 more products
✅ Expected: Each shows success toast
```

#### Step 4: View Cart
```
1. Look for cart icon in toolbar (top right)
2. Click the cart icon
✅ Expected: Navigate to Cart Activity

3. Observe cart contents
✅ Expected: See all added products with:
   - Product image
   - Product name
   - Unit price
   - Quantity controls (+ / -)
   - Subtotal per item
   - Total amount at bottom
```

#### Step 5: Adjust Quantities
```
1. Click "+" button on a product
✅ Expected: Quantity increases, subtotal updates

2. Click "-" button on a product
✅ Expected: Quantity decreases, subtotal updates

3. Try to decrease below 1
✅ Expected: Product removed from cart OR quantity stays at 1

4. Verify total amount updates correctly
✅ Expected: Total = Sum of all (price × quantity)
```

#### Step 6: Proceed to Payment
```
1. Click "PROCEED TO PAYMENT" button
✅ Expected: Navigate to Payment Activity

2. Observe payment screen
✅ Expected: See:
   - Order details (all products with quantities)
   - Final amount
   - Customer name field
   - Customer phone field
   - CONFIRM PAYMENT button
   - CANCEL button
   - SHARE INVOICE button
   - SAVE PDF button
```

#### Step 7: Enter Customer Information
```
1. Enter customer name: "John Doe"
2. Enter customer phone: "012345678"
✅ Expected: Fields accept input
```

#### Step 8: Test Invoice Features (Optional)
```
1. Click "SHARE INVOICE" button
✅ Expected: Share dialog appears with invoice text

2. Click "SAVE PDF" button
✅ Expected: 
   - PDF created
   - Toast: "PDF Saved: Invoice_[timestamp].pdf"
   - File saved in Documents folder
```

#### Step 9: Complete Payment
```
1. Click "CONFIRM PAYMENT" button
✅ Expected:
   - Toast: "Payment Success!"
   - Navigate back to Main Dashboard
   - Cart is cleared
```

#### Step 10: Verify Stock Reduction
```
1. On Main Dashboard, find the products you sold
2. Check their stock quantities
✅ Expected: Stock reduced by sold quantities

Example:
- Before: Nike Football - Stock: 10
- Sold: 2 units
- After: Nike Football - Stock: 8
```

#### Step 11: Verify Sale Record
```
1. Click "Reports" button
2. View sales history
✅ Expected: See your sale with:
   - Sale date (today)
   - Product names
   - Quantities sold
   - Total amount
   - Customer name: "John Doe"
   - Customer phone: "012345678"
```

---

## 💰 Scenario 2: BUYING GOODS (Stock Purchase)

### Prerequisites
- Logged in as admin
- Products exist in system

### Step-by-Step Test

#### Step 1: Access Stock Management
```
1. From Main Dashboard
2. Click "Stock Mgmt" button
✅ Expected: Navigate to Stock Management Activity
```

#### Step 2: Select Product to Purchase
```
1. Click on "Product" dropdown
2. Select a product (e.g., "Nike Football (SKU: SKU001)")
✅ Expected: Product selected in dropdown
```

#### Step 3: Choose Transaction Type
```
1. Select "Purchase" radio button
✅ Expected: Purchase option selected
```

#### Step 4: Enter Purchase Details
```
1. Enter Quantity: 50
2. Enter Unit Price ($): 20.00
3. Enter Note: "Supplier: ABC Sports, Invoice #12345"
✅ Expected: All fields accept input
```

#### Step 5: Submit Purchase
```
1. Click "Submit Transaction" button
✅ Expected:
   - Toast: "ប្រតិបត្តិការជោគជ័យ!" (Transaction successful!)
   - Form fields cleared
   - Transaction appears in "Recent Transactions" list
```

#### Step 6: Verify Transaction Record
```
1. Scroll down to "Recent Transactions"
2. Find your transaction at the top
✅ Expected: See transaction with:
   - Product name: "Nike Football"
   - Type: "PURCHASE" (in green)
   - Quantity: "Qty: 50"
   - Amount: "$1000.00"
   - Date: Current date/time
   - Note: "Supplier: ABC Sports, Invoice #12345"
```

#### Step 7: Verify Stock Increase
```
1. Press back to return to Main Dashboard
2. Find the product you purchased
3. Check stock quantity
✅ Expected: Stock increased by 50 units

Example:
- Before: Nike Football - Stock: 8
- Purchased: 50 units
- After: Nike Football - Stock: 58
```

#### Step 8: Verify Price Update
```
1. Check product price
✅ Expected: Price updated to $20.00 (the purchase price)
```

---

## 📦 Scenario 3: IMPORT/EXPORT OPERATIONS

### Test 3A: IMPORT Goods

#### Step 1: Access Stock Management
```
1. Click "Stock Mgmt" button
✅ Expected: Navigate to Stock Management
```

#### Step 2: Create Import Transaction
```
1. Select product: "Adidas Jersey"
2. Select "Import" radio button
3. Enter Quantity: 100
4. Enter Unit Price: 35.00
5. Enter Note: "Shipment from warehouse A"
6. Click "Submit Transaction"
✅ Expected:
   - Success toast
   - Stock increases by 100
   - Transaction recorded
```

#### Step 3: Verify Import
```
1. Check Recent Transactions
✅ Expected: See IMPORT transaction in green

2. Return to dashboard
3. Check product stock
✅ Expected: Stock increased by 100
```

### Test 3B: EXPORT Goods

#### Step 1: Create Export Transaction
```
1. In Stock Management
2. Select product: "Nike Football"
3. Select "Export" radio button
4. Enter Quantity: 20
5. Enter Unit Price: 25.00
6. Enter Note: "Transfer to Branch B"
7. Click "Submit Transaction"
✅ Expected:
   - Success toast
   - Stock decreases by 20
   - Transaction recorded
```

#### Step 2: Verify Export
```
1. Check Recent Transactions
✅ Expected: See EXPORT transaction in red

2. Return to dashboard
3. Check product stock
✅ Expected: Stock decreased by 20
```

---

## 🧪 Scenario 4: EDGE CASES & VALIDATION

### Test 4A: Sell Out of Stock Product

```
1. Find a product with 0 stock
2. Click BUY button
✅ Expected: Toast "ទំនិញនេះអស់ពីស្តុកហើយ!" (Out of stock!)
❌ Should NOT add to cart
```

### Test 4B: Sell More Than Available Stock

```
1. Add product with stock = 5 to cart
2. In cart, try to increase quantity to 10
✅ Expected: Either:
   - Quantity limited to 5
   - OR warning message
   - OR payment fails with error
```

### Test 4C: Empty Cart Checkout

```
1. Go to cart with no items
2. Try to proceed to payment
✅ Expected: 
   - Button disabled
   - OR error message
   - OR payment screen shows empty
```

### Test 4D: Export More Than Available

```
1. Product has stock = 10
2. Try to export 20 units
✅ Expected:
   - Transaction succeeds (system allows negative stock)
   - OR error message (if validation added)
   - Stock becomes -10 or shows error
```

### Test 4E: Missing Required Fields

#### In Payment:
```
1. Leave customer name empty
2. Click Confirm Payment
✅ Expected: Uses "General Customer" as default
```

#### In Stock Management:
```
1. Leave quantity empty
2. Click Submit
✅ Expected: Toast "សូមបំពេញចំនួន និងតម្លៃ!" (Fill quantity and price!)
```

### Test 4F: Invalid Input

```
1. Enter negative quantity: -5
2. Enter invalid price: "abc"
✅ Expected: 
   - Validation error
   - OR fields reject invalid input
```

---

## 📊 Verification Checklist

### After Selling Goods:
- [ ] Cart shows correct items
- [ ] Quantities can be adjusted
- [ ] Total amount calculates correctly
- [ ] Customer info is captured
- [ ] Invoice can be shared/saved
- [ ] Payment completes successfully
- [ ] Stock quantities decrease
- [ ] Sale recorded in database
- [ ] Sale appears in reports
- [ ] Customer info saved with sale

### After Buying/Purchasing Goods:
- [ ] Transaction form accepts input
- [ ] All transaction types work (Import/Export/Purchase)
- [ ] Stock quantities update correctly
- [ ] Prices update on purchase
- [ ] Transactions appear in history
- [ ] Transaction details are accurate
- [ ] Notes are saved and displayed
- [ ] Color coding works (green/red)

### Dashboard Updates:
- [ ] Total products count updates
- [ ] Low stock count updates
- [ ] Total inventory value updates
- [ ] Revenue chart updates (after sales)

---

## 🔍 Database Verification

### Check Sales Table:
```sql
SELECT * FROM sales ORDER BY sale_date DESC LIMIT 10;
```
✅ Expected: See recent sales with customer info

### Check Stock Transactions Table:
```sql
SELECT * FROM stock_transactions ORDER BY trans_date DESC LIMIT 10;
```
✅ Expected: See all import/export/purchase transactions

### Check Product Quantities:
```sql
SELECT name, quantity FROM products;
```
✅ Expected: Quantities match after transactions

### Check Revenue:
```sql
SELECT SUM(total_price) as total_revenue FROM sales;
```
✅ Expected: Sum of all sales

---

## 🎬 Complete Test Scenario (End-to-End)

### Full Business Day Simulation:

```
1. LOGIN as admin

2. PURCHASE new stock:
   - Buy 100 Nike Footballs @ $20 each
   - Buy 50 Adidas Jerseys @ $35 each
   
3. VERIFY stock increased:
   - Nike Football: +100 units
   - Adidas Jersey: +50 units

4. MAKE SALES (switch to user role or continue as admin):
   - Sell 5 Nike Footballs to "Customer A"
   - Sell 3 Adidas Jerseys to "Customer B"
   - Sell 2 Nike Footballs + 1 Jersey to "Customer C"

5. EXPORT goods:
   - Export 20 Nike Footballs to Branch B

6. VERIFY final state:
   - Nike Football: 100 - 5 - 2 - 20 = 73 units
   - Adidas Jersey: 50 - 3 - 1 = 46 units

7. CHECK REPORTS:
   - 3 sales recorded
   - 3 transactions (2 purchases, 1 export)
   - Revenue = (5×$20) + (3×$35) + (2×$20 + 1×$35)
   - Revenue = $100 + $105 + $75 = $280

8. VERIFY DASHBOARD:
   - Total products: 2
   - Total value: (73×$20) + (46×$35) = $1460 + $1610 = $3070
   - Revenue chart shows today's sales
```

---

## 🐛 Common Issues & Solutions

### Issue 1: Cart is empty after adding items
**Solution**: Check CartManager singleton is working. Items stored in memory only.

### Issue 2: Stock doesn't decrease after sale
**Solution**: Check PaymentActivity processFinalPayment() method executes.

### Issue 3: Transaction doesn't appear in history
**Solution**: Check database insert is successful, verify table exists.

### Issue 4: Price doesn't update on purchase
**Solution**: Verify PURCHASE transaction type updates price field.

### Issue 5: Customer info not saved
**Solution**: Check sales table has customer_name and customer_phone columns.

---

## ✅ Success Criteria

All tests pass if:
- ✅ Products can be added to cart
- ✅ Cart quantities can be adjusted
- ✅ Payment completes successfully
- ✅ Stock reduces after sales
- ✅ Sales are recorded with customer info
- ✅ Purchases increase stock
- ✅ Prices update on purchase
- ✅ Import/Export work correctly
- ✅ Transactions are logged
- ✅ Dashboard metrics update
- ✅ Reports show accurate data

---

## 📱 Test on Different Roles

### As Admin:
- Can buy/purchase goods
- Can import/export
- Can make sales
- Can view all reports

### As User:
- Can make sales only
- Cannot access stock management
- Can view limited reports

---

**Happy Testing! 🚀**

Report any issues found during testing for immediate resolution.

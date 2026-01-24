# ⚡ Quick Test Checklist - Buying & Selling

## 🚀 5-Minute Quick Test

### ✅ SELLING TEST (2 minutes)

```
□ Login (admin/1234)
□ Click BUY on any product → See "Added to cart" toast
□ Click cart icon (top right)
□ See product in cart with quantity
□ Click + to increase quantity → Quantity increases
□ Click "PROCEED TO PAYMENT"
□ Enter customer name: "Test Customer"
□ Click "CONFIRM PAYMENT" → See "Payment Success!"
□ Back on dashboard → Check product stock decreased
```

**Result**: ⬜ PASS | ⬜ FAIL

---

### ✅ BUYING TEST (2 minutes)

```
□ Click "Stock Mgmt" button
□ Select any product from dropdown
□ Select "Purchase" radio button
□ Enter Quantity: 10
□ Enter Unit Price: 25.00
□ Click "Submit Transaction" → See success toast
□ See transaction in "Recent Transactions" (green PURCHASE)
□ Press back to dashboard
□ Check product stock increased by 10
```

**Result**: ⬜ PASS | ⬜ FAIL

---

### ✅ IMPORT/EXPORT TEST (1 minute)

```
□ In Stock Mgmt, select "Import"
□ Enter Qty: 5, Price: 20
□ Submit → Stock increases
□ Select "Export"
□ Enter Qty: 3, Price: 20
□ Submit → Stock decreases
```

**Result**: ⬜ PASS | ⬜ FAIL

---

## 🎯 Critical Path Test (10 minutes)

### Scenario: Complete Business Transaction

**Starting State**: Fresh app install

#### 1. Setup (2 min)
```
□ Login as admin
□ Add new product:
  - Name: "Test Product"
  - SKU: "TEST001"
  - Category: Select any
  - Price: 50.00
  - Quantity: 0
  - Expiry: 2027-12-31
□ Save product
```

#### 2. Purchase Stock (2 min)
```
□ Go to Stock Mgmt
□ Select "Test Product"
□ Choose "Purchase"
□ Quantity: 100
□ Price: 40.00
□ Note: "Initial stock"
□ Submit
□ Verify: Stock = 100, Price = $40.00
```

#### 3. Make Sale (3 min)
```
□ Return to dashboard
□ Click BUY on "Test Product"
□ Go to cart
□ Increase quantity to 5
□ Proceed to payment
□ Customer: "John Smith"
□ Phone: "012345678"
□ Confirm payment
□ Verify: Stock = 95 (100 - 5)
```

#### 4. Export Stock (1 min)
```
□ Go to Stock Mgmt
□ Select "Test Product"
□ Choose "Export"
□ Quantity: 10
□ Submit
□ Verify: Stock = 85 (95 - 10)
```

#### 5. Verify Reports (2 min)
```
□ Click "Reports"
□ See sale: 5 units to John Smith
□ Total revenue: $200 (5 × $40)
□ Go to Stock Mgmt
□ See 3 transactions:
  - PURCHASE: 100 units (green)
  - SALE: 5 units (via payment)
  - EXPORT: 10 units (red)
```

**Final Stock Check**: 85 units ✅

---

## 🔥 Stress Test (Optional)

### Test 1: Multiple Items in Cart
```
□ Add 5 different products to cart
□ Adjust quantities on each
□ Complete payment
□ Verify all stocks decreased correctly
```

### Test 2: Large Quantity
```
□ Purchase 1000 units
□ Sell 500 units
□ Export 300 units
□ Verify: Stock = 200
```

### Test 3: Rapid Transactions
```
□ Make 10 sales in a row
□ Make 10 purchases in a row
□ Check all recorded correctly
```

---

## 📊 Data Verification Queries

### After Testing, Run These Checks:

#### Check Sales:
```sql
SELECT COUNT(*) FROM sales;
-- Should show number of sales made
```

#### Check Transactions:
```sql
SELECT trans_type, COUNT(*) 
FROM stock_transactions 
GROUP BY trans_type;
-- Should show counts for IMPORT, EXPORT, PURCHASE
```

#### Check Stock Accuracy:
```sql
SELECT name, quantity FROM products WHERE name = 'Test Product';
-- Should match your calculations
```

#### Check Revenue:
```sql
SELECT SUM(total_price) FROM sales;
-- Should match total of all sales
```

---

## 🎨 Visual Verification

### Dashboard Should Show:
```
□ Total Products: [number]
□ Low Stock: [number of products ≤ reorder point]
□ Total Value: $[sum of quantity × price]
□ Revenue Chart: Shows today's sales
```

### Product List Should Show:
```
□ Status badges (NEW/OLD/EXPIRING)
□ Correct stock quantities
□ Updated prices (after purchase)
□ BUY buttons (user) or EDIT/DELETE (admin)
```

### Stock Management Should Show:
```
□ Recent transactions (up to 50)
□ Color-coded types (green/red)
□ Correct dates and amounts
□ Notes displayed
```

---

## ⚠️ Error Scenarios to Test

### Test Invalid Operations:
```
□ Try to sell out-of-stock item → Should show error
□ Try to submit transaction with empty fields → Should show error
□ Try to export more than available → Should handle gracefully
□ Try to checkout with empty cart → Should prevent or show error
```

---

## 📱 Device-Specific Tests

### Test on Different Screen Sizes:
```
□ Phone (small screen)
□ Tablet (large screen)
□ Different orientations (portrait/landscape)
```

### Test Performance:
```
□ Add 100+ products
□ Make 50+ transactions
□ Check app remains responsive
```

---

## ✅ Final Checklist

Before marking complete, verify:

### Core Functionality:
- [ ] Can add products to cart
- [ ] Can adjust cart quantities
- [ ] Can complete payment
- [ ] Stock decreases after sale
- [ ] Can purchase stock
- [ ] Stock increases after purchase
- [ ] Can import goods
- [ ] Can export goods
- [ ] Transactions are logged
- [ ] Reports show accurate data

### Data Integrity:
- [ ] Stock quantities are accurate
- [ ] Prices update correctly
- [ ] Customer info is saved
- [ ] Transaction history is complete
- [ ] Dashboard metrics are correct

### User Experience:
- [ ] All buttons work
- [ ] Navigation is smooth
- [ ] Toast messages appear
- [ ] Forms validate input
- [ ] No crashes or freezes

---

## 🎯 Test Results Summary

| Test Category | Status | Notes |
|--------------|--------|-------|
| Selling Goods | ⬜ Pass / ⬜ Fail | |
| Buying Goods | ⬜ Pass / ⬜ Fail | |
| Import/Export | ⬜ Pass / ⬜ Fail | |
| Data Accuracy | ⬜ Pass / ⬜ Fail | |
| Error Handling | ⬜ Pass / ⬜ Fail | |
| UI/UX | ⬜ Pass / ⬜ Fail | |

**Overall Status**: ⬜ READY FOR PRODUCTION | ⬜ NEEDS FIXES

---

## 🐛 Issues Found

| # | Issue | Severity | Status |
|---|-------|----------|--------|
| 1 | | High/Med/Low | Open/Fixed |
| 2 | | High/Med/Low | Open/Fixed |
| 3 | | High/Med/Low | Open/Fixed |

---

**Tester**: _______________  
**Date**: _______________  
**Build Version**: _______________  
**Device**: _______________

---

## 🚀 Quick Commands for Testing

### Reset Test Data:
```java
// In Android Studio, run this in debug console
context.deleteDatabase("Inventory.db");
```

### View Database:
```bash
adb shell
cd /data/data/com.narith.aims/databases/
sqlite3 Inventory.db
.tables
SELECT * FROM products;
SELECT * FROM sales;
SELECT * FROM stock_transactions;
```

### Clear App Data:
```bash
adb shell pm clear com.narith.aims
```

---

**Remember**: Test both as admin and as regular user to verify role-based access!

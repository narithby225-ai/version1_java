# 🧪 Test Customer Feature - Complete Guide

## Overview
The Test Customer feature allows admins to test the sales/purchase flow without switching to a user account. All test transactions are clearly marked and can be reviewed or cleared separately from real customer data.

---

## 🎯 Key Features

### 1. **Admin-Only Access**
- Only visible when logged in as admin
- Orange button on main dashboard: "🧪 Test Customer (Admin Only)"
- Completely separate from regular user sales flow

### 2. **Test Sales Mode**
- Browse all products
- Add items to test cart
- Complete test sales
- All transactions marked as "🧪 TEST CUSTOMER"

### 3. **Test Transaction History**
- View all test transactions
- Separate tab for test history
- Clear all test data with one click
- Orange color coding for easy identification

### 4. **No Account Switching**
- Test sales directly from admin account
- No need to logout and login as user
- Faster testing workflow
- Maintains admin privileges

---

## 📱 How to Use

### Step 1: Access Test Customer Mode

```
1. Login as admin (admin/1234)
2. On Main Dashboard, look for orange button
3. Click "🧪 Test Customer (Admin Only)"
4. Test Customer screen opens
```

### Step 2: Browse Products

```
1. Default tab: "Products"
2. See all available products
3. Each product shows:
   - Product name
   - Category
   - Price
   - Stock quantity
   - Status badge (NEW/OLD/EXPIRING)
   - BUY button
```

### Step 3: Add Items to Test Cart

```
1. Click BUY button on any product
2. See toast: "✅ Added to TEST cart: [Product Name]"
3. Cart badge updates (shows item count)
4. Repeat for multiple products
```

### Step 4: View Test Cart

```
1. Click "🛒 Cart" button (top right)
2. Dialog shows:
   - All cart items
   - Quantities
   - Prices
   - Subtotals
   - Total amount
3. Three options:
   - "Complete Test Sale" → Process the sale
   - "Clear Cart" → Empty the cart
   - "Cancel" → Close dialog
```

### Step 5: Complete Test Sale

```
1. Click "Complete Test Sale"
2. System processes:
   - Updates stock quantities
   - Records sale in database
   - Marks as "🧪 TEST CUSTOMER"
   - Clears cart
3. See success toast with total amount
4. Product list refreshes with updated stock
```

### Step 6: View Test History

```
1. Click "Test History" tab
2. See all test transactions:
   - Product name with 🧪 icon
   - Quantity sold
   - Total amount
   - Date
   - "TEST SALE" badge (orange)
3. Info shows: "🧪 Test Mode: X test transactions"
```

### Step 7: Clear Test Data (Optional)

```
1. In "Test History" tab
2. Scroll to bottom
3. Click "🗑️ Clear All Test Data"
4. Confirm deletion
5. All test transactions removed
6. Note: Stock quantities NOT restored
```

---

## 🎨 Visual Guide

### Main Dashboard (Admin View):
```
┌─────────────────────────────────────┐
│  Dashboard Cards                    │
│  - Total Products                   │
│  - Low Stock                        │
│  - Total Value                      │
├─────────────────────────────────────┤
│  Revenue Chart                      │
├─────────────────────────────────────┤
│  [Categories] [Stock Mgmt] [Reports]│
├─────────────────────────────────────┤
│  🧪 Test Customer (Admin Only)      │ ← NEW BUTTON
│  (Orange, full width)               │
└─────────────────────────────────────┘
```

### Test Customer Screen:
```
┌─────────────────────────────────────┐
│  🧪 Test Customer Mode    [🛒 Cart] │
│  All transactions marked as TEST    │
├─────────────────────────────────────┤
│  [Products] [Test History]          │ ← Tabs
├─────────────────────────────────────┤
│                                     │
│  Product List (if Products tab)     │
│  OR                                 │
│  Test Transaction History           │
│                                     │
├─────────────────────────────────────┤
│  ℹ️ Admin Only: Test sales without  │
│  affecting real customer data       │
└─────────────────────────────────────┘
```

### Test Cart Dialog:
```
┌─────────────────────────────────────┐
│  Test Cart                          │
├─────────────────────────────────────┤
│  🛒 TEST CART ITEMS:                │
│                                     │
│  Nike Football                      │
│    Qty: 2 × $25.00 = $50.00        │
│                                     │
│  Adidas Jersey                      │
│    Qty: 1 × $45.00 = $45.00        │
│                                     │
│  ─────────────────                  │
│  TOTAL: $95.00                      │
├─────────────────────────────────────┤
│  [Complete Test Sale]               │
│  [Clear Cart] [Cancel]              │
└─────────────────────────────────────┘
```

### Test Transaction Item:
```
┌─────────────────────────────────────┐
│  🧪 Nike Football    [TEST SALE]    │
│  Qty: 2              $50.00         │
│  2026-02-25                         │
└─────────────────────────────────────┘
(Orange background, easy to identify)
```

---

## 🔄 Complete Workflow Example

### Scenario: Test a Sale of 3 Products

```
1. LOGIN
   - Username: admin
   - Password: 1234
   
2. CLICK TEST CUSTOMER BUTTON
   - Orange button on dashboard
   
3. ADD PRODUCTS TO CART
   - Click BUY on "Nike Football"
   - Click BUY on "Adidas Jersey"
   - Click BUY on "Nike Football" again
   - Cart badge shows: 3
   
4. VIEW CART
   - Click "🛒 Cart" button
   - See:
     * Nike Football: Qty 2 × $25 = $50
     * Adidas Jersey: Qty 1 × $45 = $45
     * TOTAL: $95.00
   
5. COMPLETE SALE
   - Click "Complete Test Sale"
   - See: "✅ Test Sale Complete! Total: $95.00"
   - Cart clears automatically
   
6. VERIFY STOCK UPDATED
   - Products tab refreshes
   - Nike Football: Stock decreased by 2
   - Adidas Jersey: Stock decreased by 1
   
7. VIEW TEST HISTORY
   - Click "Test History" tab
   - See 2 transactions:
     * 🧪 Nike Football - Qty: 2 - $50.00
     * 🧪 Adidas Jersey - Qty: 1 - $45.00
   
8. CLEAR TEST DATA (Optional)
   - Click "🗑️ Clear All Test Data"
   - Confirm deletion
   - Test transactions removed
   - Stock remains at reduced levels
```

---

## 📊 Database Impact

### What Gets Recorded:

#### Sales Table:
```sql
INSERT INTO sales (
    sale_date,
    product_name,
    quantity,
    total_price,
    customer_name,
    customer_phone
) VALUES (
    '2026-02-25',
    'Nike Football',
    2,
    50.00,
    '🧪 TEST CUSTOMER',  ← Special marker
    'TEST-MODE'          ← Special marker
);
```

#### Products Table:
```sql
UPDATE products 
SET quantity = quantity - 2 
WHERE id = 1;
```

### How to Identify Test Transactions:

```sql
-- Query all test transactions
SELECT * FROM sales 
WHERE customer_name = '🧪 TEST CUSTOMER';

-- Count test transactions
SELECT COUNT(*) FROM sales 
WHERE customer_name = '🧪 TEST CUSTOMER';

-- Delete test transactions
DELETE FROM sales 
WHERE customer_name = '🧪 TEST CUSTOMER';
```

---

## ⚠️ Important Notes

### Stock Quantities:
- ✅ Test sales DO reduce stock
- ✅ Stock changes are REAL
- ❌ Clearing test data does NOT restore stock
- 💡 Use Stock Management to add stock back if needed

### Customer Data:
- ✅ Test transactions clearly marked
- ✅ Easy to identify in reports
- ✅ Can be filtered out
- ✅ Can be bulk deleted

### Use Cases:
- ✅ Testing sales flow
- ✅ Training new staff
- ✅ Demonstrating features
- ✅ Verifying stock updates
- ✅ Testing payment process
- ❌ NOT for real customer sales

---

## 🆚 Comparison: Test Customer vs Regular User

| Feature | Test Customer | Regular User |
|---------|--------------|--------------|
| Access | Admin only | User account |
| Customer Name | "🧪 TEST CUSTOMER" | Real customer |
| Phone | "TEST-MODE" | Real phone |
| Color Coding | Orange | Normal |
| History Tab | Separate | Mixed with real |
| Clear Data | One-click | Manual delete |
| Stock Impact | Yes | Yes |
| Reports | Filterable | Included |

---

## 🎯 Best Practices

### When to Use Test Customer:
1. ✅ Testing new features
2. ✅ Training sessions
3. ✅ Demonstrating to stakeholders
4. ✅ Verifying bug fixes
5. ✅ Quick functionality checks

### When NOT to Use:
1. ❌ Real customer sales
2. ❌ Production environment
3. ❌ Financial reporting
4. ❌ Inventory audits

### Recommended Workflow:
```
1. Use Test Customer for testing
2. Review test transactions
3. Clear test data when done
4. Restore stock if needed (via Stock Management)
5. Use regular user account for real sales
```

---

## 🔍 Troubleshooting

### Issue: Test Customer button not visible
**Solution**: 
- Ensure logged in as admin
- Check userRole is "admin"
- Button visibility set in MainActivity

### Issue: Cart not updating
**Solution**:
- Check CartManager singleton
- Verify product has stock > 0
- Check toast messages

### Issue: Stock not decreasing
**Solution**:
- Verify database transaction completes
- Check UPDATE statement executes
- Review logs for errors

### Issue: Test transactions not showing
**Solution**:
- Switch to "Test History" tab
- Check customer_name = "🧪 TEST CUSTOMER"
- Verify sales table has data

### Issue: Cannot clear test data
**Solution**:
- Check database permissions
- Verify DELETE statement
- Ensure customer_name matches exactly

---

## 📈 Testing Scenarios

### Scenario 1: Single Product Sale
```
1. Add 1 product to cart
2. Complete sale
3. Verify stock decreased by 1
4. Check test history shows 1 transaction
```

### Scenario 2: Multiple Products
```
1. Add 3 different products
2. Complete sale
3. Verify all stocks decreased
4. Check test history shows 3 transactions
```

### Scenario 3: Same Product Multiple Times
```
1. Add same product 3 times
2. Cart shows quantity: 3
3. Complete sale
4. Verify stock decreased by 3
5. Check test history shows 1 transaction with qty: 3
```

### Scenario 4: Out of Stock
```
1. Try to add product with stock = 0
2. See error: "❌ Out of stock!"
3. Product NOT added to cart
```

### Scenario 5: Clear Cart
```
1. Add multiple products
2. Click "Clear Cart" in dialog
3. Cart empties
4. No sale recorded
5. Stock unchanged
```

---

## 🎓 Training Guide

### For New Admins:

**Step 1: Introduction**
- Explain purpose of Test Customer
- Show how to access feature
- Demonstrate orange color coding

**Step 2: Practice Sales**
- Add products to cart
- View cart contents
- Complete test sale
- Verify stock changes

**Step 3: Review History**
- Switch to Test History tab
- Identify test transactions
- Understand orange badges

**Step 4: Clean Up**
- Clear test data
- Restore stock if needed
- Return to normal operations

---

## 📝 Quick Reference

### Access Test Customer:
```
Dashboard → 🧪 Test Customer (Admin Only)
```

### Add to Cart:
```
Products Tab → Click BUY → See toast
```

### Complete Sale:
```
🛒 Cart → Complete Test Sale → Confirm
```

### View History:
```
Test History Tab → See all test transactions
```

### Clear Data:
```
Test History Tab → 🗑️ Clear All Test Data → Confirm
```

---

## ✅ Feature Checklist

- [x] Admin-only access
- [x] Separate test cart
- [x] Test transaction marking
- [x] Orange color coding
- [x] Test history tab
- [x] Clear test data function
- [x] Stock quantity updates
- [x] Toast notifications
- [x] Cart badge counter
- [x] Transaction summary
- [x] Database integration
- [x] No account switching needed

---

**The Test Customer feature is now ready to use! Perfect for testing sales without affecting real customer data.** 🚀

Login as admin and look for the orange "🧪 Test Customer" button on your dashboard!

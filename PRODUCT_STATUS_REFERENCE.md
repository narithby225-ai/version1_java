# 📊 Product Status Reference Card

## Quick Status Guide

### 🆕 NEW (Green Badge)
- **Condition**: Product entered within last 24 hours
- **Calculation**: `dateAdded - today ≤ 1 day`
- **Display**: "🆕 NEW"
- **Color**: Green background
- **Purpose**: Highlight recently added inventory

### 📦 OLD (Grey Badge)
- **Condition**: Product in stock for 6+ days
- **Calculation**: `today - dateAdded ≥ 6 days`
- **Display**: "📦 OLD"
- **Color**: Grey background
- **Purpose**: Identify slow-moving inventory

### ⚠️ EXPIRING SOON (Orange Badge)
- **Condition**: 5 days or less until expiry
- **Calculation**: `expiryDate - today ≤ 5 days AND ≥ 0`
- **Display**: "⚠️ Xd left" (where X = days remaining)
- **Color**: Orange background
- **Purpose**: Urgent action needed - discount or remove

### ❌ EXPIRED (Grey Badge)
- **Condition**: Past expiry date
- **Calculation**: `expiryDate < today`
- **Display**: "❌ EXPIRED"
- **Color**: Grey background
- **Purpose**: Must be removed from sale

### ⚪ NORMAL (No Badge)
- **Condition**: None of the above apply
- **Display**: No badge shown
- **Purpose**: Standard inventory

## Priority Order

When multiple conditions could apply, the system shows status in this priority:

1. **NEW** (highest priority)
2. **EXPIRING_SOON**
3. **EXPIRED**
4. **OLD**
5. **NORMAL** (lowest priority)

## Examples

### Example 1: Fresh Product
- Date Added: 2026-02-25
- Today: 2026-02-25
- Expiry: 2026-12-31
- **Status**: 🆕 NEW

### Example 2: Expiring Product
- Date Added: 2026-02-01
- Today: 2026-02-25
- Expiry: 2026-02-28
- **Status**: ⚠️ 3d left

### Example 3: Expired Product
- Date Added: 2026-01-01
- Today: 2026-02-25
- Expiry: 2026-02-20
- **Status**: ❌ EXPIRED

### Example 4: Old Stock
- Date Added: 2026-02-10
- Today: 2026-02-25
- Expiry: 2027-12-31
- **Status**: 📦 OLD

### Example 5: Normal Stock
- Date Added: 2026-02-20
- Today: 2026-02-25
- Expiry: 2026-12-31
- **Status**: (no badge)

## Business Rules

### NEW Products (≤24 hours):
- ✅ Promote as "Just Arrived"
- ✅ Feature in marketing
- ✅ Check quality on receipt

### OLD Products (≥6 days):
- ⚠️ Review sales velocity
- ⚠️ Consider promotions
- ⚠️ Check storage conditions

### EXPIRING SOON (≤5 days):
- 🚨 Immediate discount (30-50%)
- 🚨 Move to clearance section
- 🚨 Notify customers
- 🚨 Consider donation if unsold

### EXPIRED Products:
- ❌ Remove from sale immediately
- ❌ Dispose properly
- ❌ Document for inventory write-off
- ❌ Review ordering patterns

## Code Implementation

### Status Calculation (DatabaseHelper.java):
```java
public static String getProductStatus(String dateAdded, String expiryDate) {
    String today = getCurrentDate();
    long daysFromAdded = getDaysDifference(dateAdded, today);
    long daysToExpiry = getDaysDifference(today, expiryDate);

    if (daysFromAdded <= 1) {
        return "NEW";
    } else if (daysToExpiry <= 5 && daysToExpiry >= 0) {
        return "EXPIRING_SOON";
    } else if (daysToExpiry < 0) {
        return "EXPIRED";
    } else if (daysFromAdded >= 6) {
        return "OLD";
    }
    return "NORMAL";
}
```

### Display Logic (ProductAdapter.java):
```java
switch (status) {
    case "NEW":
        badge.setText("🆕 NEW");
        badge.setBackground(green);
        break;
    case "EXPIRING_SOON":
        badge.setText("⚠️ " + daysLeft + "d left");
        badge.setBackground(orange);
        break;
    case "EXPIRED":
        badge.setText("❌ EXPIRED");
        badge.setBackground(grey);
        break;
    case "OLD":
        badge.setText("📦 OLD");
        badge.setBackground(grey);
        break;
}
```

## Dashboard Integration

### Low Stock Alert:
- Shows count of products with `quantity ≤ reorderPoint`
- Click to see detailed list
- Separate from expiry status

### Inventory Value:
- Calculated as `SUM(quantity × price)`
- Includes all products regardless of status
- Real-time updates

## Reporting

### Status Distribution Report:
```sql
SELECT 
    CASE 
        WHEN julianday('now') - julianday(date_added) <= 1 THEN 'NEW'
        WHEN julianday(expiry_date) - julianday('now') <= 5 THEN 'EXPIRING'
        WHEN julianday(expiry_date) - julianday('now') < 0 THEN 'EXPIRED'
        WHEN julianday('now') - julianday(date_added) >= 6 THEN 'OLD'
        ELSE 'NORMAL'
    END as status,
    COUNT(*) as count
FROM products
GROUP BY status;
```

## Best Practices

### Daily Tasks:
1. Check EXPIRED products - remove immediately
2. Review EXPIRING_SOON - apply discounts
3. Verify NEW products - quality check
4. Monitor OLD products - adjust ordering

### Weekly Tasks:
1. Analyze status distribution
2. Review expiry date accuracy
3. Update reorder points
4. Plan promotions for slow movers

### Monthly Tasks:
1. Audit expired product disposal
2. Review supplier lead times
3. Optimize stock levels
4. Update expiry date policies

---

**Quick Tip**: Set expiry dates conservatively to ensure products are sold well before actual expiration!

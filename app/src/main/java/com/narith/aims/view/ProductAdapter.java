package com.narith.aims.view;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.narith.aims.R;
import com.narith.aims.model.Product;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * PRODUCT ADAPTER - បង្ហាញបញ្ជីទំនិញក្នុងទម្រង់ Form អាជីព
 * មុខងារ៖ Search (Filterable), Role-Based UI, និង Status Badges
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {

    private List<Product> productList;      // បញ្ជីដែលបង្ហាញលើអេក្រង់ (អាចប្រែប្រួលតាមការ Search)
    private List<Product> productListFull;  // បញ្ជីដើមដែលរក្សាទុកទិន្នន័យទាំងអស់ (សម្រាប់ Backup)
    private String userRole;
    private OnProductActionListener listener;
    private Context context;

    public interface OnProductActionListener {
        void onBuyClick(Product product);
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    public ProductAdapter(Context context, List<Product> productList, String userRole) {
        this.context = context;
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList); // ចម្លងបញ្ជីដើមទុក
        this.userRole = userRole;
    }

    // --- 🔍 មុខងារស្វែងរក (Filter Logic) ---
    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // បើគ្មានការវាយអក្សរ ត្រឡប់ទៅបញ្ជីពេញលេញវិញ
                filteredList.addAll(productListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Product item : productListFull) {
                    // ស្វែងរកតាមឈ្មោះទំនិញ ឬប្រភេទ ឬ SKU
                    if (item.getName().toLowerCase().contains(filterPattern) ||
                            item.getCategory().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productList.clear();
            productList.addAll((Collection<? extends Product>) results.values);
            notifyDataSetChanged();
        }
    };

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sport_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvCategory.setText(product.getCategory());
        holder.tvPrice.setText(String.format("$ %.2f", product.getPrice()));
        holder.tvQty.setText("Stock: " + product.getQuantity());

        // --- ១. ប្តូរពណ៌អក្សរស្តុក បើជិតអស់ ---
        if (product.getQuantity() <= product.getReorderPoint()) {
            holder.tvQty.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvQty.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        // --- ២. រូបភាព Glide ---
        String imageUri = product.getImageUri();
        if (imageUri != null && !imageUri.isEmpty()) {
            if (imageUri.startsWith("drawable://")) {
                // Load sample drawable
                String drawableName = imageUri.replace("drawable://", "");
                int drawableId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
                if (drawableId != 0) {
                    holder.imgProduct.setImageResource(drawableId);
                } else {
                    holder.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                // Load URI image with Glide
                Glide.with(context)
                        .load(Uri.parse(imageUri))
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.imgProduct);
            }
        } else {
            holder.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // --- ៣. Badge "NEW" (០-៦ ថ្ងៃ) ---
        updateStatusBadge(holder, product);

        // --- ៤. Role Logic ---
        if ("admin".equals(userRole)) {
            holder.layoutAdminActions.setVisibility(View.VISIBLE);
            holder.btnBuy.setVisibility(View.GONE);
        } else {
            holder.layoutAdminActions.setVisibility(View.GONE);
            holder.btnBuy.setVisibility(View.VISIBLE);
        }

        holder.btnBuy.setOnClickListener(v -> { if(listener!=null) listener.onBuyClick(product); });
        holder.btnEdit.setOnClickListener(v -> { if(listener!=null) listener.onEditClick(product); });
        holder.btnDelete.setOnClickListener(v -> { if(listener!=null) listener.onDeleteClick(product); });
    }

    private void updateStatusBadge(ProductViewHolder holder, Product product) {
        holder.tvStatusBadge.setVisibility(View.VISIBLE);
        String status = product.getStatus();
        
        switch (status) {
            case "NEW":
                holder.tvStatusBadge.setText("🆕 NEW");
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_new);
                break;
            case "EXPIRING_SOON":
                long daysLeft = product.getDaysUntilExpiry();
                holder.tvStatusBadge.setText("⚠️ " + daysLeft + "d left");
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_expiring);
                break;
            case "EXPIRED":
                holder.tvStatusBadge.setText("❌ EXPIRED");
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_old);
                break;
            case "OLD":
                holder.tvStatusBadge.setText("📦 OLD");
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_old);
                break;
            default:
                holder.tvStatusBadge.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvPrice, tvQty, tvStatusBadge;
        CircleImageView imgProduct;
        ImageButton btnBuy, btnEdit, btnDelete;
        LinearLayout layoutAdminActions;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnBuy = itemView.findViewById(R.id.btnBuy);
            layoutAdminActions = itemView.findViewById(R.id.layoutAdminActions);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
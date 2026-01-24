package com.narith.aims.view;

import android.net.Uri; // Import ថ្មី
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Import Glide
import com.narith.aims.R;
import com.narith.aims.model.Product;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnQuantityChangeListener listener;

    public interface OnQuantityChangeListener {
        void onAddClick(Product product);
        void onMinusClick(Product product);
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.listener = listener;
    }

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

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
        holder.tvQty.setText(String.valueOf(product.getQuantity()));

        // --- កូដថ្មី៖ ប្រើ Glide បង្ហាញរូបភាព ---
        String imageUri = product.getImageUri();
        if (imageUri != null && !imageUri.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(imageUri)) // Load ពី URI
                    .placeholder(android.R.drawable.ic_menu_gallery) // រូបរង់ចាំ
                    .error(android.R.drawable.ic_menu_report_image) // រូបពេល Error
                    .into(holder.imgProduct);
        } else {
            // បើគ្មានរូប ដាក់រូប Default
            holder.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.btnPlus.setOnClickListener(v -> { if(listener!=null) listener.onAddClick(product); });
        holder.btnMinus.setOnClickListener(v -> { if(listener!=null) listener.onMinusClick(product); });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvPrice, tvQty;
        CircleImageView imgProduct;
        ImageButton btnPlus, btnMinus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}
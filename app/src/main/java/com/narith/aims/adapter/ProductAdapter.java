package com.narith.aims.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.narith.aims.databinding.ItemProductBinding;
import com.narith.aims.model.Product;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.binding.tvProductName.setText(product.getName());
        holder.binding.tvProductCategory.setText(product.getCategory());
        holder.binding.tvProductPrice.setText(String.format("$%.2f", product.getPrice()));
        holder.binding.tvProductQuantity.setText("Qty: " + product.getQuantity());
        
        if (product.getExpiryDate() != null) {
            holder.binding.tvProductExpiry.setVisibility(View.VISIBLE);
            holder.binding.tvProductExpiry.setText("Expires: " + dateFormat.format(product.getExpiryDate()));
        } else {
            holder.binding.tvProductExpiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;
        public ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

package com.narith.aims.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.narith.aims.R;
import com.narith.aims.model.Product;
import com.narith.aims.utils.CartManager;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private Context context;
    private List<Product> cartItems;
    private OnCartItemChangeListener listener;

    public interface OnCartItemChangeListener {
        void onQuantityChanged();
        void onItemRemoved();
    }

    public CartItemAdapter(Context context, List<Product> cartItems, OnCartItemChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_checkout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = cartItems.get(position);
        
        holder.tvProductName.setText(product.getName());
        holder.tvPrice.setText("$" + String.format("%.2f", product.getPrice()));
        holder.tvQuantity.setText(String.valueOf(product.getCartQuantity()));
        holder.tvSubtotal.setText("$" + String.format("%.2f", product.getPrice() * product.getCartQuantity()));
        
        // Increase quantity
        holder.btnIncrease.setOnClickListener(v -> {
            if (product.getCartQuantity() < product.getQuantity()) {
                product.setCartQuantity(product.getCartQuantity() + 1);
                notifyItemChanged(position);
                if (listener != null) listener.onQuantityChanged();
            }
        });
        
        // Decrease quantity
        holder.btnDecrease.setOnClickListener(v -> {
            if (product.getCartQuantity() > 1) {
                product.setCartQuantity(product.getCartQuantity() - 1);
                notifyItemChanged(position);
                if (listener != null) listener.onQuantityChanged();
            }
        });
        
        // Remove item
        holder.btnRemove.setOnClickListener(v -> {
            CartManager.getInstance().getCartItems().remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            if (listener != null) listener.onItemRemoved();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice, tvQuantity, tvSubtotal;
        ImageButton btnIncrease, btnDecrease, btnRemove;

        ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}

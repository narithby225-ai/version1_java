package com.narith.aims.view;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.narith.aims.R;
import com.narith.aims.model.Product;
import com.narith.aims.utils.CartManager;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Product> cartItems;
    private Runnable updatePriceCallback;

    public CartAdapter(Context context, List<Product> cartItems, Runnable updatePriceCallback) {
        this.context = context;
        this.cartItems = cartItems;
        this.updatePriceCallback = updatePriceCallback;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ប្រើ Layout list_item_cart.xml ដែល Narith បានផ្ដល់ឱ្យ
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartItems.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("$ %.2f", product.getPrice()));
        holder.tvQty.setText("x" + product.getCartQuantity()); // បង្ហាញតាមស្ទីល x1, x2

        // បង្ហាញរូបភាពផលិតផល
        String imageUri = product.getImageRes();
        if (imageUri != null && !imageUri.isEmpty()) {
            try {
                holder.imgProduct.setImageURI(Uri.parse(imageUri));
            } catch (Exception e) {
                holder.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
        }

        // មុខងារប៊ូតុង (+) បន្ថែមចំនួន
        if (holder.btnPlus != null) {
            holder.btnPlus.setOnClickListener(v -> {
                CartManager.getInstance().addToCart(product);
                notifyItemChanged(position);
                updatePriceCallback.run();
            });
        }

        // មុខងារប៊ូតុង (-) បន្ថយចំនួន
        if (holder.btnMinus != null) {
            holder.btnMinus.setOnClickListener(v -> {
                CartManager.getInstance().decreaseQuantity(product);
                notifyDataSetChanged();
                updatePriceCallback.run();
            });
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvQty;
        ImageButton btnPlus, btnMinus, btnDelete; //

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            // ភ្ជាប់ ID ឱ្យត្រូវតាម XML ចុងក្រោយរបស់អ្នក
            imgProduct = itemView.findViewById(R.id.ivCartProduct);
            tvName = itemView.findViewById(R.id.tvCartName);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvQty = itemView.findViewById(R.id.tvCartQty);

            // ប៊ូតុងទាំងនេះអាចមាន ឬមិនមាន អាស្រ័យលើការរចនា Layout បច្ចុប្បន្ន
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
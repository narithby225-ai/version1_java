package com.narith.aims.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.narith.aims.R;
import com.narith.aims.model.StockTransaction;
import java.util.List;

public class StockTransactionAdapter extends RecyclerView.Adapter<StockTransactionAdapter.ViewHolder> {

    private Context context;
    private List<StockTransaction> transactions;

    public StockTransactionAdapter(Context context, List<StockTransaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stock_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StockTransaction transaction = transactions.get(position);
        
        holder.tvProductName.setText(transaction.getProductName());
        holder.tvType.setText(transaction.getType());
        holder.tvQuantity.setText("Qty: " + transaction.getQuantity());
        holder.tvAmount.setText("$" + String.format("%.2f", transaction.getTotalAmount()));
        holder.tvDate.setText(transaction.getDate());
        
        if (transaction.getNote() != null && !transaction.getNote().isEmpty()) {
            holder.tvNote.setText(transaction.getNote());
            holder.tvNote.setVisibility(View.VISIBLE);
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }
        
        // កំណត់ពណ៌តាមប្រភេទប្រតិបត្តិការ
        switch (transaction.getType()) {
            case "IMPORT":
            case "PURCHASE":
                holder.tvType.setTextColor(Color.parseColor("#4CAF50")); // បៃតង
                break;
            case "EXPORT":
            case "SALE":
                holder.tvType.setTextColor(Color.parseColor("#F44336")); // ក្រហម
                break;
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvType, tvQuantity, tvAmount, tvDate, tvNote;

        ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvType = itemView.findViewById(R.id.tvType);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNote = itemView.findViewById(R.id.tvNote);
        }
    }
}

package com.narith.aims.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.narith.aims.databinding.ItemTransactionBinding;
import com.narith.aims.model.Transaction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactions = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TransactionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction trans = transactions.get(position);
        holder.binding.tvTransProduct.setText(trans.getProductName());
        holder.binding.tvTransType.setText(trans.getType().replace("_", " "));
        holder.binding.tvTransDate.setText(dateFormat.format(new Date(trans.getTimestamp())));
        
        if (trans.getType().equals("STOCK_IN")) {
            holder.binding.tvTransQty.setText("+" + trans.getQuantity());
            holder.binding.tvTransQty.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.binding.tvTransQty.setText("-" + trans.getQuantity());
            holder.binding.tvTransQty.setTextColor(Color.parseColor("#F44336"));
        }
    }

    @Override
    public int getItemCount() { return transactions.size(); }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ItemTransactionBinding binding;
        public TransactionViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

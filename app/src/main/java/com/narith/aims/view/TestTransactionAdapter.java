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
import java.util.List;

public class TestTransactionAdapter extends RecyclerView.Adapter<TestTransactionAdapter.ViewHolder> {

    private Context context;
    private List<TestCustomerActivity.TestTransaction> transactions;
    private OnTransactionDeleteListener deleteListener;

    public interface OnTransactionDeleteListener {
        void onDeleteTransaction(int transactionId, int position);
    }

    public TestTransactionAdapter(Context context, List<TestCustomerActivity.TestTransaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    public void setOnTransactionDeleteListener(OnTransactionDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_test_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestCustomerActivity.TestTransaction transaction = transactions.get(position);
        
        // Show all products in the transaction
        holder.tvProductName.setText(transaction.getProductName());
        holder.tvQuantity.setText("Total Items: " + transaction.getQuantity());
        holder.tvTotal.setText("$" + String.format("%.2f", transaction.getTotal()));
        holder.tvDate.setText("📅 " + transaction.getDate());
        holder.tvCustomer.setText("👤 " + transaction.getCustomerName() + " | 📞 " + transaction.getCustomerPhone());
        
        // Color coding
        holder.tvTotal.setTextColor(Color.parseColor("#4CAF50")); // Green for money
        holder.tvProductName.setTextColor(Color.parseColor("#212121")); // Dark text
        holder.tvCustomer.setTextColor(Color.parseColor("#666666")); // Gray for customer info

        // Long press to delete
        holder.itemView.setOnLongClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteTransaction(transaction.getId(), position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvTotal, tvDate, tvCustomer;

        ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
        }
    }
}

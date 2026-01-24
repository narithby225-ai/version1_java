package com.narith.aims.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.narith.aims.R;
import com.narith.aims.model.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categoryList;
    private OnCategoryAction listener;

    // Interface សម្រាប់ចាប់យកសកម្មភាព Edit និង Delete
    public interface OnCategoryAction {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    public CategoryAdapter(List<Category> list, OnCategoryAction listener) {
        this.categoryList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // បញ្ចូល Layout item_category.xml ដែលយើងបានរចនា
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category cat = categoryList.get(position);

        // បង្ហាញឈ្មោះប្រភេទទៅកាន់ TextView
        holder.tvName.setText(cat.getName());

        // កំណត់សកម្មភាពនៅពេលចុចប៊ូតុង Edit
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(cat);
            }
        });

        // កំណត់សកម្មភាពនៅពេលចុចប៊ូតុង Delete
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(cat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // ភ្ជាប់ ID ឱ្យត្រូវតាម item_category.xml
            tvName = itemView.findViewById(R.id.tvCategoryName);
            btnEdit = itemView.findViewById(R.id.btnEditCat);
            btnDelete = itemView.findViewById(R.id.btnDeleteCat);
        }
    }
}
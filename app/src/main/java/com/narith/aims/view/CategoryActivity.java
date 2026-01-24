package com.narith.aims.view;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.narith.aims.R;
import com.narith.aims.database.DatabaseHelper;
import com.narith.aims.model.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private CategoryAdapter adapter;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        dbHelper = new DatabaseHelper(this);

        // ១. ភ្ជាប់ View និងរៀបចំ RecyclerView
        recyclerView = findViewById(R.id.rvCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ២. កំណត់មុខងារឱ្យប៊ូតុងបន្ថែម (+)
        FloatingActionButton fab = findViewById(R.id.fabAddCategory);
        if (fab != null) {
            fab.setOnClickListener(v -> showCategoryDialog(null));
        }

        loadCategories();
    }

    /**
     * ទាញទិន្នន័យពីតារាង categories មកបង្ហាញក្នុងបញ្ជី
     */
    private void loadCategories() {
        categoryList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES, null);
        if (cursor.moveToFirst()) {
            do {
                categoryList.add(new Category(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // រៀបចំ Adapter ជាមួយ Action (Edit & Delete)
        adapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnCategoryAction() {
            @Override
            public void onEdit(Category category) {
                showCategoryDialog(category);
            }

            @Override
            public void onDelete(Category category) {
                showDeleteConfirmation(category);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * បង្ហាញ Dialog សម្រាប់បញ្ចូលឈ្មោះប្រភេទ (ទាំង Add និង Edit)
     */
    private void showCategoryDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(category == null ? "បន្ថែមប្រភេទថ្មី" : "កែប្រែប្រភេទ");

        final EditText input = new EditText(this);
        input.setHint("ឈ្មោះប្រភេទទំនិញ");
        if (category != null) {
            input.setText(category.getName());
        }
        builder.setView(input);

        builder.setPositiveButton("រក្សាទុក", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                if (category == null) {
                    saveCategory(name);
                } else {
                    updateCategory(category.getId(), name);
                }
            } else {
                Toast.makeText(this, "សូមបញ្ចូលឈ្មោះប្រភេទ!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("បោះបង់", null);
        builder.show();
    }

    private void saveCategory(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CAT_NAME, name);
        try {
            db.insertOrThrow(DatabaseHelper.TABLE_CATEGORIES, null, values);
            Toast.makeText(this, "បន្ថែមបានជោគជ័យ!", Toast.LENGTH_SHORT).show();
            loadCategories();
        } catch (Exception e) {
            Toast.makeText(this, "ឈ្មោះប្រភេទនេះមានរួចហើយ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCategory(int id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CAT_NAME, name);
        db.update(DatabaseHelper.TABLE_CATEGORIES, values, "id=?", new String[]{String.valueOf(id)});
        Toast.makeText(this, "កែប្រែបានជោគជ័យ!", Toast.LENGTH_SHORT).show();
        loadCategories();
    }

    private void showDeleteConfirmation(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("លុបទិន្នន័យ")
                .setMessage("តើអ្នកប្រាកដថាចង់លុបប្រភេទ '" + category.getName() + "' ឬទេ?")
                .setPositiveButton("លុប", (dialog, which) -> deleteCategory(category.getId()))
                .setNegativeButton("បោះបង់", null)
                .show();
    }

    private void deleteCategory(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_CATEGORIES, "id=?", new String[]{String.valueOf(id)});
        loadCategories();
        Toast.makeText(this, "បានលុបប្រភេទជោគជ័យ!", Toast.LENGTH_SHORT).show();
    }
}
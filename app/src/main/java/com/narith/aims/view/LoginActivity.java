package com.narith.aims.view;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.narith.aims.R;
import com.narith.aims.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_login);
            Log.d(TAG, "Layout set successfully");

            // Initialize DatabaseHelper with error handling
            try {
                dbHelper = new DatabaseHelper(this);
                Log.d(TAG, "DatabaseHelper initialized");
            } catch (Exception e) {
                Log.e(TAG, "Error initializing DatabaseHelper", e);
                Toast.makeText(this, "Database initialization failed", Toast.LENGTH_SHORT).show();
            }

            // Find views with null checks
            EditText etUsername = findViewById(R.id.et_username);
            EditText etPassword = findViewById(R.id.et_password);
            Button btnLogin = findViewById(R.id.btn_login);

            if (etUsername == null || etPassword == null || btnLogin == null) {
                Log.e(TAG, "One or more views not found");
                Toast.makeText(this, "Layout error", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setOnClickListener(v -> {
                try {
                    String username = etUsername.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (username.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    checkLogin(username, password);
                } catch (Exception e) {
                    Log.e(TAG, "Error in login click", e);
                    Toast.makeText(this, "Login error", Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Activity initialization failed", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check login credentials in database
     */
    private void checkLogin(String user, String pass) {
        try {
            if (dbHelper == null) {
                Toast.makeText(this, "Database not initialized", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String query = "SELECT " + DatabaseHelper.COLUMN_ROLE +
                    " FROM " + DatabaseHelper.TABLE_USERS +
                    " WHERE " + DatabaseHelper.COLUMN_USERNAME + "=? AND " +
                    DatabaseHelper.COLUMN_PASSWORD + "=?";

            Cursor cursor = db.rawQuery(query, new String[]{user, pass});

            if (cursor.moveToFirst()) {
                int roleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ROLE);
                String role = (roleIndex != -1) ? cursor.getString(roleIndex) : "user";
                cursor.close();

                Toast.makeText(this, "Login successful! (" + role + ")", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("USER_ROLE", role);
                intent.putExtra("USERNAME", user);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                cursor.close();
                Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking login", e);
            Toast.makeText(this, "Login check failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            try {
                dbHelper.close();
            } catch (Exception e) {
                Log.e(TAG, "Error closing database", e);
            }
        }
    }
}
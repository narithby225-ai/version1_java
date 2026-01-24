package com.narith.aims.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.narith.aims.R;

/**
 * SIMPLIFIED SPLASH SCREEN
 * Minimal implementation to prevent crashes
 * Just shows layout for 1.5 seconds then goes to login
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1500; // 1.5 seconds
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            // Set content view
            setContentView(R.layout.activity_splash);
            
            // Simple delay then navigate
            handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToLogin();
                }
            }, SPLASH_DURATION);
            
        } catch (Exception e) {
            e.printStackTrace();
            // If anything fails, go directly to login
            goToLogin();
        }
    }

    private void goToLogin() {
        try {
            if (!isFinishing() && !isDestroyed()) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onBackPressed() {
        // Disable back button
    }
}

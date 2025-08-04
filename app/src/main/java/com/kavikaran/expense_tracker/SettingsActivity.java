package com.kavikaran.expense_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    ImageButton backButton;
    TextView categoryManagement, themeManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backButton = findViewById(R.id.backButton);
        categoryManagement = findViewById(R.id.categoryManagement);
        themeManagement = findViewById(R.id.themeManagement);

        // Back to home
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Category Management
        categoryManagement.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, CategoryManagement.class);
            startActivity(intent);
        });

        // Theme Management
        themeManagement.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ThemeManagement.class);
            startActivity(intent);
        });
    }

    // Handle physical back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

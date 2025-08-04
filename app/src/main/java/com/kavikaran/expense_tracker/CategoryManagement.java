package com.kavikaran.expense_tracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kavikaran.expense_tracker.database.DatabaseHelper;
import com.kavikaran.expense_tracker.model.Category;


public class CategoryManagement extends AppCompatActivity {

    ImageButton backButton, addCategoryButton;
    ListView categoryListView;
    ArrayAdapter<String> adapter;
    DatabaseHelper dbHelper;
    List<Category> categoryList;

    private FirebaseAuth mAuth;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        backButton = findViewById(R.id.backButton);
        addCategoryButton = findViewById(R.id.addCategoryButton);
        categoryListView = findViewById(R.id.categoryListView);

        dbHelper = new DatabaseHelper(this);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        }

        // Load existing categories
        loadCategories();

        // Back button
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryManagement.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });

        // Add category button
        addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void loadCategories() {
        try {
            categoryList = dbHelper.getAllCategories(userEmail);

            if (categoryList == null || categoryList.isEmpty()) {
                Toast.makeText(this, "No categories found. Please add one.", Toast.LENGTH_SHORT).show();
                categoryListView.setAdapter(null); // Optional: clear old data
                return;
            }

            List<String> names = new ArrayList<>();
            for (Category cat : categoryList) {
                names.add(cat.getCategoryName());
            }

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
            categoryListView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading categories: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Category");

        final EditText input = new EditText(this);
        input.setHint("Enter category name");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String categoryName = input.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                dbHelper.insertCategory(userEmail,categoryName);
                loadCategories(); // Refresh list
                Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CategoryManagement.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}

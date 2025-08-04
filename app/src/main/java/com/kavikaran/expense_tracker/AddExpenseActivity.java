package com.kavikaran.expense_tracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kavikaran.expense_tracker.database.DatabaseHelper;
import com.kavikaran.expense_tracker.model.Category;

public class AddExpenseActivity extends AppCompatActivity {

    EditText titleInput, amountInput, locationInput, dateInput;
    Spinner categorySpinner;
    Button saveExpenseBtn;
    ImageButton backButton;
    DatabaseHelper dbHelper;
    List<Category> categoryList;
    String selectedCategory;
    String selectedDate;

    private FirebaseAuth mAuth;
    private String userEmail;

    // Location picker request code
    private static final int LOCATION_PICKER_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        titleInput = findViewById(R.id.titleInput);
        amountInput = findViewById(R.id.amountInput);
        locationInput = findViewById(R.id.locationInput);
        dateInput = findViewById(R.id.dateInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        saveExpenseBtn = findViewById(R.id.saveExpenseBtn);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(AddExpenseActivity.this, ExpensesActivity.class));
            finish();
        });
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        }


        dbHelper = new DatabaseHelper(this);
        categoryList = dbHelper.getAllCategories(userEmail);

        List<String> categoryNames = new java.util.ArrayList<>();
        for (Category c : categoryList) categoryNames.add(c.getCategoryName());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedCategory = parent.getItemAtPosition(pos).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Date Picker logic
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateInput.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                calendar.set(year1, month1, dayOfMonth);
                selectedDate = sdf.format(calendar.getTime());
                dateInput.setText(selectedDate);
            }, year, month, day);
            dialog.show();
        });

        // Location picker click listener
        locationInput.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapLocationPickerActivity.class);
            startActivityForResult(intent, LOCATION_PICKER_REQUEST_CODE);
        });

        saveExpenseBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String location = locationInput.getText().toString();
            String amountText = amountInput.getText().toString();

            if (title.isEmpty() || amountText.isEmpty() || selectedDate == null || selectedCategory == null) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Integer.parseInt(amountText);
            long result = dbHelper.insertExpense(userEmail,title, selectedCategory, amount, selectedDate, location);

            if (result != -1) {
                Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle the result from MapLocationPickerActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String locationName = data.getStringExtra("location_name");
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);

                // Set the location name in the EditText
                locationInput.setText(locationName);

                // Optional: You can store latitude and longitude for future use if needed
                // For now, we're just storing the location name as requested
            }
        }
    }
}
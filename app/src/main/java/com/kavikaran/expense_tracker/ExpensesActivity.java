package com.kavikaran.expense_tracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kavikaran.expense_tracker.database.DatabaseHelper;
import com.kavikaran.expense_tracker.model.Category;
import com.kavikaran.expense_tracker.model.Expense;

public class ExpensesActivity extends AppCompatActivity {

    ImageButton backButton, addExpenseBtn;
    TextView totalExpenseText, noExpensesText;
    ListView expenseListView;
    EditText searchInput, startDateInput, endDateInput, minAmountInput, maxAmountInput;
    Spinner categoryFilterSpinner;
    Button applyFiltersBtn, clearFiltersBtn;
    LinearLayout filtersLayout;
    ImageButton toggleFiltersBtn;

    DatabaseHelper dbHelper;
    List<Expense> allExpenses;
    List<Expense> filteredExpenses;
    List<Category> categoryList;
    ExpenseAdapter expenseAdapter;

    private boolean filtersVisible = false;
    private String selectedFilterCategory = "All Categories";

    private String userEmail;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
            }

        initializeViews();
        setupDatabase();
        setupSearchFunctionality();
        setupFilterFunctionality();
        setupClickListeners();
        loadExpenses();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        addExpenseBtn = findViewById(R.id.addExpenseBtn);
        totalExpenseText = findViewById(R.id.totalExpenseText);
        expenseListView = findViewById(R.id.expenseListView);
        noExpensesText = findViewById(R.id.noExpensesText);
        searchInput = findViewById(R.id.searchInput);
        toggleFiltersBtn = findViewById(R.id.toggleFiltersBtn);
        filtersLayout = findViewById(R.id.filtersLayout);
        startDateInput = findViewById(R.id.startDateInput);
        endDateInput = findViewById(R.id.endDateInput);
        minAmountInput = findViewById(R.id.minAmountInput);
        maxAmountInput = findViewById(R.id.maxAmountInput);
        categoryFilterSpinner = findViewById(R.id.categoryFilterSpinner);
        applyFiltersBtn = findViewById(R.id.applyFiltersBtn);
        clearFiltersBtn = findViewById(R.id.clearFiltersBtn);
    }

    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        categoryList = dbHelper.getAllCategories(userEmail);
        allExpenses = new ArrayList<>();
        filteredExpenses = new ArrayList<>();
    }

    private void setupSearchFunctionality() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterFunctionality() {
        // Setup category filter spinner
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("All Categories");
        for (Category c : categoryList) {
            categoryNames.add(c.getCategoryName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFilterSpinner.setAdapter(spinnerAdapter);

        categoryFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilterCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Setup date pickers
        setupDatePicker(startDateInput);
        setupDatePicker(endDateInput);
    }

    private void setupDatePicker(EditText dateInput) {
        dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                calendar.set(year1, month1, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                dateInput.setText(sdf.format(calendar.getTime()));
            }, year, month, day);
            dialog.show();
        });
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        addExpenseBtn.setOnClickListener(v -> {
            startActivity(new Intent(ExpensesActivity.this, AddExpenseActivity.class));
        });

        toggleFiltersBtn.setOnClickListener(v -> toggleFilters());

        applyFiltersBtn.setOnClickListener(v -> applyFilters());

        clearFiltersBtn.setOnClickListener(v -> clearFilters());

        // Setup long click for delete functionality
        expenseListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Expense expense = filteredExpenses.get(position);
            showDeleteConfirmationDialog(expense);
            return true;
        });

        // Setup regular click for view/edit functionality
        expenseListView.setOnItemClickListener((parent, view, position, id) -> {
            Expense expense = filteredExpenses.get(position);
            showExpenseDetailsDialog(expense);
        });
    }

    private void toggleFilters() {
        filtersVisible = !filtersVisible;
        filtersLayout.setVisibility(filtersVisible ? View.VISIBLE : View.GONE);
        toggleFiltersBtn.setImageResource(filtersVisible ?
                android.R.drawable.ic_menu_close_clear_cancel :
                android.R.drawable.ic_menu_sort_by_size);
    }

    private void loadExpenses() {
        try {
            allExpenses = dbHelper.getAllExpenses(userEmail);
            filteredExpenses = new ArrayList<>(allExpenses);

            if (allExpenses == null || allExpenses.isEmpty()) {
                Toast.makeText(this, "No expenses found", Toast.LENGTH_SHORT).show();
                expenseListView.setAdapter(null);
            }

            else if (expenseAdapter == null) {
                expenseAdapter = new ExpenseAdapter(this, filteredExpenses);
                expenseListView.setAdapter(expenseAdapter);
            } else {
                expenseAdapter.updateExpenses(filteredExpenses);
            }

            updateUI();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading expenses: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void applyFilters() {
        String searchQuery = searchInput.getText().toString().toLowerCase().trim();
        String startDate = startDateInput.getText().toString().trim();
        String endDate = endDateInput.getText().toString().trim();
        String minAmountStr = minAmountInput.getText().toString().trim();
        String maxAmountStr = maxAmountInput.getText().toString().trim();

        filteredExpenses.clear();

        for (Expense expense : allExpenses) {
            if (matchesAllFilters(expense, searchQuery, startDate, endDate, minAmountStr, maxAmountStr)) {
                filteredExpenses.add(expense);
            }
        }

        expenseAdapter.updateExpenses(filteredExpenses);
        updateUI();
    }

    private boolean matchesAllFilters(Expense expense, String searchQuery, String startDate,
                                      String endDate, String minAmountStr, String maxAmountStr) {
        // Search filter
        boolean matchesSearch = searchQuery.isEmpty() ||
                expense.getTitle().toLowerCase().contains(searchQuery);

        // Category filter
        boolean matchesCategory = selectedFilterCategory.equals("All Categories") ||
                expense.getCategory().equals(selectedFilterCategory);

        // Date range filter
        boolean matchesDateRange = matchesDateRange(expense.getDate(), startDate, endDate);

        // Amount range filter
        boolean matchesAmountRange = matchesAmountRange(expense.getAmount(), minAmountStr, maxAmountStr);

        return matchesSearch && matchesCategory && matchesDateRange && matchesAmountRange;
    }

    private boolean matchesDateRange(String expenseDate, String startDate, String endDate) {
        if (startDate.isEmpty() && endDate.isEmpty()) return true;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date expenseDateTime = sdf.parse(expenseDate);

            if (!startDate.isEmpty()) {
                Date startDateTime = sdf.parse(startDate);
                if (expenseDateTime.before(startDateTime)) return false;
            }

            if (!endDate.isEmpty()) {
                Date endDateTime = sdf.parse(endDate);
                if (expenseDateTime.after(endDateTime)) return false;
            }

            return true;
        } catch (ParseException e) {
            return true; // If parsing fails, don't filter by date
        }
    }

    private boolean matchesAmountRange(double expenseAmount, String minAmountStr, String maxAmountStr) {
        try {
            if (!minAmountStr.isEmpty()) {
                int minAmount = Integer.parseInt(minAmountStr);
                if (expenseAmount < minAmount) return false;
            }

            if (!maxAmountStr.isEmpty()) {
                int maxAmount = Integer.parseInt(maxAmountStr);
                if (expenseAmount > maxAmount) return false;
            }

            return true;
        } catch (NumberFormatException e) {
            return true; // If parsing fails, don't filter by amount
        }
    }

    private void clearFilters() {
        searchInput.setText("");
        startDateInput.setText("");
        endDateInput.setText("");
        minAmountInput.setText("");
        maxAmountInput.setText("");
        categoryFilterSpinner.setSelection(0);
        selectedFilterCategory = "All Categories";

        filteredExpenses.clear();
        filteredExpenses.addAll(allExpenses);
        expenseAdapter.notifyDataSetChanged();
        updateUI();
    }

    private void updateUI() {
        updateTotalExpense();
        updateEmptyState();
    }

    private void updateTotalExpense() {
        int total = 0;
        for (Expense expense : filteredExpenses) {
            total += expense.getAmount();
        }
        totalExpenseText.setText("Total Expenses: Rs. " + total);
    }

    private void updateEmptyState() {
        if (filteredExpenses.isEmpty()) {
            expenseListView.setVisibility(View.GONE);
            noExpensesText.setVisibility(View.VISIBLE);
            if (allExpenses.isEmpty()) {
                noExpensesText.setText("No expenses found.\nTap + to add your first expense!");
            } else {
                noExpensesText.setText("No expenses match your filters.\nTry adjusting the search criteria.");
            }
        } else {
            expenseListView.setVisibility(View.VISIBLE);
            noExpensesText.setVisibility(View.GONE);
        }
    }

    private void showExpenseDetailsDialog(Expense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(expense.getTitle());

        String details = "Category: " + expense.getCategory() + "\n" +
                "Amount: Rs. " + expense.getAmount() + "\n" +
                "Date: " + expense.getDate();

        if (expense.getLocation() != null && !expense.getLocation().isEmpty()) {
            details += "\nLocation: " + expense.getLocation();
        }

        builder.setMessage(details);
        builder.setPositiveButton("OK", null);
        builder.setNeutralButton("Edit", (dialog, which) -> {
            // TODO: Implement edit functionality
            Toast.makeText(this, "Edit functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void showDeleteConfirmationDialog(Expense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Expense");
        builder.setMessage("Are you sure you want to delete \"" + expense.getTitle() + "\"?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            if (dbHelper.deleteExpense(expense.getId())) {
                Toast.makeText(this, "Expense deleted successfully", Toast.LENGTH_SHORT).show();
                loadExpenses(); // Reload the list
            } else {
                Toast.makeText(this, "Failed to delete expense", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses(); // Reload expenses when returning from AddExpenseActivity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
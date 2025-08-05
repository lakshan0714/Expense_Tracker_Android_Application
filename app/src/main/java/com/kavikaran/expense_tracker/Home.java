package com.kavikaran.expense_tracker;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kavikaran.expense_tracker.database.DatabaseHelper;
import com.kavikaran.expense_tracker.model.Expense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {

    private CardView expensesCard, settingsCard;
    private TextView welcomeText,userEmailText;

    private ImageButton logoutIcon;

    private FirebaseAuth mAuth;

    private PieChart pieChart;

    private String email;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initialize Pie chart
        pieChart=findViewById(R.id.pieChart);

        dbHelper = new DatabaseHelper(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            email = currentUser.getEmail();}




        // Initialize views
        initializeViews();

        //Initialize pie chart
        showExpenseChart();

        // Setup user info
        setupUserInfo();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        expensesCard = findViewById(R.id.expenses_card);
//        dashboardCard = findViewById(R.id.dashboard_card);
        settingsCard = findViewById(R.id.settings_card);
        welcomeText = findViewById(R.id.welcome_text);
        userEmailText = findViewById(R.id.user_email_text);
        logoutIcon = findViewById(R.id.logoutIcon);


    }
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Home.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears the activity stack
        startActivity(intent);
        finish();
    }

    private void setupUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            String displayName = currentUser.getDisplayName();
            Log.d(TAG, "Email after login..."+email);

            if (displayName != null && !displayName.isEmpty()) {
                welcomeText.setText("Welcome back, " + displayName + "!");
            } else {
                welcomeText.setText("Welcome back!");
            }

            if (email != null) {
                userEmailText.setText(email);
            }
        }
    }


    private void showExpenseChart() {
        List<Expense> expenseList = dbHelper.getAllExpenses(email);
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Expense expense : expenseList) {
            String category = expense.getCategory();
            double amount = expense.getAmount();

            if (categoryTotals.containsKey(category)) {
                categoryTotals.put(category, categoryTotals.get(category) + amount);
            } else {
                categoryTotals.put(category, amount);
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");
        dataSet.setColors(Color.parseColor("#7C4DFF"), Color.parseColor("#536DFE"), Color.parseColor("#448AFF"), Color.parseColor("#FF4081"), Color.parseColor("#D500F9"), Color.parseColor("#00B0FF"));
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.parseColor("#1D1D55"));

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(58f);
        pieChart.setCenterText("Expenses");
        pieChart.setCenterTextColor(Color.parseColor("#333333"));
        pieChart.setCenterTextSize(18f);
        pieChart.animateY(1400);
        pieChart.getLegend().setEnabled(false);

        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate(); // Refresh chart
    }


    private void setupClickListeners() {
        logoutIcon.setOnClickListener(v -> logoutUser());
        expensesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Expenses Activity
                Intent intent = new Intent(Home.this, ExpensesActivity.class);
                startActivity(intent);
            }
        });

//        dashboardCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to Dashboard Activity
//                Intent intent = new Intent(Home.this, DashboardActivity.class);
//                startActivity(intent);
//            }
//        });

        settingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Settings Activity
                Intent intent = new Intent(Home.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        showExpenseChart(); // Refresh the pie chart every time Home screen is visible
    }

//    @Override
//    public void onBackPressed() {
//        // Override back button to prevent going back to login
//        super.onBackPressed();
//        finishAffinity(); // Close all activities
//    }
}
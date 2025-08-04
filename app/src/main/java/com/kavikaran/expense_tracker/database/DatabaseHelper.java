package com.kavikaran.expense_tracker.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kavikaran.expense_tracker.model.Category;
import com.kavikaran.expense_tracker.model.Expense;


import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "ExpenseTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_EXPENSES = "expenses";

    // Category Table Columns
    private static final String KEY_CATEGORY_ID = "id";
    private static final String KEY_USER_MAIL= "email";
    private static final String KEY_CATEGORY_NAME = "category_name";




    // Expenses Table Columns
    private static final String KEY_EXPENSE_ID = "id";
    private static final String KEY_USER = "email";
    private static final String KEY_EXPENSE_Title = "Title";
    private static final String KEY_EXPENSE_CATEGORY = "category";
    private static final String KEY_EXPENSE_AMOUNT = "amount";
    private static final String KEY_EXPENSE_DATE = "date";
    private static final String KEY_EXPENSE_LOCATION = "location";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Category Table
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_MAIL + " TEXT NOT NULL,"
                + KEY_CATEGORY_NAME + " TEXT NOT NULL" + ")";


        // Create Expenses Table
        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + KEY_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER + " TEXT NOT NULL,"
                + KEY_EXPENSE_Title + " TEXT NOT NULL,"
                + KEY_EXPENSE_CATEGORY + " TEXT NOT NULL,"
                + KEY_EXPENSE_AMOUNT + " REAL NOT NULL,"
                + KEY_EXPENSE_DATE + " TEXT NOT NULL,"
                + KEY_EXPENSE_LOCATION + " TEXT NOT NULL" + ")";

        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_EXPENSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    // ==================== CATEGORY OPERATIONS ====================

    // Insert Category
    public long insertCategory(String email,String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_MAIL, email);
        values.put(KEY_CATEGORY_NAME, categoryName);

        long id = db.insert(TABLE_CATEGORY, null, values);
        db.close();
        return id;
    }

    // Get All Categories
    public List<Category> getAllCategories(String email) {
        List<Category> categoryList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY + " WHERE email = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(0));
                category.setEmail(cursor.getString(1));
                category.setCategoryName(cursor.getString(2));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return categoryList;
    }




    // Delete Category
    public void deleteCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORY, KEY_CATEGORY_ID + " = ?",
                new String[] { String.valueOf(category.getId()) });
        db.close();
    }





    // ==================== EXPENSE OPERATIONS ====================

    // Insert Expense
    public long insertExpense(String email,String Title,String category, double amount, String date, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER, email);
        values.put(KEY_EXPENSE_Title, Title);
        values.put(KEY_EXPENSE_CATEGORY, category);
        values.put(KEY_EXPENSE_AMOUNT, amount);
        values.put(KEY_EXPENSE_DATE, date);
        values.put(KEY_EXPENSE_LOCATION, location);

        long id = db.insert(TABLE_EXPENSES, null, values);
        db.close();
        return id;
    }


    // Get All Expenses
    public List<Expense> getAllExpenses(String email) {
        List<Expense> expenseList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSES + " WHERE email = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(0));
                expense.setEmail(cursor.getString(1));
                expense.setTitle(cursor.getString(2));
                expense.setCategory(cursor.getString(3));
                expense.setAmount(cursor.getDouble(4));
                expense.setDate(cursor.getString(5));
                expense.setLocation(cursor.getString(6));
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenseList;
    }



    // Get Expenses by Category
    public List<Expense> getExpensesByCategory(String category) {
        List<Expense> expenseList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSES + " WHERE " + KEY_EXPENSE_CATEGORY + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(0));
                expense.setTitle(cursor.getString(1));
                expense.setCategory(cursor.getString(2));
                expense.setAmount(cursor.getDouble(3));
                expense.setDate(cursor.getString(4));
                expense.setLocation(cursor.getString(5));
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenseList;
    }


    // Update Expense
    public int updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXPENSE_Title, expense.getTitle());
        values.put(KEY_EXPENSE_CATEGORY, expense.getCategory());
        values.put(KEY_EXPENSE_AMOUNT, expense.getAmount());
        values.put(KEY_EXPENSE_DATE, expense.getDate());
        values.put(KEY_EXPENSE_LOCATION, expense.getLocation());

        int result = db.update(TABLE_EXPENSES, values, KEY_EXPENSE_ID + " = ?",
                new String[] { String.valueOf(expense.getId()) });
        db.close();
        return result;
    }

    // Delete Expense
    public void deleteExpense(Expense expense) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_EXPENSES, KEY_EXPENSE_ID + " = ?",
                new String[] { String.valueOf(expense.getId()) });
        db.close();
    }


    // Get Total Expenses Amount
    public double getTotalExpenses() {
        String selectQuery = "SELECT SUM(" + KEY_EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        return total;
    }



// Filter Expenses Methods
    // Method to get expenses by date range
    public List<Expense> getExpensesByDateRange(String email,String startDate, String endDate) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM expenses WHERE date BETWEEN ? AND ? AND email = ?ORDER BY date DESC";
        Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate,email});

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("Title"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                double amount = cursor.getInt(cursor.getColumnIndexOrThrow("amount"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));

                expenses.add(new Expense(id,title, category, amount, date, location));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenses;
    }




    // Method to search expenses by title
    public List<Expense> searchExpensesByTitle(String email,String searchQuery) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM expenses WHERE title LIKE ? AND email = ?ORDER BY date DESC";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchQuery + "%",email});

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("Title"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                double amount = cursor.getInt(cursor.getColumnIndexOrThrow("amount"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));

                expenses.add(new Expense(id, title, category, amount, date, location));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenses;
    }

    // Method to get total expense amount
    public int getTotalExpenseAmount(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        int total = 0;

        String query = "SELECT SUM(amount) as total FROM expenses WHERE email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        db.close();
        return total;
    }



    // Method to delete an expense
    public boolean deleteExpense(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("expenses", "id = ?", new String[]{String.valueOf(expenseId)});
        db.close();
        return result > 0;
    }

    // Method to update an expense
    public boolean updateExpense(int id, String title, String category, int amount, String date, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("Title", title);
        values.put("category", category);
        values.put("amount", amount);
        values.put("date", date);
        values.put("location", location);

        int result = db.update("expenses", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;

    }}


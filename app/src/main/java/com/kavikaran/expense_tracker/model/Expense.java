package com.kavikaran.expense_tracker.model;


public class Expense {
    private int id;

    private String email;

    private String Title;

    private String category;
    private double amount;
    private String date;
    private String location;

    // Default constructor
    public Expense() {
    }

    // Constructor with parameters (without id)
    public Expense(String email,String Title,String category, double amount, String date, String location) {
        this.email=email;
        this.Title = Title;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.location = location;
    }

    // Constructor with all parameters
    public Expense(int id,String email,String Title, String category, double amount, String date, String location) {
        this.email=email;
        this.id = id;
        this.Title = Title;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.location = location;
    }

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return Title;
    }

    public  void setEmail(String email){this.email=email;}
    public  String  getEmail(){return  email;}

    public void setTitle(String Title) {
        this.Title = Title;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
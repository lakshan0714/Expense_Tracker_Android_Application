package com.kavikaran.expense_tracker.model;



public class Category {
    private int id;

    private String email;
    private String categoryName;

    // Default constructor
    public Category() {
    }

    // Constructor with parameters
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    // Constructor with all parameters
    public Category(int id, String email, String categoryName) {
        this.id = id;
        this.email=email;
        this.categoryName = categoryName;
    }

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getemail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                "email=" + email +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
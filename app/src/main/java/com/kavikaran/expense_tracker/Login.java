package com.kavikaran.expense_tracker;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kavikaran.expense_tracker.database.DatabaseHelper;

public class Login extends AppCompatActivity {

    private DatabaseHelper db;
    private static final String TAG = "Login";
    TextView textView;
    Button buttonlogin;

    TextInputLayout editTextEmail,editTextPassword;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView=findViewById(R.id.login_click);
        buttonlogin=findViewById(R.id.btn_login);
        editTextEmail=findViewById(R.id.email);
        editTextPassword=findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();

        initializeDatabase();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,Signup.class);
                startActivity(intent);
                finish();
            }
        });

        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Login button clicked");

                String email = editTextEmail.getEditText().getText().toString().trim();
                String password = editTextPassword.getEditText().getText().toString().trim();

                Log.d(TAG, "Email entered: " + email);
                Log.d(TAG, "Password entered: " + (password.isEmpty() ? "EMPTY" : "********"));

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Email field is empty");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Password field is empty");
                    return;
                }

                Log.d(TAG, "Attempting to authenticate user with Firebase");

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, navigate to Home page
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                                    // Navigate to Home Activity
                                    Intent intent = new Intent(Login.this, Home.class);
                                    Log.d(TAG,"Intent loaded successfully");
                                    startActivity(intent);
                                    finish(); // Close login activity

                                } else {
                                    // If sign in fails, display a message to the user
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void initializeDatabase() {
        try {
            Log.d(TAG, "Initializing database...");

            // Create database instance
            db = new DatabaseHelper(this);

            // Force database creation by trying to read from it
            db.getReadableDatabase();

            Log.d(TAG, "Database initialized successfully");



        } catch (Exception e) {
            Log.e(TAG, "Error initializing database: " + e.getMessage());
            Toast.makeText(this, "Database initialization failed", Toast.LENGTH_SHORT).show();
        }
    }
}
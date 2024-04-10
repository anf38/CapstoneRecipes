package com.example.pantrypal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    private TextInputEditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;
    private TextView usernameError, emailError, passwordError;
    private Button buttonReg;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView textView;
    private CheckBox mCheckBox;

    @Override
    public void onStart() { //if the user is already logged in, then it will bring them to the main page
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirmPassword);
        usernameError = findViewById(R.id.usernameError);
        emailError = findViewById(R.id.emailError);
        passwordError = findViewById(R.id.passwordError);
        buttonReg = findViewById(R.id.RegisterBtn);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        mCheckBox = findViewById(R.id.passCheckBox);


        //sends a true or false signal if something is being keyed in for username
        editTextUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });

// Set a key listener to intercept the user from entering a newline for username
        editTextUsername.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Consume the enter key event to prevent newline insertion
                return keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN;
            }
        });

        editTextEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // Consume the enter key event to prevent newline insertion
                    return true;
                }
                return false;
            }
        });

        editTextEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // Consume the enter key event to prevent newline insertion
                    return true;
                }
                return false;
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String username, email, password, confirmPassword;
                username = String.valueOf(editTextUsername.getText());
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText()); //same as -> password = editTextPassword.getText().toString();
                confirmPassword = String.valueOf(editTextConfirmPassword.getText());

                if (TextUtils.isEmpty(username)) {
                    usernameError.setVisibility(View.VISIBLE); // Make the TextView visible
                    usernameError.setText("Enter username");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailError.setVisibility(View.VISIBLE); // Make the TextView visible
                    emailError.setText("Enter email");
                    progressBar.setVisibility(View.GONE);

                    return;
                } else if (!email.contains("@")) {
                    emailError.setVisibility(View.VISIBLE);
                    emailError.setText("Email must contain @ symbol");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else {
                    emailError.setText(null);
                    emailError.setVisibility(View.GONE);

                }

                if (TextUtils.isEmpty(password)) {
                    passwordError.setVisibility(View.VISIBLE);
                    passwordError.setText("Enter password");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (password.length() < 8) {
                    passwordError.setVisibility(View.VISIBLE);
                    passwordError.setText("Password must be at least 8 characters long");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (password.contains(" ")) {
                    passwordError.setVisibility(View.VISIBLE);
                    passwordError.setText("Password must not contain spaces");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!password.matches(".*\\d.*")) {
                    passwordError.setVisibility(View.VISIBLE);
                    passwordError.setText("Password must contain at least one number");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!password.matches(".*[!@#$%^&*].*")) {
                    passwordError.setVisibility(View.VISIBLE);
                    passwordError.setText("Password must contain at least one special character");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!password.equals(confirmPassword)) {
                    passwordError.setVisibility(View.VISIBLE);
                    passwordError.setText("Passwords must match");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            String username = editTextUsername.getText().toString();

                            // Get reference to Firestore instance
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Reference to the "Users" collection in Firestore
                            DocumentReference userRef = db.collection("Users").document(uid);

                            // Create a map to hold the user data
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("username", username);
                            Toast.makeText(Registration.this, "Authentication created.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Registration.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                            emailError.setVisibility(View.GONE);
                            passwordError.setText(null);
                            if (!task.isSuccessful()) {
                                emailError.setVisibility(View.VISIBLE);
                                emailError.setText("Invalid email/email is already taken");
                                progressBar.setVisibility(View.GONE);

                            }
                        }
                    }
                });
            }
        });
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //shows the password if checkboxed
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextPassword.setTransformationMethod(null);
                } else {
                    editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
    }
}
package com.example.pantrypal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Registration extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    TextView emailError, passwordError;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    CheckBox mCheckBox;
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
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        emailError = findViewById(R.id.emailError);
        passwordError = findViewById(R.id.passwordError);
        buttonReg = findViewById(R.id.RegisterBtn);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        mCheckBox = findViewById(R.id.passCheckBox);
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
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText()); //same as -> password = editTextPassword.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Registration.this, "Authentication created.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Registration.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                    if (TextUtils.isEmpty(email)) {
                                        emailError.setVisibility(View.VISIBLE); // Make the TextView visible
                                        emailError.setText("Enter email");
                                    } else if(!email.contains("@")){
                                        emailError.setVisibility(View.VISIBLE);
                                        emailError.setText("Email must contain @ symbol");
                                    } else {
                                        emailError.setText(null);
                                        emailError.setVisibility(View.GONE);
                                    }

                                    if (TextUtils.isEmpty(password)) {
                                        passwordError.setVisibility(View.VISIBLE);
                                        passwordError.setText("Enter password");
                                    } else if (password.length() < 8) {
                                        passwordError.setVisibility(View.VISIBLE);
                                        passwordError.setText("Password must be at least 8 characters long");
                                    } else if (password.contains(" ")) {
                                        passwordError.setVisibility(View.VISIBLE);
                                        passwordError.setText("Password must not contain spaces");
                                    }else if (!password.matches(".*\\d.*")) {
                                        passwordError.setVisibility(View.VISIBLE);
                                        passwordError.setText("Password must contain at least one number");
                                    } else if (!password.matches(".*[!@#$%^&*].*")) {
                                        passwordError.setVisibility(View.VISIBLE);
                                        passwordError.setText("Password must contain at least one special character");
                                    }
                                    else {
                                        emailError.setVisibility(View.GONE);
                                        passwordError.setText(null);
                                        if (!task.isSuccessful()) {
                                            emailError.setVisibility(View.VISIBLE);
                                            emailError.setText("Email is already taken");
                                        }
                                    }
                                }
                            }
                        });
            }
        });
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //shows the password if checkboxed
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)  {
                if (isChecked) {
                    editTextPassword.setTransformationMethod(null);
                } else {
                    editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
    }
}
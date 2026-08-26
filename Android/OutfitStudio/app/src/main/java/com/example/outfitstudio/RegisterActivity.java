package com.example.outfitstudio;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword =
                etConfirmPassword.getText().toString();

        // Validate fields
        if (name.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError(
                    "Confirm your password"
            );
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(
                    "Passwords do not match"
            );
            etConfirmPassword.requestFocus();
            return;
        }

        setLoading(true);

        RegisterRequest request =
                new RegisterRequest(
                        name,
                        email,
                        password
                );

        RetrofitClient.getApiService()
                .registerUser(request)
                .enqueue(new Callback<RegisterResponse>() {

                    @Override
                    public void onResponse(
                            Call<RegisterResponse> call,
                            Response<RegisterResponse> response) {

                        setLoading(false);

                        if (response.body() != null) {

                            RegisterResponse result =
                                    response.body();

                            if (result.isSuccess()) {

                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Registration successful",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Log.d(
                                        "REGISTER_API",
                                        "User ID: "
                                                + result.getUserId()
                                );

                                finish();

                            } else {

                                Toast.makeText(
                                        RegisterActivity.this,
                                        result.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        } else {

                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Registration failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<RegisterResponse> call,
                            Throwable t) {

                        setLoading(false);

                        Log.e(
                                "REGISTER_API",
                                "Registration error",
                                t
                        );

                        Toast.makeText(
                                RegisterActivity.this,
                                "Unable to connect to server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void setLoading(boolean loading) {

        progressBar.setVisibility(
                loading
                        ? ProgressBar.VISIBLE
                        : ProgressBar.GONE
        );

        btnRegister.setEnabled(!loading);
    }
}
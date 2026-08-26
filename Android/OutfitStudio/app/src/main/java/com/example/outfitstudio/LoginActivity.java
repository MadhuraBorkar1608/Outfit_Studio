package com.example.outfitstudio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginEmail;
    private EditText etLoginPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

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

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        TextView tvRegister = findViewById(R.id.tvRegister);

        tvRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String email =
                etLoginEmail.getText().toString().trim();

        String password =
                etLoginPassword.getText().toString();

        if (email.isEmpty()) {
            etLoginEmail.setError("Email is required");
            etLoginEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etLoginPassword.setError("Password is required");
            etLoginPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        LoginRequest request =
                new LoginRequest(email, password);

        RetrofitClient.getApiService()
                .loginUser(request)
                .enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(
                            Call<LoginResponse> call,
                            Response<LoginResponse> response) {

                        btnLogin.setEnabled(true);

                        if (response.body() != null) {

                            LoginResponse result =
                                    response.body();

                            if (result.isSuccess()) {

                                Log.d(
                                        "LOGIN_API",
                                        "Login successful. User ID: "
                                                + result.getUserId()
                                );

                                Toast.makeText(
                                        LoginActivity.this,
                                        "Login successful",
                                        Toast.LENGTH_SHORT
                                ).show();

                                // Temporary:
                                // Open HomeActivity after login
                                Intent intent = new Intent(
                                        LoginActivity.this,
                                        HomeActivity.class
                                );

                                intent.putExtra(
                                        "user_id",
                                        result.getUserId()
                                );

                                intent.putExtra(
                                        "user_name",
                                        result.getName()
                                );

                                intent.putExtra(
                                        "user_email",
                                        result.getEmail()
                                );

                                startActivity(intent);
                                finish();

                            } else {

                                Toast.makeText(
                                        LoginActivity.this,
                                        result.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        } else {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<LoginResponse> call,
                            Throwable t) {

                        btnLogin.setEnabled(true);

                        Log.e(
                                "LOGIN_API",
                                "Login connection error",
                                t
                        );

                        Toast.makeText(
                                LoginActivity.this,
                                "Unable to connect to server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
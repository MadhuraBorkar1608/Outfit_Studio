package com.example.outfitstudio;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private int userId;

    private EditText etName;
    private EditText etEmail;
    private EditText etHeight;
    private EditText etWeight;
    private EditText etChest;
    private EditText etWaist;
    private EditText etHip;
    private EditText etPreferredStyle;
    private EditText etPreferredColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

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

        userId = getIntent().getIntExtra("user_id", -1);

        etName = findViewById(R.id.etProfileName);
        etEmail = findViewById(R.id.etProfileEmail);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etChest = findViewById(R.id.etChest);
        etWaist = findViewById(R.id.etWaist);
        etHip = findViewById(R.id.etHip);
        etPreferredStyle = findViewById(R.id.etPreferredStyle);
        etPreferredColors = findViewById(R.id.etPreferredColors);

        Button btnSaveProfile =
                findViewById(R.id.btnSaveProfile);

        // Load profile from backend
        loadProfile();

        // Save button
        btnSaveProfile.setOnClickListener(v -> updateProfile());
    }

    private void loadProfile() {

        if (userId == -1) {

            Toast.makeText(
                    this,
                    "User ID not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        RetrofitClient.getApiService()
                .getProfile(userId)
                .enqueue(new Callback<ProfileResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProfileResponse> call,
                            Response<ProfileResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getProfile() != null) {

                            ProfileResponse.Profile profile =
                                    response.body().getProfile();

                            displayProfile(profile);

                        } else {

                            Toast.makeText(
                                    ProfileActivity.this,
                                    "Failed to load profile",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ProfileResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                ProfileActivity.this,
                                "Unable to connect to server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void displayProfile(
            ProfileResponse.Profile profile) {

        etName.setText(profile.getName());
        etEmail.setText(profile.getEmail());

        if (profile.getHeight() != null) {
            etHeight.setText(
                    String.valueOf(profile.getHeight())
            );
        }

        if (profile.getWeight() != null) {
            etWeight.setText(
                    String.valueOf(profile.getWeight())
            );
        }

        if (profile.getChest() != null) {
            etChest.setText(
                    String.valueOf(profile.getChest())
            );
        }

        if (profile.getWaist() != null) {
            etWaist.setText(
                    String.valueOf(profile.getWaist())
            );
        }

        if (profile.getHip() != null) {
            etHip.setText(
                    String.valueOf(profile.getHip())
            );
        }

        if (profile.getPreferred_style() != null) {
            etPreferredStyle.setText(
                    profile.getPreferred_style()
            );
        }

        if (profile.getPreferred_colors() != null) {
            etPreferredColors.setText(
                    profile.getPreferred_colors()
            );
        }
    }
    private void updateProfile() {

        if (userId == -1) {
            Toast.makeText(
                    this,
                    "User ID not found",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Double height = parseDouble(etHeight);
        Double weight = parseDouble(etWeight);
        Double chest = parseDouble(etChest);
        Double waist = parseDouble(etWaist);
        Double hip = parseDouble(etHip);

        String preferredStyle =
                etPreferredStyle.getText().toString().trim();

        String preferredColors =
                etPreferredColors.getText().toString().trim();

        ProfileUpdateRequest request =
                new ProfileUpdateRequest(
                        height,
                        weight,
                        chest,
                        waist,
                        hip,
                        preferredStyle,
                        preferredColors
                );

        RetrofitClient.getApiService()
                .updateProfile(userId, request)
                .enqueue(new Callback<ProfileUpdateResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProfileUpdateResponse> call,
                            Response<ProfileUpdateResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            Toast.makeText(
                                    ProfileActivity.this,
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            Toast.makeText(
                                    ProfileActivity.this,
                                    "Failed to update profile",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ProfileUpdateResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                ProfileActivity.this,
                                "Unable to connect to server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
    private Double parseDouble(EditText editText) {

        String value =
                editText.getText().toString().trim();

        if (value.isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
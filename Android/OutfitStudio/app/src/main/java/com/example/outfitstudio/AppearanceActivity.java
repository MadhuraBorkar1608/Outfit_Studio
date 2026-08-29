package com.example.outfitstudio;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.Toast;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import androidx.core.content.FileProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AppearanceActivity extends AppCompatActivity {

    private ImageView ivAppearancePreview;

    private Button btnSelectImage;
    private Button btnCamera;
    private Button btnGallery;
    private Button btnAnalyze;

    private ProgressBar progressAppearance;

    private TextView tvFaceShape;
    private TextView tvSkinTone;
    private TextView tvBodyType;

    private int userId;

    private Double chest;
    private Double waist;
    private Double hip;

    private Uri cameraImageUri;
    private Uri selectedImageUri;

    // Gallery image picker
    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri != null) {

                            selectedImageUri = uri;

                            ivAppearancePreview.setImageURI(selectedImageUri);

                            Log.d(
                                    "APPEARANCE_IMAGE",
                                    "Selected image URI: " + selectedImageUri
                            );
                        }
                    }
            );

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    success -> {

                        if (success && cameraImageUri != null) {

                            selectedImageUri = cameraImageUri;

                            ivAppearancePreview.setImageURI(selectedImageUri);

                            Log.d(
                                    "APPEARANCE_CAMERA",
                                    "Camera image captured successfully: "
                                            + selectedImageUri
                            );

                        } else {

                            Log.d(
                                    "APPEARANCE_CAMERA",
                                    "Camera capture cancelled or failed"
                            );
                        }
                    }
            );

    private Uri createCameraImageUri() {

        try {

            java.io.File imageFile = new java.io.File(
                    getCacheDir(),
                    "appearance_camera_" +
                            System.currentTimeMillis() +
                            ".jpg"
            );

            return FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    imageFile
            );

        } catch (Exception e) {

            Log.e(
                    "APPEARANCE_CAMERA",
                    "Unable to create camera image URI",
                    e
            );

            return null;
        }
    }


    private void openCamera() {

        if (androidx.core.content.ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED) {

            cameraImageUri = createCameraImageUri();

            if (cameraImageUri != null) {

                cameraLauncher.launch(cameraImageUri);

            } else {

                Toast.makeText(
                        this,
                        "Unable to prepare camera.",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.CAMERA},
                    100
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == 100) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                cameraImageUri = createCameraImageUri();

                if (cameraImageUri != null) {
                    cameraLauncher.launch(cameraImageUri);
                }

            } else {

                android.widget.Toast.makeText(
                        this,
                        "Camera permission is required to take a photo.",
                        android.widget.Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private MultipartBody.Part createImagePartFromUri(Uri imageUri) {

        try {

            java.io.InputStream inputStream =
                    getContentResolver().openInputStream(imageUri);

            if (inputStream == null) {
                return null;
            }

            java.io.ByteArrayOutputStream byteArrayOutputStream =
                    new java.io.ByteArrayOutputStream();

            byte[] buffer = new byte[4096];

            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {

                byteArrayOutputStream.write(
                        buffer,
                        0,
                        bytesRead
                );
            }

            inputStream.close();

            byte[] imageBytes =
                    byteArrayOutputStream.toByteArray();

            RequestBody requestBody =
                    RequestBody.create(
                            MediaType.parse("image/*"),
                            imageBytes
                    );

            return MultipartBody.Part.createFormData(
                    "image",
                    "appearance_image.jpg",
                    requestBody
            );

        } catch (Exception e) {

            Log.e(
                    "APPEARANCE_IMAGE",
                    "Error preparing gallery image",
                    e
            );

            return null;
        }
    }

    private MultipartBody.Part createImagePart() {

        if (selectedImageUri != null) {

            return createImagePartFromUri(
                    selectedImageUri
            );
        }

        return null;
    }

    private void displayAppearanceResults(AppearanceResponse response) {

        if (response == null) {

            Toast.makeText(
                    this,
                    "Unable to process the analysis result.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!response.isSuccess()) {

            String message = response.getMessage();

            if (message == null || message.trim().isEmpty()) {
                message = "Appearance analysis failed. Please try again.";
            }

            Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (response.getAppearance() == null) {

            Toast.makeText(
                    this,
                    "Appearance results are unavailable.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        AppearanceResponse.Appearance appearance =
                response.getAppearance();

        String faceShape = appearance.getFace_shape();
        String skinTone = appearance.getSkin_tone();
        String bodyType = appearance.getBody_type();

        if (faceShape == null || faceShape.trim().isEmpty()) {
            faceShape = "Unavailable";
        }

        if (skinTone == null || skinTone.trim().isEmpty()) {
            skinTone = "Unavailable";
        }

        if (bodyType == null || bodyType.trim().isEmpty()) {
            bodyType = "Unavailable";
        }

        tvFaceShape.setText(
                "Estimated Face Shape: " + faceShape
        );

        tvSkinTone.setText(
                "Estimated Skin Tone: " + skinTone
        );

        tvBodyType.setText(
                "Estimated Body Type: " + bodyType
        );
    }

    private void loadBodyMeasurements() {

        if (userId == -1) {

            Toast.makeText(
                    this,
                    "User ID not found.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        RetrofitClient.getApiService()
                .getProfile(userId)
                .enqueue(new retrofit2.Callback<ProfileResponse>() {

                    @Override
                    public void onResponse(
                            retrofit2.Call<ProfileResponse> call,
                            retrofit2.Response<ProfileResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getProfile() != null) {

                            ProfileResponse.Profile profile =
                                    response.body().getProfile();

                            chest = profile.getChest();
                            waist = profile.getWaist();
                            hip = profile.getHip();

                            Log.d(
                                    "APPEARANCE_MEASUREMENTS",
                                    "Chest: " + chest
                                            + ", Waist: " + waist
                                            + ", Hip: " + hip
                            );

                        } else {

                            Toast.makeText(
                                    AppearanceActivity.this,
                                    "Unable to load body measurements.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<ProfileResponse> call,
                            Throwable t) {

                        Log.e(
                                "APPEARANCE_MEASUREMENTS",
                                "Failed to load body measurements",
                                t
                        );

                        Toast.makeText(
                                AppearanceActivity.this,
                                "Unable to connect to server.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appearance);

        // Connect XML views
        ivAppearancePreview =
                findViewById(R.id.ivAppearancePreview);

        btnSelectImage =
                findViewById(R.id.btnSelectImage);

        btnCamera =
                findViewById(R.id.btnCamera);

        btnGallery =
                findViewById(R.id.btnGallery);

        btnAnalyze =
                findViewById(R.id.btnAnalyze);

        progressAppearance =
                findViewById(R.id.progressAppearance);

        tvFaceShape =
                findViewById(R.id.tvFaceShape);

        tvSkinTone =
                findViewById(R.id.tvSkinTone);

        tvBodyType =
                findViewById(R.id.tvBodyType);

        // Get existing Feature 1 user ID
        userId =
                getIntent().getIntExtra("user_id", -1);

        Log.d(
                "APPEARANCE_USER_ID",
                "User ID: " + userId
        );

        loadBodyMeasurements();

        // Select Image
        btnSelectImage.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
        });

        // Gallery
        btnGallery.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
        });

        // Camera - implemented in Phase H
        btnCamera.setOnClickListener(v -> {
            openCamera();
        });



        // Analyze - implemented later
        btnAnalyze.setOnClickListener(v -> {

            if (selectedImageUri == null) {

                Toast.makeText(
                        AppearanceActivity.this,
                        "Please select an image first.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (userId == -1) {

                Toast.makeText(
                        AppearanceActivity.this,
                        "User ID not found.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (chest == null || waist == null || hip == null) {

                Toast.makeText(
                        AppearanceActivity.this,
                        "Please complete your body measurements in Profile first.",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            MultipartBody.Part imagePart =
                    createImagePart();

            if (imagePart == null) {

                Toast.makeText(
                        AppearanceActivity.this,
                        "Unable to prepare the selected image.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            progressAppearance.setVisibility(View.VISIBLE);
            btnAnalyze.setEnabled(false);

            RequestBody userIdBody =
                    RequestBody.create(
                            MediaType.parse("text/plain"),
                            String.valueOf(userId)
                    );

            RequestBody chestBody =
                    RequestBody.create(
                            MediaType.parse("text/plain"),
                            String.valueOf(chest)
                    );

            RequestBody waistBody =
                    RequestBody.create(
                            MediaType.parse("text/plain"),
                            String.valueOf(waist)
                    );

            RequestBody hipBody =
                    RequestBody.create(
                            MediaType.parse("text/plain"),
                            String.valueOf(hip)
                    );

            RetrofitClient.getApiService()
                    .analyzeAppearance(
                            userIdBody,
                            imagePart,
                            chestBody,
                            waistBody,
                            hipBody
                    )
                    .enqueue(new Callback<AppearanceResponse>() {

                        @Override
                        public void onResponse(
                                Call<AppearanceResponse> call,
                                Response<AppearanceResponse> response) {

                            progressAppearance.setVisibility(View.GONE);
                            btnAnalyze.setEnabled(true);

                            if (response.isSuccessful()
                                    && response.body() != null) {

                                displayAppearanceResults(
                                        response.body()
                                );

                            } else {

                                String errorMessage =
                                        "Appearance analysis failed.";

                                if (response.errorBody() != null) {

                                    try {

                                        errorMessage +=
                                                " HTTP " + response.code();

                                    } catch (Exception e) {

                                        Log.e(
                                                "APPEARANCE_API",
                                                "Unable to read error response",
                                                e
                                        );
                                    }
                                } else {

                                    errorMessage +=
                                            " HTTP " + response.code();
                                }

                                Log.e(
                                        "APPEARANCE_API",
                                        "HTTP error: " + response.code()
                                );

                                Toast.makeText(
                                        AppearanceActivity.this,
                                        errorMessage,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<AppearanceResponse> call,
                                Throwable t) {

                            progressAppearance.setVisibility(View.GONE);
                            btnAnalyze.setEnabled(true);

                            Log.e(
                                    "APPEARANCE_API",
                                    "Appearance analysis request failed",
                                    t
                            );

                            Toast.makeText(
                                    AppearanceActivity.this,
                                    "Unable to connect to the server. Please try again.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        });
    }
}
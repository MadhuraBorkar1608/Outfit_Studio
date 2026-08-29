package com.example.outfitstudio;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddClothingActivity extends AppCompatActivity {

    private ImageView ivClothingPreview;
    private Button btnSelectClothingImage;
    private EditText etClothingName;
    private Spinner spClothingCategory;
    private EditText etClothingColor;
    private Button btnAddClothing;
    private ProgressBar progressAddClothing;

    private int userId;

    private Uri selectedImageUri;

    // Gallery image picker
    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri != null) {

                            selectedImageUri = uri;

                            ivClothingPreview.setImageURI(
                                    selectedImageUri
                            );

                            Log.d(
                                    "WARDROBE_IMAGE",
                                    "Selected image URI: "
                                            + selectedImageUri
                            );
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_clothing
        );

        // Get existing logged-in user ID
        userId = getIntent().getIntExtra(
                "user_id",
                -1
        );

        // Connect XML views
        ivClothingPreview =
                findViewById(R.id.ivClothingPreview);

        btnSelectClothingImage =
                findViewById(R.id.btnSelectClothingImage);

        etClothingName =
                findViewById(R.id.etClothingName);

        spClothingCategory =
                findViewById(R.id.spClothingCategory);

        etClothingColor =
                findViewById(R.id.etClothingColor);

        btnAddClothing =
                findViewById(R.id.btnAddClothing);

        progressAddClothing =
                findViewById(R.id.progressAddClothing);

        setupCategorySpinner();

        // Select image
        btnSelectClothingImage.setOnClickListener(v -> {

            galleryLauncher.launch("image/*");

        });

        // Add clothing
        btnAddClothing.setOnClickListener(v -> {

            addClothing();

        });
    }

    private void setupCategorySpinner() {

        String[] categories = {
                "T-Shirts",
                "Shirts",
                "Jeans",
                "Trousers",
                "Footwear",
                "Jackets",
                "Accessories"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categories
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spClothingCategory.setAdapter(adapter);
    }

    private MultipartBody.Part createImagePartFromUri(
            Uri imageUri
    ) {

        try {

            InputStream inputStream =
                    getContentResolver()
                            .openInputStream(imageUri);

            if (inputStream == null) {
                return null;
            }

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];

            int bytesRead;

            while ((bytesRead =
                    inputStream.read(buffer)) != -1) {

                outputStream.write(
                        buffer,
                        0,
                        bytesRead
                );
            }

            inputStream.close();

            byte[] imageBytes =
                    outputStream.toByteArray();

            RequestBody requestBody =
                    RequestBody.create(
                            MediaType.parse("image/*"),
                            imageBytes
                    );

            return MultipartBody.Part.createFormData(
                    "image",
                    "wardrobe_image.jpg",
                    requestBody
            );

        } catch (Exception e) {

            Log.e(
                    "WARDROBE_IMAGE",
                    "Error preparing image",
                    e
            );

            return null;
        }
    }

    private void addClothing() {

        // Validate user ID
        if (userId == -1) {

            Toast.makeText(
                    this,
                    "User ID not found.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Validate image
        if (selectedImageUri == null) {

            Toast.makeText(
                    this,
                    "Please select an image first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Get name
        String name =
                etClothingName
                        .getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {

            etClothingName.setError(
                    "Please enter clothing name."
            );

            return;
        }

        // Get category
        String category =
                spClothingCategory
                        .getSelectedItem()
                        .toString();

        // Get color
        String color =
                etClothingColor
                        .getText()
                        .toString()
                        .trim();

        if (color.isEmpty()) {

            etClothingColor.setError(
                    "Please enter clothing color."
            );

            return;
        }

        // Prepare image
        MultipartBody.Part imagePart =
                createImagePartFromUri(
                        selectedImageUri
                );

        if (imagePart == null) {

            Toast.makeText(
                    this,
                    "Unable to prepare the selected image.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Prepare multipart fields
        RequestBody userIdBody =
                RequestBody.create(
                        MediaType.parse("text/plain"),
                        String.valueOf(userId)
                );

        RequestBody nameBody =
                RequestBody.create(
                        MediaType.parse("text/plain"),
                        name
                );

        RequestBody categoryBody =
                RequestBody.create(
                        MediaType.parse("text/plain"),
                        category
                );

        RequestBody colorBody =
                RequestBody.create(
                        MediaType.parse("text/plain"),
                        color
                );

        // Show loading
        progressAddClothing.setVisibility(
                View.VISIBLE
        );

        btnAddClothing.setEnabled(false);

        // Send request
        RetrofitClient.getApiService()
                .addWardrobeItem(
                        userIdBody,
                        nameBody,
                        categoryBody,
                        colorBody,
                        imagePart
                )
                .enqueue(
                        new Callback<WardrobeResponse>() {

                            @Override
                            public void onResponse(
                                    Call<WardrobeResponse> call,
                                    Response<WardrobeResponse> response
                            ) {

                                progressAddClothing
                                        .setVisibility(
                                                View.GONE
                                        );

                                btnAddClothing
                                        .setEnabled(true);

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    WardrobeResponse result =
                                            response.body();

                                    if (result.isSuccess()) {

                                        Toast.makeText(
                                                AddClothingActivity.this,
                                                result.getMessage(),
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        Log.d(
                                                "WARDROBE_API",
                                                "Item added. ID: "
                                                        + result.getItem_id()
                                        );

                                        // Return to wardrobe
                                        finish();

                                    } else {

                                        String message =
                                                result.getMessage();

                                        if (message == null
                                                || message.isEmpty()) {

                                            message =
                                                    "Failed to add clothing.";
                                        }

                                        Toast.makeText(
                                                AddClothingActivity.this,
                                                message,
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                } else {

                                    Log.e(
                                            "WARDROBE_API",
                                            "HTTP error: "
                                                    + response.code()
                                    );

                                    Toast.makeText(
                                            AddClothingActivity.this,
                                            "Failed to add clothing. HTTP "
                                                    + response.code(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<WardrobeResponse> call,
                                    Throwable t
                            ) {

                                progressAddClothing
                                        .setVisibility(
                                                View.GONE
                                        );

                                btnAddClothing
                                        .setEnabled(true);

                                Log.e(
                                        "WARDROBE_API",
                                        "Wardrobe request failed",
                                        t
                                );

                                Toast.makeText(
                                        AddClothingActivity.this,
                                        "Unable to connect to server.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }
}
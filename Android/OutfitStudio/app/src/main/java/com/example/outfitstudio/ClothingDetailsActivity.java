package com.example.outfitstudio;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClothingDetailsActivity extends AppCompatActivity {

    private ImageView ivClothingDetails;

    private EditText etClothingName;
    private Spinner spClothingCategory;
    private EditText etClothingColor;

    private Button btnUpdateClothing;
    private Button btnDeleteClothing;

    private ProgressBar progressClothingDetails;

    private int itemId;
    private int userId;

    private final String[] categories = {
            "T-Shirts",
            "Shirts",
            "Jeans",
            "Trousers",
            "Footwear",
            "Jackets",
            "Accessories"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_clothing_details);

        // Connect XML views
        ivClothingDetails =
                findViewById(R.id.ivClothingDetails);

        etClothingName =
                findViewById(R.id.etClothingName);

        spClothingCategory =
                findViewById(R.id.spClothingCategory);

        etClothingColor =
                findViewById(R.id.etClothingColor);

        btnUpdateClothing =
                findViewById(R.id.btnUpdateClothing);

        btnDeleteClothing =
                findViewById(R.id.btnDeleteClothing);

        progressClothingDetails =
                findViewById(R.id.progressClothingDetails);

        // Get item ID
        itemId = getIntent().getIntExtra(
                "item_id",
                -1
        );

        // Get user ID
        userId = getIntent().getIntExtra(
                "user_id",
                -1
        );

        // Setup category spinner
        setupCategorySpinner();

        // Check item ID
        if (itemId == -1) {

            Toast.makeText(
                    this,
                    "Clothing item not found.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        // Load item details
        loadClothingDetails();

        // Update button
        btnUpdateClothing.setOnClickListener(v -> {
            updateClothing();
        });

        // Delete button
        btnDeleteClothing.setOnClickListener(v -> {
            showDeleteConfirmation();
        });
    }

    // ---------------------------------------------------------
    // CATEGORY SPINNER
    // ---------------------------------------------------------

    private void setupCategorySpinner() {

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

    // ---------------------------------------------------------
    // LOAD CLOTHING DETAILS
    // ---------------------------------------------------------

    private void loadClothingDetails() {

        progressClothingDetails.setVisibility(
                View.VISIBLE
        );

        RetrofitClient.getApiService()
                .getWardrobeItem(itemId)
                .enqueue(
                        new Callback<WardrobeResponse>() {

                            @Override
                            public void onResponse(
                                    Call<WardrobeResponse> call,
                                    Response<WardrobeResponse> response) {

                                progressClothingDetails
                                        .setVisibility(View.GONE);

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()
                                        && response.body().getItem() != null) {

                                    WardrobeItem item =
                                            response.body().getItem();

                                    displayItem(item);

                                } else {

                                    Toast.makeText(
                                            ClothingDetailsActivity.this,
                                            "Unable to load clothing details.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<WardrobeResponse> call,
                                    Throwable t) {

                                progressClothingDetails
                                        .setVisibility(View.GONE);

                                Log.e(
                                        "CLOTHING_DETAILS",
                                        "Failed to load item",
                                        t
                                );

                                Toast.makeText(
                                        ClothingDetailsActivity.this,
                                        "Unable to connect to server.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    // ---------------------------------------------------------
    // DISPLAY CLOTHING DETAILS
    // ---------------------------------------------------------

    private void displayItem(WardrobeItem item) {

        // Set name
        etClothingName.setText(
                item.getName()
        );

        // Set color
        etClothingColor.setText(
                item.getColor()
        );

        // Set category
        String itemCategory =
                item.getCategory();

        for (int i = 0; i < categories.length; i++) {

            if (categories[i].equalsIgnoreCase(
                    itemCategory
            )) {

                spClothingCategory.setSelection(i);

                break;
            }
        }

        // -----------------------------------------------------
        // LOAD CLOTHING IMAGE
        // -----------------------------------------------------

        String imagePath =
                item.getImage_path();

        if (imagePath != null
                && !imagePath.isEmpty()) {

            String imageUrl;

            /*
             * If backend already returns a complete URL,
             * use it directly.
             */
            if (imagePath.startsWith("http://")
                    || imagePath.startsWith("https://")) {

                imageUrl = imagePath;

            } else {

                /*
                 * Otherwise combine the backend base URL
                 * with the image path.
                 */
                imageUrl =
                        RetrofitClient.BASE_URL
                                + imagePath;
            }

            Log.d(
                    "CLOTHING_IMAGE",
                    "Loading image: " + imageUrl
            );

            Glide.with(
                            ClothingDetailsActivity.this
                    )
                    .load(imageUrl)
                    .placeholder(
                            android.R.drawable.ic_menu_gallery
                    )
                    .error(
                            android.R.drawable.ic_menu_report_image
                    )
                    .into(ivClothingDetails);

        } else {

            // No image available
            ivClothingDetails.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }
    }

    // ---------------------------------------------------------
    // UPDATE CLOTHING
    // ---------------------------------------------------------

    private void updateClothing() {

        String name =
                etClothingName
                        .getText()
                        .toString()
                        .trim();

        String category =
                spClothingCategory
                        .getSelectedItem()
                        .toString();

        String color =
                etClothingColor
                        .getText()
                        .toString()
                        .trim();

        // Validate name
        if (name.isEmpty()) {

            etClothingName.setError(
                    "Please enter clothing name."
            );

            return;
        }

        // Validate color
        if (color.isEmpty()) {

            etClothingColor.setError(
                    "Please enter clothing color."
            );

            return;
        }

        // Create update request
        WardrobeUpdateRequest request =
                new WardrobeUpdateRequest(
                        name,
                        category,
                        color
                );

        // Show loading
        progressClothingDetails.setVisibility(
                View.VISIBLE
        );

        btnUpdateClothing.setEnabled(false);

        // Send update request
        RetrofitClient.getApiService()
                .updateWardrobeItem(
                        itemId,
                        request
                )
                .enqueue(
                        new Callback<WardrobeResponse>() {

                            @Override
                            public void onResponse(
                                    Call<WardrobeResponse> call,
                                    Response<WardrobeResponse> response) {

                                progressClothingDetails
                                        .setVisibility(View.GONE);

                                btnUpdateClothing
                                        .setEnabled(true);

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    Toast.makeText(
                                            ClothingDetailsActivity.this,
                                            response.body().getMessage(),
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    /*
                                     * Return to wardrobe.
                                     * WardrobeActivity.onResume()
                                     * will reload the updated data.
                                     */
                                    finish();

                                } else {

                                    Toast.makeText(
                                            ClothingDetailsActivity.this,
                                            "Failed to update clothing.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<WardrobeResponse> call,
                                    Throwable t) {

                                progressClothingDetails
                                        .setVisibility(View.GONE);

                                btnUpdateClothing
                                        .setEnabled(true);

                                Log.e(
                                        "WARDROBE_UPDATE",
                                        "Update failed",
                                        t
                                );

                                Toast.makeText(
                                        ClothingDetailsActivity.this,
                                        "Unable to connect to server.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    // ---------------------------------------------------------
    // DELETE CONFIRMATION
    // ---------------------------------------------------------

    private void showDeleteConfirmation() {

        new AlertDialog.Builder(this)
                .setTitle("Delete Clothing")
                .setMessage(
                        "Are you sure you want to delete this clothing item?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> deleteClothing()
                )
                .show();
    }

    // ---------------------------------------------------------
    // DELETE CLOTHING
    // ---------------------------------------------------------

    private void deleteClothing() {

        progressClothingDetails.setVisibility(
                View.VISIBLE
        );

        btnDeleteClothing.setEnabled(false);
        btnUpdateClothing.setEnabled(false);

        RetrofitClient.getApiService()
                .deleteWardrobeItem(itemId)
                .enqueue(
                        new Callback<WardrobeResponse>() {

                            @Override
                            public void onResponse(
                                    Call<WardrobeResponse> call,
                                    Response<WardrobeResponse> response) {

                                progressClothingDetails
                                        .setVisibility(View.GONE);

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    Toast.makeText(
                                            ClothingDetailsActivity.this,
                                            response.body().getMessage(),
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    /*
                                     * Return to wardrobe.
                                     */
                                    finish();

                                } else {

                                    btnDeleteClothing
                                            .setEnabled(true);

                                    btnUpdateClothing
                                            .setEnabled(true);

                                    Toast.makeText(
                                            ClothingDetailsActivity.this,
                                            "Failed to delete clothing.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<WardrobeResponse> call,
                                    Throwable t) {

                                progressClothingDetails
                                        .setVisibility(View.GONE);

                                btnDeleteClothing
                                        .setEnabled(true);

                                btnUpdateClothing
                                        .setEnabled(true);

                                Log.e(
                                        "WARDROBE_DELETE",
                                        "Delete failed",
                                        t
                                );

                                Toast.makeText(
                                        ClothingDetailsActivity.this,
                                        "Unable to connect to server.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }
}
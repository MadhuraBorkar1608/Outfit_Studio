package com.example.outfitstudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WardrobeActivity extends AppCompatActivity {

    private int userId;

    private Button btnAddClothing;

    private Spinner spCategoryFilter;

    private RecyclerView wardrobeRecyclerView;

    private TextView tvEmptyWardrobe;

    private WardrobeAdapter wardrobeAdapter;

    private List<WardrobeItem> allItems =
            new ArrayList<>();

    private final String[] categories = {
            "All",
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

        setContentView(
                R.layout.activity_wardrobe
        );

        // Get user ID
        userId = getIntent().getIntExtra(
                "user_id",
                -1
        );

        // Connect views
        btnAddClothing =
                findViewById(R.id.btnAddClothing);

        spCategoryFilter =
                findViewById(R.id.spCategoryFilter);

        wardrobeRecyclerView =
                findViewById(R.id.wardrobeRecyclerView);

        tvEmptyWardrobe =
                findViewById(R.id.tvEmptyWardrobe);

        // Setup category spinner
        setupCategoryFilter();

        // Setup RecyclerView
        setupRecyclerView();

        // Add Clothing button
        btnAddClothing.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WardrobeActivity.this,
                            AddClothingActivity.class
                    );

            intent.putExtra(
                    "user_id",
                    userId
            );

            startActivity(intent);
        });

        // Category selection
        spCategoryFilter.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        String selectedCategory =
                                categories[position];

                        filterItems(
                                selectedCategory
                        );
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (userId != -1) {
            loadWardrobeItems();
        }
    }

    private void setupCategoryFilter() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categories
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCategoryFilter.setAdapter(adapter);
    }

    private void setupRecyclerView() {

        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(
                        this,
                        2
                );

        wardrobeRecyclerView.setLayoutManager(
                gridLayoutManager
        );

        wardrobeAdapter =
                new WardrobeAdapter(
                        this,
                        new ArrayList<>(),
                        userId
                );

        wardrobeRecyclerView.setAdapter(
                wardrobeAdapter
        );
    }

    private void loadWardrobeItems() {

        RetrofitClient.getApiService()
                .getWardrobeItems(userId)
                .enqueue(
                        new Callback<WardrobeListResponse>() {

                            @Override
                            public void onResponse(
                                    Call<WardrobeListResponse> call,
                                    Response<WardrobeListResponse> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    List<WardrobeItem> items =
                                            response.body()
                                                    .getItems();

                                    if (items == null) {

                                        allItems =
                                                new ArrayList<>();

                                    } else {

                                        allItems =
                                                new ArrayList<>(items);
                                    }

                                    String selectedCategory =
                                            spCategoryFilter
                                                    .getSelectedItem()
                                                    .toString();

                                    filterItems(
                                            selectedCategory
                                    );

                                } else {

                                    Toast.makeText(
                                            WardrobeActivity.this,
                                            "Unable to load wardrobe.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<WardrobeListResponse> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        WardrobeActivity.this,
                                        "Unable to connect to server.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    private void filterItems(
            String selectedCategory
    ) {

        List<WardrobeItem> filteredItems =
                new ArrayList<>();

        if (selectedCategory.equals("All")) {

            filteredItems.addAll(
                    allItems
            );

        } else {

            for (WardrobeItem item : allItems) {

                if (item.getCategory() != null
                        && item.getCategory()
                        .equalsIgnoreCase(
                                selectedCategory
                        )) {

                    filteredItems.add(item);
                }
            }
        }

        displayWardrobeItems(
                filteredItems
        );
    }

    private void displayWardrobeItems(
            List<WardrobeItem> items
    ) {

        if (items == null || items.isEmpty()) {

            wardrobeRecyclerView.setVisibility(
                    View.GONE
            );

            tvEmptyWardrobe.setVisibility(
                    View.VISIBLE
            );

            return;
        }

        wardrobeRecyclerView.setVisibility(
                View.VISIBLE
        );

        tvEmptyWardrobe.setVisibility(
                View.GONE
        );

        wardrobeAdapter =
                new WardrobeAdapter(
                        this,
                        items,
                        userId
                );

        wardrobeRecyclerView.setAdapter(
                wardrobeAdapter
        );
    }
}
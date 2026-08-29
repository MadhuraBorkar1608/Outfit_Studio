package com.example.outfitstudio;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WardrobeAdapter
        extends RecyclerView.Adapter<WardrobeAdapter.WardrobeViewHolder> {

    private Context context;
    private List<WardrobeItem> itemList;
    private int userId;

    public WardrobeAdapter(
            Context context,
            List<WardrobeItem> itemList,
            int userId
    ) {
        this.context = context;
        this.itemList = itemList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public WardrobeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_wardrobe,
                                parent,
                                false
                        );

        return new WardrobeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull WardrobeViewHolder holder,
            int position
    ) {

        WardrobeItem item =
                itemList.get(position);

        // Set clothing name
        holder.tvItemName.setText(
                item.getName()
        );

        // Set category
        holder.tvItemCategory.setText(
                item.getCategory()
        );

        // Set color
        holder.tvItemColor.setText(
                item.getColor()
        );

        // Default image
        holder.ivItemImage.setImageResource(
                android.R.drawable.ic_menu_gallery
        );

        // Load clothing image
        String imagePath =
                item.getImage_path();

        if (imagePath != null
                && !imagePath.isEmpty()) {

            String imageUrl;

            if (imagePath.startsWith("http")) {

                imageUrl = imagePath;

            } else {

                imageUrl =
                        RetrofitClient.BASE_URL
                                + imagePath;
            }

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(
                            android.R.drawable.ic_menu_gallery
                    )
                    .error(
                            android.R.drawable.ic_menu_report_image
                    )
                    .into(holder.ivItemImage);
        }

        // Open clothing details
        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            ClothingDetailsActivity.class
                    );

            intent.putExtra(
                    "item_id",
                    item.getId()
            );

            intent.putExtra(
                    "user_id",
                    userId
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {

        if (itemList == null) {
            return 0;
        }

        return itemList.size();
    }

    public static class WardrobeViewHolder
            extends RecyclerView.ViewHolder {

        ImageView ivItemImage;
        TextView tvItemName;
        TextView tvItemCategory;
        TextView tvItemColor;

        public WardrobeViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            ivItemImage =
                    itemView.findViewById(
                            R.id.ivItemImage
                    );

            tvItemName =
                    itemView.findViewById(
                            R.id.tvItemName
                    );

            tvItemCategory =
                    itemView.findViewById(
                            R.id.tvItemCategory
                    );

            tvItemColor =
                    itemView.findViewById(
                            R.id.tvItemColor
                    );
        }
    }
}
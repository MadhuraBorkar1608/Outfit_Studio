package com.example.outfitstudio;

public class WardrobeUpdateRequest {

    private String name;
    private String category;
    private String color;

    public WardrobeUpdateRequest(
            String name,
            String category,
            String color
    ) {
        this.name = name;
        this.category = category;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getColor() {
        return color;
    }
}
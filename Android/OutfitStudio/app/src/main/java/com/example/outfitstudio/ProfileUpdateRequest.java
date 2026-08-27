package com.example.outfitstudio;

public class ProfileUpdateRequest {

    private Double height;
    private Double weight;
    private Double chest;
    private Double waist;
    private Double hip;
    private String preferred_style;
    private String preferred_colors;

    public ProfileUpdateRequest(
            Double height,
            Double weight,
            Double chest,
            Double waist,
            Double hip,
            String preferred_style,
            String preferred_colors) {

        this.height = height;
        this.weight = weight;
        this.chest = chest;
        this.waist = waist;
        this.hip = hip;
        this.preferred_style = preferred_style;
        this.preferred_colors = preferred_colors;
    }
}
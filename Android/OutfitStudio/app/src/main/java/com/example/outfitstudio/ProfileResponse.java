package com.example.outfitstudio;

public class ProfileResponse {

    private boolean success;
    private String message;
    private Profile profile;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Profile getProfile() {
        return profile;
    }

    public static class Profile {

        private int id;
        private String name;
        private String email;

        private Double height;
        private Double weight;
        private Double chest;
        private Double waist;
        private Double hip;

        private String preferred_style;
        private String preferred_colors;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public Double getHeight() {
            return height;
        }

        public Double getWeight() {
            return weight;
        }

        public Double getChest() {
            return chest;
        }

        public Double getWaist() {
            return waist;
        }

        public Double getHip() {
            return hip;
        }

        public String getPreferred_style() {
            return preferred_style;
        }

        public String getPreferred_colors() {
            return preferred_colors;
        }
    }
}
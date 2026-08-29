package com.example.outfitstudio;

public class AppearanceResponse {

    private boolean success;
    private String message;
    private Appearance appearance;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public static class Appearance {

        private String face_shape;
        private String skin_tone;
        private String body_type;

        public String getFace_shape() {
            return face_shape;
        }

        public String getSkin_tone() {
            return skin_tone;
        }

        public String getBody_type() {
            return body_type;
        }
    }
}
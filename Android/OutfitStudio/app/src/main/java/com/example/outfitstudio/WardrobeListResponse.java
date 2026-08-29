package com.example.outfitstudio;

import java.util.List;

public class WardrobeListResponse {

    private boolean success;
    private String message;
    private List<WardrobeItem> items;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<WardrobeItem> getItems() {
        return items;
    }
}
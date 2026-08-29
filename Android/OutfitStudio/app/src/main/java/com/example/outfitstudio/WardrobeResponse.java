package com.example.outfitstudio;

public class WardrobeResponse {

    private boolean success;
    private String message;
    private int item_id;
    private WardrobeItem item;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getItem_id() {
        return item_id;
    }

    public WardrobeItem getItem() {
        return item;
    }
}
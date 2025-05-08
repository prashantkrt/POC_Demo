package com.mylearning.poc.dto;

import jakarta.validation.constraints.NotNull;

public class ImageRequest {

    @NotNull
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
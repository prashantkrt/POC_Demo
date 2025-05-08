package com.mylearning.poc.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class ImageRequest {

    @NotBlank(message = "Image URL must not be blank")
    @URL(message = "Invalid image URL")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
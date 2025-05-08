package com.mylearning.poc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImageRequest {
    
    @NotBlank(message = "Image URL must not be blank")
    @URL(message = "Invalid image URL")
    private String imageUrl;
}
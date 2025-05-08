package com.mylearning.poc.dto;

import com.mylearning.poc.validations.ValidImageUrl;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PsdGenerationRequest {

    @ValidImageUrl
    @NotBlank(message = "Image URL must not be blank.")
    private String imageUrl;

    @NotBlank(message = "Logo URL must not be blank.")
    private String logoUrl;

    @NotBlank(message = "Header text must not be blank.")
    private String headerText;

    @NotBlank(message = "Font path/url must not be blank.")
    private String fontUrl; // provide the font local path

    @Min(value = 3, message = "Font size must be at least 3.")
    private int fontSize;

    @NotBlank(message = "Template ID must not be blank.")
    private String templateId;
}

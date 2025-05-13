package com.mylearning.poc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PsdTextLayerRequest {
    private String psdUrl;      // Google signed PSD URL
    private String headerText;  // Text to overlay
    private String fontPath;    // Full .ttf file path
    private int fontSize;
}

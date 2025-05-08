package com.mylearning.poc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PsdGenerationResponse {
    private boolean apiSuccess;
    private LocalDateTime timeStamp;
    private String imagePsd;
    private String imagePng;
    private String imageJpg;

    public PsdGenerationResponse(boolean b, String outputPsd, String outputPng, String outputJpg) {
        this.apiSuccess = b;
        this.timeStamp = LocalDateTime.now();
        this.imagePsd = outputPsd;
        this.imagePng = outputPng;
        this.imageJpg = outputJpg;
    }
}

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

    public PsdGenerationResponse(boolean apiSuccess, String outputPsd) {
        this.apiSuccess = apiSuccess;
        this.timeStamp = LocalDateTime.now();
        this.imagePsd = outputPsd;
    }
}

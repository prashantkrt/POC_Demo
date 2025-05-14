package com.mylearning.poc.controller;

import com.mylearning.poc.dto.PsdGenerationRequest;
import com.mylearning.poc.dto.PsdGenerationResponse;
import com.mylearning.poc.exception.PsdGenerationException;
import com.mylearning.poc.service.AsposePsd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/psd")
@Profile({"prod", "local"})
@Slf4j
public class AsposePsdController {


    private final AsposePsd psdService;

    public AsposePsdController(AsposePsd psdService) {
        this.psdService = psdService;
    }

    /**
     * Generates a new PSD file by downloading a base PSD and a font file from signed URLs,
     * and applying a new text layer using the provided header text and font size.
     *
     * @param request the input containing psdUrl, fontUrl, headerText, and fontSize
     * @return HTTP 202 (Accepted) with the generated PSD file path
     *
     * <p><b>Example Request Body:</b></p>
     * <pre>
     * {
     *   "psdUrl": "https://storage.googleapis.com/bucket/original.psd?Expires=...",
     *   "fontUrl": "https://storage.googleapis.com/bucket/font.ttf?Expires=...",
     *   "headerText": "Hello from Aspose!",
     *   "fontSize": 36
     * }
     * </pre>
     */
    @PostMapping("/generate/psd-from-input")
    public ResponseEntity<PsdGenerationResponse> generatePsdFromInput(@Validated @RequestBody PsdGenerationRequest request) throws Exception {
        String requestId = UUID.randomUUID().toString().split("-")[0];
        log.info("[{}] ➤ PSD generation started for: {}", requestId, request.getPsdUrl());
        try {
            PsdGenerationResponse response = psdService.generatePsdFromInput(request);
            log.info("[{}] PSD generation completed: {}", requestId, response.getImagePsd());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (PsdGenerationException e) {
            log.error("PSD generation failed for image URL: requestId: {}, psdUrl: {}", requestId, request.getPsdUrl(), e);
            throw e;
        }
    }

}

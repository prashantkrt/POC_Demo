package com.mylearning.poc.controller;


import com.mylearning.poc.dto.PsdGenerationRequest;
import com.mylearning.poc.dto.PsdGenerationResponse;
import com.mylearning.poc.exception.PsdGenerationException;
import com.mylearning.poc.service.AsposePsd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/psd")
@Profile({"prod", "local"})
@Slf4j
public class AsposePsdController {

    @Qualifier("customAsposePsd")
    private final AsposePsd psdService;

    public AsposePsdController(AsposePsd psdService) {
        this.psdService = psdService;
    }

    /**
     * Generates a PSD file using provided image, logo, text, and font details.
     *
     * @param request the input containing imageUrl, logoUrl, headerText, fontName, fontSize, and templateId
     * @return HTTP 202 (Accepted) with PSD generation result; handled by global exception handler on failure
     *
     * <p><b>Example Request Body:</b></p>
     * <pre>
     * {
     *   "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/a/a3/June_odd-eyed-cat.jpg",
     *   "logoUrl": "https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png",
     *   "headerText": "Hello from Aspose!",
     *   "fontName": "Arial",
     *   "fontSize": 36,
     *   "templateId": "template-001"
     * }
     * </pre>
     */
    @PostMapping("/generate/psd-from-input")
    public ResponseEntity<PsdGenerationResponse> generatePsdFromInput(@Validated @RequestBody PsdGenerationRequest request) throws Exception {
        log.info("Received PSD generation request with image URL: {}", request.getImageUrl());

        try {
            PsdGenerationResponse response = psdService.generatePsdFromInput(request);
            log.info("PSD generated successfully. Paths => PSD: {}, PNG: {}, JPG: {}",
                    response.getImagePsd(), response.getImagePng(), response.getImageJpg());

            return ResponseEntity.accepted().body(response);
        } catch (PsdGenerationException e) {
            log.error("PSD generation failed for image URL: {}", request.getImageUrl(), e);
            throw e;
        }
    }

}

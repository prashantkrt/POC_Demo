package com.mylearning.poc.controller;

import com.mylearning.poc.dto.ImageRequest;
import com.mylearning.poc.service.AsposePsdGenerator;
import com.mylearning.poc.service.LocalFontPsdGeneratorService;
import com.mylearning.poc.service.PsdGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/psd")
@Slf4j
public class PsdGeneratorController {

    @Autowired
    private PsdGeneratorService psdGeneratorService;

    @Autowired
    private AsposePsdGenerator asposePsdGenerator;

    @Autowired
    private LocalFontPsdGeneratorService localFontPsdGeneratorService;

    @Value("${output.file.path:/Users/prashant/Desktop/test/final_output.psd}")
    private String outputFilePath;

    /**
     * Quick test to trigger PSD generation using internal setup.
     *
     * @return HTTP 202 Accepted on success, 400 Bad Request on failure
     */
    @GetMapping("/generate-internal")
    public ResponseEntity<String> generateInternalPsd() {
        try {
            psdGeneratorService.generatePsd();
            return ResponseEntity.accepted().body("PSD generated successfully.");
        } catch (Exception e) {
            log.error("Exception during internal PSD generation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Failed to generate PSD internally.");
        }
    }

    /**
     * Generates a PSD file from a public image URL sent in the request.
     *
     * @param request JSON payload containing the imageUrl
     * @return HTTP 202 Accepted on success, 500 on error
     * <p>
     * Example Request Body:
     * {
     * "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/a/a3/June_odd-eyed-cat.jpg"
     * }
     */
    @PostMapping("/generate-from-url")
    public ResponseEntity<String> generatePsdFromUrl(@Validated @RequestBody ImageRequest request) {
        try {
            log.info("Received PSD generation request for image: {}", request.getImageUrl());
            psdGeneratorService.downloadAndConvertToPsd(request.getImageUrl(), outputFilePath);
            log.info(" PSD created successfully at: {}", outputFilePath);
            return ResponseEntity.accepted().body("PSD generation successful.");
        } catch (Exception e) {
            log.error(" Error during PSD generation from URL: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate PSD: " + e.getMessage());
        }
    }

    /**
     * Triggers the alternate Aspose PSD generation method (e.g., with layered editing).
     *
     * @return HTTP 202 Accepted on success, 400 on failure
     */
    @GetMapping("/generate-aspose")
    public ResponseEntity<String> generateWithAspose() {
        try {
            asposePsdGenerator.generatePsd();
            return ResponseEntity.accepted().body(" PSD generated via Aspose pipeline.");
        } catch (Exception e) {
            log.error("Exception in Aspose PSD generation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Aspose PSD generation failed: " + e.getMessage());
        }
    }


    /**
     * Generates a PSD using the local font + image approach (via Aspose pipeline).
     * <p>
     * This method internally calls {@code localFontPsdGeneratorService.generatePsd()}.
     *
     * @return HTTP 202 Accepted if successful, or 400 Bad Request on failure.
     */
    @GetMapping("/generate/psd-local-font")
    public ResponseEntity<String> generatePsdUsingLocalFont() {
        try {
            localFontPsdGeneratorService.generatePsd();
            log.info("PSD generated successfully via Aspose pipeline.");
            return ResponseEntity.accepted().body("PSD generated via Aspose pipeline.");
        } catch (Exception e) {
            log.error("Exception in Aspose PSD generation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Aspose PSD generation failed: " + e.getMessage());
        }
    }

}

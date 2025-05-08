package com.mylearning.poc.controller;

import com.mylearning.poc.dto.ImageRequest;
import com.mylearning.poc.exception.PsdGenerationException;
import com.mylearning.poc.service.AsposePsdGenerator;
import com.mylearning.poc.service.LocalFontPsdGeneratorService;
import com.mylearning.poc.service.PsdGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
     * Triggers internal PSD generation using a preconfigured setup.
     *
     * @return HTTP 202 Accepted on success
     */
    @GetMapping("/generate-internal")
    public ResponseEntity<String> generateInternalPsd() throws IOException {
        log.info("Internal PSD generation request received.");

        try {
            psdGeneratorService.generatePsd();
        } catch (PsdGenerationException e) {
            log.error("Error during internal PSD generation: {}", e.getMessage(), e);
            throw e; // Let global exception handler return proper 500 response
        }

        log.info("Internal PSD generated successfully.");
        return ResponseEntity.accepted().body("PSD generated successfully.");
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
    public ResponseEntity<String> generatePsdFromUrl(@Validated @RequestBody ImageRequest request) throws Exception {
        log.info("Received PSD generation request for image: {}", request.getImageUrl());

        try {
            psdGeneratorService.downloadAndConvertToPsd(request.getImageUrl(), outputFilePath);
        } catch (PsdGenerationException e) {
            log.error("Error generating PSD for URL {}: {}", request.getImageUrl(), e.getMessage(), e);
            throw e; // rethrow so global handler can handle it
        }

        log.info("PSD created successfully at: {}", outputFilePath);
        return ResponseEntity.accepted().body("PSD generation successful.");
    }

    /**
     * Triggers the alternate Aspose PSD generation method (e.g., with layered editing).
     *
     * @return HTTP 202 Accepted on success
     */
    @GetMapping("/generate-aspose")
    public ResponseEntity<String> generateWithAspose() throws Exception {
        log.info("Aspose PSD generation request received.");

        try {
            asposePsdGenerator.generatePsd();
        } catch (PsdGenerationException e) {
            log.error("Error during Aspose PSD generation: {}", e.getMessage(), e);
            throw e;
        }

        log.info("PSD successfully generated via Aspose pipeline.");
        return ResponseEntity.accepted().body("PSD generated via Aspose pipeline.");
    }


    /**
     * Generates a PSD using the local font + image approach (via Aspose pipeline).
     * <p>
     * This method internally calls {@code localFontPsdGeneratorService.generatePsd()}.
     *
     * @return HTTP 202 Accepted if successful
     */
    @GetMapping("/generate/psd-local-font")
    public ResponseEntity<String> generatePsdUsingLocalFont() throws Exception {
        log.info("Local font PSD generation request received.");

        try {
            localFontPsdGeneratorService.generatePsd();
        } catch (PsdGenerationException e) {
            log.error("Error during local font PSD generation: {}", e.getMessage(), e);
            throw e; // Delegates to global handler
        }

        log.info("PSD generated successfully via local font + Aspose pipeline.");
        return ResponseEntity.accepted().body("PSD generated via Aspose pipeline.");
    }

}

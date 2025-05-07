package com.mylearning.poc.controller;

import com.mylearning.poc.dto.ImageRequest;
import com.mylearning.poc.service.AsposePsdGenerator;
import com.mylearning.poc.service.PsdGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/")
@RestController
@Slf4j
public class PsdGeneratorController {

    @Autowired
    private PsdGeneratorService psdGeneratorService;

    @Autowired
    private AsposePsdGenerator asposePsdGenerator;

    @GetMapping("/generatePsd")
    public ResponseEntity<Object> getPsd() {
        try {
            psdGeneratorService.generatePsd();
        } catch (Exception e) {
            log.error("Exception occurred {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.accepted().body("Success");
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generatePsd(@RequestBody ImageRequest request) {
        try {
            String outputFilePath = "/Users/prashant/Desktop/test/final_output.psd";
            psdGeneratorService.downloadAndConvertToPsd(request.getImageUrl(), outputFilePath);
        } catch (Exception e) {
            log.error("Exception occurred {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.accepted().body("Success");
    }


    @GetMapping("/process/generate")
    public ResponseEntity<String> processImageToPsd() {
        try {
            asposePsdGenerator.generatePsd();
        } catch (Exception e) {
            log.error("Exception occurred {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.accepted().body("Success");
    }
}


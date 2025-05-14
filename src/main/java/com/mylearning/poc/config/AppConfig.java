package com.mylearning.poc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {
    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(10); // tune as needed
    }
}
//
//@PostConstruct
//public void warmUp() {
//    try {
//        new com.aspose.psd.License().setLicense(getClass().getClassLoader().getResourceAsStream("Aspose.PSD.Java.lic"));
//
//        try (PsdImage dummy = new PsdImage(100, 100);
//             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            dummy.save(out, new PsdOptions());
//        }
//
//        InputStream fontStream = getClass().getClassLoader().getResourceAsStream("warmup-font.ttf");
//        if (fontStream != null) {
//            new TTFParser().parse(fontStream).getName();
//        }
//
//        log.info("✅ Aspose warm-up complete.");
//    } catch (Exception e) {
//        log.warn("Warm-up failed", e);
//    }
//}

//
//import com.aspose.psd.fileformats.psd.PsdImage;
//import com.aspose.psd.imageoptions.PsdOptions;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//import javax.annotation.PostConstruct;
//import java.io.ByteArrayOutputStream;
//
//@Configuration
//@Profile("prod")
//@Slf4j
//public class AsposeWarmUpConfig {
//
//    @PostConstruct
//    public void warmUp() {
//        try {
//            log.info("🔧 Warming up Aspose.PSD engine...");
//            // Create a small in-memory PSD to initialize Aspose internals
//            try (PsdImage dummy = new PsdImage(100, 100);
//                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//
//                dummy.save(out, new PsdOptions());
//            }
//
//            log.info("✅ Aspose.PSD engine warm-up completed successfully.");
//        } catch (Exception e) {
//            log.warn("⚠️ Aspose.PSD warm-up failed: {}", e.getMessage(), e);
//        }
//    }
//}
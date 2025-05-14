package com.mylearning.poc.config;

import com.aspose.psd.License;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
@Slf4j
public class AsposePsdLicenseConfig {

    private static final String LICENSE_FILE = "Aspose.PSD.Java.lic";

    @PostConstruct
    public void applyLicense() {
        try (InputStream licenseStream = getClass().getClassLoader().getResourceAsStream(LICENSE_FILE)) {
            if (licenseStream == null) {
                throw new IllegalStateException("License file not found in classpath: " + LICENSE_FILE);
            }

            License license = new License();
            license.setLicense(licenseStream);
            log.info("Aspose.PSD license applied successfully.");
        } catch (Exception e) {
            log.error("Aspose.PSD license could not be applied: {}", e.getMessage(),e);
        }
    }
}
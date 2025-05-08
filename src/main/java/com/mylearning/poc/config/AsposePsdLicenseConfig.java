package com.mylearning.poc.config;

import com.aspose.psd.License;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsposePsdLicenseConfig {

    @PostConstruct
    public void setLicense() {
        try {
            License license = new License();
            license.setLicense(getClass().getClassLoader().getResourceAsStream("Aspose.PSD.Java.lic"));
            System.out.println("Aspose.PSD license applied successfully.");
        } catch (Exception e) {
            System.err.println("Aspose.PSD license could not be applied: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
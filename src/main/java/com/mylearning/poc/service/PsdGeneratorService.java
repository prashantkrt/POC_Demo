package com.mylearning.poc.service;

import com.aspose.psd.Image;
import com.aspose.psd.RasterImage;
import com.aspose.psd.fileformats.psd.PsdImage;
import com.aspose.psd.fileformats.psd.layers.Layer;
import com.aspose.psd.imageoptions.PsdOptions;
import org.apache.hc.client5.http.fluent.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Service
public class PsdGeneratorService {

    @Value("${input.file.path}")
    private String inputFilePath = "/Users/prashant/Desktop/test/test.jpg";

    @Value("${output.file.path}")
    private String outputFilePath = "/Users/prashant/Desktop/test/output.psd";

    public void generatePsd() throws IOException {
        String inputImagePath = "/Users/prashant/Desktop/test/test.jpg";      // your input image path
        String outputPsdPath = "/Users/prashant/Desktop/test/output.psd";      // output PSD file

        // Load input image
        Image image = Image.load(inputImagePath);

        // Create a new PSD image with same dimensions
        PsdImage psdImage = new PsdImage(image.getWidth(), image.getHeight());

        // Create a new layer from the loaded image
        Layer imageLayer = new Layer((RasterImage) image);

        // Add layer to PSD
        psdImage.addLayer(imageLayer);

        // Save PSD file
        psdImage.save(outputPsdPath, new PsdOptions());

        System.out.println("PSD created: " + outputPsdPath);
    }


    public String downloadAndConvertToPsd(String imageUrl, String outputPath) throws Exception {
        // Download the image
        File tempImageFile = File.createTempFile("aspose_image", ".tmp");
        try (InputStream in = Request.get(imageUrl).execute().returnContent().asStream();
             FileOutputStream out = new FileOutputStream(tempImageFile)) {
            in.transferTo(out);
        }

        // Load downloaded image using Aspose
        Image inputImage = Image.load(tempImageFile.getAbsolutePath());

        // Create PSD with the same size
        try (PsdImage psdImage = new PsdImage(inputImage.getWidth(), inputImage.getHeight())) {
            Layer imageLayer = new Layer((RasterImage) inputImage);
            psdImage.addLayer(imageLayer);

            // Save PSD file
            psdImage.save(outputPath, new PsdOptions());
        }

        // Delete temp image
        Files.deleteIfExists(tempImageFile.toPath());

        return outputPath;
    }


}

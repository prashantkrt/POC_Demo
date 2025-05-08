package com.mylearning.poc.service;

import com.aspose.psd.Image;
import com.aspose.psd.RasterImage;
import com.aspose.psd.fileformats.psd.PsdImage;
import com.aspose.psd.fileformats.psd.layers.Layer;
import com.aspose.psd.imageoptions.PsdOptions;
import org.apache.hc.client5.http.fluent.Request;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Service
public class PsdGeneratorService2 {

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
        // Step 1: Download image
        File tempImageFile = File.createTempFile("aspose_image", ".tmp");
        try (InputStream in = Request.get(imageUrl).execute().returnContent().asStream();
             FileOutputStream out = new FileOutputStream(tempImageFile)) {
            in.transferTo(out);
        }

        // Step 2: Load and cast image safely
        Image loadedImage = Image.load(tempImageFile.getAbsolutePath());
        if (!(loadedImage instanceof RasterImage)) {
            throw new IllegalArgumentException("Downloaded image is not a valid raster image.");
        }
        RasterImage rasterImage = (RasterImage) loadedImage;

        // Cache the image if needed (some formats require this before use)
        if (!rasterImage.isCached()) {
            rasterImage.cacheData();
        }

        // Step 3: Create and write PSD
        try (PsdImage psdImage = new PsdImage(rasterImage.getWidth(), rasterImage.getHeight())) {
            Layer imageLayer = new Layer(rasterImage);
            psdImage.addLayer(imageLayer);
            psdImage.save(outputPath, new PsdOptions());
        }

        // Step 4: Cleanup
        loadedImage.dispose();
        Files.deleteIfExists(tempImageFile.toPath());

        return outputPath;
    }

}

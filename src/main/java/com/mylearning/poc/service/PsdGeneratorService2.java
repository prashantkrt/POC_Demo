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
        RasterImage rasterImage = null;

        try (InputStream in = Request.get(imageUrl).execute().returnContent().asStream();
             FileOutputStream out = new FileOutputStream(tempImageFile)) {
            in.transferTo(out);
        }

        try {
            Image loadedImage = Image.load(tempImageFile.getAbsolutePath());
            if (!(loadedImage instanceof RasterImage)) {
                loadedImage.dispose(); // make sure to release even if invalid
                throw new IllegalArgumentException("Downloaded image is not a valid raster image.");
            }
            rasterImage = (RasterImage) loadedImage;

            if (!rasterImage.isCached()) {
                rasterImage.cacheData();
            }

            try (PsdImage psdImage = new PsdImage(rasterImage.getWidth(), rasterImage.getHeight())) {
                Layer imageLayer = new Layer(rasterImage);
                psdImage.addLayer(imageLayer);
                psdImage.save(outputPath, new PsdOptions());
            }
        } finally {
            if (rasterImage != null) {
                rasterImage.dispose(); // important: release file handle
            }
            Files.deleteIfExists(tempImageFile.toPath()); // delete only after dispose
        }

        return outputPath;
    }
}

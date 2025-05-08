package com.mylearning.poc.service;

import com.aspose.psd.Color;
import com.aspose.psd.Image;
import com.aspose.psd.RasterImage;
import com.aspose.psd.Rectangle;
import com.aspose.psd.fileformats.psd.PsdImage;
import com.aspose.psd.fileformats.psd.layers.Layer;
import com.aspose.psd.fileformats.psd.layers.TextLayer;
import com.aspose.psd.fileformats.psd.layers.text.ITextPortion;
import com.aspose.psd.fileformats.psd.layers.text.ITextStyle;
import com.aspose.psd.imageoptions.PsdOptions;
import org.apache.hc.client5.http.fluent.Request;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;

@Service
public class LocalFontPsdGeneratorService {

    public void generatePsd() throws Exception {
        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/a/a3/June_odd-eyed-cat.jpg";
        String logoUrl = "https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png";
        String headerText = "Hello from Aspose!";
        int fontSize = 36;
        String fontPath = "/Users/prashant/Desktop/test/fontawesome-webfont.ttf";  // Must be installed on your machine
        String outputPath = "/Users/prashant/Desktop/test/final_font_output.psd";

        new AsposePsdGenerator().downloadAndCreatePsd(imageUrl, logoUrl, headerText, fontPath, fontSize, outputPath);
        System.out.println("PSD created at: " + outputPath);
    }

    public void downloadAndCreatePsd(String imageUrl, String logoUrl, String headerText,
                                     String fontPath, int fontSize, String outputPath) throws Exception {

        File bgImageFile = downloadFile(imageUrl, ".jpg");
        File logoFile = downloadFile(logoUrl, ".png");
        File fontFile = new File(fontPath);

        if (!fontFile.exists()) {
            throw new IllegalArgumentException("Font file does not exist: " + fontPath);
        }

        // 1. Register local font folder
        String fontDir = fontFile.getParent();
        com.aspose.psd.FontSettings.setFontsFolder(fontDir);

        // 2. Extract font name (e.g., "Montserrat-Regular.ttf" -> "Montserrat")
        String fontName = extractFontName(fontFile.getName());

        RasterImage bgImage = null;
        RasterImage logo = null;

        try {
            bgImage = (RasterImage) Image.load(bgImageFile.getAbsolutePath());
            logo = (RasterImage) Image.load(logoFile.getAbsolutePath());

            try (PsdImage psdImage = new PsdImage(bgImage.getWidth(), bgImage.getHeight())) {

                // Add background layer
                Layer bgLayer = new Layer(bgImage);
                psdImage.addLayer(bgLayer);

                // Add logo layer
                Layer logoLayer = new Layer(logo);
                logoLayer.setLeft(30);
                logoLayer.setTop(30);
                psdImage.addLayer(logoLayer);

                // Add editable text layer
                Rectangle textBounds = new Rectangle(0, 0, psdImage.getWidth(), psdImage.getHeight());
                TextLayer textLayer = psdImage.addTextLayer(headerText, textBounds);

                // Style the text
                ITextPortion[] portions = textLayer.getTextData().getItems();
                if (portions.length > 0) {
                    ITextStyle style = portions[0].getStyle();
                    style.setFontSize(fontSize);
                    style.setFontName(fontName); // Now uses extracted font name
                    style.setFillColor(Color.getBlack());
                }

                textLayer.getTextData().updateLayerData();

                // Save PSD
                psdImage.save(outputPath, new PsdOptions());
            }
        } finally {
            if (bgImage != null) bgImage.dispose();
            if (logo != null) logo.dispose();

            Files.deleteIfExists(bgImageFile.toPath());
            Files.deleteIfExists(logoFile.toPath());
        }
    }

    private String extractFontName(String fontFileName) {
        return fontFileName.replace(".ttf", "").split("-")[0].trim();
    }

    private File downloadFile(String url, String extension) throws Exception {
        File file = File.createTempFile("aspose_", extension);
        try (InputStream in = Request.get(url).execute().returnContent().asStream(); FileOutputStream out = new FileOutputStream(file)) {
            in.transferTo(out);
        }
        return file;
    }
}
package com.mylearning.poc.service;

import com.aspose.psd.FontSettings;
import com.aspose.psd.Image;
import com.aspose.psd.RasterImage;
import com.aspose.psd.Rectangle;
import com.aspose.psd.fileformats.psd.PsdImage;
import com.aspose.psd.fileformats.psd.layers.Layer;
import com.aspose.psd.fileformats.psd.layers.TextLayer;
import com.aspose.psd.fileformats.psd.layers.text.ITextPortion;
import com.aspose.psd.fileformats.psd.layers.text.ITextStyle;
import com.aspose.psd.imageoptions.PsdOptions;
import com.mylearning.poc.dto.PsdGenerationRequest;
import com.mylearning.poc.dto.PsdGenerationResponse;
import com.mylearning.poc.exception.PsdGenerationException;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.hc.client5.http.fluent.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

@Service
@Profile("prod")
class generatePsdFromGooglePsd implements AsposePsd {

    @Value("${psd.output.base-path:/Users/prashant/Desktop/test/}")
    private String outputBasePath;

    @Override
    public PsdGenerationResponse generatePsdFromInput(PsdGenerationRequest req) throws Exception {
        String fileBase = UUID.randomUUID().toString().split("-")[0];
        String outputPsd = outputBasePath + fileBase + ".psd";
        String outputPng = outputBasePath + fileBase + ".png";
        String outputJpg = outputBasePath + fileBase + ".jpg";

        generatePsdFromGoogleUrl(
                req.getImageUrl(),   // PSD input signed URL
                req.getLogoUrl(),    // logo input signed URL
                req.getHeaderText(),
                req.getFontUrl(),    // font input signed URL
                req.getFontSize(),
                outputPsd
        );

        return new PsdGenerationResponse(true, outputPsd, outputPng, outputJpg);
    }

    private void generatePsdFromGoogleUrl(String psdUrl, String logoUrl, String headerText,
                                          String fontUrl, int fontSize, String outputBasePath) throws Exception {

        File psdFile = downloadFile(psdUrl);
        File logoFile = downloadFile(logoUrl);
        File fontFile = downloadFile(fontUrl); // downloaded font via signed URL

        // Register font from downloaded font file
        FontSettings.setFontsFolder(fontFile.getParent());

        String fontName = extractFontName(fontFile);
        System.out.println("Extracted Font Name: " + fontName);

        RasterImage logo = null;
        PsdImage psdImage = null;

        try {
            psdImage = (PsdImage) Image.load(psdFile.getAbsolutePath());
            logo = (RasterImage) Image.load(logoFile.getAbsolutePath());

            // Add logo layer
            Layer logoLayer = new Layer(logo);
            logoLayer.setLeft(30);
            logoLayer.setTop(30);
            psdImage.addLayer(logoLayer);

            // Add text layer
            Rectangle textBounds = new Rectangle(0, 0, psdImage.getWidth(), psdImage.getHeight());
            TextLayer textLayer = psdImage.addTextLayer(headerText, textBounds);

            ITextPortion[] portions = textLayer.getTextData().getItems();
            if (portions.length > 0) {
                ITextStyle style = portions[0].getStyle();
                style.setFontSize(fontSize);
                style.setFontName(fontName);
                style.setFillColor(com.aspose.psd.Color.getBlack());
            }

            textLayer.getTextData().updateLayerData();

            // Save final outputs
            psdImage.save(outputBasePath, new PsdOptions());
            psdImage.save(outputBasePath.replace(".psd", ".png"), new com.aspose.psd.imageoptions.PngOptions());
            psdImage.save(outputBasePath.replace(".psd", ".jpg"), new com.aspose.psd.imageoptions.JpegOptions());

        } finally {
            if (logo != null) logo.dispose();
            if (psdImage != null) psdImage.dispose();
            Files.deleteIfExists(psdFile.toPath());
            Files.deleteIfExists(logoFile.toPath());
            Files.deleteIfExists(fontFile.toPath());
        }
    }

    private File downloadFile(String url) throws Exception {
        String extension = getExtensionFromUrl(url);
        File file = File.createTempFile("aspose_", extension);
        try (InputStream in = Request.get(url).execute().returnContent().asStream();
             FileOutputStream out = new FileOutputStream(file)) {
            in.transferTo(out);
        }
        return file;
    }

    private String getExtensionFromUrl(String url) {
        try {
            String ext = url.substring(url.lastIndexOf('.')).toLowerCase();
            if (ext.matches("\\.(jpg|jpeg|png|gif|bmp|webp|ttf|psd)")) {
                return ext;
            }
        } catch (Exception e) {
            throw new PsdGenerationException("Invalid file URL: " + url, e);
        }
        return ".tmp";
    }

    private String extractFontName(File fontFile) throws Exception {
        TTFParser parser = new TTFParser();
        try (TrueTypeFont ttf = parser.parse(fontFile)) {
            return ttf.getName();
        }
    }
}

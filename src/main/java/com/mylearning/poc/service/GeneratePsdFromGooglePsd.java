package com.mylearning.poc.service;

import com.aspose.psd.FontSettings;
import com.aspose.psd.Image;
import com.aspose.psd.Rectangle;
import com.aspose.psd.fileformats.psd.PsdImage;
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
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

@Service
@Profile("prod")
@Primary
class GeneratePsdFromGooglePsd implements AsposePsd {

    @Value("${psd.output.base-path:/Users/prashant/Desktop/test/}")
    private String outputBasePath;

    @Override
    public PsdGenerationResponse generatePsdFromInput(PsdGenerationRequest req) throws Exception {
        String fileBase = UUID.randomUUID().toString().split("-")[0];
        String outputPsd = outputBasePath + fileBase + ".psd";

        generatePsdWithTextOnly(
                req.getPsdUrl(),   // PSD input signed URL
                req.getHeaderText(),
                req.getFontUrl(),    // font input signed URL
                req.getFontSize(),
                outputPsd
        );

        return new PsdGenerationResponse(true, outputPsd, null, null);
    }

    private void generatePsdWithTextOnly(String psdUrl, String headerText,
                                         String fontUrl, int fontSize, String outputPath) throws Exception {

        File psdFile = downloadFile(psdUrl);
        File fontFile = downloadFile(fontUrl);

        // Register font from downloaded .ttf file
        FontSettings.setFontsFolder(fontFile.getParent());

        // Extract font name
        String fontName = extractFontName(fontFile);
        System.out.println("Extracted Font Name: " + fontName);

        try (PsdImage psdImage = (PsdImage) Image.load(psdFile.getAbsolutePath())) {

            // Add text layer
            Rectangle bounds = new Rectangle(0, 0, psdImage.getWidth(), psdImage.getHeight());
            TextLayer textLayer = psdImage.addTextLayer(headerText, bounds);

            ITextPortion[] portions = textLayer.getTextData().getItems();
            if (portions.length > 0) {
                ITextStyle style = portions[0].getStyle();
                style.setFontSize(fontSize);
                style.setFontName(fontName);
                style.setFillColor(com.aspose.psd.Color.getBlack());
            }

            textLayer.getTextData().updateLayerData();

            // Save only PSD output
            psdImage.save(outputPath, new PsdOptions());
        } finally {
            Files.deleteIfExists(psdFile.toPath());
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
            if (ext.matches("\\.(psd|ttf)")) {
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

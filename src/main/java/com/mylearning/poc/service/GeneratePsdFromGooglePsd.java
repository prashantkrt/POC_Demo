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
import lombok.extern.slf4j.Slf4j;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.hc.client5.http.fluent.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@Profile("prod")
@Primary
@Slf4j
class GeneratePsdFromGooglePsd implements AsposePsd {

    private final ExecutorService executor;

    GeneratePsdFromGooglePsd(ExecutorService executor) {
        this.executor = executor;
    }

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

        return new PsdGenerationResponse(true, outputPsd);
    }

    private void generatePsdWithTextOnly(String psdUrl, String headerText,
                                         String fontUrl, int fontSize, String outputPath) throws Exception {

        long start = System.currentTimeMillis();

        // Parallel download using CompletableFuture
        CompletableFuture<File> psdFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return downloadFileWithRetry(psdUrl, 3);
            } catch (Exception e) {
                throw new PsdGenerationException("Failed to download PSD from URL: " + psdUrl, e);
            }
        }, executor);

        CompletableFuture<File> fontFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return downloadFileWithRetry(fontUrl, 3);
            } catch (Exception e) {
                throw new PsdGenerationException("Failed to download font from URL: " + fontUrl, e);
            }
        }, executor);

        CompletableFuture<Void> combined = CompletableFuture.allOf(psdFuture, fontFuture);
        combined.get(); // wait until both complete

        File psdFile = psdFuture.join();
        File fontFile = fontFuture.join();

        // Clean temp files when done
        psdFuture.whenComplete((file, ex) -> safelyDelete(file));
        fontFuture.whenComplete((file, ex) -> safelyDelete(file));

        // Register font from a downloaded .ttf file
        FontSettings.setFontsFolder(fontFile.getParent());
        String fontName = extractFontName(fontFile);
        log.info(" Extracted Font Name: {}", fontName);

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

            psdImage.save(outputPath, new PsdOptions());
        } finally {
            log.info("PSD generation took {} ms", System.currentTimeMillis() - start);
        }
    }

    private File downloadFileWithRetry(String url, int attempts) throws Exception {
        for (int i = 0; i < attempts; i++) {
            try {
                return downloadFile(url);
            } catch (Exception e) {
                log.warn("Attempt {}/{} failed for URL: {}", (i + 1), attempts, url);
                if (i == attempts - 1) throw e;
                Thread.sleep(300);
            }
        }
        throw new IOException("All attempts to download failed for URL: " + url);
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

    private void safelyDelete(File file) {
        try {
            if (file != null && file.exists()) {
                Files.deleteIfExists(file.toPath());
                log.debug("🗑 Temp file deleted: {}", file.getAbsolutePath());
            }
        } catch (Exception e) {
            log.warn("⚠️ Failed to delete temp file: {}", file.getAbsolutePath(), e);
        }
    }
}

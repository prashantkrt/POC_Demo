//package com.mylearning.poc.service;
//
//import com.aspose.psd.Color;
//import com.aspose.psd.Image;
//import com.aspose.psd.RasterImage;
//import com.aspose.psd.Rectangle;
//import com.aspose.psd.fileformats.psd.PsdImage;
//import com.aspose.psd.fileformats.psd.layers.Layer;
//import com.aspose.psd.fileformats.psd.layers.TextLayer;
//import com.aspose.psd.fileformats.psd.layers.text.ITextPortion;
//import com.aspose.psd.fileformats.psd.layers.text.ITextStyle;
//import com.aspose.psd.imageoptions.PsdOptions;
//import com.mylearning.poc.dto.PsdGenerationRequest;
//import com.mylearning.poc.dto.PsdGenerationResponse;
//import com.mylearning.poc.exception.PsdGenerationException;
//import org.apache.hc.client5.http.fluent.Request;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.util.UUID;
//
//@Service
//@Profile("prod")
//public class AsposePsdServiceImpl implements AsposePsd {
//
//    @Value("${psd.output.base-path:/Users/prashant/Desktop/test/}")
//    private String outputBasePath;
//
//    @Override
//    public PsdGenerationResponse generatePsdFromInput(PsdGenerationRequest req) throws Exception {
//
//        String fileBase = UUID.randomUUID().toString().split("-")[0];
//        String outputPsd = outputBasePath + fileBase + ".psd";
//        String outputPng = outputBasePath + fileBase + ".png";
//        String outputJpg = outputBasePath + fileBase + ".jpg";
//
//        generatePsdUsingLocalFont(
//                req.getImageUrl(),
//                req.getLogoUrl(),
//                req.getHeaderText(),
//                req.getFontUrl(),
//                req.getFontSize(),
//                outputPsd
//        );
//
//        return new PsdGenerationResponse(true, outputPsd, outputPng, outputJpg);
//    }
//
//    private void generatePsdUsingLocalFont(String imageUrl, String logoUrl, String headerText,
//                                           String fontPath, int fontSize, String outputPath) throws Exception {
//
//        File bgImageFile = downloadFile(imageUrl);
//        File logoFile = downloadFile(logoUrl);
//        File fontFile = new File(fontPath);
//
//        if (!fontFile.exists()) {
//            throw new IllegalArgumentException("Font file not found: " + fontPath);
//        }
//
//        // Register font directory with Aspose
//        com.aspose.psd.FontSettings.setFontsFolder(fontFile.getParent());
//
//        // Extract font name from filename
//        String fontName = extractFontName(fontFile.getName());
//
//        RasterImage bgImage = null;
//        RasterImage logo = null;
//
//        try {
//            bgImage = (RasterImage) Image.load(bgImageFile.getAbsolutePath());
//            logo = (RasterImage) Image.load(logoFile.getAbsolutePath());
//
//            try (PsdImage psdImage = new PsdImage(bgImage.getWidth(), bgImage.getHeight())) {
//                // Background
//                psdImage.addLayer(new Layer(bgImage));
//
//                // Logo
//                Layer logoLayer = new Layer(logo);
//                logoLayer.setLeft(30);
//                logoLayer.setTop(30);
//                psdImage.addLayer(logoLayer);
//
//                // Text
//                Rectangle textBounds = new Rectangle(0, 0, psdImage.getWidth(), psdImage.getHeight());
//                TextLayer textLayer = psdImage.addTextLayer(headerText, textBounds);
//
//                ITextPortion[] portions = textLayer.getTextData().getItems();
//                if (portions.length > 0) {
//                    ITextStyle style = portions[0].getStyle();
//                    style.setFontSize(fontSize);
//                    style.setFontName(fontName); // Extracted font name
//                    style.setFillColor(Color.getBlack());
//                }
//
//                textLayer.getTextData().updateLayerData();
//
//                // Save
//                psdImage.save(outputPath, new PsdOptions());
//                psdImage.save(outputPath.replace(".psd", ".png"), new com.aspose.psd.imageoptions.PngOptions());
//                psdImage.save(outputPath.replace(".psd", ".jpg"), new com.aspose.psd.imageoptions.JpegOptions());
//            }
//
//        } finally {
//            if (bgImage != null) bgImage.dispose();
//            if (logo != null) logo.dispose();
//            Files.deleteIfExists(bgImageFile.toPath());
//            Files.deleteIfExists(logoFile.toPath());
//        }
//    }
//
//    private File downloadFile(String url) throws Exception {
//        String extension = getExtensionFromUrl(url);
//        File file = File.createTempFile("aspose_", extension);
//        try (InputStream in = Request.get(url).execute().returnContent().asStream();
//             FileOutputStream out = new FileOutputStream(file)) {
//            in.transferTo(out);
//        }
//        return file;
//    }
//
//    private String getExtensionFromUrl(String url) {
//        try {
//            String ext = url.substring(url.lastIndexOf('.')).toLowerCase();
//            if (ext.matches("\\.(jpg|jpeg|png|gif|bmp|webp)")) {
//                return ext;
//            }
//        } catch (Exception e) {
//            throw new PsdGenerationException("Invalid image URL: " + url, e);
//        }
//        return ".img";
//    }
//
//    private String extractFontName(String fontFileName) {
//        return fontFileName.replace(".ttf", "").replace(".otf", "").trim();
//    }
//}
//

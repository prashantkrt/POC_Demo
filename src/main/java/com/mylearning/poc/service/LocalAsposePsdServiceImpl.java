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
import com.mylearning.poc.dto.PsdGenerationRequest;
import com.mylearning.poc.dto.PsdGenerationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Service
@Profile("local")
@Slf4j
public class LocalAsposePsdServiceImpl implements AsposePsd {

    @Value("${psd.output.base-path:/Users/prashant/Desktop/test/}")
    private String outputBasePath;

    @Override
    public PsdGenerationResponse generatePsdFromInput(PsdGenerationRequest req) throws Exception {
        File bgImageFile = new File(req.getImageUrl());
        File logoFile = new File(req.getLogoUrl());
        File fontFile = new File(req.getFontUrl());

        if (!bgImageFile.exists() || !logoFile.exists() || !fontFile.exists()) {
            throw new IllegalArgumentException("Image, logo, or font file not found on disk.");
        }

        // Register local font folder
        com.aspose.psd.FontSettings.setFontsFolder(fontFile.getParent());

        // Extract font name from file
        String fontName = extractFontName(fontFile.getName());

        String fileBase = UUID.randomUUID().toString().split("-")[0];
        String basePath = outputBasePath + fileBase;
        String outputPsd = basePath + ".psd";
        String outputPng = basePath + ".png";
        String outputJpg = basePath + ".jpg";

        RasterImage bgImage = null;
        RasterImage logo = null;

        try {
            bgImage = (RasterImage) Image.load(bgImageFile.getAbsolutePath());
            logo = (RasterImage) Image.load(logoFile.getAbsolutePath());

            try (PsdImage psdImage = new PsdImage(bgImage.getWidth(), bgImage.getHeight())) {

                // Background
                psdImage.addLayer(new Layer(bgImage));

                // Logo
                Layer logoLayer = new Layer(logo);
                logoLayer.setLeft(30);
                logoLayer.setTop(30);
                psdImage.addLayer(logoLayer);

                // Text
                Rectangle textBounds = new Rectangle(0, 0, psdImage.getWidth(), psdImage.getHeight());
                TextLayer textLayer = psdImage.addTextLayer(req.getHeaderText(), textBounds);

                ITextPortion[] portions = textLayer.getTextData().getItems();
                if (portions.length > 0) {
                    ITextStyle style = portions[0].getStyle();
                    style.setFontSize(req.getFontSize());
                    style.setFontName(fontName); // ✅ use extracted font name
                    style.setFillColor(Color.getBlack());
                }

                textLayer.getTextData().updateLayerData();

                // Save files
                psdImage.save(outputPsd, new PsdOptions());
                psdImage.save(outputPng, new com.aspose.psd.imageoptions.PngOptions());
                psdImage.save(outputJpg, new com.aspose.psd.imageoptions.JpegOptions());
            }

        } finally {
            if (bgImage != null) bgImage.dispose();
            if (logo != null) logo.dispose();
        }

        return new PsdGenerationResponse(true, outputPsd, outputPng, outputJpg);
    }

    private String extractFontName(String fontFileName) {
        return fontFileName
                .replace(".ttf", "")
                .replace(".otf", "")
                .trim();
    }
}

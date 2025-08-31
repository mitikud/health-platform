package com.medical.diagnosisservice.service.ocr.impl;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.medical.diagnosisservice.service.ocr.OcrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


import java.io.InputStream;
import java.util.List;

@Profile("prod")
@Component
public class GoogleVisionOcrClient implements OcrClient {

    @Value("#{'${ocr.vision.hints:en,am,ti}'.split(',')}")
    private List<String> hints;

    @Override
    public String extractText(InputStream imageStream, String filename) throws Exception {
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            ByteString content = ByteString.readFrom(imageStream);
            Image img = Image.newBuilder().setContent(content).build();

            ImageContext ctx = ImageContext.newBuilder().addAllLanguageHints(hints).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest req = AnnotateImageRequest.newBuilder()
                    .setImage(img)
                    .addFeatures(feat)
                    .setImageContext(ctx)
                    .build();

            BatchAnnotateImagesResponse resp = client.batchAnnotateImages(List.of(req));
            AnnotateImageResponse r = resp.getResponses(0);
            if (r.hasError()) throw new RuntimeException("Vision error: " + r.getError().getMessage());
            return r.getFullTextAnnotation() != null ? r.getFullTextAnnotation().getText() : "";
        }
    }
}

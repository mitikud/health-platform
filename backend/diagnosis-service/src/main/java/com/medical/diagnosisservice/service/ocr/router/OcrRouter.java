package com.medical.diagnosisservice.service.ocr.router;

import com.medical.diagnosisservice.service.ocr.OcrClient;
import com.medical.diagnosisservice.util.ProcessingMode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Optional;

@Component
public class OcrRouter implements OcrClient {
    private final Optional<OcrClient> local;  // TessOcrClient
    private final Optional<OcrClient> cloud;  // GoogleVisionOcrClient
    private static final ThreadLocal<ProcessingMode> MODE = new ThreadLocal<>();

    public static void setMode(ProcessingMode m){ MODE.set(m); }
    public static void clearMode(){ MODE.remove(); }

    public OcrRouter(@Qualifier("localOcr") Optional<OcrClient> local,
                     @Qualifier("cloudOcr") Optional<OcrClient> cloud) {
        this.local = local; this.cloud = cloud;
    }

    @Override
    public String extractText(InputStream imageStream, String filename) throws Exception {
        var m = MODE.get();
        try {
            if (m == ProcessingMode.LOCAL && local.isPresent())
                return local.get().extractText(imageStream, filename);
            if (m == ProcessingMode.CLOUD && cloud.isPresent())
                return cloud.get().extractText(imageStream, filename);
            if (cloud.isPresent()) return cloud.get().extractText(imageStream, filename);
            if (local.isPresent()) return local.get().extractText(imageStream, filename);
            throw new IllegalStateException("No OCR provider available");
        } finally { clearMode(); }
    }
}


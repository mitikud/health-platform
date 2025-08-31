package com.medical.diagnosisservice.service.stt.router;

import com.medical.diagnosisservice.service.stt.SttClient;
import com.medical.diagnosisservice.util.ProcessingMode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Optional;

@Component
public class SttRouter implements SttClient {
    private final Optional<SttClient> local;  // e.g., VoskSttClient (@Qualifier("localStt"))
    private final Optional<SttClient> cloud;  // e.g., WhisperSttClient (@Qualifier("cloudStt"))
    private static final ThreadLocal<ProcessingMode> MODE = new ThreadLocal<>();

    public SttRouter(@Qualifier("localStt") Optional<SttClient> local,
                     @Qualifier("cloudStt") Optional<SttClient> cloud) {
        this.local = local; this.cloud = cloud;
    }

    // helper to set mode per request
    public static void setMode(ProcessingMode m) { MODE.set(m); }
    public static void clearMode() { MODE.remove(); }

    @Override
    public String transcribe(InputStream audio, String filename, String locale) throws Exception {
        var m = MODE.get();
        try {
            if (m == ProcessingMode.LOCAL && local.isPresent())
                return local.get().transcribe(audio, filename, locale);
            if (m == ProcessingMode.CLOUD && cloud.isPresent())
                return cloud.get().transcribe(audio, filename, locale);

            // AUTO: prefer cloud if present, else local
            if (cloud.isPresent()) return cloud.get().transcribe(audio, filename, locale);
            if (local.isPresent()) return local.get().transcribe(audio, filename, locale);
            throw new IllegalStateException("No STT provider available");
        } finally {
            clearMode();
        }
    }
}


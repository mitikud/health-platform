package com.medical.diagnosisservice.service.stt.impl;

import com.medical.diagnosisservice.service.stt.SttClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Profile({"prod","offline","default"})
@Component("localStt")
public class VoskSttClient implements SttClient {

    @Value("${ai.vosk.modelPath}") private String modelPath;
    @Value("${ai.vosk.sampleRate:16000}") private int sampleRate;

    private volatile Model model;

    private Model model() throws IOException {
        if (model == null) {
            synchronized (this) {
                if (model == null) model = new Model(modelPath);
            }
        }
        return model;
    }

    @Override
    public String transcribe(InputStream audio, String filename, String locale) throws Exception {
        // Expect 16-bit PCM mono at sampleRate. If your uploads are WAV/MP3,
        // decode & resample before this step (e.g., ffmpeg in an init container or a tiny Java decoder).
        try (Recognizer rec = new Recognizer(model(), sampleRate)) {
            byte[] buf = audio.readAllBytes();
            // interpret as little-endian 16-bit PCM
            ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
            byte[] chunk = new byte[Math.min(4096, buf.length)];
            int off = 0;
            while (off < buf.length) {
                int len = Math.min(chunk.length, buf.length - off);
                System.arraycopy(buf, off, chunk, 0, len);
                rec.acceptWaveForm(chunk, len);
                off += len;
            }
            String resultJson = rec.getFinalResult();
            // resultJson is like {"text":"..."}; extract "text" naïvely:
            int i = resultJson.indexOf("\"text\":\"");
            if (i >= 0) {
                int j = resultJson.indexOf("\"", i + 8);
                return j > i ? resultJson.substring(i + 8, j) : "";
            }
            return "";
        }
    }
}

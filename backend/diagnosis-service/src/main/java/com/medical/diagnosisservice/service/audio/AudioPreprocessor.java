package com.medical.diagnosisservice.service.audio;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.UUID;

@Component
public class AudioPreprocessor {

    public static final int TARGET_RATE = 16000; // 16kHz mono PCM

    /**
     * Converts any common audio (wav, mp3, m4a, webm, etc.) to
     * 16kHz mono 16-bit PCM WAV and returns a File handle.
     * Caller is responsible for deleting the returned temp file.
     */
    public File toPcm16kWav(MultipartFile audio) {
        try {
            // Save input to a temp file
            File input = Files.createTempFile("in_", "_" + sanitize(audio.getOriginalFilename())).toFile();
            audio.transferTo(input);

            File output = Files.createTempFile("pcm16k_", ".wav").toFile();
            // ffmpeg -y -i input -ac 1 -ar 16000 -f wav -acodec pcm_s16le output.wav
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg","-y","-i", input.getAbsolutePath(),
                    "-ac","1","-ar", String.valueOf(TARGET_RATE),
                    "-f","wav","-acodec","pcm_s16le",
                    output.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                while (r.readLine() != null) { /* drain */ }
            }
            int code = p.waitFor();
            if (code != 0) throw new RuntimeException("ffmpeg failed with code " + code);
            input.delete(); // cleanup original
            return output;
        } catch (Exception e) {
            throw new RuntimeException("Audio normalization failed", e);
        }
    }

    private String sanitize(String name) {
        if (name == null) return UUID.randomUUID().toString();
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}

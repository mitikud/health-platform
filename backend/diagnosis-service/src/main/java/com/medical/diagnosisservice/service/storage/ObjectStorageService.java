package com.medical.diagnosisservice.service.storage;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public String upload(String prefix, InputStream data, long size, String contentType) {
        try {
            String objectName = prefix + "/" + UUID.randomUUID();
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(data, size, -1)
                    .contentType(contentType)
                    .build();
            minioClient.putObject(args);
            return objectName;
        } catch (MinioException me) {
            throw new RuntimeException("MinIO error: " + me.getMessage(), me);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload object", e);
        }
    }
}

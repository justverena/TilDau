package kz.kbtu.tildau.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class MinioService {
    private final MinioClient minioClient;
    private final String publicUrl;
    private final String bucket;

    public MinioService(@Value("${minio.endpoint}") String endpoint,
                        @Value("${minio.public-url}") String publicUrl,
                        @Value("${minio.access-key}") String accessKey,
                        @Value("${minio.secret-key}") String secretKey,
                        @Value("${minio.bucket.tildau}") String bucket) {
        this.minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        this.publicUrl = publicUrl;
        this.bucket = bucket;
    }

    public String getFileUrl(String objectName) {
        return "/storage/" + objectName;
    }

    public void putObject(String objectName, byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(bais, data.length, -1)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to put object in MinIO: " + objectName, e);
        }
    }
}
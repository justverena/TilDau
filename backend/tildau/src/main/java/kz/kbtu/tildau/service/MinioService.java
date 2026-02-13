package kz.kbtu.tildau.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class MinioService {
    private final MinioClient minioClient;
    private final long urlExpirationSeconds;

    public MinioService(@Value("${minio.url}") String endpoint,
                        @Value("${minio.access-key}") String accessKey,
                        @Value("${minio.secret-key}") String secretKey,
                        @Value("${minio.url-expiration-minutes}") long urlExpirationMinutes) {
        this.minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        this.urlExpirationSeconds = urlExpirationMinutes * 60;
    }

    public String getPresignedUrl(String objectName) {
        try{
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket("tildau")
                            .object(objectName)
                            .expiry((int) urlExpirationSeconds)
                            .build()
            );
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate presigned URL for " + objectName, ex);
        }
    }

    public void putObject(String objectName, byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("tildau")
                            .object(objectName)
                            .stream(bais, data.length, -1)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to put object in MinIO: " + objectName, e);
        }
    }
}
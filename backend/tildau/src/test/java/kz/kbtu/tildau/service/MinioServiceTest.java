package kz.kbtu.tildau.service;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import kz.kbtu.tildau.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    private MinioService minioService;

    @BeforeEach
    void setUp() {
        minioService = new MinioService(
                "http://localhost:9000",
                "minio",
                "minio123",
                10
        );
    }

    @Test
    void putObject_Success() throws Exception {
        byte[] data = "audio".getBytes();

        ObjectWriteResponse mockResponse = mock(ObjectWriteResponse.class);
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(mockResponse);

        ReflectionTestUtils.setField(minioService, "minioClient", minioClient);

        minioService.putObject("test.wav", data);

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void putObject_Fails() throws Exception {
        byte[] data = "audio".getBytes();

        doThrow(new RuntimeException("Minio error")).when(minioClient).putObject(any(PutObjectArgs.class));

        ReflectionTestUtils.setField(minioService, "minioClient", minioClient);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> minioService.putObject("test.wav", data));

        assertTrue(ex.getMessage().contains("Failed to put object"));
    }

    @Test
    void getPresignedUrl_Success() throws Exception {

        when(minioClient.getPresignedObjectUrl(any())).thenReturn("http://url");

        ReflectionTestUtils.setField(minioService, "minioClient", minioClient);

        String url = minioService.getPresignedUrl("file.wav");

        assertEquals("http://url", url);
    }

    @Test
    void getPresignedUrl_Fails() throws Exception {

        when(minioClient.getPresignedObjectUrl(any()))
                .thenThrow(new RuntimeException("error"));

        ReflectionTestUtils.setField(minioService, "minioClient", minioClient);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> minioService.getPresignedUrl("file.wav"));

        assertTrue(ex.getMessage().contains("Failed to generate presigned URL"));
    }
}
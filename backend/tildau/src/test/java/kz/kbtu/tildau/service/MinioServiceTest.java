package kz.kbtu.tildau.service;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    private MinioService minioService;

    @BeforeEach
    void setUp() {
        minioService = new MinioService(
                "http://minio:9000",
                "http://localhost",
                "minio",
                "minio123",
                "tildau"
        );

        ReflectionTestUtils.setField(minioService, "minioClient", minioClient);
    }

    @Test
    void putObject_Success() throws Exception {
        byte[] data = "audio".getBytes();

        ObjectWriteResponse mockResponse = mock(ObjectWriteResponse.class);

        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenReturn(mockResponse);

        minioService.putObject("test.wav", data);

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void putObject_Fails() throws Exception {
        byte[] data = "audio".getBytes();

        doThrow(new RuntimeException("Minio error"))
                .when(minioClient)
                .putObject(any(PutObjectArgs.class));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> minioService.putObject("test.wav", data)
        );

        assertTrue(ex.getMessage().contains("Failed to put object"));
    }

    @Test
    void getFileUrl_Success() {
        String url = minioService.getFileUrl("audio/test.wav");

        assertEquals(
                "/storage/audio/test.wav",
                url
        );
    }
}
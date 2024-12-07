package org.example.vivesbankproject.storage.csvProductos.services;

import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.cuenta.repositories.TipoCuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CsvProductosStorageServiceImplTest {

    @Mock
    private TipoCuentaRepository tipoCuentaRepository;

    @Mock
    private TipoCuentaMapper tipoCuentaMapper;

    private CsvProductosStorageServiceImpl csvProductosStorageService;

    private Path tempDirectory;

    @BeforeEach
    void setUp() throws IOException {
        tempDirectory = Files.createTempDirectory("test-storage");
        csvProductosStorageService = new CsvProductosStorageServiceImpl(tempDirectory.toString(), tipoCuentaRepository, tipoCuentaMapper);
    }

    @Test
    void testConvertToTipoCuentaRequest() {
        String[] data = {"Cuenta Corriente", "3.5"};
        TipoCuentaRequest request = csvProductosStorageService.convertToTipoCuentaRequest(data);

        assertNotNull(request);
        assertEquals("Cuenta Corriente", request.getNombre());
        assertEquals(new BigDecimal("3.5"), request.getInteres());
    }

    @Test
    void testStoreImportedCsv() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", "content".getBytes()
        );

        String storedFilename = csvProductosStorageService.storeImportedCsv(file);

        assertNotNull(storedFilename);
        assertTrue(storedFilename.startsWith("tipos_cuenta_"));
        assertTrue(storedFilename.endsWith(".csv"));
        assertTrue(Files.exists(tempDirectory.resolve(storedFilename)));
    }
}
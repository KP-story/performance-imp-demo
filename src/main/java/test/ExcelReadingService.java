package test;

import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.io.InputStream;

public interface ExcelReadingService {
    void insert(InputStream inputStream) throws Exception;
}

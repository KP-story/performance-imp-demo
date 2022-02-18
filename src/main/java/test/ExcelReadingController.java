package test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@Getter
@Setter
@RequestMapping("/excel-read")
@RestController()
public class ExcelReadingController {
    private ExcelReadingService excelReadingService;

    @PostMapping()
    public ResponseEntity insertToDb(MultipartFile file) throws Exception {
        excelReadingService.insert(file.getInputStream());
        return new ResponseEntity (HttpStatus.OK);
    }
}

package olihef.stratvis.controller;

import com.fasterxml.jackson.databind.JsonNode;
import olihef.stratvis.service.ImageAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ImageAnalysisController {

    private final ImageAnalysisService imageAnalysisService;

    public ImageAnalysisController(ImageAnalysisService imageAnalysisService) {
        this.imageAnalysisService = imageAnalysisService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> analyzeImage(@RequestPart("image") MultipartFile image) throws IOException {
        return ResponseEntity.ok(imageAnalysisService.analyze(image));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConfigOrParsingError(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({IOException.class, RestClientException.class})
    public ResponseEntity<Map<String, String>> handleProviderOrIoError(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", "Failed to analyze image with OpenAI API."));
    }
}

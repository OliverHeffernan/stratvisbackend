package olihef.stratvis.service;

import olihef.stratvis.config.CorsConfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import olihef.stratvis.prompt.AnalysisPrompts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class ImageAnalysisService {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final String apiUrl;
	private final String model;
	private final String apiKey;
	private final String analysisSchemaText;
	private final JsonNode analysisJsonSchema;

	public ImageAnalysisService(
		RestTemplate restTemplate,
		ObjectMapper objectMapper,
		@Value("${openai.api.url}") String apiUrl,
		@Value("${openai.model}") String model,
		@Value("${openai.api.key:}") String apiKey
	) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
		this.apiUrl = apiUrl;
		this.model = model;
		this.apiKey = apiKey;
		try {
			ClassPathResource schemaResource = new ClassPathResource("analysis-output-schema.json");
			this.analysisSchemaText = new String(schemaResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			this.analysisJsonSchema = objectMapper.readTree(analysisSchemaText);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to initialize analysis JSON schema.", e);
		}
	}

	public JsonNode analyze(MultipartFile image) throws IOException {
		if (apiKey == null || apiKey.isBlank()) {
			throw new IllegalStateException("OPENAI_API_KEY is not configured.");
		}
		if (image == null || image.isEmpty()) {
			throw new IllegalArgumentException("Image file is required.");
		}

		String mimeType = image.getContentType();
		if (mimeType == null || mimeType.isBlank()) {
			mimeType = MediaType.IMAGE_JPEG_VALUE;
		}
		String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
		String imageDataUrl = "data:%s;base64,%s".formatted(mimeType, base64Image);

		Map<String, Object> requestBody = Map.of(
			"model", model,
			"response_format", Map.of(
				"type", "json_schema",
				"json_schema", Map.of(
					"name", "stratvis_environmental_analysis",
					"strict", true,
					"schema", analysisJsonSchema
				)
			),
			"messages", List.of(
				Map.of("role", "system", "content", AnalysisPrompts.SYSTEM_PROMPT),
				Map.of(
					"role", "user",
					"content", List.of(
						Map.of("type", "text", "text", AnalysisPrompts.buildUserPrompt(analysisSchemaText)),
						Map.of("type", "image_url", "image_url", Map.of("url", imageDataUrl))
					)
				)
			)
		);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);
		CorsConfig.addCorsToHeaders(headers);

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
		JsonNode root = objectMapper.readTree(response.getBody());
		JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
		if (contentNode.isMissingNode() || contentNode.asText().isBlank()) {
			throw new IllegalStateException("OpenAI response did not include message content.");
		}
		return objectMapper.readTree(contentNode.asText());
	}
}
package olihef.stratvis.config;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import java.util.List;

public class CorsConfig {
	public static void addCorsToHeaders(HttpHeaders headers) {
		/*
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
		*/
		headers.setAccessControlAllowOrigin("http://localhost:5173");
		headers.setAccessControlAllowCredentials(true);
		headers.setAccessControlAllowMethods(List.of(HttpMethod.POST, HttpMethod.OPTIONS, HttpMethod.GET));
	}
}
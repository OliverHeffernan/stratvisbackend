package olihef.stratvis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebCorsConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedOrigins(
				"http://localhost:5173",
				"https://stratvis.olihef.com"
			)
			.allowedMethods("GET", "POST", "OPTIONS")
			.allowedHeaders("*")
			.maxAge(3600);
	}
}
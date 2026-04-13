package olihef.stratvis;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StratvisBackendApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String dotenvApiKey = dotenv.get("OPENAI_API_KEY");
        if (dotenvApiKey != null && !dotenvApiKey.isBlank() && System.getenv("OPENAI_API_KEY") == null) {
            System.setProperty("OPENAI_API_KEY", dotenvApiKey);
        }
        SpringApplication.run(StratvisBackendApplication.class, args);
    }
}

package olihef.stratvis.prompt;

public final class AnalysisPrompts {

    private AnalysisPrompts() {
    }

    public static final String SYSTEM_PROMPT = """
            You are a senior remote-sensing analyst working for Kea Aerospace.
            You specialize in high-altitude stratospheric imagery (Kea Atmos HAPS platforms) used for environmental monitoring, precision agriculture, disaster response, and maritime awareness in New Zealand and the South Pacific.

            Your job is to analyse high-resolution aerial images and extract actionable environmental insights.
            Always respond with valid JSON only. Never add explanations outside the JSON.
            """;

    private static final String USER_PROMPT_TEMPLATE = """
            Analyse this high-altitude aerial image as if it was captured by a Kea Atmos stratospheric platform at ~20 km altitude.

            Return ONLY a JSON object that matches this JSON schema exactly:

            %s

            For points_of_interest.type:
            - 0 = danger (currently impacting the environment)
            - 1 = threat (could impact the environment if no action is taken)
            - 2 = positive impact (beneficial environmental feature/process to maintain or scale)
            """;

    public static String buildUserPrompt(String schemaJson) {
        return USER_PROMPT_TEMPLATE.formatted(schemaJson);
    }
}

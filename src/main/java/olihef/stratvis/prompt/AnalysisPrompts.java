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

    public static final String USER_PROMPT = """
            Analyse this high-altitude aerial image as if it was captured by a Kea Atmos stratospheric platform at ~20 km altitude.

            Return ONLY a JSON object with exactly these fields:

            {
              "summary": "One-sentence overall environmental assessment",
              "vegetation_health": {
                "status": "healthy" | "stressed" | "poor" | "unknown",
                "canopy_density_percent": number (0-100),
                "greenness_index": number (0-100, proxy for NDVI),
                "explanation": "short description"
              },
              "crop_health": {
                "status": "excellent" | "good" | "fair" | "poor" | "not_applicable",
                "stressed_area_percent": number (0-100),
                "main_issue": "drought" | "pest" | "nutrient_deficiency" | "disease" | "none" | "unknown",
                "explanation": "short description"
              },
              "deforestation": {
                "tree_cover_percent": number (0-100),
                "recent_loss_percent": number (0-100),
                "explanation": "short description"
              },
              "water_quality": {
                "status": "clear" | "turbid" | "polluted" | "algal_bloom" | "unknown",
                "visible_pollution": boolean,
                "explanation": "short description"
              },
              "coastal_erosion": {
                "erosion_detected": boolean,
                "severity": "low" | "medium" | "high" | "none",
                "explanation": "short description"
              },
              "disaster_indicators": {
                "flooding": boolean,
                "fire_scar": boolean,
                "landslide": boolean,
                "other": "string or null",
                "explanation": "short description"
              },
              "maritime_features": {
                "ships_detected": number,
                "oil_spill": boolean,
                "explanation": "short description"
              },
              "points_of_interest": [
                {
                  "x": number (0-100, horizontal position as % from left of image),
                  "y": number (0-100, vertical position as % from top of image),
                  "message": "short description of the point, impact, and action to mitigate/maintain",
                  "long_message": "longer description of the point. Expands on the \"message\" field. Should include actionable suggestions for what should be done about it.",
                  "type": 0 | 1 | 2
                }
              ],
              "land_use": ["agriculture", "native_forest", "urban", "wetland", ...]
            }

            For points_of_interest.type:
            - 0 = danger (currently impacting the environment)
            - 1 = threat (could impact the environment if no action is taken)
            - 2 = positive impact (beneficial environmental feature/process to maintain or scale)

            Include a confidence score (0-100) for each top-level category if you want, but keep the structure exactly as shown.
            """;
}

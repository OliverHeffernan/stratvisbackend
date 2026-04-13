package olihef.stratvis.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class AnalyzeRateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_MILLIS = 30_000;
    private static final String ANALYZE_PATH = "/api/v1/analyze";

    private final ConcurrentHashMap<String, Long> lastRequestByUser = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!shouldRateLimit(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        long now = System.currentTimeMillis();
        String userKey = resolveUserKey(request);
        AtomicBoolean allowed = new AtomicBoolean(false);
        AtomicLong retryAfterSeconds = new AtomicLong(0);

        lastRequestByUser.compute(userKey, (key, lastRequestTime) -> {
            if (lastRequestTime == null || now - lastRequestTime >= WINDOW_MILLIS) {
                allowed.set(true);
                return now;
            }

            long remainingMillis = WINDOW_MILLIS - (now - lastRequestTime);
            retryAfterSeconds.set((remainingMillis + 999) / 1000);
            allowed.set(false);
            return lastRequestTime;
        });

        if (!allowed.get()) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds.get()));
            response.getWriter().write("{\"error\":\"Rate limit exceeded. Please wait before retrying.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldRateLimit(HttpServletRequest request) {
        return HttpMethod.POST.matches(request.getMethod()) && ANALYZE_PATH.equals(request.getRequestURI());
    }

    private String resolveUserKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
package olihef.stratvis.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AnalyzeRateLimitFilterTest {

    @Test
    void secondAnalyzeRequestFromSameUserWithinThirtySecondsReturns429() throws Exception {
        AnalyzeRateLimitFilter filter = new AnalyzeRateLimitFilter();

        MockHttpServletRequest request1 = new MockHttpServletRequest("POST", "/api/v1/analyze");
        request1.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        filter.doFilter(request1, response1, new MockFilterChain());

        assertEquals(200, response1.getStatus());

        MockHttpServletRequest request2 = new MockHttpServletRequest("POST", "/api/v1/analyze");
        request2.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        filter.doFilter(request2, response2, new MockFilterChain());

        assertEquals(429, response2.getStatus());
        assertNotNull(response2.getHeader("Retry-After"));
        assertEquals("{\"error\":\"Rate limit exceeded. Please wait before retrying.\"}", response2.getContentAsString());
    }

    @Test
    void differentPathsAreNotRateLimited() throws Exception {
        AnalyzeRateLimitFilter filter = new AnalyzeRateLimitFilter();

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/other");
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(200, response.getStatus());
    }
}

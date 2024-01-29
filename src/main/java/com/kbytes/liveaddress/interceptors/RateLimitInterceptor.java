package com.kbytes.liveaddress.interceptors;

import com.kbytes.liveaddress.configs.Constants;
import com.kbytes.liveaddress.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientId = getClientId(request);
        if (rateLimitService.exceedsRateLimit(clientId)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write(Constants.RATE_LIMIT_ERROR_MSG);
            return false;
        }
        return true;
    }

    /**
     * Extract client ID from the request (e.g., API key, user ID)
     * This could be a header, query parameter, or part of the request body
     */
    private String getClientId(HttpServletRequest request) {
        return request.getHeader(Constants.REQUEST_CLIENT_HEADER_NAME);
    }
}

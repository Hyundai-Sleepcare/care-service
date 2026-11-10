package com.sleepcare.server.common.owner;

import com.sleepcare.server.common.error.ApiException;
import com.sleepcare.server.common.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class OwnerIdInterceptor implements HandlerInterceptor {

    public static final String HEADER = "X-Owner-Id";
    public static final String ATTRIBUTE = "ownerId";

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader(HEADER);
        if (header == null) {
            throw new ApiException(ErrorCode.OWNER_ID_REQUIRED);
        }
        String ownerId = header.trim().toLowerCase();
        if (!UUID_PATTERN.matcher(ownerId).matches()) {
            throw new ApiException(ErrorCode.OWNER_ID_REQUIRED);
        }
        request.setAttribute(ATTRIBUTE, ownerId);
        return true;
    }
}

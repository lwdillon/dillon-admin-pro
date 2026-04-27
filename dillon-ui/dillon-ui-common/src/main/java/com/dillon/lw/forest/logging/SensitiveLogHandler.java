package com.dillon.lw.forest.logging;

import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.LogHeaderMessage;
import com.dtflys.forest.logging.RequestLogMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Forest 请求日志脱敏处理器。
 * <p>
 * 保留请求方法、URL 等调试信息，但对认证类请求头做掩码，
 * 避免 token / cookie 等敏感信息直接出现在日志中。
 */
public class SensitiveLogHandler extends DefaultLogHandler {

    private static final String MASK = "***";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String BASIC_PREFIX = "Basic ";
    private static final Set<String> SENSITIVE_HEADERS = new HashSet<>(Arrays.asList(
            "authorization",
            "proxy-authorization",
            "cookie",
            "set-cookie",
            "x-api-key",
            "api-key"
    ));

    @Override
    protected String requestLoggingHeaders(RequestLogMessage requestLogMessage) {
        List<LogHeaderMessage> headers = requestLogMessage.getHeaders();
        if (headers == null || headers.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            LogHeaderMessage header = headers.get(i);
            builder.append("\t\t")
                    .append(header.getName())
                    .append(": ")
                    .append(maskHeaderValue(header.getName(), header.getValue()));
            if (i < headers.size() - 1) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private String maskHeaderValue(String headerName, String headerValue) {
        if (StringUtils.isBlank(headerName) || StringUtils.isEmpty(headerValue)) {
            return headerValue;
        }
        String normalizedHeaderName = headerName.trim().toLowerCase(Locale.ROOT);
        if (!SENSITIVE_HEADERS.contains(normalizedHeaderName)) {
            return headerValue;
        }
        if ("authorization".equals(normalizedHeaderName) || "proxy-authorization".equals(normalizedHeaderName)) {
            return maskAuthorizationValue(headerValue);
        }
        return MASK;
    }

    private String maskAuthorizationValue(String headerValue) {
        String trimmedValue = headerValue.trim();
        if (StringUtils.startsWithIgnoreCase(trimmedValue, BEARER_PREFIX)) {
            return BEARER_PREFIX + maskToken(trimmedValue.substring(BEARER_PREFIX.length()).trim());
        }
        if (StringUtils.startsWithIgnoreCase(trimmedValue, BASIC_PREFIX)) {
            return BASIC_PREFIX + MASK;
        }
        return maskToken(trimmedValue);
    }

    private String maskToken(String token) {
        if (StringUtils.isBlank(token)) {
            return MASK;
        }
        if (token.length() <= 10) {
            return MASK;
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}

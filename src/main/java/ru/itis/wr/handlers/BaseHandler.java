package ru.itis.wr.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class BaseHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected void sendJsonResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String json = objectMapper.writeValueAsString(data);
        resp.getWriter().write(json);
    }

    protected void sendJsonError(HttpServletResponse resp, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String json = objectMapper.writeValueAsString(java.util.Map.of("error", message));
        resp.getWriter().write(json);
    }
}
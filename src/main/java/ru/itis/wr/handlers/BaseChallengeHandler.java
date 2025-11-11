package ru.itis.wr.handlers;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class BaseChallengeHandler {
    protected void sendJsonResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"result\": \"success\", \"data\": " + data.toString() + "}");
    }

    protected void sendJsonError(HttpServletResponse resp, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}

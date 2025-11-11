package ru.itis.wr.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.User;

import java.io.IOException;

public interface ChallengeHandler {
    boolean canHandle(String path, String method);
    void handle(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException;
}

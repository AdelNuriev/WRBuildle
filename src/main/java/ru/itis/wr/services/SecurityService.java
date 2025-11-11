package ru.itis.wr.services;

import ru.itis.wr.entities.User;

public interface SecurityService {
    User getUser(String sessionId);
    String registerUser(String username, String email, String password, String passwordRepeat);
    String loginUser(String email, String password);
    void logoutUser(String sessionId);
    boolean validateSession(String sessionId);
}

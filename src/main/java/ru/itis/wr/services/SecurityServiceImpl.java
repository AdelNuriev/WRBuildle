package ru.itis.wr.services;

import ru.itis.wr.entities.User;
import ru.itis.wr.entities.Role;
import ru.itis.wr.exceptions.AuthenticationException;
import ru.itis.wr.helper.PasswordHasher;
import ru.itis.wr.repositories.UserRepository;
import ru.itis.wr.repositories.SessionRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class SecurityServiceImpl implements SecurityService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordHasher passwordHasher;

    private static final int SESSION_DURATION_HOURS = 24;

    public SecurityServiceImpl(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordHasher = new PasswordHasher();
    }

    @Override
    public User getUser(String sessionId) {
        var sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty() || sessionOpt.get().getExpireAt().isBefore(LocalDateTime.now())) {
            return null;
        }

        return userRepository.findById(sessionOpt.get().getUserId()).orElse(null);
    }

    @Override
    public String registerUser(String username, String email, String password, String passwordRepeat) {
        if (!password.equals(passwordRepeat)) {
            throw new AuthenticationException("Passwords don't match");
        }

        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username is required");
        }

        if (email == null || !email.contains("@")) {
            throw new AuthenticationException("Invalid email");
        }

        if (password.length() < 6) {
            throw new AuthenticationException("Password must be at least 6 characters long");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new AuthenticationException("User with this email already exists");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new AuthenticationException("Username is already taken");
        }

        String salt = passwordHasher.generateSalt();
        String passwordHash = passwordHasher.hashPassword(password, salt);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setSalt(salt);
        user.setRole(Role.USER);
        user.setCoins(100);
        user.setExperience(0);
        user.setLevel(1);
        user.setCreatedAt(LocalDateTime.now());

        Long userId = userRepository.save(user);
        user.setId(userId);

        return createSession(userId);
    }

    @Override
    public String loginUser(String email, String password) {
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Invalid email or password");
        }

        User user = userOpt.get();

        if (!passwordHasher.verifyPassword(password, user.getPasswordHash(), user.getSalt())) {
            throw new AuthenticationException("Invalid email or password");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.update(user);

        return createSession(user.getId());
    }

    @Override
    public void logoutUser(String sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    @Override
    public boolean validateSession(String sessionId) {
        var sessionOpt = sessionRepository.findById(sessionId);
        return sessionOpt.isPresent() && sessionOpt.get().getExpireAt().isAfter(LocalDateTime.now());
    }

    private String createSession(Long userId) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime expireAt = LocalDateTime.now().plusHours(SESSION_DURATION_HOURS);

        sessionRepository.save(new ru.itis.wr.entities.Session(sessionId, userId, expireAt));
        return sessionId;
    }
}

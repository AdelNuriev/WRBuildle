package ru.itis.wr.services;

import ru.itis.wr.entities.UserStatistics;
import ru.itis.wr.entities.BlockType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatisticsService {
    UserStatistics getUserStatistics(Long userId);
    List<Map<String, Object>> getRecentUserResults(Long userId, int days);
    Map<BlockType, Map<String, Object>> getBlockTypeStatistics(Long userId);
    List<Map<String, Object>> getLeaderboard(String period, LocalDate date);
    Map<String, Object> getAttemptsGraphData(Long userId, String blockType, int days);
    void updateUserStatistics(Long userId, int score, boolean won);
}

package ru.itis.wr.services;

import ru.itis.wr.entities.*;
import ru.itis.wr.helper.RepositoryHelper;
import ru.itis.wr.repositories.UserResultRepository;
import ru.itis.wr.repositories.UserStatisticsRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsServiceImpl implements StatisticsService {
    private final UserResultRepository userResultRepository;
    private final UserStatisticsRepository userStatisticsRepository;
    private final RepositoryHelper repositoryHelper;

    public StatisticsServiceImpl(UserResultRepository userResultRepository,
                                 UserStatisticsRepository userStatisticsRepository,
                                 RepositoryHelper repositoryHelper) {
        this.userResultRepository = userResultRepository;
        this.userStatisticsRepository = userStatisticsRepository;
        this.repositoryHelper = repositoryHelper;
    }

    @Override
    public UserStatistics getUserStatistics(Long userId) {
        return userStatisticsRepository.findByUserId(userId)
                .orElse(new UserStatistics(userId));
    }

    @Override
    public List<Map<String, Object>> getRecentUserResults(Long userId, int days) {
        List<UserResult> results = userResultRepository.findRecentByUserId(userId, days);

        return results.stream().map(result -> Map.<String, Object>of(
                "date", result.getChallengeDate(),
                "blockType", result.getBlockType().name(),
                "attempts", result.getAttempts(),
                "completed", result.getCompleted(),
                "score", result.getScore()
        )).collect(Collectors.toList());
    }

    @Override
    public Map<BlockType, Map<String, Object>> getBlockTypeStatistics(Long userId) {
        Map<BlockType, Map<String, Object>> stats = new HashMap<>();

        for (BlockType blockType : BlockType.values()) {
            List<UserResult> results = userResultRepository.findRecentByUserId(userId, 30).stream()
                    .filter(r -> r.getBlockType() == blockType)
                    .collect(Collectors.toList());

            long totalGames = results.size();
            long wins = results.stream().filter(UserResult::getCompleted).count();
            double winRate = totalGames > 0 ? (double) wins / totalGames * 100 : 0;
            int totalScore = results.stream().mapToInt(UserResult::getScore).sum();
            double avgAttempts = totalGames > 0 ?
                    results.stream().mapToInt(UserResult::getAttempts).average().orElse(0) : 0;

            stats.put(blockType, Map.of(
                    "totalGames", totalGames,
                    "wins", wins,
                    "winRate", Math.round(winRate * 100) / 100.0,
                    "totalScore", totalScore,
                    "avgAttempts", Math.round(avgAttempts * 100) / 100.0
            ));
        }

        return stats;
    }

    @Override
    public List<Map<String, Object>> getLeaderboard(String period, LocalDate date) {
        int limit = 10;
        List<UserStatistics> topStats = userStatisticsRepository.findTopByTotalScore(limit);

        List<Map<String, Object>> leaderboard = new ArrayList<>();
        int rank = 1;

        for (UserStatistics stats : topStats) {
            leaderboard.add(Map.of(
                    "rank", rank++,
                    "userId", stats.getUserId(),
                    "totalScore", stats.getTotalScore(),
                    "totalGames", stats.getTotalGames(),
                    "winRate", stats.getTotalGames() > 0 ?
                            Math.round((double) stats.getGamesWon() / stats.getTotalGames() * 10000) / 100.0 : 0,
                    "dailyStreak", stats.getDailyStreak()
            ));
        }

        return leaderboard;
    }

    @Override
    public Map<String, Object> getAttemptsGraphData(Long userId, String blockType, int days) {
        List<UserResult> results = userResultRepository.findRecentByUserId(userId, days);

        if (blockType != null && !blockType.isEmpty()) {
            BlockType type = BlockType.valueOf(blockType.toUpperCase());
            results = results.stream()
                    .filter(r -> r.getBlockType() == type)
                    .collect(Collectors.toList());
        }


        Map<LocalDate, List<UserResult>> resultsByDate = results.stream()
                .collect(Collectors.groupingBy(UserResult::getChallengeDate));

        List<String> labels = new ArrayList<>();
        List<Integer> attemptsData = new ArrayList<>();
        List<Integer> scoreData = new ArrayList<>();

        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            String label = date.format(DateTimeFormatter.ofPattern("MM-dd"));
            labels.add(label);

            List<UserResult> dateResults = resultsByDate.getOrDefault(date, Collections.emptyList());
            int totalAttempts = dateResults.stream().mapToInt(UserResult::getAttempts).sum();
            int totalScore = dateResults.stream().mapToInt(UserResult::getScore).sum();

            attemptsData.add(totalAttempts);
            scoreData.add(totalScore);
        }

        return Map.of(
                "labels", labels,
                "attempts", attemptsData,
                "score", scoreData
        );
    }

    @Override
    public void updateUserStatistics(Long userId, int score, boolean won) {
        UserStatistics stats = userStatisticsRepository.findByUserId(userId)
                .orElse(new UserStatistics(userId));

        stats.setTotalGames(stats.getTotalGames() + 1);
        stats.setTotalScore(stats.getTotalScore() + score);

        if (won) {
            stats.setGamesWon(stats.getGamesWon() + 1);
        }

        LocalDate today = LocalDate.now();
        if (stats.getLastDailyPlay() == null ||
                !stats.getLastDailyPlay().equals(today.minusDays(1))) {
            stats.setDailyStreak(1);
        } else {
            stats.setDailyStreak(stats.getDailyStreak() + 1);
        }
        stats.setLastDailyPlay(today);

        if (score > stats.getBestDailyScore()) {
            stats.setBestDailyScore(score);
        }

        repositoryHelper.putUserStatistics(userStatisticsRepository, stats);
    }
}

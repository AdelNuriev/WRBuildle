package ru.itis.wr.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserStatistics {
    private Long id;
    private Long userId;
    private Integer totalGames;
    private Integer gamesWon;
    private Integer totalScore;
    private Integer dailyStreak;
    private LocalDate lastDailyPlay;
    private Integer bestDailyScore;
    private LocalDateTime updatedAt;

    public UserStatistics() {}

    public UserStatistics(Long userId) {
        this.userId = userId;
        this.totalGames = 0;
        this.gamesWon = 0;
        this.totalScore = 0;
        this.dailyStreak = 0;
        this.bestDailyScore = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public UserStatistics(Long id,
                          Long userId,
                          Integer totalGames,
                          Integer gamesWon,
                          Integer totalScore,
                          Integer dailyStreak,
                          LocalDate lastDailyPlay,
                          Integer bestDailyScore,
                          LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.totalGames = totalGames;
        this.gamesWon = gamesWon;
        this.totalScore = totalScore;
        this.dailyStreak = dailyStreak;
        this.lastDailyPlay = lastDailyPlay;
        this.bestDailyScore = bestDailyScore;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getTotalGames() { return totalGames; }
    public void setTotalGames(Integer totalGames) { this.totalGames = totalGames; }
    public Integer getGamesWon() { return gamesWon; }
    public void setGamesWon(Integer gamesWon) { this.gamesWon = gamesWon; }
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
    public Integer getDailyStreak() { return dailyStreak; }
    public void setDailyStreak(Integer dailyStreak) { this.dailyStreak = dailyStreak; }
    public LocalDate getLastDailyPlay() { return lastDailyPlay; }
    public void setLastDailyPlay(LocalDate lastDailyPlay) { this.lastDailyPlay = lastDailyPlay; }
    public Integer getBestDailyScore() { return bestDailyScore; }
    public void setBestDailyScore(Integer bestDailyScore) { this.bestDailyScore = bestDailyScore; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
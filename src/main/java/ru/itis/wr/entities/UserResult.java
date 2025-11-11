package ru.itis.wr.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserResult {
    private Long id;
    private Long userId;
    private LocalDate challengeDate;
    private BlockType blockType;
    private Integer attempts;
    private Boolean completed;
    private Integer score;
    private LocalDateTime completedAt;

    public UserResult() {}

    public UserResult(Long userId, LocalDate challengeDate, BlockType blockType) {
        this.userId = userId;
        this.challengeDate = challengeDate;
        this.blockType = blockType;
        this.attempts = 0;
        this.completed = false;
        this.score = 0;
    }

    public UserResult(Long id,
                      Long userId,
                      LocalDate challengeDate,
                      BlockType blockType,
                      Integer attempts,
                      Boolean completed,
                      Integer score,
                      LocalDateTime completedAt) {
        this.id = id;
        this.userId = userId;
        this.challengeDate = challengeDate;
        this.blockType = blockType;
        this.attempts = attempts;
        this.completed = completed;
        this.score = score;
        this.completedAt = completedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getChallengeDate() { return challengeDate; }
    public void setChallengeDate(LocalDate challengeDate) { this.challengeDate = challengeDate; }
    public BlockType getBlockType() { return blockType; }
    public void setBlockType(BlockType blockType) { this.blockType = blockType; }
    public Integer getAttempts() { return attempts; }
    public void setAttempts(Integer attempts) { this.attempts = attempts; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}

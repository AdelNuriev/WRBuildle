package ru.itis.wr.entities;

import java.sql.Date;
import java.time.LocalDateTime;

public class GameHistory {
    private long id;
    private long userId;
    private GameMode mode;
    private Date challengeDate;
    private BlockType type;
    private long targetItemId;
    private short attempts;
    private boolean completed;
    private int earnedScore;
    private LocalDateTime completedAt;

    public GameHistory(long id,
                       long userId,
                       GameMode mode,
                       Date challengeDate,
                       BlockType type,
                       long targetItemId,
                       short attempts,
                       boolean completed,
                       int earnedScore,
                       LocalDateTime completedAt)
    {
        this.id = id;
        this.userId = userId;
        this.mode = mode;
        this.challengeDate = challengeDate;
        this.type = type;
        this.targetItemId = targetItemId;
        this.attempts = attempts;
        this.completed = completed;
        this.earnedScore = earnedScore;
        this.completedAt = completedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public GameMode getMode() { return mode; }
    public void setMode(GameMode mode) { this.mode = mode; }
    public Date getChallengeDate() { return challengeDate; }
    public void setChallengeDate(Date challengeDate) { this.challengeDate = challengeDate; }
    public BlockType getType() { return type; }
    public void setType(BlockType type) { this.type = type; }
    public long getTargetItemId() { return targetItemId; }
    public void setTargetItemId(long targetItemId) { this.targetItemId = targetItemId; }
    public short getAttempts() { return attempts; }
    public void setAttempts(short attempts) { this.attempts = attempts; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public int getEarnedScore() { return earnedScore; }
    public void setEarnedScore(int earnedScore) { this.earnedScore = earnedScore; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}

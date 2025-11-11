package ru.itis.wr.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyChallenge {
    private Long id;
    private LocalDate challengeDate;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Boolean isActive;

    public DailyChallenge() {}

    public DailyChallenge(Long id, LocalDate challengeDate, Long createdBy) {
        this.id = id;
        this.challengeDate = challengeDate;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public DailyChallenge(Long id,
                          LocalDate challengeDate,
                          Long createdBy,
                          LocalDateTime createdAt,
                          Boolean isActive) {
        this.id = id;
        this.challengeDate = challengeDate;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getChallengeDate() { return challengeDate; }
    public void setChallengeDate(LocalDate challengeDate) { this.challengeDate = challengeDate; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}

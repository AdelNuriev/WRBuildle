package ru.itis.wr.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InfiniteGame {
    private Long id;
    private Long userId;
    private Item currentTarget;
    private int score;
    private int streak;
    private List<Item> previousItems;
    private LocalDateTime startedAt;
    private LocalDateTime lastGuessAt;
    private int hintsUsed;

    public InfiniteGame(Long userId, LocalDateTime startedAt) {
        this.userId = userId;
        this.startedAt = startedAt;
        this.score = 0;
        this.streak = 0;
        this.previousItems = new ArrayList<>();
        this.hintsUsed = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Item getCurrentTarget() { return currentTarget; }
    public void setCurrentTarget(Item currentTarget) { this.currentTarget = currentTarget; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }
    public List<Item> getPreviousItems() { return previousItems; }
    public void setPreviousItems(List<Item> previousItems) { this.previousItems = previousItems; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getLastGuessAt() { return lastGuessAt; }
    public void setLastGuessAt(LocalDateTime lastGuessAt) { this.lastGuessAt = lastGuessAt; }
    public int getHintsUsed() { return hintsUsed; }
    public void setHintsUsed(int hintsUsed) { this.hintsUsed = hintsUsed; }

    // Вспомогательные методы
    public void addScore(int points) {
        this.score += points;
    }

    public void incrementStreak() {
        this.streak++;
    }

    public void resetStreak() {
        this.streak = 0;
    }

    public void useHint() {
        this.hintsUsed++;
    }

    public void addPreviousItem(Item item) {
        this.previousItems.add(item);
        // Ограничиваем историю последними 10 предметами
        if (this.previousItems.size() > 10) {
            this.previousItems.remove(0);
        }
    }

    public int getGameDurationMinutes() {
        return (int) java.time.Duration.between(startedAt, LocalDateTime.now()).toMinutes();
    }
}

package ru.itis.wr.entities;

import java.time.LocalDateTime;

public class ChallengeBlock {
    private Long id;
    private Long dailyChallengeId;
    private BlockType blockType;
    private Long targetItemId;
    private Long extraItemId;
    private String settings;
    private LocalDateTime createdAt;

    public ChallengeBlock() {}

    public ChallengeBlock(Long id, Long dailyChallengeId, BlockType blockType, Long targetItemId) {
        this.id = id;
        this.dailyChallengeId = dailyChallengeId;
        this.blockType = blockType;
        this.targetItemId = targetItemId;
        this.createdAt = LocalDateTime.now();
    }

    public ChallengeBlock(Long id,
                          Long dailyChallengeId,
                          BlockType blockType,
                          Long targetItemId,
                          Long extraItemId,
                          String settings,
                          LocalDateTime createdAt) {
        this.id = id;
        this.dailyChallengeId = dailyChallengeId;
        this.blockType = blockType;
        this.targetItemId = targetItemId;
        this.extraItemId = extraItemId;
        this.settings = settings;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDailyChallengeId() { return dailyChallengeId; }
    public void setDailyChallengeId(Long dailyChallengeId) { this.dailyChallengeId = dailyChallengeId; }
    public BlockType getBlockType() { return blockType; }
    public void setBlockType(BlockType blockType) { this.blockType = blockType; }
    public Long getTargetItemId() { return targetItemId; }
    public void setTargetItemId(Long targetItemId) { this.targetItemId = targetItemId; }
    public Long getExtraItemId() { return extraItemId; }
    public void setExtraItemId(Long extraItemId) { this.extraItemId = extraItemId; }
    public String getSettings() { return settings; }
    public void setSettings(String settings) { this.settings = settings; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

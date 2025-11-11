package ru.itis.wr.dto;

import ru.itis.wr.entities.BlockType;

public class ChallengeBlockUpdateRequest {
    private Long id;
    private BlockType blockType;
    private Long targetItemId;
    private Long extraItemId;
    private String settings;

    public ChallengeBlockUpdateRequest() {}

    public ChallengeBlockUpdateRequest(Long id,
                                       BlockType blockType,
                                       Long targetItemId,
                                       Long extraItemId,
                                       String settings) {
        this.id = id;
        this.blockType = blockType;
        this.targetItemId = targetItemId;
        this.extraItemId = extraItemId;
        this.settings = settings;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BlockType getBlockType() { return blockType; }
    public void setBlockType(BlockType blockType) { this.blockType = blockType; }
    public Long getTargetItemId() { return targetItemId; }
    public void setTargetItemId(Long targetItemId) { this.targetItemId = targetItemId; }
    public Long getExtraItemId() { return extraItemId; }
    public void setExtraItemId(Long extraItemId) { this.extraItemId = extraItemId; }
    public String getSettings() { return settings; }
    public void setSettings(String settings) { this.settings = settings; }

    public boolean isValid() {
        return id != null && id > 0 &&
                blockType != null &&
                targetItemId != null && targetItemId > 0;
    }

    public boolean hasBlockType() { return blockType != null; }
    public boolean hasTargetItemId() { return targetItemId != null; }
    public boolean hasExtraItemId() { return extraItemId != null; }
    public boolean hasSettings() { return settings != null; }
}
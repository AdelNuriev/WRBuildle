package ru.itis.wr.dto;

import ru.itis.wr.entities.BlockType;

public class ChallengeBlockCreateRequest {
    private BlockType blockType;
    private Long targetItemId;
    private Long extraItemId;
    private String settings;

    public BlockType getBlockType() { return blockType; }
    public void setBlockType(BlockType blockType) { this.blockType = blockType; }
    public Long getTargetItemId() { return targetItemId; }
    public void setTargetItemId(Long targetItemId) { this.targetItemId = targetItemId; }
    public Long getExtraItemId() { return extraItemId; }
    public void setExtraItemId(Long extraItemId) { this.extraItemId = extraItemId; }
    public String getSettings() { return settings; }
    public void setSettings(String settings) { this.settings = settings; }
}

package ru.itis.wr.entities;

import java.time.LocalDateTime;

public class UserProfileSetting {
    private long id;
    private long userId;
    private long profileIconId;
    private long backgroundId;
    private long fontId;
    private long borderId;
    private String bio;
    private LocalDateTime updatedAt;

    public UserProfileSetting(long id,
                              long userId,
                              long profileIconId,
                              long backgroundId,
                              long fontId,
                              long borderId,
                              String bio,
                              LocalDateTime updatedAt)
    {
        this.id = id;
        this.userId = userId;
        this.profileIconId = profileIconId;
        this.backgroundId = backgroundId;
        this.fontId = fontId;
        this.borderId = borderId;
        this.bio = bio;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getProfileIconId() { return profileIconId; }
    public void setProfileIconId(long profileIconId) { this.profileIconId = profileIconId; }
    public long getBackgroundId() { return backgroundId; }
    public void setBackgroundId(long backgroundId) { this.backgroundId = backgroundId; }
    public long getFontId() { return fontId; }
    public void setFontId(long fontId) { this.fontId = fontId; }
    public long getBorderId() { return borderId; }
    public void setBorderId(long borderId) { this.borderId = borderId; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

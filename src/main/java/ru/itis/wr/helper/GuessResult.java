package ru.itis.wr.helper;

import ru.itis.wr.entities.UserResult;

public class GuessResult {
    private boolean correct;
    private String message;
    private int scoreEarned;
    private UserResult userResult;
    private Object additionalData;

    public GuessResult(boolean correct, String message, int scoreEarned, UserResult userResult) {
        this.correct = correct;
        this.message = message;
        this.scoreEarned = scoreEarned;
        this.userResult = userResult;
    }

    public GuessResult(boolean correct, String message, int scoreEarned,
                       UserResult userResult, Object additionalData) {
        this(correct, message, scoreEarned, userResult);
        this.additionalData = additionalData;
    }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getScoreEarned() { return scoreEarned; }
    public void setScoreEarned(int scoreEarned) { this.scoreEarned = scoreEarned; }
    public UserResult getUserResult() { return userResult; }
    public void setUserResult(UserResult userResult) { this.userResult = userResult; }
    public Object getAdditionalData() { return additionalData; }
    public void setAdditionalData(Object additionalData) { this.additionalData = additionalData; }
}

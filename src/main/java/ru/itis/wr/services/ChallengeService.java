package ru.itis.wr.services;

import ru.itis.wr.entities.*;
import ru.itis.wr.helper.GuessResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ChallengeService {
    DailyChallenge getTodayChallenge();
    ChallengeBlock getChallengeBlock(LocalDate date, BlockType blockType);
    List<UserResult> getUserResultsForDate(Long userId, LocalDate date);
    UserResult getUserResult(Long userId, LocalDate date, BlockType blockType);

    GuessResult processIconGuess(Long userId, Long itemId, int difficulty, LocalDate date);
    GuessResult processClassicGuess(Long userId, Long itemId, String guessType, LocalDate date);
    GuessResult processAttributesGuess(Long userId, Long itemId, LocalDate date);
    GuessResult processMissingGuess(Long userId, Long itemId, LocalDate date);
    GuessResult processImposterGuess(Long userId, Long itemId, LocalDate date);
    GuessResult processCostGuess(Long userId, int guessedCost, LocalDate date);

    InfiniteGame getCurrentInfiniteGame(Long userId);
    InfiniteGame startInfiniteGame(Long userId);
    GuessResult processInfiniteGuess(Long userId, Long itemId, Long targetItemId);
    Map<String, Object> getInfiniteHint(Long userId);

    ItemTree getItemTree(Long itemId);
    boolean isDailyChallengeCompleted(Long userId, LocalDate date);
    int calculateScore(BlockType blockType, int attempts, boolean success);
}

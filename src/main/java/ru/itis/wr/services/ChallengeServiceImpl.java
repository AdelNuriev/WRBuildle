package ru.itis.wr.services;

import ru.itis.wr.entities.*;
import ru.itis.wr.helper.RepositoryHelper;
import ru.itis.wr.repositories.*;
import ru.itis.wr.helper.GuessResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ChallengeServiceImpl implements ChallengeService {
    private final DailyChallengeRepository dailyChallengeRepository;
    private final ChallengeBlockRepository challengeBlockRepository;
    private final UserResultRepository userResultRepository;
    private final ItemService itemService;
    private final UserStatisticsRepository userStatisticsRepository;
    private final RepositoryHelper repositoryHelper;

    private static final int BASE_SCORE = 100;

    public ChallengeServiceImpl(DailyChallengeRepository dailyChallengeRepository,
                                ChallengeBlockRepository challengeBlockRepository,
                                UserResultRepository userResultRepository,
                                ItemService itemService,
                                UserStatisticsRepository userStatisticsRepository,
                                RepositoryHelper repositoryHelper) {
        this.dailyChallengeRepository = dailyChallengeRepository;
        this.challengeBlockRepository = challengeBlockRepository;
        this.userResultRepository = userResultRepository;
        this.itemService = itemService;
        this.userStatisticsRepository = userStatisticsRepository;
        this.repositoryHelper = repositoryHelper;
    }

    @Override
    public DailyChallenge getTodayChallenge() {
        return dailyChallengeRepository.findByDate(LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("No challenge for today"));
    }

    @Override
    public ChallengeBlock getChallengeBlock(LocalDate date, BlockType blockType) {
        var challengeOpt = dailyChallengeRepository.findByDate(date);
        if (challengeOpt.isEmpty()) {
            throw new IllegalArgumentException("No challenge for date: " + date);
        }

        return challengeBlockRepository.findByChallengeIdAndType(challengeOpt.get().getId(), blockType)
                .orElseThrow(() -> new IllegalArgumentException("No " + blockType + " challenge for date: " + date));
    }

    @Override
    public List<UserResult> getUserResultsForDate(Long userId, LocalDate date) {
        return userResultRepository.findByUserIdAndDate(userId, date);
    }

    @Override
    public UserResult getUserResult(Long userId, LocalDate date, BlockType blockType) {
        return userResultRepository.findByUserAndDateAndType(userId, date, blockType)
                .orElse(new UserResult(userId, date, blockType));
    }

    @Override
    public GuessResult processIconGuess(Long userId, Long itemId, int difficulty, LocalDate date) {
        ChallengeBlock block = getChallengeBlock(date, BlockType.ICON);
        UserResult userResult = getOrCreateUserResult(userId, date, BlockType.ICON);

        if (userResult.getCompleted()) {
            return new GuessResult(false, "Already completed", 0, userResult);
        }

        boolean isCorrect = itemId.equals(block.getTargetItemId());
        userResult.setAttempts(userResult.getAttempts() + 1);

        if (isCorrect) {
            userResult.setCompleted(true);
            userResult.setScore(calculateScore(BlockType.ICON, userResult.getAttempts(), true));
            userResult.setCompletedAt(LocalDateTime.now());
        }

        repositoryHelper.putUserResult(userResultRepository, userResult);
        updateUserStatistics(userId, userResult);

        return new GuessResult(isCorrect, isCorrect ? "Correct!" : "Wrong guess",
                userResult.getScore(), userResult);
    }

    @Override
    public GuessResult processClassicGuess(Long userId, Long itemId, String guessType, LocalDate date) {
        ChallengeBlock block = getChallengeBlock(date, BlockType.CLASSIC);
        UserResult userResult = getOrCreateUserResult(userId, date, BlockType.CLASSIC);

        if (userResult.getCompleted()) {
            return new GuessResult(false, "Already completed", 0, userResult);
        }

        // Логика для классического режима (угадывание по дереву сборки)
        boolean isCorrect = checkClassicGuess(block.getTargetItemId(), itemId, guessType);
        userResult.setAttempts(userResult.getAttempts() + 1);

        if (isCorrect && "root".equals(guessType)) {
            userResult.setCompleted(true);
            userResult.setScore(calculateScore(BlockType.CLASSIC, userResult.getAttempts(), true));
            userResult.setCompletedAt(LocalDateTime.now());
        }

        repositoryHelper.putUserResult(userResultRepository, userResult);
        updateUserStatistics(userId, userResult);

        return new GuessResult(isCorrect, isCorrect ? "Correct component!" : "Wrong component",
                userResult.getScore(), userResult);
    }

    @Override
    public GuessResult processAttributesGuess(Long userId, Long itemId, LocalDate date) {
        ChallengeBlock block = getChallengeBlock(date, BlockType.ATTRIBUTES);
        UserResult userResult = getOrCreateUserResult(userId, date, BlockType.ATTRIBUTES);

        if (userResult.getCompleted()) {
            return new GuessResult(false, "Already completed", 0, userResult);
        }

        boolean isCorrect = itemId.equals(block.getTargetItemId());
        userResult.setAttempts(userResult.getAttempts() + 1);

        if (isCorrect) {
            userResult.setCompleted(true);
            userResult.setScore(calculateScore(BlockType.ATTRIBUTES, userResult.getAttempts(), true));
            userResult.setCompletedAt(LocalDateTime.now());
        }

        repositoryHelper.putUserResult(userResultRepository, userResult);
        updateUserStatistics(userId, userResult);

        String hint = isCorrect ? "Correct!" : getAttributesHint(block.getTargetItemId(), itemId);

        return new GuessResult(isCorrect, hint, userResult.getScore(), userResult);
    }

    @Override
    public GuessResult processMissingGuess(Long userId, Long itemId, LocalDate date) {
        ChallengeBlock block = getChallengeBlock(date, BlockType.MISSING);
        UserResult userResult = getOrCreateUserResult(userId, date, BlockType.MISSING);

        if (userResult.getCompleted()) {
            return new GuessResult(false, "Already completed", 0, userResult);
        }

        boolean isCorrect = itemId.equals(block.getExtraItemId());
        userResult.setAttempts(userResult.getAttempts() + 1);

        if (isCorrect) {
            userResult.setCompleted(true);
            userResult.setScore(calculateScore(BlockType.MISSING, userResult.getAttempts(), true));
            userResult.setCompletedAt(LocalDateTime.now());
        }

        repositoryHelper.putUserResult(userResultRepository, userResult);
        updateUserStatistics(userId, userResult);

        return new GuessResult(isCorrect, isCorrect ? "Correct missing item!" : "Wrong item",
                userResult.getScore(), userResult);
    }

    @Override
    public GuessResult processImposterGuess(Long userId, Long itemId, LocalDate date) {
        ChallengeBlock block = getChallengeBlock(date, BlockType.IMPOSTER);
        UserResult userResult = getOrCreateUserResult(userId, date, BlockType.IMPOSTER);

        if (userResult.getCompleted()) {
            return new GuessResult(false, "Already completed", 0, userResult);
        }

        boolean isCorrect = itemId.equals(block.getExtraItemId());
        userResult.setAttempts(userResult.getAttempts() + 1);

        if (isCorrect) {
            userResult.setCompleted(true);
            userResult.setScore(calculateScore(BlockType.IMPOSTER, userResult.getAttempts(), true));
            userResult.setCompletedAt(LocalDateTime.now());
        }

        repositoryHelper.putUserResult(userResultRepository, userResult);
        updateUserStatistics(userId, userResult);

        return new GuessResult(isCorrect, isCorrect ? "Correct imposter found!" : "Wrong choice",
                userResult.getScore(), userResult);
    }

    @Override
    public GuessResult processCostGuess(Long userId, int guessedCost, LocalDate date) {
        ChallengeBlock block = getChallengeBlock(date, BlockType.COST);
        UserResult userResult = getOrCreateUserResult(userId, date, BlockType.COST);

        if (userResult.getCompleted()) {
            return new GuessResult(false, "Already completed", 0, userResult);
        }

        Item targetItem = itemService.getItemById(block.getTargetItemId())
                .orElseThrow(() -> new IllegalArgumentException("Target item not found"));

        boolean isCorrect = guessedCost == targetItem.getCost();
        userResult.setAttempts(userResult.getAttempts() + 1);

        if (isCorrect) {
            userResult.setCompleted(true);
            userResult.setScore(calculateScore(BlockType.COST, userResult.getAttempts(), true));
            userResult.setCompletedAt(LocalDateTime.now());
        }

        repositoryHelper.putUserResult(userResultRepository, userResult);
        updateUserStatistics(userId, userResult);

        String hint = isCorrect ? "Correct cost!" :
                (guessedCost < targetItem.getCost() ? "Higher" : "Lower");

        return new GuessResult(isCorrect, hint, userResult.getScore(), userResult);
    }

    @Override
    public ItemTree getItemTree(Long itemId) {
        return itemService.getItemTree(itemId);
    }

    @Override
    public boolean isDailyChallengeCompleted(Long userId, LocalDate date) {
        List<UserResult> results = getUserResultsForDate(userId, date);
        return results.stream().allMatch(UserResult::getCompleted);
    }

    @Override
    public int calculateScore(BlockType blockType, int attempts, boolean success) {
        if (!success) return 0;

        int score = BASE_SCORE;
        if (attempts <= 3) score += 50;
        else if (attempts <= 5) score += 25;

        return score;
    }

    @Override
    public InfiniteGame getCurrentInfiniteGame(Long userId) {
        return new InfiniteGame(userId, LocalDateTime.now());
    }

    @Override
    public InfiniteGame startInfiniteGame(Long userId) {
        return new InfiniteGame(userId, LocalDateTime.now());
    }

    @Override
    public GuessResult processInfiniteGuess(Long userId, Long itemId) {
        return new GuessResult(true, "Correct!", 10, null);
    }

    @Override
    public Map<String, Object> getInfiniteHint(Long userId) {
        Map<String, Object> hint = new HashMap<>();
        hint.put("attribute", "AD");
        hint.put("value", 25);
        return hint;
    }

    private UserResult getOrCreateUserResult(Long userId, LocalDate date, BlockType blockType) {
        return userResultRepository.findByUserAndDateAndType(userId, date, blockType)
                .orElse(new UserResult(userId, date, blockType));
    }

    private boolean checkClassicGuess(Long targetItemId, Long guessedItemId, String guessType) {
        if ("root".equals(guessType)) {
            return targetItemId.equals(guessedItemId);
        } else {
            return itemService.getItemComponents(targetItemId).stream()
                    .anyMatch(item -> item.getId().equals(guessedItemId));
        }
    }

    private String getAttributesHint(Long targetItemId, Long guessedItemId) {
        Item targetItem = itemService.getItemById(targetItemId)
                .orElseThrow(() -> new IllegalArgumentException("Target item not found"));
        Item guessedItem = itemService.getItemById(guessedItemId)
                .orElseThrow(() -> new IllegalArgumentException("Guessed item not found"));

        StringBuilder hint = new StringBuilder();

        if (targetItem.getCost() > guessedItem.getCost()) {
            hint.append("Target item is more expensive. ");
        } else if (targetItem.getCost() < guessedItem.getCost()) {
            hint.append("Target item is cheaper. ");
        }

        return hint.length() > 0 ? hint.toString() : "Different attributes";
    }

    private void updateUserStatistics(Long userId, UserResult result) {
        var statsOpt = userStatisticsRepository.findByUserId(userId);
        UserStatistics stats = statsOpt.orElse(new UserStatistics(userId));

        stats.setTotalGames(stats.getTotalGames() + 1);
        if (result.getCompleted()) {
            stats.setGamesWon(stats.getGamesWon() + 1);
            stats.setTotalScore(stats.getTotalScore() + result.getScore());
        }

        LocalDate today = LocalDate.now();
        if (stats.getLastDailyPlay() == null ||
                !stats.getLastDailyPlay().equals(today.minusDays(1))) {
            stats.setDailyStreak(1);
        } else {
            stats.setDailyStreak(stats.getDailyStreak() + 1);
        }
        stats.setLastDailyPlay(today);

        if (result.getScore() > stats.getBestDailyScore()) {
            stats.setBestDailyScore(result.getScore());
        }

        stats.setUpdatedAt(LocalDateTime.now());

        repositoryHelper.putUserStatistics(userStatisticsRepository, stats);
    }
}

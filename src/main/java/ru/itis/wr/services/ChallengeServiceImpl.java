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
    private final InfiniteGameRepository infiniteGameRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private static final int BASE_SCORE = 100;

    public ChallengeServiceImpl(DailyChallengeRepository dailyChallengeRepository,
                                ChallengeBlockRepository challengeBlockRepository,
                                UserResultRepository userResultRepository,
                                ItemService itemService,
                                UserStatisticsRepository userStatisticsRepository,
                                RepositoryHelper repositoryHelper,
                                InfiniteGameRepository infiniteGameRepository,
                                ItemRepository itemRepository,
                                UserRepository userRepository) {
        this.dailyChallengeRepository = dailyChallengeRepository;
        this.challengeBlockRepository = challengeBlockRepository;
        this.userResultRepository = userResultRepository;
        this.itemService = itemService;
        this.userStatisticsRepository = userStatisticsRepository;
        this.repositoryHelper = repositoryHelper;
        this.infiniteGameRepository = infiniteGameRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public InfiniteGame getCurrentInfiniteGame(Long userId) {
        return infiniteGameRepository.findByUserId(userId)
                .orElseGet(() -> startInfiniteGame(userId));
    }

    @Override
    public InfiniteGame startInfiniteGame(Long userId) {
        List<Item> allItems = itemService.getAllItems();
        if (allItems.isEmpty()) {
            throw new RuntimeException("No items available for infinite game");
        }

        Random random = new Random();
        Item randomItem = allItems.get(random.nextInt(allItems.size()));

        InfiniteGame game = new InfiniteGame(userId, LocalDateTime.now());
        game.setCurrentTarget(randomItem);

        Long gameId = infiniteGameRepository.save(game);
        game.setId(gameId);

        return game;
    }

    @Override
    public GuessResult processInfiniteGuess(Long userId, Long itemId, Long targetItemId) {
        try {
            var user = userRepository.findById(userId).orElseThrow(() ->
                    new RuntimeException("User not found: " + userId));
            var guessedItem = itemRepository.findById(itemId).orElseThrow(() ->
                    new RuntimeException("Item not found: " + itemId));
            var targetItem = itemRepository.findById(targetItemId).orElseThrow(() ->
                    new RuntimeException("Target item not found: " + targetItemId));

            var game = getCurrentInfiniteGame(userId);
            boolean isCorrect = guessedItem.getId().equals(targetItem.getId());

            game.setLastGuessAt(LocalDateTime.now());

            if (isCorrect) {
                int scoreEarned = calculateInfiniteScore(game.getStreak() + 1);
                game.addScore(scoreEarned);
                game.incrementStreak();
                game.addPreviousItem(targetItem);

                List<Item> allItems = itemService.getAllItems();
                Random random = new Random();
                Item newTargetItem;
                do {
                    newTargetItem = allItems.get(random.nextInt(allItems.size()));
                } while (newTargetItem.getId().equals(targetItem.getId()));

                game.setCurrentTarget(newTargetItem);

                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("streak", game.getStreak());
                additionalData.put("newTargetItemId", newTargetItem.getId());

                infiniteGameRepository.update(game);

                return new GuessResult(true, "Правильно! Предмет угадан!", scoreEarned, null, additionalData);
            } else {
                game.resetStreak();
                infiniteGameRepository.update(game);

                String hint = getAttributesHint(targetItemId, itemId);
                return new GuessResult(false, "Неправильно! " + hint, 0, null);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing infinite guess: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getInfiniteHint(Long userId) {
        Map<String, Object> hint = new HashMap<>();
        try {
            InfiniteGame game = getCurrentInfiniteGame(userId);
            Item targetItem = game.getCurrentTarget();

            hint.put("rarity", targetItem.getRarity());
            hint.put("effectType", targetItem.isActive());
            hint.put("costRange", getCostRangeHint(targetItem.getCost()));

            game.useHint();
            infiniteGameRepository.update(game);
            hint.put("hintsUsed", game.getHintsUsed());

        } catch (Exception e) {
            hint.put("error", "Unable to provide hint");
        }
        return hint;
    }

    private int calculateInfiniteScore(int streak) {
        return 100 + (streak * 50);
    }

    private String getCostRangeHint(int cost) {
        if (cost <= 500) return "Дешевый предмет (до 500 золота)";
        else if (cost <= 1000) return "Средний предмет (500-1000 золота)";
        else if (cost <= 2000) return "Дорогой предмет (1000-2000 золота)";
        else return "Очень дорогой предмет (свыше 2000 золота)";
    }

    private String getAttributesHint(Long targetItemId, Long guessedItemId) {
        Item targetItem = itemService.getItemById(targetItemId)
                .orElseThrow(() -> new IllegalArgumentException("Target item not found"));
        Item guessedItem = itemService.getItemById(guessedItemId)
                .orElseThrow(() -> new IllegalArgumentException("Guessed item not found"));

        StringBuilder hint = new StringBuilder();

        if (!targetItem.getRarity().equals(guessedItem.getRarity())) {
            hint.append("Редкость отличается. ");
        }

        if (!targetItem.isActive() && guessedItem.isActive()) {
            hint.append("Тип эффекта отличается. ");
        }

        if (targetItem.getCost() > guessedItem.getCost()) {
            hint.append("Нужный предмет дороже. ");
        } else if (targetItem.getCost() < guessedItem.getCost()) {
            hint.append("Нужный предмет дешевле. ");
        }

        return hint.length() > 0 ? hint.toString() : "Проверьте атрибуты предмета";
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
        try {
            ChallengeBlock block = getChallengeBlock(date, BlockType.CLASSIC);
            UserResult userResult = getOrCreateUserResult(userId, date, BlockType.CLASSIC);

            if (userResult.getCompleted()) {
                return new GuessResult(false, "Already completed", 0, userResult);
            }

            boolean isRoot = itemId.equals(block.getTargetItemId());
            boolean isComponent = isItemInTree(block.getTargetItemId(), itemId);
            boolean isCorrect = isRoot || isComponent;
            userResult.setAttempts(userResult.getAttempts() + 1);

            if (isRoot) {
                userResult.setCompleted(true);
                userResult.setScore(calculateScore(BlockType.CLASSIC, userResult.getAttempts(), true));
                userResult.setCompletedAt(LocalDateTime.now());
            }

            repositoryHelper.putUserResult(userResultRepository, userResult);
            updateUserStatistics(userId, userResult);

            String message = isRoot ? "Поздравляем! Вы угадали корневой предмет!" :
                    isComponent ? "Правильно! Этот предмет есть в сборке" :
                            "Этот предмет не входит в сборку";

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("isRoot", isRoot);
            additionalData.put("guessedItemId", itemId);

            return new GuessResult(isCorrect, message, userResult.getScore(), userResult, additionalData);

        } catch (Exception e) {
            System.err.println("Error in processClassicGuess: " + e.getMessage());
            return new GuessResult(false, "Ошибка обработки предположения: " + e.getMessage(), 0, null);
        }
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


    private UserResult getOrCreateUserResult(Long userId, LocalDate date, BlockType blockType) {
        try {
            Optional<UserResult> existingResult = userResultRepository.findByUserAndDateAndType(userId, date, blockType);
            if (existingResult.isPresent()) {
                return existingResult.get();
            } else {
                UserResult newResult = new UserResult(userId, date, blockType);
                Long newId = userResultRepository.save(newResult);
                newResult.setId(newId);
                return newResult;
            }
        } catch (Exception e) {
            return new UserResult(userId, date, blockType);
        }
    }

    private boolean isItemInTree(Long rootItemId, Long searchedItemId) {
        if (rootItemId.equals(searchedItemId)) {
            return true;
        }

        ItemTree tree = itemService.getItemTree(rootItemId);
        return searchItemInTree(tree, searchedItemId);
    }

    private boolean searchItemInTree(ItemTree tree, Long searchedItemId) {
        if (tree == null || tree.getItem() == null) {
            return false;
        }

        if (tree.getItem().getId().equals(searchedItemId)) {
            return true;
        }

        if (tree.getComponents() != null) {
            for (ItemTree component : tree.getComponents()) {
                if (searchItemInTree(component, searchedItemId)) {
                    return true;
                }
            }
        }

        return false;
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
        if (stats.getLastDailyPlay() == null || !stats.getLastDailyPlay().equals(today.minusDays(1))) {
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
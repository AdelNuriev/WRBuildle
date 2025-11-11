package ru.itis.wr.services;

import ru.itis.wr.dto.ChallengeBlockCreateRequest;
import ru.itis.wr.dto.ChallengeBlockUpdateRequest;
import ru.itis.wr.dto.ItemCreateRequest;
import ru.itis.wr.dto.ItemUpdateRequest;
import ru.itis.wr.entities.*;
import ru.itis.wr.repositories.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AdminServiceImpl implements AdminService {
    private final DailyChallengeRepository dailyChallengeRepository;
    private final ChallengeBlockRepository challengeBlockRepository;
    private final StatisticsService statisticsService;
    private final ItemService itemService;
    private final UserRepository userRepository;

    public AdminServiceImpl(DailyChallengeRepository dailyChallengeRepository,
                            ChallengeBlockRepository challengeBlockRepository,
                            StatisticsService statisticsService,
                            ItemService itemService,
                            UserRepository userRepository) {
        this.dailyChallengeRepository = dailyChallengeRepository;
        this.challengeBlockRepository = challengeBlockRepository;
        this.statisticsService = statisticsService;
        this.itemService = itemService;
        this.userRepository = userRepository;
    }

    @Override
    public Item createItem(ItemCreateRequest request) {
        Item item = new Item();
        item.setName(request.getName());
        item.setRarity(request.getRarity());
        item.setCost(request.getCost());
        item.setIconUrl(request.getIconUrl());
        item.setAttributes(request.getAttributes());
        item.setActive(true);

        return itemService.createItem(item);
    }

    @Override
    public Item updateItem(Long itemId, ItemUpdateRequest request) {
        Optional<Item> itemOpt = itemService.getItemById(itemId);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item not found");
        }

        Item item = itemOpt.get();
        if (request.getName() != null) item.setName(request.getName());
        if (request.getCost() != null) item.setCost(request.getCost());
        if (request.getRarity() != null) item.setRarity(request.getRarity());
        if (request.getIconUrl() != null) item.setIconUrl(request.getIconUrl());
        if (request.getAttributes() != null) item.setAttributes(request.getAttributes());

        return itemService.updateItem(item);
    }

    @Override
    public boolean deleteItem(Long itemId) {
        try {
            itemService.deleteItem(itemId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public DailyChallenge createDailyChallenge(LocalDate date, Long adminId) {
        if (dailyChallengeRepository.findByDate(date).isPresent()) {
            throw new IllegalArgumentException("Challenge for date " + date + " already exists");
        }

        DailyChallenge challenge = new DailyChallenge();
        challenge.setChallengeDate(date);
        challenge.setCreatedBy(adminId);
        challenge.setCreatedAt(LocalDateTime.now());
        challenge.setIsActive(true);

        Long challengeId = dailyChallengeRepository.save(challenge);
        challenge.setId(challengeId);

        return challenge;
    }

    @Override
    public boolean createChallengeBlock(Long challengeId, ChallengeBlockCreateRequest request) {
        try {
            ChallengeBlock block = new ChallengeBlock();
            block.setDailyChallengeId(challengeId);
            block.setBlockType(request.getBlockType());
            block.setTargetItemId(request.getTargetItemId());
            block.setExtraItemId(request.getExtraItemId());
            block.setSettings(request.getSettings());
            block.setCreatedAt(LocalDateTime.now());

            challengeBlockRepository.save(block);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Map<String, Object> getSystemStatistics() {
        return Map.of(
                "totalUsers", getTotalUsers(),
                "totalItems", itemService.getAllItems().size(),
                "activeChallenges", getActiveChallengesCount(),
                "totalGamesPlayed", getTotalGamesPlayed()
        );
    }

    @Override
    public List<Map<String, Object>> getRecentChallenges(int days) {
        List<DailyChallenge> challenges = dailyChallengeRepository.findRecent(days);

        return challenges.stream().map(challenge -> Map.<String, Object>of(
                "date", challenge.getChallengeDate(),
                "createdBy", challenge.getCreatedBy(),
                "blocksCount", challengeBlockRepository.findByDailyChallengeId(challenge.getId()).size(),
                "isActive", challenge.getIsActive()
        )).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getChallengesForDate(LocalDate date) {
        Optional<DailyChallenge> challengeOpt = dailyChallengeRepository.findByDate(date);
        if (challengeOpt.isEmpty()) {
            return Collections.emptyList();
        }

        DailyChallenge challenge = challengeOpt.get();
        List<ChallengeBlock> blocks = challengeBlockRepository.findByDailyChallengeId(challenge.getId());

        return blocks.stream().map(block -> {
            Map<String, Object> blockMap = new HashMap<>();
            blockMap.put("id", block.getId());
            blockMap.put("blockType", block.getBlockType().name());
            blockMap.put("targetItem", itemService.getItemById(block.getTargetItemId()).orElse(null));
            if (block.getExtraItemId() != null) {
                blockMap.put("extraItem", itemService.getItemById(block.getExtraItemId()).orElse(null));
            }
            return blockMap;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean updateUserRole(Long userId, Role role) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        user.setRole(role);
        userRepository.update(user);
        return true;
    }

    @Override
    public boolean grantCoins(Long userId, int coins) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        user.setCoins(user.getCoins() + coins);
        userRepository.update(user);
        return true;
    }

    //TODO: еще в разработке
    @Override
    public List<User> getAllUsers(int page, int size) {
        return Collections.emptyList();
    }

    private long getTotalUsers() {
        return 0;
    }

    private long getActiveChallengesCount() {
        return dailyChallengeRepository.findRecent(30).size();
    }

    private long getTotalGamesPlayed() {
        return 0;
    }

    @Override
    public List<Map<String, Object>> getLeaderboard(String period, LocalDate date) {
        return statisticsService.getLeaderboard(period, date);
    }
}

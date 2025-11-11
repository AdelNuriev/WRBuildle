package ru.itis.wr.services;

import ru.itis.wr.dto.ChallengeBlockCreateRequest;
import ru.itis.wr.dto.*;
import ru.itis.wr.dto.ItemCreateRequest;
import ru.itis.wr.dto.ItemUpdateRequest;
import ru.itis.wr.entities.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AdminService {
    Item createItem(ItemCreateRequest request);
    Item updateItem(Long itemId, ItemUpdateRequest request);
    boolean deleteItem(Long itemId);

    DailyChallenge createDailyChallenge(LocalDate date, Long adminId);
    boolean createChallengeBlock(Long challengeId, ChallengeBlockCreateRequest request);
    boolean updateChallengeBlock(Long blockId, ChallengeBlockUpdateRequest request);

    Map<String, Object> getSystemStatistics();
    List<Map<String, Object>> getRecentChallenges(int days);
    List<Map<String, Object>> getChallengesForDate(LocalDate date);

    List<User> getAllUsers(int page, int size);
    boolean updateUserRole(Long userId, Role role);
    boolean grantCoins(Long userId, int coins);
    List<Map<String, Object>> getLeaderboard(String period, LocalDate date);
}

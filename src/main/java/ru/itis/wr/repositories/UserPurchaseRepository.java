package ru.itis.wr.repositories;

import ru.itis.wr.entities.UserPurchase;

import java.util.List;
import java.util.Optional;

public interface UserPurchaseRepository {
    Long save(UserPurchase purchase);
    Optional<UserPurchase> findById(Long id);
    List<UserPurchase> findByUserId(Long userId);
    Optional<UserPurchase> findByUserAndItem(Long userId, Long shopItemId);
    List<UserPurchase> findEquippedByUserId(Long userId);
    void update(UserPurchase purchase);
    boolean userOwnsItem(Long userId, Long shopItemId);
}

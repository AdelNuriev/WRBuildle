package ru.itis.wr.repositories;

import ru.itis.wr.entities.UserResult;
import ru.itis.wr.entities.BlockType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserResultRepository {
    Long save(UserResult result);
    Optional<UserResult> findByUserAndDateAndType(Long userId, LocalDate date, BlockType blockType);
    List<UserResult> findByUserIdAndDate(Long userId, LocalDate date);
    List<UserResult> findRecentByUserId(Long userId, int days);
    Integer getTotalScoreByUserAndDate(Long userId, LocalDate date);
    void update(UserResult result);
}

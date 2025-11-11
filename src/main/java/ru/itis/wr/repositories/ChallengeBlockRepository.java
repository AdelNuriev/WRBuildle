package ru.itis.wr.repositories;

import ru.itis.wr.entities.BlockType;
import ru.itis.wr.entities.ChallengeBlock;

import java.util.List;
import java.util.Optional;

public interface ChallengeBlockRepository {
    Long save(ChallengeBlock challengeBlock);
    List<ChallengeBlock> findByDailyChallengeId(Long challengeId);
    Optional<ChallengeBlock> findByChallengeIdAndType(Long challengeId, BlockType blockType);
    void deleteByDailyChallengeId(Long challengeId);
}

package gg.bayes.challenge.persistence;

import gg.bayes.challenge.model.KillEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface KillRepository extends CrudRepository<KillEntity, Long> {

    List<KillEntity> findAllByMatchId(long matchId);
}

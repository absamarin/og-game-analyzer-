package gg.bayes.challenge.persistence;

import gg.bayes.challenge.model.HitEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HitRepository extends CrudRepository<HitEntity, Long> {
    List<HitEntity> findAllByMatchIdAndHeroName(long matchId, String heroName);


}

package gg.bayes.challenge.persistence;

import gg.bayes.challenge.model.SpellEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpellRepository extends CrudRepository<SpellEntity, Long> {
    List<SpellEntity> findAllByMatchIdAndHeroName(long matchId, String heroName);
}

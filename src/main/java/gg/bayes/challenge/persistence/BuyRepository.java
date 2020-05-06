package gg.bayes.challenge.persistence;

import gg.bayes.challenge.model.BuyEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BuyRepository extends CrudRepository<BuyEntity, Long> {

    List<BuyEntity> findAllByMatchIdAndAndHeroName(long matchId, String heroName);
}

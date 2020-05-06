package gg.bayes.challenge.persistence;

import gg.bayes.challenge.model.MatchEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MatchRepository extends CrudRepository<MatchEntity, Long> {

    List<MatchEntity> findAll();

}

package gg.bayes.challenge.service;

import java.util.Optional;

// was separated for cohesion and linear interface
public interface MatchInstantiateService {
    Optional<Long> ingestMatch(String payload);

}

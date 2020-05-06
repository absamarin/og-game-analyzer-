package gg.bayes.challenge.service.impl;


import gg.bayes.challenge.logfileutility.PayloadLogUtility;
import gg.bayes.challenge.model.MatchEntity;
import gg.bayes.challenge.persistence.MatchRepository;
import gg.bayes.challenge.service.MatchInstantiateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class MatchInstantiateServiceImpl implements MatchInstantiateService {


    private final PayloadLogUtility payloadLogUtility;
    private final MatchRepository matchRepository;

    @Autowired
    public MatchInstantiateServiceImpl(PayloadLogUtility payloadLogUtility, MatchRepository matchRepository) {
        this.payloadLogUtility = payloadLogUtility;
        this.matchRepository = matchRepository;
    }


    @Override
    @Transactional
    public Optional<Long> ingestMatch(String payload) {
        MatchEntity matchEntity = matchRepository.save(new MatchEntity());
        payloadLogUtility.ingest(payload, matchEntity.getId());
        return Optional.of(matchEntity.getId());
    }


}

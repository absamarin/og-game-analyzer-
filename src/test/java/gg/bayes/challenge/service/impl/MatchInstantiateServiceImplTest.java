package gg.bayes.challenge.service.impl;

import gg.bayes.challenge.logfileutility.PayloadLogUtility;
import gg.bayes.challenge.model.MatchEntity;
import gg.bayes.challenge.persistence.MatchRepository;
import gg.bayes.challenge.service.MatchInstantiateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


class MatchInstantiateServiceImplTest {

    @Test
    void ingestMatch() {
        String testLogMatchString = "test";
        PayloadLogUtility mockPayloadLogUtility = Mockito.mock(PayloadLogUtility.class);
        MatchRepository mockMatchRepository = Mockito.mock(MatchRepository.class);

        Mockito.when(mockMatchRepository.save(any(MatchEntity.class))).thenReturn(MatchEntity.builder().id(1L).build());

        MatchInstantiateService service = new MatchInstantiateServiceImpl(mockPayloadLogUtility, mockMatchRepository);
        assertEquals(service.ingestMatch(testLogMatchString), Optional.of(1L));
    }
}
package gg.bayes.challenge.persistence;

import gg.bayes.challenge.model.MatchEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(SpringExtension.class)
@DataJpaTest
class MatchPersistenceTest {

    @Autowired
    private MatchRepository repository;


    @Test
    void setUp() {
        repository.deleteAll();
        String logFileName = "combatlog_1.log.txt";
        MatchEntity entity = new MatchEntity();
        MatchEntity savedEntity = repository.save(entity);
        assertEquals(savedEntity.getId(), 1);
    }


}
package gg.bayes.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class IgnoredEntity implements GameEvent {


    private long matchId;
    private String eventDescription;


    @Override
    public void store() {
        log.info("the event is ignored:{}", eventDescription);
    }
}

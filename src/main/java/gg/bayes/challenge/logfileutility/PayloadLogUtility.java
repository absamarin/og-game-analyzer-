package gg.bayes.challenge.logfileutility;


import gg.bayes.challenge.exceptions.InvalidInputException;
import gg.bayes.challenge.model.*;
import gg.bayes.challenge.persistence.BuyRepository;
import gg.bayes.challenge.persistence.HitRepository;
import gg.bayes.challenge.persistence.KillRepository;
import gg.bayes.challenge.persistence.SpellRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Slf4j
@Component
public class PayloadLogUtility {

    private final SpellRepository spellRepository;
    private final BuyRepository buyRepository;
    private final HitRepository hitRepository;
    private final KillRepository killRepository;

    // split based on [00:00:00.000]
    public static final String REGEX_LOG_SPLITTER = "\\[\\d{2}:\\d{2}:\\d{2}.\\d{3}] ";
    public static final int MIN_LOG_LINE_LENGTH = 20;

    @Autowired
    public PayloadLogUtility(SpellRepository spellRepository, BuyRepository buyRepository, HitRepository hitRepository, KillRepository killRepository) {
        this.spellRepository = spellRepository;
        this.buyRepository = buyRepository;
        this.hitRepository = hitRepository;
        this.killRepository = killRepository;
    }

    public void ingest(String payload, Long matchId) {

        Stream.of(payload.split(REGEX_LOG_SPLITTER))
                .filter(e -> e.length() > MIN_LOG_LINE_LENGTH)
                .forEach(
                        e -> transformLogLineToEvent(e, matchId).store()
                );
    }

    private GameEvent transformLogLineToEvent(String gameLogLine, Long matchId) {


        if (isIgnorable(gameLogLine)) return new IgnoredEntity(matchId, gameLogLine);
        String[] eventKeyword = gameLogLine.split(" ");

        String heroName = eventKeyword[0];

        switch (eventKeyword[1]) {
            case "buys":
                log.debug("creating buy_event with matchId:{}, heroName:{}, item_name:{}", matchId, heroName, eventKeyword[3]);
                return new BuyEntity(matchId, heroName, eventKeyword[3], buyRepository);
            case "is":
                if (eventKeyword[2].contains("killed")) {
                    log.debug("creating kill_event with match_id:{}, heroNameKiller:{}, heroNameVictim:{}", matchId, eventKeyword[4], heroName);
                    return KillEntity.builder().matchId(matchId).heroNameKiller(eventKeyword[4]).heroNameVictim(heroName).repository(killRepository).build();
                } else {
                    throw new InvalidInputException("The event in the log is unrecognizable, line:" + gameLogLine);
                }
            case "casts":
            case "toggles":
                log.debug("creating spell_event with match_id:{}, heroName:{}, spellName:{}", matchId, heroName, eventKeyword[3]);
                return new SpellEntity(matchId, heroName, eventKeyword[3], spellRepository);
            case "hits":
                //[00:12:15.908] npc_dota_hero_puck hits npc_dota_hero_bane with dota_unknown for 40 damage (559->519)
                log.debug("creating hit_event with match_id:{}, heroName:{}, heroNameVictim:{}, damage:{}", matchId, heroName, eventKeyword[2], eventKeyword[6]);
                return HitEntity.builder().heroName(heroName).matchId(matchId).heroNameVictim(eventKeyword[2]).damage(Integer.parseInt(eventKeyword[6])).repository(hitRepository).build();
            case "uses":
            case "state":
                return new IgnoredEntity(matchId, gameLogLine);

            default:
                throw new InvalidInputException("The event in the log is unrecognizable, line:" + gameLogLine);
        }
    }

    private boolean isIgnorable(String gameLogLine) {
        return gameLogLine.split(" ")[2].contains("heals")
                || gameLogLine.contains("player in slot");
    }

}

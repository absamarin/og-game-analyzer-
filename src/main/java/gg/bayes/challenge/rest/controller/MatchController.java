package gg.bayes.challenge.rest.controller;

import gg.bayes.challenge.exceptions.InvalidInputException;
import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import gg.bayes.challenge.service.MatchInstantiateService;
import gg.bayes.challenge.service.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/match")

public class MatchController {

    private final MatchService matchService;
    private final MatchInstantiateService matchInstantiateService;


    @Autowired
    public MatchController(MatchService matchService, MatchInstantiateService matchInstantiateService) {
        this.matchService = matchService;
        this.matchInstantiateService = matchInstantiateService;
    }

    @PostMapping(consumes = "text/plain")
    public ResponseEntity<Long> ingestMatch(@RequestBody @NotNull @NotBlank String payload) {
        log.debug("ingesting the imported match");
        final Optional<Long> matchId = matchInstantiateService.ingestMatch(payload);
        return ResponseEntity.ok(matchId.orElseThrow(() -> new InvalidInputException("No match_Id found for entered payload")));
    }

    @GetMapping("{matchId}")
    public ResponseEntity<List<HeroKills>> getMatch(@PathVariable("matchId") Long matchId) {
        matchIdValidityCheck(matchId);
        log.debug("/api/match return the found HeroKills for matchId={}", matchId);

        // use match service to retrieve stats
        return ResponseEntity.ok(matchService.getHeroKills(matchId));
    }

    @GetMapping("{matchId}/{heroName}/items")
    public ResponseEntity<List<HeroItems>> getItems(@PathVariable("matchId") Long matchId,
                                                    @PathVariable("heroName") String heroName) {
        matchIdValidityCheck(matchId);


        return ResponseEntity.ok(matchService.getItems(matchId, heroName));
    }

    @GetMapping("{matchId}/{heroName}/spells")
    public ResponseEntity<List<HeroSpells>> getSpells(@PathVariable("matchId") Long matchId,
                                                      @PathVariable("heroName") String heroName) {
        matchIdValidityCheck(matchId);


        return ResponseEntity.ok(matchService.getSpells(matchId, heroName));

    }

    @GetMapping("{matchId}/{heroName}/damage")
    public ResponseEntity<List<HeroDamage>> getDamage(@PathVariable("matchId") Long matchId,
                                                      @PathVariable("heroName") String heroName) {
        matchIdValidityCheck(matchId);

        log.debug("return the found Damage for match_id:{} and hero_name:{}", matchId, heroName);
        return ResponseEntity.ok(matchService.getDamage(matchId, heroName));

    }

    private void matchIdValidityCheck(long matchId) {
        if (matchId < 1)
            throw new InvalidInputException("The matchId should be grater than zero, Invalid matchId: " + matchId);
    }
}

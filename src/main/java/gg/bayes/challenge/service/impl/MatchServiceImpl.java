package gg.bayes.challenge.service.impl;

import gg.bayes.challenge.model.HitEntity;
import gg.bayes.challenge.model.KillEntity;
import gg.bayes.challenge.model.SpellEntity;
import gg.bayes.challenge.persistence.BuyRepository;
import gg.bayes.challenge.persistence.HitRepository;
import gg.bayes.challenge.persistence.KillRepository;
import gg.bayes.challenge.persistence.SpellRepository;
import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import gg.bayes.challenge.service.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {
    private BuyRepository buyRepository;
    private KillRepository killRepository;
    private HitRepository hitRepository;
    private SpellRepository spellRepository;

    @Autowired
    public MatchServiceImpl(BuyRepository buyRepository, KillRepository killRepository, HitRepository hitRepository, SpellRepository spellRepository) {
        this.buyRepository = buyRepository;
        this.killRepository = killRepository;
        this.hitRepository = hitRepository;
        this.spellRepository = spellRepository;
    }

    @Override
    public List<HeroKills> getHeroKills(long matchId) {

        List<HeroKills> resultList = new ArrayList<>();

        killRepository.findAllByMatchId(matchId)
                .stream()
                .collect(groupingBy(KillEntity::getHeroNameKiller, counting()))
                .forEach((name, cnt) -> resultList.add(new HeroKills(name, Math.toIntExact(cnt))));
        return resultList;
    }

    @Override
    public List<HeroItems> getItems(long matchId, String heroName) {

        return buyRepository.findAllByMatchIdAndAndHeroName(matchId, heroName)
                .stream()
                .map(e -> new HeroItems(e.getItemName(), e.getCreatedDate().getTime()))
                .collect(Collectors.toList());
    }

    @Override
    public List<HeroSpells> getSpells(long matchId, String heroName) {
        List<HeroSpells> resultList = new ArrayList<>();
        spellRepository.findAllByMatchIdAndHeroName(matchId, heroName)
                .stream()
                .collect(groupingBy(SpellEntity::getSpellName, counting()))
                .forEach((name, cnt) -> resultList.add(new HeroSpells(name, Math.toIntExact(cnt))));
        return resultList;
    }

    @Override
    public List<HeroDamage> getDamage(long matchId, String heroName) {
        List<HeroDamage> resultList = new ArrayList<>();

        // Collecting sum and count at the same time
        hitRepository.findAllByMatchIdAndHeroName(matchId, heroName)
                .stream()
                .collect(groupingBy(HitEntity::getHeroNameVictim,
                        Collector.of(
                                () -> new int[2],
                                (a, e) -> {
                                    a[0] += 1;
                                    a[1] += e.getDamage();
                                },
                                (a, b) -> {
                                    a[0] += b[0];
                                    a[1] += b[1];
                                    return a;
                                })))
                .forEach(
                        (name, a) -> resultList.add(new HeroDamage(name, a[0], a[1]))
                );
        log.debug("finding damages made by hero_name:{} in match_id:{} size:{}", heroName, matchId, resultList.size());
        return resultList;
    }
}

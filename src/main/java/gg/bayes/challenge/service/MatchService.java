package gg.bayes.challenge.service;

import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;

import java.util.List;

public interface MatchService {
    List<HeroKills> getHeroKills(long matchId);

    List<HeroItems> getItems(long matchId, String heroName);

    List<HeroSpells> getSpells(long matchId, String heroName);

    List<HeroDamage> getDamage(long matchId, String heroName);

}

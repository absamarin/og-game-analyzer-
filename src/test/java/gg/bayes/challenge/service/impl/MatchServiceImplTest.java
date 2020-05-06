package gg.bayes.challenge.service.impl;

import gg.bayes.challenge.model.BuyEntity;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.doReturn;

class MatchServiceImplTest {
    MatchService service;
    private BuyRepository buyRepository;
    private KillRepository killRepository;
    private HitRepository hitRepository;
    private SpellRepository spellRepository;


    @BeforeEach
    void setUp() {
        buyRepository = Mockito.mock(BuyRepository.class);
        killRepository = Mockito.mock(KillRepository.class);
        hitRepository = Mockito.mock(HitRepository.class);
        spellRepository = Mockito.mock(SpellRepository.class);

        service = new MatchServiceImpl(buyRepository, killRepository, hitRepository, spellRepository);
    }

    @Test
    void getHeroKills() {


        long matchId = 1;

        final List<KillEntity> mockKillEntity = new ArrayList<>() {{
            add(new KillEntity(killRepository, 4000, matchId, "npc_dota_badguys_siege", "npc_dota_goodguys_siege"));
            add(new KillEntity(killRepository, 4001, matchId, "npc_dota_badguys_siege", "npc_dota_goodguys_siege"));
            add(new KillEntity(killRepository, 4002, matchId, "npc_dota_badguys_siege", "npc_dota_goodguys_siege"));

            add(new KillEntity(killRepository, 4003, matchId, "npc_dota_badguys_tower1_mid", "npc_dota_goodguys_siege"));
            add(new KillEntity(killRepository, 4005, matchId, "npc_dota_badguys_tower1_mid", "npc_dota_goodguys_siege"));

            add(new KillEntity(killRepository, 4004, matchId, "npc_dota_creep_goodguys_melee", "npc_dota_badguys_siege"));
        }};

        doReturn(mockKillEntity).when(killRepository).findAllByMatchId(matchId);

        List<HeroKills> expectedList = new ArrayList<>() {{
            add(new HeroKills("npc_dota_badguys_siege", 3));
            add(new HeroKills("npc_dota_badguys_tower1_mid", 2));
            add(new HeroKills("npc_dota_creep_goodguys_melee", 1));
        }};

        assertThat(service.getHeroKills(matchId), containsInAnyOrder(expectedList.toArray()));


    }

    @Test
    void getItems() {
        String heroName = "npc_dota_hero_mars";
        long matchId = 1;

        List<BuyEntity> mockBuyEntity = new ArrayList<>() {{
            add(BuyEntity.builder().matchId(matchId).heroName(heroName).itemName("item_blight_stone").createdDate(Date.valueOf("2020-05-06")).build());
            add(BuyEntity.builder().matchId(matchId).heroName(heroName).itemName("item_blight_stoneitem_blight_stone").createdDate(Date.valueOf("2020-05-06")).build());
        }};

        doReturn(mockBuyEntity).when(buyRepository).findAllByMatchIdAndAndHeroName(matchId, heroName);

        List<HeroItems> expectedList = new ArrayList<>() {{
            add(new HeroItems("item_blight_stone", Date.valueOf("2020-05-06").getTime()));
            add(new HeroItems("item_blight_stoneitem_blight_stone", Date.valueOf("2020-05-06").getTime()));
        }};
        assertThat(service.getItems(matchId, heroName), containsInAnyOrder(expectedList.toArray()));
    }

    @Test
    void getSpells() {
        String heroName = "npc_dota_hero_mars";
        long matchId = 1;

        List<SpellEntity> mockSpellEntity = new ArrayList<>() {{
            add(new SpellEntity(matchId, heroName, "pangolier_shield_crash", spellRepository));
            add(new SpellEntity(matchId, heroName, "pangolier_shield_crash", spellRepository));
            add(new SpellEntity(matchId, heroName, "bloodseeker_bloodrage", spellRepository));
        }};

        doReturn(mockSpellEntity).when(spellRepository).findAllByMatchIdAndHeroName(matchId, heroName);

        List<HeroSpells> expectedList = new ArrayList<>() {{
            add(new HeroSpells("pangolier_shield_crash", 2));
            add(new HeroSpells("bloodseeker_bloodrage", 1));
        }};
        assertThat(service.getSpells(matchId, heroName), containsInAnyOrder(expectedList.toArray()));
    }

    @Test
    void getDamage() {
        String heroName = "npc_dota_hero_mars";
        long matchId = 1;

        List<HitEntity> mockHitEntity = new ArrayList<>() {{
            add(HitEntity.builder().repository(hitRepository).heroName(heroName).heroNameVictim("pangolier_shield_crash").damage(40).build());
            add(HitEntity.builder().repository(hitRepository).heroName(heroName).heroNameVictim("pangolier_shield_crash").damage(60).build());
            add(HitEntity.builder().repository(hitRepository).heroName(heroName).heroNameVictim("npc_dota_badguys_tower1_mid").damage(20).build());
        }};

        doReturn(mockHitEntity).when(hitRepository).findAllByMatchIdAndHeroName(matchId, heroName);

        List<HeroDamage> expectedList = new ArrayList<>() {{
            add(new HeroDamage("pangolier_shield_crash", 2, 100));
            add(new HeroDamage("npc_dota_badguys_tower1_mid", 1, 20));
        }};
        assertThat(service.getDamage(matchId, heroName), containsInAnyOrder(expectedList.toArray()));
    }
}
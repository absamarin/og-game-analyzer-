package gg.bayes.challenge.rest.controller;

import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import gg.bayes.challenge.service.MatchInstantiateService;
import gg.bayes.challenge.service.MatchService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class MatchControllerTest {

    public static final String BASE_URL = "/api/match/";
    @MockBean
    private MatchService service;
    @MockBean
    private MatchInstantiateService instantiateService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/match - Success")
    void ingestMatch() throws Exception {
        final long expectedMachId = 1;

        doReturn(Optional.of(expectedMachId)).when(instantiateService).ingestMatch(any());

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.TEXT_PLAIN)
                .content("test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(expectedMachId));
    }

    @Test
    @DisplayName("POST /api/match/ - Unsuccessful")
    void getMatchUnsuccessful() throws Exception {

        doReturn(Optional.empty()).when(instantiateService).ingestMatch(any());

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.TEXT_PLAIN)
                .content("test"))
                .andExpect(jsonPath("$.message").value("No match_Id found for entered payload"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /api/match - Fail")
    void ingestMatchWithEmptyInput() throws Exception {
        final long expectedMachId = 1;

        doReturn(Optional.of(expectedMachId)).when(instantiateService).ingestMatch(any(String.class));

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.TEXT_PLAIN)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/match/match_id - Found")
    void getMatch() throws Exception {

        final long machId = 1;
        final HeroKills testHeroKills = new HeroKills("rubick", 7);

        doReturn(singletonList(testHeroKills)).when(service).getHeroKills(any(Long.class));

        mockMvc.perform(get(BASE_URL + "{matchId}", machId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].hero").value(testHeroKills.getHero()));
    }

    @Test
    @DisplayName("GET /api/match/match_id - Negative")
    void getMatchWithNotPositiveMatchId() throws Exception {

        final long machId = 0;

        mockMvc.perform(get(BASE_URL + "{matchId}", machId))
                .andExpect(jsonPath("$.message").value("The matchId should be grater than zero, Invalid matchId: " + machId))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET /api/match/$match_id - Not Found")
    void getMatchNotFound() throws Exception {

        final long matchId = 1;

        doReturn(Lists.emptyList()).when(service).getHeroKills(matchId);

        mockMvc.perform(get(BASE_URL + "{matchId}", matchId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    //HeroItems>> getItems
    @Test
    @DisplayName("GET /api/match/match_id/hero_name/items - Found")
    void getItem() throws Exception {
        final long matchId = 1;
        final String heroName = "rubick";

        HeroItems testHeroItem = new HeroItems("quelling_blade", 530925L);

        doReturn(singletonList(testHeroItem)).when(service).getItems(any(Long.class), any(String.class));

        mockMvc.perform(get(BASE_URL + "{matchId}/{heroName}/items", matchId, heroName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].item").value(testHeroItem.getItem()))
                .andExpect(jsonPath("$[0].timestamp").value(testHeroItem.getTimestamp()));

    }

    @Test
    @DisplayName("GET /api/match/match_id/hero_name/items - Negative")
    void getItemWithNotPositiveMatchId() throws Exception {

        final long matchId = 0;

        final String heroName = "rubick";

        mockMvc.perform(get(BASE_URL + "{matchId}/{heroName}/items", matchId, heroName))
                .andExpect(jsonPath("$.message").value("The matchId should be grater than zero, Invalid matchId: " + matchId))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET /api/match/$match_id - Not Found")
    void getItemNotFound() throws Exception {

        final long matchId = 1;

        final String heroName = "rubick";

        doReturn(Lists.emptyList()).when(service).getItems(any(Long.class), any(String.class));

        mockMvc.perform(get(BASE_URL + "{matchId}/{heroName}/items", matchId, heroName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("{matchId}/{heroName}/spells - Found")
    void getSpells() throws Exception {

        final long matchId = 1;
        final String heroName = "rubick";
        final HeroSpells testHeroSpells = new HeroSpells("abyssal_underlord_firestorm", 83);

        doReturn(singletonList(testHeroSpells)).when(service).getSpells(any(Long.class), any(String.class));

        mockMvc.perform(get(BASE_URL + "{matchId}/{heroName}/spells", matchId, heroName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].spell").value(testHeroSpells.getSpell()))
                .andExpect(jsonPath("$[0].casts").value(testHeroSpells.getCasts()));

    }

    @Test
    @DisplayName("GET {matchId}/{heroName}/spells - Negative")
    void getSpellsWithNotPositiveMatchId() throws Exception {

        final long matchId = 0;
        final String heroName = "rubick";

        mockMvc.perform(get(BASE_URL + "{matchId}/{heroName}/spells", matchId, heroName))
                .andExpect(jsonPath("$.message").value("The matchId should be grater than zero, Invalid matchId: " + matchId))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET {matchId}/{heroName}/spells - Not Found")
    void getSpellsNotFound() throws Exception {

        final long matchId = 1;

        final String heroName = "rubick";

        doReturn(Lists.emptyList()).when(service).getItems(matchId, heroName);

        mockMvc.perform(get(BASE_URL + "{matchId}/{heroName}/spells", matchId, heroName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("{matchId}/{heroName}/damage - Found")
    void getDamage() throws Exception {

        final long matchId = 1;
        final String heroName = "rubick";
        final HeroDamage testHeroDamage = new HeroDamage("snapfire", 67, 79254);

        doReturn(singletonList(testHeroDamage)).when(service).getDamage(any(Long.class), any(String.class));

        mockMvc.perform(get(BASE_URL + "{matchId}/{heroName}/damage", matchId, heroName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].target").value(testHeroDamage.getTarget()))
                .andExpect(jsonPath("$[0].damage_instances").value(testHeroDamage.getDamageInstances()))
                .andExpect(jsonPath("$[0].total_damage").value(testHeroDamage.getTotalDamage()));

    }

    @Test
    @DisplayName("GET {matchId}/{heroName}/spells - Negative")
    void getDamagesWithNotPositiveMatchId() throws Exception {

        final long matchId = 0;
        final String heroName = "rubick";

        mockMvc.perform(get(BASE_URL + "{matchId}/{heroName}/damage", matchId, heroName))
                .andExpect(jsonPath("$.message").value("The matchId should be grater than zero, Invalid matchId: " + matchId))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET {matchId}/{heroName}/spells - Not Found")
    void getDamagesNotFound() throws Exception {

        final long matchId = 1;

        final String heroName = "rubick";

        doReturn(Lists.emptyList()).when(service).getDamage(matchId, heroName);

        mockMvc.perform(get(BASE_URL + "{matchId}/{heroName}/damage", matchId, heroName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }
}
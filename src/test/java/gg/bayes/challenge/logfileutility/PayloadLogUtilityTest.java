package gg.bayes.challenge.logfileutility;

import gg.bayes.challenge.exceptions.InvalidInputException;
import gg.bayes.challenge.model.*;
import gg.bayes.challenge.persistence.BuyRepository;
import gg.bayes.challenge.persistence.HitRepository;
import gg.bayes.challenge.persistence.KillRepository;
import gg.bayes.challenge.persistence.SpellRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.isA;


class PayloadLogUtilityTest {

    private SpellRepository mockSpellRepository;
    private BuyRepository mockBuyRepository;
    private HitRepository mockHitRepository;
    private KillRepository mockKillRepository;

    private PayloadLogUtility payloadLogUtility;


    @BeforeEach
    void setUp() {
        mockSpellRepository = Mockito.mock(SpellRepository.class);
        mockBuyRepository = Mockito.mock(BuyRepository.class);
        mockHitRepository = Mockito.mock(HitRepository.class);
        mockKillRepository = Mockito.mock(KillRepository.class);

        payloadLogUtility = new PayloadLogUtility(mockSpellRepository, mockBuyRepository, mockHitRepository, mockKillRepository);
    }

    @Test
    void ingest() throws IOException {

        doReturn(null).when(mockSpellRepository).save(isA(SpellEntity.class));
        doReturn(null).when(mockBuyRepository).save(isA(BuyEntity.class));
        doReturn(null).when(mockHitRepository).save(isA(HitEntity.class));
        doReturn(null).when(mockKillRepository).save(isA(KillEntity.class));

        String filePath = "data/combatlog_1.log.txt";
        File file = new File(filePath);

        String testStr = String.valueOf(Files.readAllLines(file.toPath()));

        assertDoesNotThrow(() -> payloadLogUtility.ingest(testStr, 1L));

    }

    @Test
    void transformLogLineToSpell() {
        final String logLine = "npc_dota_hero_pangolier casts ability pangolier_swashbuckle (lvl 1) on dota_unknown";

        GameEvent gameEvent = ReflectionTestUtils.invokeMethod(payloadLogUtility, "transformLogLineToEvent", logLine, 1L);

        assertEquals(SpellEntity.builder()
                .heroName("npc_dota_hero_pangolier")
                .matchId(1L)
                .spellName("pangolier_swashbuckle")
                .repository(mockSpellRepository).build(), gameEvent);
    }

    @Test
    void transformLogLineToBuy() {
        final String logLine = "npc_dota_hero_rubick buys item item_magic_stick";

        GameEvent gameEvent = ReflectionTestUtils.invokeMethod(payloadLogUtility, "transformLogLineToEvent", logLine, 1L);

        assertEquals(new BuyEntity(1L, "npc_dota_hero_rubick", "item_magic_stick", mockBuyRepository), gameEvent);
    }

    @Test
    void transformLogLineToKill() {
        final String logLine = "npc_dota_hero_snapfire is killed by npc_dota_hero_mars";

        GameEvent gameEvent = ReflectionTestUtils.invokeMethod(payloadLogUtility, "transformLogLineToEvent", logLine, 1L);

        assertEquals(KillEntity.builder()
                .matchId(1L)
                .heroNameKiller("npc_dota_hero_mars")
                .heroNameVictim("npc_dota_hero_snapfire")
                .repository(mockKillRepository).build(), gameEvent);
    }

    @Test
    void transformLogLineToKillWithError() {
        final String logLine = "npc_dota_hero_snapfire is not what we expect";

        assertThrows(InvalidInputException.class, () ->
                ReflectionTestUtils.invokeMethod(payloadLogUtility, "transformLogLineToEvent", logLine, 1L));
    }

    @Test
    void transformLogLineToHit() {
        final String logLine = "npc_dota_hero_pangolier hits npc_dota_hero_rubick with pangolier_swashbuckle for 19 damage (26->7)";

        GameEvent gameEvent = ReflectionTestUtils.invokeMethod(payloadLogUtility, "transformLogLineToEvent", logLine, 1L);

        assertEquals(HitEntity.builder()
                .heroName("npc_dota_hero_pangolier")
                .matchId(1)
                .heroNameVictim("npc_dota_hero_rubick")
                .damage(19)
                .repository(mockHitRepository).build(), gameEvent);
    }

    @Test
    void transformLogLineIgnored() {
        final String logLine = "npc_dota_hero_pangolier uses item_tango";

        GameEvent gameEvent = ReflectionTestUtils.invokeMethod(payloadLogUtility, "transformLogLineToEvent", logLine, 1L);

        assertEquals(new IgnoredEntity(1L, logLine), gameEvent);
    }

    @Test
    void transformLogUnrecognizable() {
        final String logLine = "Unrecognizable sample log line should throw";

        assertThrows(InvalidInputException.class, () ->
                ReflectionTestUtils.invokeMethod(payloadLogUtility, "transformLogLineToEvent", logLine, 1L));
    }


}
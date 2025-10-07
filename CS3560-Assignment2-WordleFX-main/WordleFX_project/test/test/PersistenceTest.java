
package test; import org.junit.Test; import static org.junit.Assert.*; import wordle.model.*; import wordle.io.GameIO; import java.nio.file.Path;
public class PersistenceTest {
    @Test public void testSaveLoadRoundTrip() throws Exception {
        Dictionary d=new Dictionary(); Stats s=new Stats(); WordleModel m=new WordleModel(d,s);
        m.getState().setSecret("CRANE"); m.submitGuess("CROWN"); GameIO.save(m.getState(), s, Path.of("savegame_test.json"));
        Stats s2=new Stats(); GameState g2=new GameState("XXXXX"); GameIO.loadInto(g2, s2, Path.of("savegame_test.json"));
        assertEquals(m.getState().getGuesses(), g2.getGuesses()); assertEquals(m.getState().getTurn(), g2.getTurn());
    }
}

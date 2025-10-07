package test;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import wordle.model.Dictionary;
import wordle.model.Feedback;
import wordle.model.HintEngine;
import wordle.model.Stats;
import wordle.model.WordleModel;

public class HintEngineTest {

    @Test
    public void testHintProducesWord() {
        // strict dictionary (only words from assets/words/answers*.txt used for candidates)
        Dictionary d = new Dictionary(false);

        List<String> guesses = new ArrayList<>();
        List<Feedback[]> fbs = new ArrayList<>();

        guesses.add("CROWN");
        Feedback[] fb = new WordleModel(d, new Stats()).evaluate("CROWN", "CRANE");
        fbs.add(fb);

        List<String> filtered = HintEngine.filterCandidates(d.allCandidates(), guesses, fbs);
        String hint = HintEngine.bestHint(filtered);

        assertNotNull(hint);
        assertEquals(5, hint.length());
    }
}

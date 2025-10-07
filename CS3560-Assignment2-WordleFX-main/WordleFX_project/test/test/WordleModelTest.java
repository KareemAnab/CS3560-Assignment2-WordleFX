
package test; import org.junit.Test; import static org.junit.Assert.*; import wordle.model.*;
public class WordleModelTest {
    @Test public void testExactMatchAllGreen(){ WordleModel m=new WordleModel(new Dictionary(), new Stats()); Feedback[] fb=m.evaluate("HELLO","HELLO"); for(Feedback f:fb) assertEquals(Feedback.GREEN,f); }
    @Test public void testDuplicateLettersHandled(){ WordleModel m=new WordleModel(new Dictionary(), new Stats()); Feedback[] fb=m.evaluate("LLAMA","HELLO"); int y=0,g=0; for(Feedback f:fb){ if(f==Feedback.YELLOW)y++; if(f==Feedback.GREEN)g++; } assertTrue(y+g<=3); }
    @Test public void testHardModeViolationThrows(){ Dictionary d=new Dictionary(); Stats s=new Stats(); WordleModel m=new WordleModel(d,s); m.getState().setSecret("CRANE"); m.submitGuess("CROWN"); m.setHardMode(true); boolean threw=false; try{ m.submitGuess("ABOUT"); } catch(IllegalArgumentException ex){ threw=true; } assertTrue(threw); }
}


package test; import org.junit.Test; import static org.junit.Assert.*; import wordle.model.*;
public class FeedbackLogicTest {
    @Test public void testPartialYellow(){ WordleModel m=new WordleModel(new Dictionary(), new Stats()); Feedback[] fb=m.evaluate("CRATE","TRACE"); boolean hasYellow=false; for(Feedback f:fb) if(f==Feedback.YELLOW) hasYellow=true; assertTrue(hasYellow); }
    @Test public void testAllGray(){ WordleModel m=new WordleModel(new Dictionary(), new Stats()); Feedback[] fb=m.evaluate("ZYXWV","TRACE"); for(Feedback f:fb) assertEquals(Feedback.GRAY,f); }
}


package test; import org.junit.Test; import static org.junit.Assert.*; import wordle.model.Dictionary;
public class DictionaryTest { @Test public void testValidCaseInsensitive(){ Dictionary d=new Dictionary(); assertTrue(d.isValidWord("crane")); assertTrue(d.isValidWord("CRANE")); assertFalse(d.isValidWord("xxxxx")); } }

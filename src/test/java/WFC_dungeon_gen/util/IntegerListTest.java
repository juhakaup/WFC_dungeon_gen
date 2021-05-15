package WFC_dungeon_gen.util;

import java.util.ArrayList;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Juha Kauppinen
 */
public class IntegerListTest {
    
    /**
     * Tests adding integers to the list.
     */
    @Test
    public void testAddingIntegersToTheList() {
        Random rng = new Random();
        int cycles = 1000;
        IntegerList iList = new IntegerList();
        ArrayList<Integer> aList = new ArrayList<>();
        
        for (int i=0; i<cycles; i++) {
            int random = rng.nextInt();
            iList.add(random);
            aList.add(random);
        }
        
        for (int i=0; i<aList.size(); i++) {
            int expected = aList.get(i);
            int result = iList.get(i);
            assertEquals(expected, result);
        }
    }
    
    /**
     * Tests that the list length returns correct value.
     */
    @Test
    public void testListLength() {
        Random rng = new Random();
        int len = rng.nextInt(100);
        IntegerList iList = new IntegerList();
        
        for (int i=0; i<len; i++) {
            iList.add(rng.nextInt());
        }
        
        assertEquals(len, iList.size());
    }
    
    /**
     * Tests that assert is thrown if given index is out of range.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void TestIndexOutOfBoundsException() {
        Random rng = new Random();
        int len = rng.nextInt(100);
        IntegerList iList = new IntegerList();
        
        for (int i=0; i<len; i++) {
            iList.add(rng.nextInt());
        }
        
        iList.get(len+1);
        iList.get(-1);
    }
}

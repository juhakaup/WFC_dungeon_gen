package WFC_dungeon_gen;

import WFC_dungeon_gen.domain.Performance;
import WFC_dungeon_gen.ui.GeneratorUi;

/**
 * Wave function collapse dungeon generator
 * @author Juha Kauppinen
 */
public class Main {

    public static void main(String[] args) {  
        if (args.length > 0) {
            if (args[0].equals("test")) {
                Performance performanceTest = new Performance();
            }
        } else {
            GeneratorUi.main(); 
        }
        
    }

}

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simple.software.meaurements.Config;
import org.simple.software.HelperFunctions;

import java.io.IOException;
import java.util.Random;

public class TestDocumentGeneration {
    public void init() {
        Config.setAllToFalse();
    }

    @Test
    void testDoWordCountInputMap() throws IOException {
        init();
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            int length  = rand.nextInt(50);
            int file    = rand.nextInt(2) + 1;
            int seed    = (int) (Math.random()*10000);
            String docu1 = HelperFunctions.generateDocument(length, file, seed);
            String docu2 = HelperFunctions.generateDocument(length, file, seed);

            Assertions.assertEquals(docu1, docu2);
        }
    }
}

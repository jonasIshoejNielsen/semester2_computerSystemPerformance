import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simple.software.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestThreading {


    @Test
    void testDoWordCountInputMap() throws IOException {
        Config.setAllToFalse();
        Random rand = new Random();
        int length  = rand.nextInt(50);
        int file    = rand.nextInt(2) + 1;
        int seed    = (int) (Math.random()*10000);
        String docu1 = DocumentGenerator.generateDocument(length, file, seed);

        long numberOfLineStorageLists = 1_000_000;
        int threadCount = 16;
        List<LineStorage> lineStorageList = new ArrayList<>();
        for (long i = 0; i < numberOfLineStorageLists; i++) {
            lineStorageList.add(new LineStorage(docu1, 0, null, 0));
        }

        for (LineStorage ls: lineStorageList) {
            Server.linesToCount.add(ls);
        }

        WoCoServer.setUpDataHandlers(true, threadCount, false);

        while (! Server.linesToCount.isEmpty()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assertions.assertEquals(0, Server.linesToCount.size());
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String res = Arrays.toString(lineStorageList.get(0).returnMessage);
        for (LineStorage ls: lineStorageList) {
            Assertions.assertEquals(res, Arrays.toString(ls.returnMessage));
        }

    }
}


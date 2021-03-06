import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simple.software.*;
import org.simple.software.meaurements.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

public class TestThreading {

    String genDocument () throws IOException {
        Random rand = new Random();
        int length  = rand.nextInt(50);
        int file    = rand.nextInt(2) + 1;
        int seed    = (int) (Math.random()*10000);
        return HelperFunctions.generateDocument(length, file, seed);
    }
    List<LineStorage> genListOfLineStorage(String docu, long numberOfLineStorageLists, int threadCount) {
        List<LineStorage> lineStorageList = new ArrayList<>();
        for (long i = 0; i < numberOfLineStorageLists; i++) {
            lineStorageList.add(new LineStorage(docu, 0, null, 0));
        }

        for (LineStorage ls: lineStorageList) {
            Server.linesToCount.add(ls);
        }
        return lineStorageList;
    }
    void waitForAllToBeDone (ExecutorService es) {
        while (! Server.linesToCount.isEmpty()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assertions.assertEquals(0, Server.linesToCount.size());
        es.shutdown();
        Assertions.assertEquals(true, es.isShutdown());
    }
    void asserAllLineStoragesAreCorrect (List<LineStorage> lineStorageList) {
        String res = Arrays.toString(lineStorageList.get(0).returnMessage);
        for (LineStorage ls: lineStorageList) {
            if(res == null || ls.returnMessage == null) {
                System.out.println("null error");
            }
            Assertions.assertEquals(res, Arrays.toString(ls.returnMessage));
        }
    }

    @Test
    void testThreaddingWorkerPrimary() throws IOException {
        Config.setAllToFalse();
        String docu = genDocument();

        long numberOfLineStorageLists = 1_000_000;
        int threadCount = 16;
        List<LineStorage> lineStorageList = genListOfLineStorage(docu, numberOfLineStorageLists, threadCount);
        ExecutorService es = WoCoServer.setUpWorkers(threadCount, false, i -> new Worker(true, false, i));

        waitForAllToBeDone (es);
        asserAllLineStoragesAreCorrect (lineStorageList);
    }
}


package org.simple.software;

import org.simple.software.meaurements.Measurements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Worker {
    private final boolean cMode;
    private final boolean fixedNumberOfClients;
    private final int workerId;

    public Worker(boolean cMode, boolean fixedNumberOfClients, int workerId) {
        this.cMode                = cMode;
        this.fixedNumberOfClients = fixedNumberOfClients;
        this.workerId             = workerId;
    }

    public int getWorkerId() {
        return workerId;
    }

    private LineStorage getNextLineStorage () throws InterruptedException {
        return Server.linesToCount.take();
    }

    public void startPipeLine (boolean repeat, boolean sendToClient) {
        do {
            try {
                LineStorage ls = null;
                try {
                    ls = getNextLineStorage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
                try {
                    long endTimeInQueue = System.nanoTime();
                    ls.doWordCount(cMode);
                    long beginSerializing = System.nanoTime();
                    byte[] returnMessage = serializeResultForClient(ls).getBytes();
                    long endSerializing = System.nanoTime();
                    ls.sendToClient(returnMessage, sendToClient);
                    long endFromStart = System.nanoTime();
                    Server.measurementsCleaning.addMeasurement(ls.beginCleaning, ls.endCleaning);
                    Server.measurementsWordCount.addMeasurement(ls.beginWordCount, ls.endWordCount);
                    Server.measurementsInQueue.addMeasurement(ls.getTimeFromEnteringServer(), endTimeInQueue);
                    Server.measurementsSerializing.addMeasurement(beginSerializing, endSerializing);
                    Server.measurementsInServer.addMeasurement(ls.getTimeFromEnteringServer(), endFromStart);
                } catch (Exception e) {
                    System.out.println(ls.line);
                    System.out.println(e.getMessage());
                }
                if (fixedNumberOfClients)
                    WoCoServer.reportFinishedMessage();
            } catch (Exception e) {
                System.out.println(e);
            }
        } while (repeat);
    }

    /**
     * Returns a serialized version of the word count associated with the last
     * processed document for a given client. If not called before processing a new
     * document, the result is overwritten by the new one.
     * @param ls
     * @return
     */
    public String serializeResultForClient(LineStorage ls) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : ls.getResults().entrySet()) {
            sb.append(entry.getKey()).append(",");
            sb.append(entry.getValue()).append(",");
        }
        sb.append("\n");
        return sb.substring(0);
    }
}
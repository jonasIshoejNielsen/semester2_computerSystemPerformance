package org.simple.software;

import org.simple.software.meaurements.Measurements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkerSynchronized implements Worker {
    private List<Measurements> measurementsCleaning   = new ArrayList<>();
    private List<Measurements> measurementsWordCount  = new ArrayList<>();
    private Measurements measurementsSerializing      = new Measurements();
    private Measurements measurementsInServer         = new Measurements();
    private final boolean cMode;
    private final boolean fixedNumberOfClients;
    private final int workerId;


    public WorkerSynchronized(boolean cMode, boolean fixedNumberOfClients, int workerId) {
        this.cMode                = cMode;
        this.fixedNumberOfClients = fixedNumberOfClients;
        this.workerId             = workerId;
    }

    public List<Measurements> getMeasurementsCleaning() {
        return measurementsCleaning;
    }

    public List<Measurements> getMeasurementsWordCount() {
        return measurementsWordCount;
    }

    public Measurements getMeasurementsSerializing() {
        return measurementsSerializing;
    }

    public Measurements getMeasurementsInServer() {
        return measurementsInServer;
    }

    public synchronized int getWorkerId() {
        return workerId;
    }

    private synchronized LineStorage getNextLineStorage () throws InterruptedException {
        return Server.linesToCount.take();
    }

    public void startPipeLine (boolean repeat, boolean sendToClient) {
        do {
            LineStorage ls = null;
            try {
                ls = getNextLineStorage();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            ls.doWordCount(cMode);
            long beginSerializing = System.nanoTime();
            byte[] returnMessage = serializeResultForClient(ls).getBytes();
            long endSerializing = System.nanoTime();
            ls.sendToClient(returnMessage, sendToClient);
            long endFromStart = System.nanoTime();

            measurementsSerializing.addMeasurement(beginSerializing, endSerializing);
            measurementsInServer.addMeasurement(ls.getTimeFromEnteringServer(), endFromStart);
            measurementsCleaning.add(ls.getMeasurementsCleaning());
            measurementsWordCount.add(ls.getMeasurementsWordCount());
            if (fixedNumberOfClients)
                WoCoServer.reportFinishedMessage();
        } while (repeat);
    }

    /**
     * Returns a serialized version of the word count associated with the last
     * processed document for a given client. If not called before processing a new
     * document, the result is overwritten by the new one.
     * @param ls
     * @return
     */
    public synchronized String serializeResultForClient(LineStorage ls) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : ls.getResults().entrySet()) {
            sb.append(entry.getKey()).append(",");
            sb.append(entry.getValue()).append(",");
        }
        sb.append("\n");
        return sb.substring(0);
    }
}
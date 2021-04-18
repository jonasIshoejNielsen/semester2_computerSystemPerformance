package org.simple.software;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataHandlerSynchronized implements DataHandler {
    private final ArrayList<List<Long>> timesCleaning                 = new ArrayList<>();
    private final ArrayList<List<Long>> timesWordCount                = new ArrayList<>();
    private final List<Long> timesSerializing                         = new ArrayList<>();
    private final List<Long> timesInServer                            = new ArrayList<>();
    private int dataHandlerId;
    private final boolean cMode;

    public DataHandlerSynchronized(boolean cMode, int dataHandlerId) {
        this.cMode = cMode;
        this.dataHandlerId = dataHandlerId;
    }

    public synchronized ArrayList<List<Long>> getTimesCleaning() {
        return timesCleaning;
    }

    public synchronized ArrayList<List<Long>> getTimesWordCount() {
        return timesWordCount;
    }

    public synchronized List<Long> getTimesSerializing() {
        return timesSerializing;
    }

    public synchronized List<Long> getTimesInServer() {
        return timesInServer;
    }
    public synchronized int getDataHandlerId() {
        return dataHandlerId;
    }

    private synchronized LineStorage getNextLineStorage () throws InterruptedException {
        Server.removed++;
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

            timesCleaning.add(ls.getTimeCleaning());
            timesWordCount.add(ls.getTimeWordCount());
            timesSerializing.add(beginSerializing - endSerializing);
            timesInServer.add(ls.getTimeFromEnteringServer() - endFromStart);
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
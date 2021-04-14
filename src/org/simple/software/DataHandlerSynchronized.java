package org.simple.software;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHandlerSynchronized implements DataHandler {
    private static final HashMap<Integer, StringBuilder> buffer              = new HashMap<>();
    private static final HashMap<Integer, Long> timesFromStart               = new HashMap<>();
    private static final ArrayList<LineStorage> linesToCount                 = new ArrayList<>();
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

    private synchronized LineStorage getNextLineStorage () {
        while (!linesToCount.isEmpty()) {
            return linesToCount.remove(0);
        }
        return null;
    }

    public synchronized boolean readFromChanel(ByteBuffer bb, SocketChannel client) throws IOException {
        int readCnt = client.read(bb);
        if (readCnt<=0) {
            return false;
        }
        int clientId = client.hashCode();
        String dataChunkToAdd = new String(bb.array(),0, readCnt);

        boolean receivedAllData = receiveData(clientId, client, dataChunkToAdd);

        if (receivedAllData) {
            startPipeLine();
        }
        return true;
    }
    private void startPipeLine () {
        LineStorage ls = getNextLineStorage();
        ls.doWordCount(cMode);
        long beginSerializing   = System.nanoTime();
        byte[] returnMessage    = serializeResultForClient(ls).getBytes();
        long endSerializing     = System.nanoTime();
        ByteBuffer ba = ByteBuffer.wrap(returnMessage);
        try {
            ls.getClient().write(ba);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endFromStart     = System.nanoTime();
        timesCleaning.add(ls.getTimeCleaning());
        timesWordCount.add(ls.getTimeWordCount());
        timesSerializing.add(beginSerializing - endSerializing);
        timesInServer.add(timesFromStart.get(ls.getClientId()) - endFromStart);
    }

    /**
     * This function handles data received from a specific client (TCP connection).
     * Internally it will check if the buffer associated with the client has a full
     * document in it (based on the SEPARATOR). If yes, it will process the document and
     * return true, otherwise it will add the data to the buffer and return false
     * @param clientId
     * @param dataChunk
     * @return A document has been processed or not.
     */
    public synchronized Boolean receiveData(int clientId, SocketChannel client, String dataChunk) {
        if(!buffer.containsKey(clientId)) {
            buffer.put(clientId, new StringBuilder());
            System.out.println("put"+clientId);
            timesFromStart.put(clientId, System.nanoTime());
        }

        StringBuilder sb = buffer.get(clientId);
        sb.append(dataChunk);

        if (dataChunk.indexOf(WoCoServer.SEPARATOR)==-1) {
            return false;
        }

        String bufData = sb.toString();
        int indexNL = bufData.indexOf(WoCoServer.SEPARATOR);

        String line = bufData.substring(0, indexNL);
        String rest = (bufData.length()>indexNL+1) ? bufData.substring(indexNL+1) : null;

        if (indexNL==0) {
            HelperFunctions.print(DataHandlerSynchronized.class, "SEP@", indexNL+"", " bufdata:\n", bufData);
        }

        if (rest != null) {
            HelperFunctions.print(DataHandlerSynchronized.class, "more than one line: \n", rest);
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            buffer.put(clientId, new StringBuilder(rest));
        } else {
            buffer.put(clientId, new StringBuilder());
        }

        //word count in line
        linesToCount.add(new LineStorage(line, clientId, client));
        return true;

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
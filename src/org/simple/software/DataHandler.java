package org.simple.software;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DataHandler {
    private static final HashMap<Integer, StringBuilder> buffer              = new HashMap<>();
    private static final HashMap<Integer, HashMap<String, Integer>> results  = new HashMap<>();
    private static final HashMap<Integer, Long> timesFromStart               = new HashMap<>();
    private static final ArrayList<LineStorage> linesToCount                 = new ArrayList<>();
    private final ArrayList<List<Long>> timesCleaning                 = new ArrayList<>();
    private final ArrayList<List<Long>> timesWordCount                = new ArrayList<>();
    private final List<Long> timesSerializing                         = new ArrayList<>();
    private final List<Long> timesInServer                            = new ArrayList<>();
    private int clientId;
    private final boolean cMode;

    public static void addLineToCount(String line, int clientId) {
        linesToCount.add(new LineStorage(line, clientId));
    }

    public DataHandler(boolean cMode, int clientId) {
        this.cMode = cMode;
        this.clientId = clientId;
    }

    public ArrayList<List<Long>> getTimesCleaning() {
        return timesCleaning;
    }

    public ArrayList<List<Long>> getTimesWordCount() {
        return timesWordCount;
    }

    public List<Long> getTimesSerializing() {
        return timesSerializing;
    }

    public List<Long> getTimesInServer() {
        return timesInServer;
    }
    public int getClientId() {
        return clientId;
    }

    public void countLine () {
        while (!linesToCount.isEmpty()) {
            LineStorage ls = linesToCount.remove(0);
            HashMap<String, Integer> wc = results.getOrDefault(ls.getClientId(), new HashMap<>());
            ls.doWordCount(wc, cMode);
            timesCleaning.add(ls.getTimeCleaning());
            timesWordCount.add(ls.getTimeWordCount());
        }
    }

    public boolean readFromChanel(ByteBuffer bb, SocketChannel client) throws IOException {
        int readCnt = client.read(bb);
        if (readCnt<=0) {
            return false;
        }
        int clientId = client.hashCode();
        String result = new String(bb.array(),0, readCnt);

        boolean hasResult = receiveData(clientId, result);

        if (hasResult) {
            long beginSerializing   = System.nanoTime();
            byte[] returnMessage    = serializeResultForClient(clientId).getBytes();
            long endSerializing     = System.nanoTime();
            timesSerializing.add(beginSerializing - endSerializing);
            ByteBuffer ba = ByteBuffer.wrap(returnMessage);
            client.write(ba);
            long endFromStart     = System.nanoTime();
            timesInServer.add(timesFromStart.get(clientId) - endFromStart);
            
        }
        return true;
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
    public boolean receiveData(int clientId, String dataChunk) {
        if(!results.containsKey(clientId)) {
            results.put(clientId, new HashMap<>());
            buffer.put(clientId, new StringBuilder());
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
            HelperFunctions.print(DataHandler.class, "SEP@", indexNL+"", " bufdata:\n", bufData);
        }

        if (rest != null) {
            HelperFunctions.print(DataHandler.class, "more than one line: \n", rest);
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
        addLineToCount(line, clientId);
        countLine();
        return true;

    }

    /**
     * Returns a serialized version of the word count associated with the last
     * processed document for a given client. If not called before processing a new
     * document, the result is overwritten by the new one.
     * @param clientId
     * @return
     */
    public String serializeResultForClient(int clientId) {
        if (results.containsKey(clientId)) {
            StringBuilder sb = new StringBuilder();
            HashMap<String, Integer> hm = results.get(clientId);
            for (Map.Entry<String, Integer> entry : hm.entrySet()) {
                sb.append(entry.getKey()).append(",");
                sb.append(entry.getValue()).append(",");
            }
            results.remove(clientId);
            sb.append("\n");
            return sb.substring(0);
        } else {
            return "";
        }
    }
}
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
    private int clientId;
    private final boolean cMode;

    public DataHandlerSynchronized(boolean cMode, int clientId) {
        this.cMode = cMode;
        this.clientId = clientId;
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
    public synchronized int getClientId() {
        return clientId;
    }

    public synchronized LineStorage countLine () {
        while (!linesToCount.isEmpty()) {
            LineStorage ls = linesToCount.remove(0);
            ls.doWordCount(cMode);
            timesCleaning.add(ls.getTimeCleaning());
            timesWordCount.add(ls.getTimeWordCount());
            return ls;
        }
        return null;
    }

    public synchronized boolean readFromChanel(ByteBuffer bb, SocketChannel client) throws IOException {
        int readCnt = client.read(bb);
        if (readCnt<=0) {
            return false;
        }
        int clientId = client.hashCode();
        String result = new String(bb.array(),0, readCnt);

        LineStorage ls = receiveData(clientId, result);

        if (ls != null) {
            long beginSerializing   = System.nanoTime();
            byte[] returnMessage    = serializeResultForClient(ls).getBytes();
            long endSerializing     = System.nanoTime();
            timesSerializing.add(beginSerializing - endSerializing);
            ByteBuffer ba = ByteBuffer.wrap(returnMessage);
            client.write(ba);
            long endFromStart     = System.nanoTime();
            timesInServer.add(timesFromStart.get(clientId) - endFromStart);
            
        }
        return true;
    }

    public synchronized LineStorage receiveData(int clientId, String dataChunk) {
        if(!buffer.containsKey(clientId)) {
            buffer.put(clientId, new StringBuilder());
            timesFromStart.put(clientId, System.nanoTime());
        }

        StringBuilder sb = buffer.get(clientId);
        sb.append(dataChunk);

        if (dataChunk.indexOf(WoCoServer.SEPARATOR)==-1) {
            return null;
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
        linesToCount.add(new LineStorage(line, clientId));
        LineStorage ls = countLine();
        return ls;

    }

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
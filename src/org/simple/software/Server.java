package org.simple.software;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.function.BiConsumer;

public class Server {
    private static final HashMap<Integer, StringBuilder> buffer = new HashMap<>();
    public static final HashMap<Integer, Long> timesFromStart   = new HashMap<>();
    public static final ArrayList<LineStorage> linesToCount     = new ArrayList<>();

    private Selector selector;
    private ServerSocketChannel serverSocket;
    private List<DataHandler> dataHandlerList = new ArrayList<>();
    private final boolean onlyOneThread;

    public Server(String lAddr, int lPort, boolean onlyOneThread) throws IOException {
        this.onlyOneThread = onlyOneThread;
        selector = Selector.open();
        openSocket(new InetSocketAddress(lAddr, lPort), selector);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Writing to logs");
                for (DataHandler dh: dataHandlerList) {
                    System.out.println("Writing to logs" + dh.getDataHandlerId());
                    logListOfTimes(dh.getDataHandlerId(), dh.getTimesCleaning(),  Logging::writeCleaningTags);
                    logListOfTimes(dh.getDataHandlerId(), dh.getTimesWordCount(), Logging::writeWordCount);
                    logTimes(dh.getDataHandlerId(), dh.getTimesSerializing(),     Logging::writeSerializing);
                    logTimes(dh.getDataHandlerId(), dh.getTimesInServer(),        Logging::writeTimeInServer);

                }
            }
        });
    }
    private void logTimes(int clientId, List<Long> times, BiConsumer<Long, Integer> writeToLog) {
        for (Long time: times) {
            writeToLog.accept(time, clientId);
        }
    }
    private void logListOfTimes(int clientId, List<List<Long>> times, BiConsumer<Long, Integer> writeToLog) {
        for (List<Long> lst: times) {
            logTimes(clientId, lst, writeToLog);
        }
    }

    public List<DataHandler> setUpDataHandlers(boolean cMode, int threadCount) { //todo: threadCOunt
        dataHandlerList.add(new DataHandlerSynchronized(cMode, 3));
        return dataHandlerList;
    }

    public void startListening() throws IOException {
        // Infinite loop..
        // Keep server running
        ByteBuffer bb = ByteBuffer.allocate(1024*1024);
        while (true) {
            selector.select();
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //todo: iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel client = serverSocket.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    HelperFunctions.print(WoCoServer.class, "Connection Accepted: ", client.getLocalAddress().toString(), "\n");

                } else if (key.isReadable()) {
                    handleRead(bb, key);
                }
                iterator.remove();
            }
        }
    }
    private void openSocket(InetSocketAddress myAddr, Selector selector) throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(myAddr);
        serverSocket.configureBlocking(false);
        int ops = serverSocket.validOps();
        serverSocket.register(selector, ops, null);
    }
    private void handleRead(ByteBuffer bb, SelectionKey key) throws IOException {
        bb.rewind();
        SocketChannel client = (SocketChannel) key.channel();
        Boolean readFromChannel = readFromChanel(bb, client);
        if (readFromChannel.equals(false)) {
            key.cancel();
        }
    }

    public synchronized boolean readFromChanel(ByteBuffer bb, SocketChannel client) throws IOException {
        int readCnt = client.read(bb);
        if (readCnt<=0) {
            return false;
        }
        int clientId = client.hashCode();
        String dataChunkToAdd = new String(bb.array(),0, readCnt);

        boolean receivedAllData = putIntoBuffer(clientId, client, dataChunkToAdd);

        if (receivedAllData) {
            if (onlyOneThread) {
                dataHandlerList.get(0).startPipeLine();
            }
        }
        else {
            System.out.println("check !receivedAllData");
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
    public synchronized Boolean putIntoBuffer(int clientId, SocketChannel client, String dataChunk) {
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

        if (rest == null) {
            buffer.remove(clientId);
        } else {
            handleMultipleLines(clientId, rest);
        }

        //word count in line
        linesToCount.add(new LineStorage(line, clientId, client));
        return true;
    }

    private static void handleMultipleLines(int clientId, String rest) {
        buffer.remove(clientId);
        HelperFunctions.print(DataHandlerSynchronized.class, "Unhandled more than one line: \n", rest);
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffer.put(clientId, new StringBuilder(rest));
    }


}

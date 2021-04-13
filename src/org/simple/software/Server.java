package org.simple.software;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class Server {
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private List<DataHandler> dataHandlerList = new ArrayList<>();

    public Server(String lAddr, int lPort) throws IOException {
        selector = Selector.open();
        openSocket(new InetSocketAddress(lAddr, lPort), selector);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Writing to logs");
                for (DataHandler dh: dataHandlerList) {
                    System.out.println("Writing to logs" + dh.getClientId());
                    logListOfTimes(dh.getClientId(), dh.getTimesCleaning(),  Logging::writeCleaningTags);
                    logListOfTimes(dh.getClientId(), dh.getTimesWordCount(), Logging::writeWordCount);
                    logTimes(dh.getClientId(), dh.getTimesSerializing(),     Logging::writeSerializing);
                    logTimes(dh.getClientId(), dh.getTimesInServer(),        Logging::writeTimeInServer);

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
                    bb.rewind();
                    SocketChannel client = (SocketChannel) key.channel();
                    Boolean readFromChannel = dataHandlerList.get(0).readFromChanel(bb, client);    //todo change
                    if (readFromChannel.equals(false)) {
                        key.cancel();
                    }
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
}

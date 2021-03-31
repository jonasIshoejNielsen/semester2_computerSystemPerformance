package org.simple.software;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public Server(String lAddr, int lPort, DataHandler dataHandler) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        InetSocketAddress myAddr = new InetSocketAddress(lAddr, lPort);
        serverSocket.bind(myAddr);
        serverSocket.configureBlocking(false);

        int ops = serverSocket.validOps();
        SelectionKey selectKey = serverSocket.register(selector, ops, null);

        // Infinite loop..
        // Keep server running
        ByteBuffer bb = ByteBuffer.allocate(1024*1024);
        ByteBuffer ba;


        while (true) {
            selector.select();
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isAcceptable()) {
                    SocketChannel client = serverSocket.accept();

                    client.configureBlocking(false);

                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("Connection Accepted: " + client.getLocalAddress() + "\n");

                } else if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    int clientId = client.hashCode();

                    bb.rewind();
                    int readCnt = client.read(bb);

                    if (readCnt>0) {
                        String result = new String(bb.array(),0, readCnt);

                        boolean hasResult = dataHandler.receiveData(clientId, result);

                        if (hasResult) {

                            ba = ByteBuffer.wrap(dataHandler.serializeResultForClient(clientId).getBytes());
                            client.write(ba);
                        }
                    } else {
                        key.cancel();
                    }


                }
                iterator.remove();
            }
        }

    }
}

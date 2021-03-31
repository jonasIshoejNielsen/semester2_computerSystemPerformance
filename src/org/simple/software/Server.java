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
    private Selector selector;
    private ServerSocketChannel serverSocket;

    public Server(String lAddr, int lPort) throws IOException {
        selector = Selector.open();
        openSocket(new InetSocketAddress(lAddr, lPort), selector);
    }
    public void startListening(DataHandler dataHandler) throws IOException {
        // Infinite loop..
        // Keep server running
        ByteBuffer bb = ByteBuffer.allocate(1024*1024);
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
                    HelperFunctions.print(WoCoServer.class, "Connection Accepted: ", client.getLocalAddress().toString(), "\n");

                } else if (key.isReadable()) {
                    bb.rewind();
                    SocketChannel client = (SocketChannel) key.channel();
                    Boolean readFromChannel = dataHandler.readFromChanel(bb, client);
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

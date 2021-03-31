package org.simple.software;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class WoCoServer {
	
	public static final char SEPARATOR = '$';




	public static void main(String[] args) throws IOException {
		
		if (args.length!=4) {
			System.out.println("Usage: <listenaddress> <listenport> <cleaning> <threadcount>");
			System.exit(0);
		}
		
		String lAddr = args[0];
		int lPort = Integer.parseInt(args[1]);
		boolean cMode = Boolean.parseBoolean(args[2]);
		int threadCount = Integer.parseInt(args[3]);
		
		if (cMode==true) {
			//TODO: will have to implement cleaning from HTML tags
			System.out.println("FEATURE NOT IMPLEMENTED");
			System.exit(0);

		}
		
		if (threadCount>1) {
			//TODO: will have to implement multithreading
			System.out.println("FEATURE NOT IMPLEMENTED");
			System.exit(0);

		}
		
		
		DataHandler dataHandler = new DataHandler();
		
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


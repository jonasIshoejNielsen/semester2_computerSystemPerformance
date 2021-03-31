package org.simple.software;

import java.io.IOException;

public class WoCoServer {
	
	public static final char SEPARATOR = '$';


	public static void main(String[] args) throws IOException {
		
		if (args.length!=4) {
			HelperFunctions.print(WoCoServer.class, "Usage: <listenaddress> <listenport> <cleaning> <threadcount>");
			System.exit(0);
		}
		
		String lAddr = args[0];
		int lPort = Integer.parseInt(args[1]);
		boolean cMode = Boolean.parseBoolean(args[2]);
		int threadCount = Integer.parseInt(args[3]);
		
		if (cMode) {
			//TODO: will have to implement cleaning from HTML tags
			HelperFunctions.print(WoCoServer.class, "FEATURE NOT IMPLEMENTED");
			System.exit(0);

		}
		
		if (threadCount>1) {
			//TODO: will have to implement multithreading
			HelperFunctions.print(WoCoServer.class, "FEATURE NOT IMPLEMENTED");
			System.exit(0);

		}

		DataHandler dataHandler = new DataHandler();

		Server server = new Server(lAddr, lPort);
		server.startListening(dataHandler);
	}

}


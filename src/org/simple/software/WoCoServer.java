package org.simple.software;

import java.io.IOException;

public class WoCoServer {
	
	public static final char SEPARATOR = '$';

	public static void main(String[] args) throws IOException {
		
		if (args.length!=4) {
			HelperFunctions.print(WoCoServer.class, "Usage: <listenaddress> <listenport> <cleaning> <threadcount>");
			System.exit(0);
		}
		
		String lAddr 	= args[0];
		int lPort 		= Integer.parseInt(args[1]);
		boolean cMode 	= Boolean.parseBoolean(args[2]);
		int threadCount = Integer.parseInt(args[3]);
		System.out.println(cMode? "Clean tags": "Don't clean tags");
		System.out.println(threadCount + " number of threads");

		Logging.createFolder();


		if (threadCount>1) {
			//TODO: will have to implement multithreading
			HelperFunctions.print(WoCoServer.class, "FEATURE NOT IMPLEMENTED");
			System.exit(0);

		}

		DataHandler dataHandler = new DataHandler(cMode);

		Server server = new Server(lAddr, lPort);
		server.startListening(dataHandler);
	}

}


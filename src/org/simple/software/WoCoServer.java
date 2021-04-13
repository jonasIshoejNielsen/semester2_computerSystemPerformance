package org.simple.software;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class WoCoServer {

	public static final char SEPARATOR = '$';

	public static void main(String[] args) throws IOException {

		if (args.length!=5) {
			HelperFunctions.print(WoCoServer.class, "Usage: <listenaddress> <listenport> <numberOfClients> <cleaning> <threadcount>");
			System.exit(0);
		}
		String lAddr 			= args[0];
		int lPort 				= Integer.parseInt(args[1].replaceAll("[^\\d.]", ""));
		int numberOfClients 	= Integer.valueOf(args[2].replaceAll("[^\\d.]", ""));
		boolean cMode 			= Boolean.parseBoolean(args[3]);
		int threadCount 		= Integer.valueOf(args[4].replaceAll("[^\\d.]", ""));
		System.out.println(cMode? "Clean tags": "Don't clean tags");
		System.out.println(threadCount + " number of threads");

		Logging.createFolder(new StringBuilder("server_-clients-").append(numberOfClients).append("-threads-").append(args[3]).append("-clean-").append(args[2]).toString());


		if (threadCount>1) {
			//TODO: will have to implement multithreading
			HelperFunctions.print(WoCoServer.class, "FEATURE NOT IMPLEMENTED");
			System.exit(0);

		}

		Server server = new Server(lAddr, lPort);
		List<DataHandler> dataHandlerList = server.setUpDataHandlers(cMode, threadCount);
		server.startListening();
	}
}


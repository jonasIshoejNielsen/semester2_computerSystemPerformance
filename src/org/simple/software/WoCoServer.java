package org.simple.software;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class WoCoServer {

	public static final char SEPARATOR = '$';

	public static void main(String[] args) throws IOException {
		if (args.length<4) {
			HelperFunctions.print(WoCoServer.class, "Usage: <listenaddress> <listenport> <cleaning> <threadcount> [<numberOfClients>]");
			System.exit(0);
		}
		String lAddr 			= args[0];
		int lPort 				= Integer.parseInt(args[1].replaceAll("[^\\d.]", ""));
		boolean cMode 			= Boolean.parseBoolean(args[2]);
		int threadCount 		= Integer.valueOf(args[3].replaceAll("[^\\d.]", ""));
		int numberOfClients 	= (args.length>=5)? Integer.valueOf(args[4].replaceAll("[^\\d.]", "")) : 1;
		System.out.println(cMode? "Clean tags": "Don't clean tags");
		System.out.println(threadCount + " number of threads");

		Logging.createFolder(new StringBuilder("server_-clients-").append(numberOfClients).append("-threads-").append(args[3]).append("-clean-").append(args[2]).toString());

		List<DataHandler> dataHandlerList = setUpDataHandlers(threadCount, true, i -> new DataHandlerPrimary(cMode, i));
		Server server = new Server(lAddr, lPort, threadCount==0, dataHandlerList);

		server.startListening();
	}


	public static List<DataHandler> setUpDataHandlers(int threadCount, boolean sendToCLient, Function<Integer, DataHandler> dataHandlerConstructor) {
		List<DataHandler> dataHandlerList = new ArrayList<>();
		for (int i = 1; i <= threadCount; i++) {
			dataHandlerList.add(dataHandlerConstructor.apply(i));
		}
		if(threadCount == 0) {
			dataHandlerList.add(dataHandlerConstructor.apply(0));
			return dataHandlerList;
		}
		final ExecutorService exec = Executors.newFixedThreadPool(threadCount);
		if (threadCount>1) {
			for (DataHandler dh: dataHandlerList ) {
				exec.submit(() ->dh.startPipeLine(true, sendToCLient));
			}
		}
		return dataHandlerList;
	}
}


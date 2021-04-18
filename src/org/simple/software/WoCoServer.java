package org.simple.software;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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

		List<DataHandler> dataHandlerList = setUpDataHandlers(threadCount, true, i -> new DataHandlerPrimary(cMode, i));
		Server server = new Server(lAddr, lPort, threadCount==1, dataHandlerList);

		server.startListening();
	}


	public static List<DataHandler> setUpDataHandlers(int threadCount, boolean sendToCLient, Function<Integer, DataHandler> dataHandlerConstructor) {
		List<DataHandler> dataHandlerList = new ArrayList<>();
		for (int i = 1; i <= threadCount; i++) {
			dataHandlerList.add(dataHandlerConstructor.apply(i));
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


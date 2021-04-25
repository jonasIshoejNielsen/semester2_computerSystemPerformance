package org.simple.software;

import org.simple.software.meaurements.Logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class WoCoServer {

	public static final char SEPARATOR = '$';
	private static final List<DataHandler> dataHandlerList = new ArrayList<>();
	private static final AtomicInteger messagesLeftCounter = new AtomicInteger(0);
	private static final Integer[] numberOfClients 			= new Integer[] {1,2,4,8,16};
	private static final AtomicInteger indexNumberOfClients = new AtomicInteger(0);
	private static boolean cMode;
	private static Integer threadCount;
	private static int dSize;
	private static int file;
	private static Server server;

	public static void main(String[] args) throws IOException {
		if (args.length<4) {
			HelperFunctions.print(WoCoServer.class, "Usage: <listenaddress> <listenport> <cleaning> <threadcount> [<numberOfClients>] [<documentsize(KiB)>] [<filesuffix>]");
			System.exit(0);
		}
		String lAddr 			= args[0];
		int lPort 				= Integer.parseInt(args[1].replaceAll("[^\\d.]", ""));
		cMode 					= Boolean.parseBoolean(args[2]);
		threadCount 			= Integer.valueOf(args[3].replaceAll("[^\\d.]", ""));
		boolean fixedNumberOfClients = (args.length>=5)? Boolean.parseBoolean(args[2]) : false;
		dSize 					= (args.length>=6)? Integer.valueOf(args[5].replaceAll("[^\\d.]", "")) : 1;
		dSize *= 1024;
		file 					= (args.length>=7)? Integer.valueOf(args[6].replaceAll("[^\\d.]", "")) : 1;
		System.out.println(cMode? "Clean tags": "Don't clean tags");
		System.out.println(threadCount + " number of threads");

		setUpLogging();

		setUpDataHandlers(threadCount, true, i -> new DataHandlerPrimary(cMode, fixedNumberOfClients, i));
		server = new Server(lAddr, lPort, threadCount==0, dataHandlerList);

		server.startListening();
	}

	public static void setUpDataHandlers(int threadCount, boolean sendToCLient, Function<Integer, DataHandler> dataHandlerConstructor) {
		for (int i = 1; i <= threadCount; i++) {
			dataHandlerList.add(dataHandlerConstructor.apply(i));
		}
		if(threadCount == 0) {
			dataHandlerList.add(dataHandlerConstructor.apply(0));
			return;
		}
		final ExecutorService exec = Executors.newFixedThreadPool(threadCount);
		for (DataHandler dh: dataHandlerList ) {
			exec.submit(() ->dh.startPipeLine(true, sendToCLient));
		}
		return;
	}

	public static void setUpLogging() throws IOException {
		int index = indexNumberOfClients.getAndIncrement();
		if (index >= numberOfClients.length) {
			System.out.println("Last client message handled");
			System.exit(0);
		}
		int currNumberOfClients = numberOfClients[index];
		messagesLeftCounter.set(currNumberOfClients*WoCoClient.PACKETS_PER_REPEAT*WoCoClient.DEFAULT_NUMBER_OF_REPEATS);
		System.out.println("currNumberOfClients="+currNumberOfClients);
		System.out.println("messagesLeftCounter="+messagesLeftCounter.get());
		Logging.createFolder("server", cMode, threadCount, currNumberOfClients, file, dSize);
	}


	public static void reportFinishedMessage() {
		int messagesLeft = messagesLeftCounter.decrementAndGet();
		if (messagesLeft < 0) {
			System.out.println("Error wrong ammount of messages handled");
			System.exit(0);
			return;
		}
		if (messagesLeft != 0) {
			return;
		}
		server.logMessages();
		for (DataHandler dh: dataHandlerList) {
			dh.restartMessages();
		}
		try {
			setUpLogging();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}


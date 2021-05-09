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
	private static final List<Worker> workerList = new ArrayList<>();
	private static final AtomicInteger messagesLeftCounter = new AtomicInteger(0);
	private static boolean cMode;
	private static Integer threadCount;
	private static int dSize;
	private static int file;
	private static Server server;
	private static int repeatCount;
	private static int numberOfClients;
	private static ExecutorService exec;

	public static void main(String[] args) throws IOException {
		if (args.length<4) {
			HelperFunctions.print(WoCoServer.class, "Usage: <listenaddress> <listenport> <cleaning> <threadcount> [<numberOfClients>] [<documentsize(KiB)>] [<filesuffix>]  [<repeatCount>]");
			System.exit(0);
		}
		String lAddr 			= args[0];
		int lPort 				= Integer.parseInt(args[1].replaceAll("[^\\d.]", ""));
		cMode 					= Boolean.parseBoolean(args[2]);
		threadCount 			= Integer.valueOf(args[3].replaceAll("[^\\d.]", ""));
		numberOfClients 		= (args.length>=5)? Integer.valueOf(args[4].replaceAll("[^\\d.]", "")) : 0;
		dSize 					= (args.length>=6)? Integer.valueOf(args[5].replaceAll("[^\\d.]", "")) : 1;
		dSize *= 1024;
		file 					= (args.length>=7)? Integer.valueOf(args[6].replaceAll("[^\\d.]", "")) : 1;
		repeatCount 			= (args.length>=8) ? Integer.valueOf(args[7].replaceAll("[^\\d.]", "")) : 0;
		int ops 				= (args.length>=9) ? Integer.parseInt(args[8])*WoCoClient.PACKETS_PER_REPEAT : 0;
		StringBuilder sb = new StringBuilder()
				.append(cMode? "Clean tags, ": "Don't clean tags, ")
				.append(threadCount + " number of threads, ")
				.append(numberOfClients+" number of clients, ")
				.append(dSize +" dSize, ")
				.append(file +" file, ")
				.append(repeatCount +" repeat, ")
				.append(lAddr +" lAddr, ")
				.append(lPort +" lPort, ");
		System.out.println(sb.toString());

		setUpLogging(ops);
		System.out.println(messagesLeftCounter.get());

		exec = setUpWorkers(threadCount, true, i -> new Worker(cMode, numberOfClients>0, i));
		server = new Server(lAddr, lPort, threadCount==0, numberOfClients, repeatCount, workerList);

		server.startListening();
	}

	public static ExecutorService setUpWorkers(int threadCount, boolean sendToCLient, Function<Integer, Worker> workerConstructor) {
		for (int i = 1; i <= threadCount; i++) {
			workerList.add(workerConstructor.apply(i));
		}
		if(threadCount == 0) {
			workerList.add(workerConstructor.apply(0));
			return null;
		}
		final ExecutorService exec = Executors.newFixedThreadPool(threadCount);
		for (Worker dh: workerList ) {
			exec.submit(() ->dh.startPipeLine(true, sendToCLient));
		}
		return exec;
	}

	public static void setUpLogging(int ops) throws IOException {
		messagesLeftCounter.set(numberOfClients*ops);
		Logging.createFolder("server", cMode, threadCount, file, dSize);
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
		try {
			server.logMessages();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(exec != null)
			exec.shutdown();
		System.out.println("Exiting");
		System.exit(0);
	}
}


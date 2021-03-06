package org.simple.software;

import org.simple.software.meaurements.Logging;
import org.simple.software.meaurements.Measurements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class WoCoClient {
	private static int everyTenthOps;
	private Socket sHandle;
	private BufferedReader sInput;
	private BufferedWriter sOutput;
	private static boolean DEBUG = false;
	private static int numberOfClients;
	private static int clientID;
	private static int repeatCount;
	private Measurements measurements;
	public static final int PACKETS_PER_REPEAT 	= 100;
	public static final int NUMBER_OF_REPEATS	= 3;

	
	/**
	 * Instantiates the client.
	 * @param serverAddress IP address or hostname of the WoCoServer.
	 * @param serverPort Port number of the server.
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public WoCoClient(String serverAddress, int serverPort) throws UnknownHostException, IOException {
        this.sHandle 	= new Socket(serverAddress, serverPort);
        this.sInput 	= new BufferedReader(new InputStreamReader(sHandle.getInputStream()));
        this.sOutput 	= new BufferedWriter(new OutputStreamWriter(sHandle.getOutputStream()));
        this.measurements = new Measurements();
	}

	/**
	 * Sends a document to the server and waits for a response. The response is an
	 * ASCII serialized version of the <word, count> map.
	 * @param doc 
	 * @return
	 * @throws IOException
	 */
	private String sendToServer(String doc) throws IOException {
		long beginResponseTime 			= System.nanoTime();

		sOutput.write(doc);
		sOutput.write(WoCoServer.SEPARATOR);
		sOutput.flush();

		String response = null;
		response = sInput.readLine();
		measurements.addMeasurement(beginResponseTime, System.nanoTime());
		return response;
	}
	
	/**
	 * Sends a document to the server and returns the map of <word,count> pairs.
	 * If DEBUG is set to false, it returns null!
	 * @param doc
	 * @return Empty hashmap if DEBUG is false, a proper one otherwise.
	 * @throws IOException
	 */
	public HashMap<String,Integer> getWordCount(String doc) throws IOException {
		String response = sendToServer(doc);

		// Parsing this text into a data structure takes time, we only do it 
		// if we are in debug mode. Otherwise we'll assume that everything went
		// alright and we'd have a correct answer.
		if (DEBUG==true) {			
			HashMap<String, Integer> wordMap = new HashMap<String,Integer>();			
			String[] rParts = response.split(",");
			for (int i=0; i<rParts.length; i+=2) {
				wordMap.put(rParts[i], Integer.valueOf(rParts[i+1]));
			}
			return wordMap;
		} else {
			return new HashMap<String,Integer>();
		}				
	}

	/**
	 * Closes the connection to the server gracefully.
	 */
	public void shutDown() {
		try {
			this.sHandle.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void sendDocu(int ops, String docu) throws IOException {
		//send requests to the server in a loop.
		for (int i=0; i<ops; i++) {
			HashMap<String, Integer> result = this.getWordCount(docu);
			if(clientID==1 && i%everyTenthOps==0)
				System.out.println(i+"/"+ops);
		}
	}
	

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		//reading in parameters

		if (args.length<5) {
			System.out.println("Usage: <servername> <serverport> <documentsize(KiB)> <opcount(x100)> <filesuffix> [<seed>] [<clientID>] [<numberOfClients>] [<cleaning>] [<threadcount>] [<repeatCount>]");
			System.exit(0);
		}
		String sName 		= args[0];
		int sPort 			= Integer.parseInt(args[1]);
		float dSize 		= Float.parseFloat(args[2])*1024;
		int ops 			= Integer.parseInt(args[3])*PACKETS_PER_REPEAT;
		int file 			= Integer.parseInt(args[4]);
		int seed 			= (args.length>=6)  ? Integer.parseInt(args[5]) : (int) (Math.random()*10000);
		seed				= (seed != -1)      ? seed : (int) (Math.random()*10000);
		clientID 			= (args.length>=7)  ? Integer.parseInt(args[6]) 		: 1;
		numberOfClients 	= (args.length>=8)  ? Integer.parseInt(args[7]) 		: 1;
		boolean cMode 		= (args.length>=9)  ? Boolean.parseBoolean(args[8]) 	: true;
		int threadCount 	= (args.length>=10) ? Integer.valueOf(args[9].replaceAll("[^\\d.]", "")) : 0;
		repeatCount			= (args.length>=11) ? Integer.valueOf(args[10].replaceAll("[^\\d.]", "")) : 1;
		Logging.createFolder("client", cMode, threadCount, file, dSize);
		Logging.resetClients(clientID, numberOfClients, repeatCount);
		if(clientID==1) {
			StringBuilder sb = new StringBuilder()
					.append(cMode ? "Clean tags" : "Don't clean tags, ")
					.append(threadCount + " number of threads, ")
					.append(numberOfClients + " number of clients, ")
					.append(dSize + " dSize, ")
					.append(file + " file, ")
					.append(repeatCount + " repeat, ")
					.append(sName + " sName, ")
					.append(sPort + " sPort, ");
			System.out.println(sb.toString());
			System.out.println(ops);
		}
		everyTenthOps=ops/10;

		String docu = HelperFunctions.generateDocument((int) (dSize), file, seed);
		WoCoClient client = new WoCoClient(sName, sPort);
    	client.sendDocu(ops, docu);

		Thread.sleep(2000);
		client.shutDown();

		Logging.writeResponseThoughput(client.measurements, repeatCount);
		if(repeatCount == WoCoClient.NUMBER_OF_REPEATS) {
			Logging.processLogs();
		}
        System.exit(0);
	}

}

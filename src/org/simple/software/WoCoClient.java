package org.simple.software;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class WoCoClient {
	
	private Socket sHandle;
	private BufferedReader sInput;
	private BufferedWriter sOutput;
	private ArrayList<Long> respTime;
	private static boolean DEBUG = false;
	private static int numberOfClients;
	private static int clientID;

	
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
        this.respTime = new ArrayList<>();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				for (Long resp: respTime) {
					Logging.writeResponseTime(resp, clientID);
				}
			}
		});
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

		long endResponseTime 			= System.nanoTime();
		respTime.add(endResponseTime - beginResponseTime);
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
		System.out.println("ops="+ops);
		for (int rep=0; rep<ops; rep++) {
			HashMap<String, Integer> result = this.getWordCount(docu);

			if (DEBUG==true) {
				System.out.println("result="+result);
			}
			if(rep%25 == 0) {
				System.out.println("client:"+ clientID +", rep="+rep+"/"+ops);
			}
		}
	}
	

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		//reading in parameters
		if (args.length<7) {
			System.out.println("Usage: <servername> <serverport> <documentsize(KiB)> <opcount(x1000)> <filesuffix> <clientID> <numberOfClients> [<seed>]");
			System.exit(0);
		}
		
		String sName = args[0];
		int sPort 			= Integer.parseInt(args[1]);
		float dSize 		= Float.parseFloat(args[2])*1024;
		int ops 			= Integer.parseInt(args[3])*1000;
		int file 			= Integer.parseInt(args[4]);
		clientID 			= Integer.parseInt(args[5]);
		numberOfClients 	= Integer.parseInt(args[6]);
		int seed = (args.length==8) ? Integer.parseInt(args[7]) : (int) (Math.random()*10000);
		Logging.createFolder("clients-"+args[6]);
		//We generate one document for the entire runtime of this client
		//Otherwise the client would spend too much time generating new inputs.
    	String docu = DocumentGenerator.generateDocument((int) (dSize), file, seed);
		WoCoClient client = new WoCoClient(sName, sPort);
    	client.sendDocu(ops, docu);

		Thread.sleep(2000);
		client.shutDown();

        System.exit(0);
	}

}

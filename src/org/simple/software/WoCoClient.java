package org.simple.software;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class WoCoClient {
	
	private Socket sHandle;
	private BufferedReader sInput;
	private BufferedWriter sOutput;
	private static boolean DEBUG = false;
	private int clientIndex;

	
	/**
	 * Function to generate a document based on the hardcoded example file. 
	 * @param length Length of the document in bytes.
	 * @param seed This random seed is used to start reading from different offsets
	 * in the file every time a new document is generated. Could be useful for debugging
	 * to return to a problematic seed.
	 * @return Returns the document which is encoded as a String 
	 * @throws IOException
	 */
	private static String generateDocument(int length, int file, int seed) throws IOException {
		
        String fileName = "input"+file+".html";
        String line = null;
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        while((line = br.readLine()) != null) {
            sb.append(line.trim()+" ");
        }   

        br.close();
                
        String ref = sb.toString();
		
		sb = new StringBuilder(length);
		int i;
		
		for (i=0; i<length; i++) {
			sb.append(ref.charAt((i+seed)%ref.length()));								
		}
		
		//we need to remove all occurences of this special character! 
		return sb.substring(0).replace(WoCoServer.SEPARATOR, '.');
		
	}
	
	/**
	 * Instantiates the client.
	 * @param serverAddress IP address or hostname of the WoCoServer.
	 * @param serverPort Port number of the server.
	 * @param index
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public WoCoClient(String serverAddress, int serverPort, int index) throws UnknownHostException, IOException {
        this.sHandle 	= new Socket(serverAddress, serverPort);
        this.sInput 	= new BufferedReader(new InputStreamReader(sHandle.getInputStream()));
        this.sOutput 	= new BufferedWriter(new OutputStreamWriter(sHandle.getOutputStream()));
        this.clientIndex = index;
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
		Logging.writeResponseTime(endResponseTime - beginResponseTime);
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
				System.out.println("client:"+ clientIndex +", rep="+rep+"/"+ops);
			}
		}
	}
	

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		//reading in parameters
		if (args.length<5) {
			System.out.println("Usage: <servername> <serverport> <documentsize(KiB)> <opcount(x1000)> <filesuffix> [<seed>]");
			System.exit(0);
		}
		
		String sName = args[0];
		int sPort = Integer.parseInt(args[1]);
		float dSize = Float.parseFloat(args[2])*1024;
		int ops = Integer.parseInt(args[3])*1000;
		int file = Integer.parseInt(args[4]);
		int seed = (args.length==6) ? Integer.parseInt(args[5]) : (int) (Math.random()*10000);
		
		//We generate one document for the entire runtime of this client
		//Otherwise the client would spend too much time generating new inputs.
    	String docu = WoCoClient.generateDocument((int) (dSize), file, seed);
		WoCoClient client = new WoCoClient(sName, sPort, 0);
    	client.sendDocu(ops, docu);

		Thread.sleep(2000);
		client.shutDown();

        System.exit(0);
	}

}

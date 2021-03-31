package org.simple.software;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataHandler {
    private static final HashMap<Integer, StringBuilder> buffer              = new HashMap<>();
    private static final HashMap<Integer, HashMap<String, Integer>> results  = new HashMap<>();
    private static final ArrayList<LineStorage> linesToCount                 = new ArrayList<>();
    private final boolean cMode;

    public static void addLineToCount(String line, int clientId) {
        linesToCount.add(new LineStorage(line, clientId));
    }

    public DataHandler(boolean cMode) {
        this.cMode = cMode;
    }

    public void countLine () {
        while (!linesToCount.isEmpty()) {
            LineStorage ls = linesToCount.remove(0);
            HashMap<String, Integer> wc = results.getOrDefault(ls.clientId, new HashMap<>());
            ls.doWordCount(wc, cMode);
        }
    }

    public boolean readFromChanel(ByteBuffer bb, SocketChannel client) throws IOException {
        int readCnt = client.read(bb);
        if (readCnt<=0) {
            return false;
        }
        int clientId = client.hashCode();
        String result = new String(bb.array(),0, readCnt);

        boolean hasResult = receiveData(clientId, result);

        if (hasResult) {
            var returnMessage = serializeResultForClient(clientId).getBytes();
            ByteBuffer ba = ByteBuffer.wrap(returnMessage);
            client.write(ba);
        }
        return true;
    }

    /**
     * This function handles data received from a specific client (TCP connection).
     * Internally it will check if the buffer associated with the client has a full
     * document in it (based on the SEPARATOR). If yes, it will process the document and
     * return true, otherwise it will add the data to the buffer and return false
     * @param clientId
     * @param dataChunk
     * @return A document has been processed or not.
     */
    public boolean receiveData(int clientId, String dataChunk) {
        results.putIfAbsent(clientId, new HashMap<>());
        buffer.putIfAbsent( clientId, new StringBuilder());

        StringBuilder sb = buffer.get(clientId);
        sb.append(dataChunk);

        if (dataChunk.indexOf(WoCoServer.SEPARATOR)==-1) {
            return false;
        }

        String bufData = sb.toString();
        int indexNL = bufData.indexOf(WoCoServer.SEPARATOR);

        String line = bufData.substring(0, indexNL);
        String rest = (bufData.length()>indexNL+1) ? bufData.substring(indexNL+1) : null;

        if (indexNL==0) {
            HelperFunctions.print(DataHandler.class, "SEP@", indexNL+"", " bufdata:\n", bufData);
        }

        if (rest != null) {
            HelperFunctions.print(DataHandler.class, "more than one line: \n", rest);
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            buffer.put(clientId, new StringBuilder(rest));
        } else {
            buffer.put(clientId, new StringBuilder());
        }

        //word count in line
        addLineToCount(line, clientId);
        countLine();
        return true;

    }

    /**
     * Returns a serialized version of the word count associated with the last
     * processed document for a given client. If not called before processing a new
     * document, the result is overwritten by the new one.
     * @param clientId
     * @return
     */
    public String serializeResultForClient(int clientId) {
        if (results.containsKey(clientId)) {
            StringBuilder sb = new StringBuilder();
            HashMap<String, Integer> hm = results.get(clientId);
            for (var entry : hm.entrySet()) {
                sb.append(entry.getKey()).append(",");
                sb.append(entry.getValue()).append(",");
            }
            results.remove(clientId);
            sb.append("\n");
            return sb.substring(0);
        } else {
            return "";
        }
    }

}
class LineStorage {
    public final String line;
    public final int clientId;

    public LineStorage(String line, int clientId) {
        this.line = line;
        this.clientId = clientId;
    }

    /**
     * Performs the word count on a document. It first converts the document to
     * lower case characters and then extracts words by considering "a-z" english characters
     * only (e.g., "alpha-beta" become "alphabeta"). The code breaks the text up into
     * words based on spaces.
     *
     * @param wc   A HashMap to store the results in.
     * @param cMode
     */
    public void doWordCount(Map<String, Integer> wc, boolean cMode) {
        String ucLine = line.toLowerCase();
        System.out.println(ucLine);
        String cleanedLine = (cMode) ? removeTags(ucLine) : ucLine;
        String[] words = getWordsFromString(cleanedLine);
        System.out.println(Arrays.toString(words));
        addWordsToMap(words, wc);
    }
    private static String removeTags(String line){
        StringBuilder sb = new StringBuilder();
        for (String v : line.split(">")) {
            int indexToDiscardFrom = v.indexOf("<");
            sb.append(v, 0, indexToDiscardFrom);
        }
        return sb.toString();
    }
    private static String[] getWordsFromString(String line) {
        StringBuilder asciiLine = new StringBuilder();
        char lastAdded = ' ';
        for (char cc : line.toCharArray()) {
            if ((cc >= 'a' && cc <= 'z') || (cc == ' ' && lastAdded != ' ')) {
                asciiLine.append(cc);
                lastAdded = cc;
            }
        }

        return asciiLine.toString().split(" ");
    }
    private static void addWordsToMap(String[] words, Map<String, Integer> wc) {
        for (String s : words) {
            wc.put(s, wc.getOrDefault(s, 0) + 1);
        }
    }
}
package org.simple.software;
import org.simple.software.meaurements.Measurements;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class LineStorage {
    public final String line;
    private final int clientId;
    private final SocketChannel client;
    private final long timeFromEnteringServer;
    private final HashMap<String, Integer> results = new HashMap<>();
    public byte[] returnMessage;
    public long endCleaning;
    public long endWordCount;
    public long beginCleaning;
    public long beginWordCount;

    public LineStorage(String line, int clientId, SocketChannel client, long timeFromEnteringServer) {
        this.line     = line;
        this.clientId = clientId;
        this.client   = client;
        this.timeFromEnteringServer = timeFromEnteringServer;
    }

    public int getClientId() {
        return clientId;
    }

    /**
     * Performs the word count on a document. It first converts the document to
     * lower case characters and then extracts words by considering "a-z" english characters
     * only (e.g., "alpha-beta" become "alphabeta"). The code breaks the text up into
     * words based on spaces.
     *
     * @param cMode
     * @param measurementsCleaning
     * @param measurementsWordCount
     */
    public void doWordCount(boolean cMode) {
        String ucLine = line.toLowerCase();
        beginCleaning = System.nanoTime();
        ucLine = ucLine.replace('_', ' ');
        String cleanedLine = (cMode) ? removeTags(ucLine) : ucLine;
        endCleaning = System.nanoTime();

        beginWordCount = System.nanoTime();
        String[] words = getWordsFromString(cleanedLine);
        addWordsToMap(words, results);
        long endWordCount = System.nanoTime();
    }
    private static String removeTags(String line){
        StringBuilder sb = new StringBuilder();
        for (String v : line.split(">")) {
            try {
                removeTagsFromLine(sb, v);
            } catch (Exception e) {
                System.out.println(v);
                throw e;
            }
        }
        return sb.toString();
    }
    private static void removeTagsFromLine(StringBuilder sb, String line) {
        String[] split = line.split("<");
        if(split.length<1) {
            return;
        }
        sb.append(split[0]);
        if (split.length < 2) {
            return;
        }
        String remainingString = split[1];
        if (!remainingString.startsWith("a")) {
            return;
        }
        int index = remainingString.indexOf("title");
        while (index != -1) {
            if (index + 6 >= remainingString.length()) {
                break;
            }
            String fromTitle = remainingString.substring(index + 6);
            int indexEndTitle = fromTitle.indexOf("&amp");
            String titleValue;
            if (indexEndTitle == -1) {
                if (fromTitle.length() == 0) {
                    break;
                }
                indexEndTitle = fromTitle.indexOf(fromTitle.charAt(0), 1);
                indexEndTitle = (indexEndTitle != -1) ? indexEndTitle : fromTitle.length();
                titleValue = fromTitle.substring(1, indexEndTitle);
            } else {
                titleValue = fromTitle.substring(0, indexEndTitle);
            }
            sb.append(" ").append(titleValue).append(" ");
            remainingString = fromTitle.substring(indexEndTitle);
            index = remainingString.indexOf("title");
        }
    }

    private static String[] getWordsFromString(String line) {
        StringBuilder asciiLine = new StringBuilder();
        char lastAdded = ' ';
        for (char cc : line.toCharArray()) {
            if ((cc >= 'a' && cc <= 'z') || (cc >= 'A' && cc <= 'Z') || (cc == ' ' && lastAdded != ' ')) {
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

    public void sendToClient(byte[] returnMessage, boolean sendToClient) {
        if (sendToClient) {
            ByteBuffer ba = ByteBuffer.wrap(returnMessage);
            try {
                getClient().write(ba);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.returnMessage = returnMessage;
        }
    }

    public Map<String, Integer> getResults() {
        return results;
    }
    public SocketChannel getClient() {
        return client;
    }

    public long getTimeFromEnteringServer() {
        return timeFromEnteringServer;
    }

    public void putResultValue(String key, Integer value) {
        results.put(key, value);
    }

}

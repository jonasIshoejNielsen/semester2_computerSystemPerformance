package org.simple.software;

import java.io.IOException;
import java.util.HashMap;

public class DataHandler {
    private HashMap<Integer, StringBuilder> buffer              = new HashMap<>();
    private HashMap<Integer, HashMap<String, Integer>> results  = new HashMap<>();

    public DataHandler() {
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

        StringBuilder sb;

        if (!results.containsKey(clientId)) {
            results.put(clientId, new HashMap<String, Integer>());
        }

        if (!buffer.containsKey(clientId)) {
            sb = new StringBuilder();
            buffer.put(clientId, sb);
        } else {
            sb = buffer.get(clientId);
        }

        sb.append(dataChunk);

        if (dataChunk.indexOf(WoCoServer.SEPARATOR)>-1) {
            //we have at least one line

            String bufData = sb.toString();

            int indexNL = bufData.indexOf(WoCoServer.SEPARATOR);

            String line = bufData.substring(0, indexNL);
            String rest = (bufData.length()>indexNL+1) ? bufData.substring(indexNL+1) : null;

            if (indexNL==0) {
                System.out.println("SEP@"+indexNL+" bufdata:\n"+bufData);
            }

            if (rest != null) {
                System.out.println("more than one line: \n"+rest);
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
            HashMap<String, Integer> wc = results.get(clientId);
            var ls = new LineStorage(line, clientId);
            ls.doWordCount(wc);


            return true;

        } else {
            return false;
        }

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
            for (String key : hm.keySet()) {
                sb.append(key+",");
                sb.append(hm.get(key)+",");
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
    public String line;
    public int clientId;

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
     */
    public void doWordCount(HashMap<String, Integer> wc) {
        String ucLine = line.toLowerCase();
        StringBuilder asciiLine = new StringBuilder();

        char lastAdded = ' ';
        for (int i = 0; i < line.length(); i++) {
            char cc = ucLine.charAt(i);
            if ((cc >= 'a' && cc <= 'z') || (cc == ' ' && lastAdded != ' ')) {
                asciiLine.append(cc);
                lastAdded = cc;
            }
        }

        String[] words = asciiLine.toString().split(" ");
        for (String s : words) {


            if (wc.containsKey(s)) {
                wc.put(s, wc.get(s) + 1);
            } else {
                wc.put(s, 1);
            }
        }
    }
}
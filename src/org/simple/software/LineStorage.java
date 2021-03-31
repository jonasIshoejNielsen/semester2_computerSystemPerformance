package org.simple.software;

import java.util.Arrays;
import java.util.Map;

public class LineStorage {
    public final String line;
    public final int clientId;

    public LineStorage(String line, int clientId) {
        this.line = line;
        this.clientId = clientId;
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
     * @param wc   A HashMap to store the results in.
     * @param cMode
     */
    public void doWordCount(Map<String, Integer> wc, boolean cMode) {
        String ucLine = line;       //todo: .toLowerCase();
        System.out.println(ucLine);
        String cleanedLine = (cMode) ? removeTags(ucLine) : ucLine;
        String[] words = getWordsFromString(cleanedLine);
        System.out.println(Arrays.toString(words));
        addWordsToMap(words, wc);
    }
    private static String removeTags(String line){
        StringBuilder sb = new StringBuilder();
        for (String v : line.split(">")) {
            String[] split = v.split("<");
            sb.append(split[0]);
            if (split.length < 2) {
                continue;
            }

            int index = split[1].toLowerCase().indexOf("title");
            if(index == -1) {
                continue;
            }
            String fromTitle    = split[1].substring(index+6);
            int indexEndTitle   = fromTitle.indexOf(fromTitle.charAt(0), 1);
            String titleValue   = fromTitle.substring(1, indexEndTitle);
            sb.append(" ").append(titleValue).append(" ");
        }
        return sb.toString();
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
}

package org.simple.software;

import java.util.Map;

class LineStorage {
    private final String line;
    private final int clientId;

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
     */
    public void doWordCount(Map<String, Integer> wc) {
        String ucLine = line.toLowerCase();
        StringBuilder asciiLine = new StringBuilder();
        char lastAdded = ' ';
        for (char cc : ucLine.toCharArray()) {
            if ((cc >= 'a' && cc <= 'z') || (cc == ' ' && lastAdded != ' ')) {
                asciiLine.append(cc);
                lastAdded = cc;
            }
        }

        String[] words = asciiLine.toString().split(" ");
        for (String s : words) {
            wc.put(s, wc.getOrDefault(s, 0) + 1);
        }
    }
}

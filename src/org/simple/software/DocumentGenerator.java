package org.simple.software;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DocumentGenerator {
    /**
     * Function to generate a document based on the hardcoded example file.
     * @param length Length of the document in bytes.
     * @param seed This random seed is used to start reading from different offsets
     * in the file every time a new document is generated. Could be useful for debugging
     * to return to a problematic seed.
     * @return Returns the document which is encoded as a String
     * @throws IOException
     */
    public static String generateDocument(int length, int file, int seed) throws IOException {

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
}

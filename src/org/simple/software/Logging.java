package org.simple.software;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logging {
    private static FileWriter writerReceiving;
    private static FileWriter writerCleaningTags;
    private static FileWriter writerWordCount;
    private static FileWriter writerSerializing;

    public static void test() {
        File file = new File("Logs/");
        if (!file.exists()){
            while (!file.mkdir()){}
        }
        writerReceiving     = createFileWriter("Receiving");
        writerCleaningTags  = createFileWriter("CleaningTags");
        writerWordCount     = createFileWriter("WordCount");
        writerSerializing   = createFileWriter("Serializing");
    }


    private static FileWriter createFileWriter(String name) {
        String path = new StringBuilder("Logs/").append(name).append(".txt").toString();
        FileWriter writer = null;
        try {
            writer = new FileWriter(path, false);
            writer.write("");
            writer.close();
            writer = new FileWriter(path, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }

}

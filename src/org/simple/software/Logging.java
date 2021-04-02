package org.simple.software;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logging {
    private static FileWriter writerReceiving;
    private static FileWriter writerCleaningTags;
    private static FileWriter writerWordCount;
    private static FileWriter writerSerializing;

    public static void createFolder() {
        File file = new File("Logs/");
        if (!file.exists()){
            while (!file.mkdir()){}
        }
    }
    public static void writeReceiving (long msg) {
        writerReceiving     = (writerReceiving==null)?      createFileWriter("Receiving") : writerReceiving;
        writeToFile(writerReceiving, msg);
    }
    public static void writeCleaningTags (long msg) {
        writerCleaningTags  = (writerCleaningTags==null)?   createFileWriter("CleaningTags") : writerCleaningTags;
        writeToFile(writerCleaningTags, msg);
    }
    public static void writeWordCount (long msg) {
        writerWordCount     = (writerWordCount==null)?      createFileWriter("WordCount") : writerWordCount;
        writeToFile(writerWordCount, msg);
    }
    public static void writeSerializing (long msg) {
        writerSerializing   = (writerSerializing==null)?    createFileWriter("Serializing") : writerSerializing;
        writeToFile(writerSerializing, msg);
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
    private static void writeToFile(FileWriter fw, long msg) {
        try {
            fw.write(String.valueOf(msg));
            fw.write("\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

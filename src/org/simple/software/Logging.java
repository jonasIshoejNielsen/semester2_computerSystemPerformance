package org.simple.software;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logging {
    private static FileWriter writerReceiving;
    private static FileWriter writerCleaningTags;
    private static FileWriter writerWordCount;
    private static FileWriter writerSerializing;
    private static FileWriter writerResponseTime;

    public static void createFolder() {
        File file = new File("Logs/");
        if (!file.exists()){
            while (!file.mkdir()){}
        }
    }
    public static void writeReceiving (long msg) {
        if(!Config.writeReceiving) return;
        writerReceiving     = (writerReceiving==null)?      createFileWriter("Receiving") : writerReceiving;
        writeToFile(writerReceiving, msg);
    }
    public static void writeCleaningTags (long msg) {
        if(!Config.writeCleaningTags) return;
        writerCleaningTags  = (writerCleaningTags==null)?   createFileWriter("CleaningTags") : writerCleaningTags;
        writeToFile(writerCleaningTags, msg);
    }
    public static void writeWordCount (long msg) {
        if(!Config.writeWordCount) return;
        writerResponseTime  = (writerResponseTime==null)?   createFileWriter("ResponseTime") : writerResponseTime;
        writeToFile(writerResponseTime, msg);
    }
    public static void writeResponseTime (long msg) {
        if(!Config.writeResponseTime) return;
        writerWordCount     = (writerWordCount==null)?      createFileWriter("WordCount") : writerWordCount;
        writeToFile(writerWordCount, msg);
    }
    public static void writeSerializing (long msg) {
        if(!Config.writeSerializing) return;
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

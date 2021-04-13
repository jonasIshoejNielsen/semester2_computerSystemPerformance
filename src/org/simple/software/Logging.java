package org.simple.software;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logging {
    private static FileWriter writerCleaningTags;
    private static FileWriter writerWordCount;
    private static FileWriter writerSerializing;
    private static FileWriter writerResponseTime;
    private static FileWriter writerTimeInServer;
    private static String FileName = "Logs";

    public static void createFolder(String prefix) {
        FileName = new StringBuilder(FileName).append(prefix).append("/").toString();
        File file = new File(FileName);
        if (!file.exists()){
            while (!file.mkdir()){}
        }
    }
    public static void writeCleaningTags (long msg) {
        if(!Config.writeCleaningTags) return;
        writerCleaningTags  = (writerCleaningTags==null)?   createFileWriter("CleaningTags") : writerCleaningTags;
        writeToFile(writerCleaningTags, msg);
    }
    public static void writeWordCount (long msg) {
        if(!Config.writeWordCount) return;
        writerResponseTime  = (writerResponseTime==null)?   createFileWriter("WordCount") : writerResponseTime;
        writeToFile(writerResponseTime, msg);
    }
    public static void writeResponseTime(long msg, int clientIndex) {
        if(!Config.writeResponseTime) return;
        writerWordCount     = (writerWordCount==null)?      createFileWriter("ResponseTime"+clientIndex) : writerWordCount;
        writeToFile(writerWordCount, msg);
    }
    public static void writeSerializing (long msg) {
        if(!Config.writeSerializing) return;
        writerSerializing   = (writerSerializing==null)?    createFileWriter("Serializing") : writerSerializing;
        writeToFile(writerSerializing, msg);
    }
    public static void writeTimeInServer (long msg) {
        if(!Config.writeTimeInServer) return;
        writerTimeInServer  = (writerTimeInServer==null)?    createFileWriter("TimeInServer") : writerTimeInServer;
        writeToFile(writerTimeInServer, msg);
    }


    private static FileWriter createFileWriter(String name) {
        String path = new StringBuilder(FileName).append(name).append(".txt").toString();
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

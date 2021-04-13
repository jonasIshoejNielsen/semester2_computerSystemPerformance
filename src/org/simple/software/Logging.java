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
    private static String FileName = "Logs/Logs";

    public static void createFolder(String prefix) {
        FileName = new StringBuilder(FileName).append(prefix).append("/").toString();
        File file = new File(FileName);
        if (!file.exists()){
            while (!file.mkdirs()){
                System.out.println(FileName);
            }
        }
    }
    public static void writeCleaningTags (long msg, int clientId) {
        if(!Config.writeCleaningTags) return;
        writerCleaningTags  = (writerCleaningTags==null)?   createFileWriter("CleaningTags", clientId) : writerCleaningTags;
        writeToFile(writerCleaningTags, msg);
    }
    public static void writeWordCount (long msg, int clientId) {
        if(!Config.writeWordCount) return;
        writerResponseTime  = (writerResponseTime==null)?   createFileWriter("WordCount", clientId) : writerResponseTime;
        writeToFile(writerResponseTime, msg);
    }
    public static void writeSerializing (long msg, int clientId) {
        if(!Config.writeSerializing) return;
        writerSerializing   = (writerSerializing==null)?    createFileWriter("Serializing", clientId) : writerSerializing;
        writeToFile(writerSerializing, msg);
    }
    public static void writeTimeInServer (long msg, int clientId) {
        if(!Config.writeTimeInServer) return;
        writerTimeInServer  = (writerTimeInServer==null)?    createFileWriter("TimeInServer", clientId) : writerTimeInServer;
        writeToFile(writerTimeInServer, msg);
    }

    public static void writeResponseTime(long msg, int clientId) {
        if(!Config.writeResponseTime) return;
        writerWordCount     = (writerWordCount==null)?      createFileWriter("ResponseTime", clientId) : writerWordCount;
        writeToFile(writerWordCount, msg);
    }

    private static FileWriter createFileWriter(String name, int clientId) {
        String path = new StringBuilder(FileName).append(name).append("-").append(clientId).append(".txt").toString();
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

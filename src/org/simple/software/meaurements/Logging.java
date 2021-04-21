package org.simple.software.meaurements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logging {
    public static String FileName;
    private static WriterHolder writerHolder_CleaningTags;
    private static WriterHolder writerHolder_WordCount;
    private static WriterHolder writerHolder_Serializing;
    private static WriterHolder writerHolder_InServer;
    private static WriterHolder writerHolder_Response;

    public static void createFolder(String prefix) {
        FileName = new StringBuilder("Logs/Logs").append(prefix).append("/").toString();
        File file = new File(FileName);
        if (!file.exists()){
            while (!file.mkdirs()){
                System.out.println(FileName);
            }
        }
    }
    public static void writeCleaningTagsThoughput(Measurements measurements, int clientId) {
        if(!Config.writeCleaningTags) return;
        writerHolder_CleaningTags   = (writerHolder_CleaningTags ==null)?   new WriterHolder("CleaningTags", clientId)      : writerHolder_CleaningTags;
        writeMeasurements(measurements, clientId, writerHolder_CleaningTags);
    }
    public static void writeWordCountThoughput(Measurements measurements, int clientId) {
        if(!Config.writeWordCount) return;
        writerHolder_WordCount      = (writerHolder_WordCount ==null)?      new WriterHolder("WordCountTags", clientId)     : writerHolder_WordCount;
        writeMeasurements(measurements, clientId, writerHolder_WordCount);
    }
    public static void writeSerializingThoughput (Measurements measurements, int clientId) {
        if(!Config.writeSerializing) return;
        writerHolder_Serializing    = (writerHolder_Serializing ==null)?    new WriterHolder("Serializing", clientId)       : writerHolder_Serializing;
        writeMeasurements(measurements, clientId, writerHolder_Serializing);
    }
    public static void writeTimeInServerThoughput (Measurements measurements, int clientId) {
        if(!Config.writeTimeInServer) return;
        writerHolder_InServer       = (writerHolder_InServer ==null)?       new WriterHolder("InServer", clientId)          : writerHolder_InServer;
        writeMeasurements(measurements, clientId, writerHolder_InServer);
    }
    public static void writeResponseThoughput(Measurements measurements, int clientId) {
        if(!Config.writeResponseTime) return;
        writerHolder_Response       = (writerHolder_Response ==null)?       new WriterHolder("Response", clientId)          : writerHolder_Response;
        writeMeasurements(measurements, clientId, writerHolder_Response);
    }

    private static void writeMeasurements(Measurements measurements, int clientId, WriterHolder writerHolder) {
        for (long time_ns : measurements.timeMeasurements) {
            writeToFile(writerHolder.writerTime, (double) time_ns / 1000000000.0);
        }
        for (TputHolder tput : measurements.tputs) {
            writeToFile(writerHolder.writerTput, tput.value);
            writeToFile(writerHolder.writerInterval, tput.intervalTime);
        }

    }

    private static void writeToFile(FileWriter fw, double seconds) {
        try {
            fw.write(String.valueOf(seconds));
            fw.write("\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
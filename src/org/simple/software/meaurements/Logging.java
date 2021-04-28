package org.simple.software.meaurements;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Logging {
    public static String folderName;
    private static WriterHolder writerHolder_CleaningTags;
    private static WriterHolder writerHolder_WordCount;
    private static WriterHolder writerHolder_Serializing;
    private static WriterHolder writerHolder_InServer;
    private static WriterHolder writerHolder_InQueue;
    private static WriterHolder writerHolder_Response;

    public static void createFolder(String type, Boolean clean, int threads, int numberOfClients, int file, float dSize) throws IOException {

        String prefix = new StringBuilder(type)
                        .append("-clean-").append(clean)
                        .append("-threads-").append(threads)
                        .append("-clients-").append(numberOfClients)
                        .append("-file-").append(file)
                        .append("-dSize-").append((long)dSize).toString();
        Path dir = Paths.get("Logs/");
        try {
            Files.createDirectory(dir);
        } catch (FileAlreadyExistsException e ) { }

        folderName = new StringBuilder("Logs/Logs").append(prefix).append("/").toString();
        dir = Paths.get(folderName);
        try {
            Files.createDirectory(dir);
        } catch (FileAlreadyExistsException e ) { }
    }

    public static void writeTimeInQueue(Measurements measurements, int repeatCount) {
        if(!Config.writeTimeInQueue) return;
        writeMeasurements(measurements, writerHolder_InQueue);
    }
    public static void writeCleaningTags(Measurements measurements, int clientId) {
        if(!Config.writeCleaningTags) return;
        writeMeasurements(measurements, writerHolder_CleaningTags);
    }
    public static void writeWordCount(Measurements measurements, int clientId) {
        if(!Config.writeWordCount) return;
        writeMeasurements(measurements, writerHolder_WordCount);
    }
    public static void writeSerializing(Measurements measurements, int clientId) {
        if(!Config.writeSerializing) return;
        writeMeasurements(measurements, writerHolder_Serializing);
    }
    public static void writeTimeInServer(Measurements measurements, int clientId) {
        if(!Config.writeTimeInServer) return;
        writeMeasurements(measurements, writerHolder_InServer);
    }
    public static void writeResponseThoughput(Measurements measurements, int repeatCount) {
        if(!Config.writeResponseTime) return;
        writeMeasurements(measurements, writerHolder_Response);
    }

    private static void writeMeasurements(Measurements measurements, WriterHolder writerHolder) {
        for (long time_ns : measurements.timeMeasurements) {
            writeToFile(writerHolder.writerTime, (double) time_ns / 1000000000.0);
        }
        for (float tput : measurements.tputs) {
            writeToFile(writerHolder.writerTput, tput);
        }
        for (float interval : measurements.tputs_interval) {
            writeToFile(writerHolder.writerInterval, interval);
        }
        for (long valueForP : measurements.computePercentilesTime()) {
            writeToFile(writerHolder.writerTimePercentile, (double) valueForP / 1000000000.0);
        }
        for (float valueForP : measurements.computePercentilesTput()) {
            writeToFile(writerHolder.writerTputPercentile, (double) valueForP / 1000000000.0);
        }

    }

    private static void writeToFile(FileWriter fw, double seconds) {
        try {
            fw.write(String.valueOf(seconds).replace('.', ','));
            fw.write("\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setupServer(int repeatCount) {
        writerHolder_InQueue      = new WriterHolder("InQueue","", repeatCount);
        writerHolder_CleaningTags = new WriterHolder("CleaningTags", "", repeatCount);
        writerHolder_Serializing  = new WriterHolder("Serializing", "", repeatCount);
        writerHolder_WordCount    = new WriterHolder("WordCountTags", "", repeatCount);
        writerHolder_InServer     = new WriterHolder("InServer", "", repeatCount);
    }

    public static void resetClients(int clientID, int repeatCount) {
        writerHolder_Response     = new WriterHolder("InQueue", "-"+clientID, repeatCount);
    }

    public static void processLogsServer() {

    }
}

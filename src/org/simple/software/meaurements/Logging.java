package org.simple.software.meaurements;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Logging {
    public static String folderName;
    private static Map<Integer, WriterHolder> writerHolder_CleaningTags = new HashMap<>();
    private static Map<Integer, WriterHolder> writerHolder_WordCount    = new HashMap<>();
    private static Map<Integer, WriterHolder> writerHolder_Serializing  = new HashMap<>();
    private static Map<Integer, WriterHolder> writerHolder_InServer     = new HashMap<>();
    private static Map<Integer, WriterHolder> writerHolder_InQueue      = new HashMap<>();
    private static Map<Integer, WriterHolder> writerHolder_Response     = new HashMap<>();

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
    public static void writeCleaningTags(Measurements measurements, int clientId, int repeatCount) {
        if(!Config.writeCleaningTags) return;
        WriterHolder wh = writerHolder_CleaningTags.computeIfAbsent(clientId, id -> new WriterHolder("CleaningTags", clientId, repeatCount));
        writeMeasurements(measurements, clientId, wh);
    }
    public static void writeWordCount(Measurements measurements, int clientId, int repeatCount) {
        if(!Config.writeWordCount) return;
        WriterHolder wh = writerHolder_WordCount.computeIfAbsent(clientId, id -> new WriterHolder("WordCountTags", clientId, repeatCount));
        writeMeasurements(measurements, clientId, wh);
    }
    public static void writeSerializing(Measurements measurements, int clientId, int repeatCount) {
        if(!Config.writeSerializing) return;
        WriterHolder wh = writerHolder_Serializing.computeIfAbsent(clientId, id -> new WriterHolder("Serializing", clientId, repeatCount));
        writeMeasurements(measurements, clientId, wh);
    }
    public static void writeTimeInServer(Measurements measurements, int clientId, int repeatCount) {
        if(!Config.writeTimeInServer) return;
        WriterHolder wh = writerHolder_InServer.computeIfAbsent(clientId, id -> new WriterHolder("InServer", clientId, repeatCount));
        writeMeasurements(measurements, clientId, wh);
    }
    public static void writeTimeInQueue(Measurements measurements, int clientId, int repeatCount) {
        if(!Config.writeTimeInQueue) return;
        WriterHolder wh = writerHolder_InQueue.computeIfAbsent(clientId, id -> new WriterHolder("InQueue", clientId, repeatCount));
        writeMeasurements(measurements, clientId, wh);
    }
    public static void writeResponseThoughput(Measurements measurements, int clientId, int repeatCount) {
        if(!Config.writeResponseTime) return;
        WriterHolder wh = writerHolder_Response.computeIfAbsent(clientId, id -> new WriterHolder("Response", clientId, repeatCount));
        writeMeasurements(measurements, clientId, wh);
    }

    private static void writeMeasurements(Measurements measurements, int clientId, WriterHolder writerHolder) {
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

    public static void reset() {
        writerHolder_CleaningTags = new HashMap<>();
        writerHolder_Serializing  = new HashMap<>();
        writerHolder_WordCount    = new HashMap<>();
        writerHolder_InServer     = new HashMap<>();
        writerHolder_Response     = new HashMap<>();
    }
}

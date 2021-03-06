package org.simple.software.meaurements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Logging {
    public static String folderName;
    private static WriterHolder writerHolder_CleaningTags;
    private static WriterHolder writerHolder_WordCount;
    private static WriterHolder writerHolder_Serializing;
    private static WriterHolder writerHolder_InServer;
    private static WriterHolder writerHolder_InQueue;
    private static WriterHolder writerHolder_Response;

    public static void createFolder(String type, Boolean clean, int threads, int file, float dSize) throws IOException {

        String prefix = new StringBuilder(type)
                        .append("-clean-").append(clean)
                        .append("-threads-").append(threads)
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

    public static void setupServer(int numberOfClients, int repeatCount) {
        String optional = "-"+numberOfClients;
        writerHolder_InQueue      = new WriterHolder("InQueue",optional, repeatCount);
        writerHolder_CleaningTags = new WriterHolder("CleaningTags", optional, repeatCount);
        writerHolder_Serializing  = new WriterHolder("Serializing", optional, repeatCount);
        writerHolder_WordCount    = new WriterHolder("WordCountTags", optional, repeatCount);
        writerHolder_InServer     = new WriterHolder("InServer", optional, repeatCount);
    }

    public static void resetClients(int clientID, int numberOfClients, int repeatCount) {
        writerHolder_Response     = new WriterHolder("Response", "-"+numberOfClients+"-"+clientID, repeatCount);
    }

    public static void processLogs() throws IOException {
        Map<String, List<List<Float>>>fileMaps = readFiles();

        for(Map.Entry<String, List<List<Float>>> entry : fileMaps.entrySet()) {
            if(entry.getKey().contains("ALL"))
                continue;
            int n = entry.getValue().size();
            int minSize = 9000000;
            for(List<Float>lst : entry.getValue()) {
                minSize = Math.min(minSize, lst.size());
            }
            Float[] mean = initArray(minSize);
            Float[] sd   = initArray(minSize);
            for(int i=0; i<minSize; i++) {
                for(List<Float>lst : entry.getValue()) {
                    mean[i] += lst.get(i);
                }
                mean[i] /= entry.getValue().size();
                for(List<Float>lst : entry.getValue()) {
                    float dif = mean[i]-lst.get(i);
                    sd[i]+= dif*dif;
                }
                sd[i]= (float) Math.sqrt(sd[i]/n);
            }
            storeMeanSD(mean, sd, entry);
        }
    }
    private static Map<String, List<List<Float>>> readFiles() throws IOException {
        File folder = new File(folderName);
        Map<String, List<List<Float>>> fileMaps = new HashMap<>();

        for (final File fileEntry : folder.listFiles()) {
            try {
                String fileName = fileEntry.getName().split("-repeat_")[0];
                Path path = Paths.get(fileEntry.getPath());
                List<Float> fileContent = Files.lines(path).map(v -> Float.parseFloat(v.replace(',', '.'))).collect(Collectors.toList());
                List<List<Float>> currList = fileMaps.getOrDefault(fileName, new ArrayList<>());
                currList.add(fileContent);
                fileMaps.put(fileName, currList);
                fileEntry.delete();
            } catch (Exception e) {
                continue;
            }
        }
        return fileMaps;
    }
    private static void storeMeanSD(Float[] mean, Float[] sd, Map.Entry<String, List<List<Float>>> entry) throws IOException {
        String path = new StringBuilder(folderName).append(entry.getKey()).append("ALL").append(".txt").toString();
        FileWriter fw = null;
        try {
            fw = new FileWriter(path, false);
            fw.write("");
            fw.close();
            fw = new FileWriter(path, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0; i<mean.length; i++) {
            fw.write(String.valueOf(mean[i]).replace('.', ','));
            fw.write("\t");
            fw.write(String.valueOf(sd[i]).replace('.', ','));
            fw.write("\n");
            fw.flush();
        }

        if(entry.getKey().contains("Interval"))
            return;
        List<Float> percentile = computePercentilesLongs(mean);
        String path2 = new StringBuilder(folderName).append(entry.getKey()).append("-Percentiles-").append("ALL").append(".txt").toString();
        fw = null;
        try {
            fw = new FileWriter(path2, false);
            fw.write("");
            fw.close();
            fw = new FileWriter(path2, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0; i<percentile.size(); i++) {
            fw.write(String.valueOf(percentile.get(i)).replace('.', ','));
            fw.write("\n");
            fw.flush();
        }
    }
    private static Float[] initArray(int size) {
        Float[] arr = new Float[size];
        for(int i=0; i<size; i++) {
            arr[i]=0F;
        }
        return arr;
    }
    private static List<Float> computePercentilesLongs(Float[] values) {
        Arrays.sort(values);
        List<Float> percentiles = new ArrayList<>();
        for (int p=1; p<=100; p++) {
            int index = values.length * p / 100 - 1;
            if(index<0) {
                percentiles.add(0.0F);
                continue;
            }
            Float valueForP = values[index];
            percentiles.add(valueForP);
        }
        return percentiles;
    }
}

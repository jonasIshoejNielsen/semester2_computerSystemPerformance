package org.simple.software;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public static void writeCleaningTagsThoughput(List<Long> times, int clientId) {
        if(!Config.writeCleaningTags) return;
        writerCleaningTags  = (writerCleaningTags==null)?   createFileWriter("CleaningTags", clientId) : writerCleaningTags;
        writeTputs(createTputs(times), clientId, writerCleaningTags);
    }
    public static void writeWordCountThoughput(List<Long> times, int clientId) {
        if(!Config.writeWordCount) return;
        writerResponseTime  = (writerResponseTime==null)?   createFileWriter("WordCount", clientId) : writerResponseTime;
        writeTputs(createTputs(times), clientId, writerResponseTime);
    }
    public static void writeSerializingThoughput (List<Long> times, int clientId) {
        if(!Config.writeSerializing) return;
        writerSerializing   = (writerSerializing==null)?    createFileWriter("Serializing", clientId) : writerSerializing;
        writeTputs(createTputs(times), clientId, writerSerializing);
    }
    public static void writeTimeInServerThoughput (List<Long> times, int clientId) {
        if(!Config.writeTimeInServer) return;
        writerTimeInServer  = (writerTimeInServer==null)?    createFileWriter("TimeInServer", clientId) : writerTimeInServer;
        writeTputs(createTputs(times), clientId, writerTimeInServer);
    }

    public static void writeResponseThoughput(List<Long> times, int clientId) {
        if(!Config.writeResponseTime) return;
        writerWordCount     = (writerWordCount==null)?      createFileWriter("ResponseTime", clientId) : writerWordCount;
        writeTputs(createTputs(times), clientId, writerWordCount);
    }

    private static int[] createTputs(List<Long> times) {
        long maxEllapsedTime_ns = times.stream().reduce(0L, (a, b) -> a + b);
        int maxEllapsedTime_s =(int) TimeUnit.SECONDS.convert(maxEllapsedTime_ns, TimeUnit.NANOSECONDS);
        int[] tputs = new int[maxEllapsedTime_s+1];
        long ellapsedTime = 0L;
        for (long t: times) {
            ellapsedTime += t;
            int t_sec = (int) TimeUnit.SECONDS.convert(ellapsedTime, TimeUnit.NANOSECONDS);
            tputs[t_sec] ++;
        }
        return tputs;
    }
    private static void writeTputs (int[] tputs, int clientID, FileWriter fileWriter) {
        for(int tput: tputs) {
            writeToFile(writerWordCount, tput);
        }
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

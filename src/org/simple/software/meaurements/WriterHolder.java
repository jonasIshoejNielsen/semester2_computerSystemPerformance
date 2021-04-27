package org.simple.software.meaurements;

import java.io.FileWriter;
import java.io.IOException;

public class WriterHolder {
    public static FileWriter writerTime;
    public static FileWriter writerTput;
    public static FileWriter writerInterval;

    public WriterHolder(String wirterType, int clientId, int repeatCount) {
        writerTime     = createFileWriter(wirterType+"_Time", clientId, repeatCount);
        writerTput     = createFileWriter(wirterType+"_Tput", clientId, repeatCount);
        writerInterval = createFileWriter(wirterType+"_Interval", clientId, repeatCount);
    }


    private static FileWriter createFileWriter(String name, int clientId, int repeatCount) {
        String path = new StringBuilder(Logging.folderName).append(name).append("-id_").append(clientId).append("-repeat_").append(repeatCount).append(".txt").toString();
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

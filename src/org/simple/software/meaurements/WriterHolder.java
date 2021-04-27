package org.simple.software.meaurements;

import java.io.FileWriter;
import java.io.IOException;

public class WriterHolder {
    public static FileWriter writerTime;
    public static FileWriter writerTput;
    public static FileWriter writerInterval;

    public WriterHolder(String wirterType, int clientId, int repretitionCount) {
        writerTime     = createFileWriter(wirterType+"_Time", clientId, repretitionCount);
        writerTput     = createFileWriter(wirterType+"_Tput", clientId, repretitionCount);
        writerInterval = createFileWriter(wirterType+"_Interval", clientId, repretitionCount);
    }


    private static FileWriter createFileWriter(String name, int clientId, int repretitionCount) {
        String path = new StringBuilder(Logging.folderName).append(name).append("-id_").append(clientId).append("-repeat_").append(repretitionCount).append(".txt").toString();
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

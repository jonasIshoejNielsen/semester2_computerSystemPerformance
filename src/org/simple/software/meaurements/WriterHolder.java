package org.simple.software.meaurements;

import java.io.FileWriter;
import java.io.IOException;

public class WriterHolder {
    public final FileWriter writerTime;
    public final FileWriter writerTput;
    public final FileWriter writerInterval;
    public final FileWriter writerTimePercentile;

    public WriterHolder(String wirterType, String optionalName, int repeatCount) {
        writerTime              = createFileWriter(wirterType+"_Time",             optionalName, repeatCount);
        writerTimePercentile    = createFileWriter(wirterType+"_percentiles_Time", optionalName, repeatCount);
        writerTput              = createFileWriter(wirterType+"_Tput",             optionalName, repeatCount);
        writerInterval          = createFileWriter(wirterType+"_Interval",         optionalName, repeatCount);
    }


    private static FileWriter createFileWriter(String name, String optionalName, int repeatCount) {
        String path = new StringBuilder(Logging.folderName).append(name).append(optionalName).append("-repeat_").append(repeatCount).append(".txt").toString();
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

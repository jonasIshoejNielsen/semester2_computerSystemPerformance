package org.simple.software;

import java.util.ArrayList;
import java.util.List;

public interface DataHandler {
    ArrayList<List<Long>> getTimesCleaning();
    ArrayList<List<Long>> getTimesWordCount();
    List<Long> getTimesSerializing();
    List<Long> getTimesInServer();
    int getDataHandlerId();
    void startPipeLine (boolean repeat);
}
package org.simple.software;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public interface DataHandler {
    ArrayList<List<Long>> getTimesCleaning();
    ArrayList<List<Long>> getTimesWordCount();
    List<Long> getTimesSerializing();
    List<Long> getTimesInServer();
    int getDataHandlerId();
    boolean readFromChanel(ByteBuffer bb, SocketChannel client) throws IOException;
}
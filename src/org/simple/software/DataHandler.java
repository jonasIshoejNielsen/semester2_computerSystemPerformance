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
    int getClientId();
    void countLine ();
    boolean readFromChanel(ByteBuffer bb, SocketChannel client) throws IOException;

    /**
    * This function handles data received from a specific client (TCP connection).
    * Internally it will check if the buffer associated with the client has a full
    * document in it (based on the SEPARATOR). If yes, it will process the document and
    * return true, otherwise it will add the data to the buffer and return false
    * @param clientId
    * @param dataChunk
    * @return A document has been processed or not.
    */
    boolean receiveData(int clientId, String dataChunk);

    /**
    * Returns a serialized version of the word count associated with the last
    * processed document for a given client. If not called before processing a new
    * document, the result is overwritten by the new one.
    * @param clientId
    * @return
    */
    String serializeResultForClient(int clientId);
}
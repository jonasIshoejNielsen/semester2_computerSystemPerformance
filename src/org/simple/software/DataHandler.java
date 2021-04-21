package org.simple.software;

import org.simple.software.meaurements.Measurements;
import java.util.List;

public interface DataHandler {
    List<Measurements> getMeasurementsCleaning();
    List<Measurements> getMeasurementsWordCount();
    Measurements getMeasurementsSerializing();
    Measurements getMeasurementsInServer();
    int getDataHandlerId();
    void startPipeLine (boolean repeat, boolean sendToClient);
}
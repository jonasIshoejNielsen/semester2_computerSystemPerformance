package org.simple.software;

import org.simple.software.meaurements.Measurements;
import java.util.List;

public interface Worker {
    List<Measurements> getMeasurementsCleaning();
    List<Measurements> getMeasurementsWordCount();
    Measurements getMeasurementsSerializing();
    Measurements getMeasurementsInServer();
    int getWorkerId();
    void startPipeLine (boolean repeat, boolean sendToClient);
    void restartMessages();
}
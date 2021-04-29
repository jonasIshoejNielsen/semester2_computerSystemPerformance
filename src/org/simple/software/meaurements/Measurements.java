package org.simple.software.meaurements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Measurements {
    private long timeLastPrint = System.nanoTime();
    private int cntSincePrint = 0;

    public List<Float> tputs            = new ArrayList<>();
    public List<Float> tputs_interval   = new ArrayList<>();
    public List<Long> timeMeasurements = new ArrayList<>();


    public synchronized void addMeasurement(long beginTimeCurrOps, long endTimeCurrOps) {
        timeMeasurements.add(endTimeCurrOps - beginTimeCurrOps);
        cntSincePrint++;
        float elapsedSeconds = (float) ((endTimeCurrOps-timeLastPrint)/1000000000.0);
        if (elapsedSeconds<1) {
            return;
        }
        float tput = cntSincePrint/elapsedSeconds;
        tputs.add(tput);
        tputs_interval.add(elapsedSeconds);
        timeLastPrint = endTimeCurrOps;
        cntSincePrint = 0;
    }

}

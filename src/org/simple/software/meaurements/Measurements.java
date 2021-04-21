package org.simple.software.meaurements;

import java.util.ArrayList;
import java.util.List;

public class Measurements {
    private long timeLastPrint = System.nanoTime();
    private int cntSincePrint = 0;

    public List<TputHolder> tputs = new ArrayList<>();
    public List<Long> timeMeasurements = new ArrayList<>();


    public void addMeasurement(long beginTimeCurrOps, long endTimeCurrOps) {
        timeMeasurements.add(endTimeCurrOps - beginTimeCurrOps);
        cntSincePrint++;
        float elapsedSeconds = (float) ((endTimeCurrOps-timeLastPrint)/1000000000.0);
        if (elapsedSeconds<1) {
            return;
        }
        float tput = cntSincePrint/elapsedSeconds;
        tputs.add(new TputHolder(elapsedSeconds, tput));
        timeLastPrint = endTimeCurrOps;
        cntSincePrint = 0;
    }
}

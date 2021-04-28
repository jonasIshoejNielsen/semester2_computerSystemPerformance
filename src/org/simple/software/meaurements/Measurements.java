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

    public synchronized List<Long> computePercentilesTime() {
        return computePercentilesLongs(timeMeasurements);
    }
    public synchronized List<Float> computePercentilesTput() {
        return computePercentilesFloats(tputs);
    }
    private synchronized List<Long> computePercentilesLongs(List<Long> values) {
        List<Long> sorted = values.stream().sorted().collect(Collectors.toList());
        List<Long> percentiles = new ArrayList<>();
        for (int p=1; p<=100; p++) {
            Long valueForP = sorted.get(sorted.size() * p / 100 - 1);
            percentiles.add(valueForP);
        }
        return percentiles;
    }
    private synchronized List<Float> computePercentilesFloats(List<Float> values) {
        List<Float> sorted = values.stream().sorted().collect(Collectors.toList());
        List<Float> percentiles = new ArrayList<>();
        for (int p=1; p<=100; p++) {
            Float valueForP = sorted.get(sorted.size() * p / 100 - 1);
            percentiles.add(valueForP);
        }
        return percentiles;
    }
}

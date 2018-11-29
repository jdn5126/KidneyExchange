package KidneyExchange;

import java.util.ArrayList;
import java.util.List;

public class TestResults {
    private int numPairsOperated;
    private int numPairsTotal;
    private long runDurationNs;
    private List<Long> roundDurationsNs;
    private double runDurationSec;
    private List<Double> roundDurationsSec;
    private double operationPercentage;

    public TestResults( int numPairsOperated, int numPairsTotal, long runDurationNs, List<Long> roundDurationsNs ) {
        this.numPairsOperated = numPairsOperated;
        this.numPairsTotal = numPairsTotal;
        this.runDurationNs = runDurationNs;
        this.roundDurationsNs = roundDurationsNs;
        this.runDurationSec = runDurationNs / 1e9;
        this.roundDurationsSec = new ArrayList<>();
        for( long roundDurationNs : roundDurationsNs ) {
            this.roundDurationsSec.add( roundDurationNs / 1e9 );
        }
        this.operationPercentage = (double)numPairsOperated / numPairsTotal * 100.0;
    }

    public int getNumPairsOperated() {
        return numPairsOperated;
    }

    public int getNumPairsTotal() {
        return numPairsTotal;
    }

    public long getRunDurationNs() {
        return runDurationNs;
    }

    public List<Long> getRoundDurationsNs() {
        return roundDurationsNs;
    }

    public double getRunDurationSec() {
        return runDurationSec;
    }

    public List<Double> getRoundDurationsSec() {
        return roundDurationsSec;
    }

    public double getOperationPercentage() {
        return operationPercentage;
    }
}

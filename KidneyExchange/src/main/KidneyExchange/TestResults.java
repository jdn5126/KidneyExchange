package KidneyExchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestResults {
    private int numPairsOperated;
    private int numPairsTotal;
    private long runDuration;
    private List<List<Long>> roundDurations;
    private Stopwatch runWatch;
    private Stopwatch roundWatch;

    public TestResults( int numHospitals, int numRounds, int initialNumPairs ) {
        roundDurations = new ArrayList<>( numHospitals );
        for( int iHospital = 0; iHospital < numHospitals; ++iHospital ) {
            roundDurations.add( new ArrayList<>( numRounds ) );
        }

        numPairsTotal = initialNumPairs;
        runWatch = new Stopwatch();
        roundWatch = new Stopwatch();
    }

    public int getNumPairsOperated() {
        return numPairsOperated;
    }

    public int getNumPairsTotal() {
        return numPairsTotal;
    }

    public long getRunDuration() {
        return runDuration;
    }

    public List<List<Long>> getRoundDurations() {
        return roundDurations;
    }

    public double getOperationRate() {
        return (double)numPairsOperated / numPairsTotal;
    }

    public void incrementNumPairsOperated() {
        ++numPairsOperated;
    }

    public void incrementNumPairsTotal() {
        ++numPairsTotal;
    }

    public void startRun() {
        runWatch.start();
    }

    public void stopRun() {
        runDuration = runWatch.stop();
    }

    public void startRound() {
        roundWatch.start();
    }

    public void stopRound( int hospitalId ) {
        roundDurations.get( hospitalId - 1 ).add( roundWatch.stop() );
    }
}

package KidneyExchange;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

public class Tester {
    private String outputFileName;
    private boolean disablePrinting;

    public Tester( String outputFileName, boolean disablePrinting ) {
        this.outputFileName = outputFileName;
        this.disablePrinting = disablePrinting;
    }

    public void printResults( TestResults results ) {
        if( disablePrinting )
            return;

        System.out.println( "Operated on " + results.getNumPairsOperated() +
                " out of " + results.getNumPairsTotal() +
                " pairs (" + results.getOperationRate() * 100.0 + "% operation rate)" );

        long runDuration = results.getRunDuration();
        System.out.println( "Total run duration in nanoseconds (seconds): " + runDuration +
                " (" + (runDuration / 1e9) + ")"  );
        System.out.println( "Durations per round in nanoseconds (seconds)" );

        List<List<Long>> roundDurations = results.getRoundDurations();
        for( int iHospital = 0; iHospital < roundDurations.size(); ++iHospital ) {
            System.out.println( "    Hospital " + (iHospital + 1) );
            for( int iRound = 0; iRound < roundDurations.get( iHospital ).size(); ++iRound ) {
                long roundDuration = roundDurations.get( iHospital ).get( iRound );
                double roundDurationSec = roundDuration / 1e9;
                System.out.println( "        Round "  + (iRound + 1) + ": " + roundDuration + " (" + roundDurationSec + ")" );
            }
        }
    }

    public void writeResults( TestResults results ) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter( TestResults.class, new TestResultsSerializer() )
                .create();
        try( Writer outputWriter = new FileWriter( outputFileName ) ) {
            outputWriter.write( gson.toJson( results ) );
        }
        catch( Exception ex ) {
            System.out.println( "Error writing test results." );
            ex.printStackTrace( System.out );
        }
    }
}

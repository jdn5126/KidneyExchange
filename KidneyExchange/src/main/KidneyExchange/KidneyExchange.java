package KidneyExchange;

import com.beust.jcommander.JCommander;

import java.util.ArrayList;
import java.util.HashMap;

public class KidneyExchange {
    private static final int DEFAULT_NUM_HOSPITALS = 3;
    private static final int DEFAULT_NUM_PAIRS = 5;
    private static final int DEFAULT_MAX_SURGERIES = 5;
    private static final int DEFAULT_NUM_ROUNDS = 4;

    // Main function controlling behavior of kidney exchange
    public static void main(String[] args) {
        CommandLine cli = new CommandLine();
        JCommander.newBuilder()
                .addObject( cli )
                .build()
                .parse( args );

        if( cli.seed != null )
            KidneyExchangeHelper.setRandomSeed( cli.seed );

        // Create Hospitals participating in Kidney Exchange
        int numHospitals = (cli.numHospitals == null) ? DEFAULT_NUM_HOSPITALS : cli.numHospitals;
        int numPairs = (cli.numPairs == null) ? DEFAULT_NUM_PAIRS : cli.numPairs;
        int maxSurgeries = (cli.maxSurgeries == null) ? DEFAULT_MAX_SURGERIES : cli.maxSurgeries;

        Hospital[] hospitals = new Hospital[numHospitals];
        for(int i=0; i < numHospitals; i++) {
            // Create each hospital with some number of participants and the ability to perform some
            // number of simultaneous surgeries.
            hospitals[i] = KidneyExchangeHelper.createHospital(numPairs, maxSurgeries);
        }

        // Print hospitals participating in KidneyExchange
        System.out.println("Hospitals participating in kidney exchange: ");
        for(int i=0; i < numHospitals; i++) {
            System.out.print(hospitals[i]);
        }

        // Run the kidney exchange for some fixed number of rounds
        int numRounds = (cli.numRounds == null) ? DEFAULT_NUM_ROUNDS : cli.numRounds;
        runKidneyExchange(numRounds, hospitals, true);
    }

    public static void runKidneyExchange(int numRounds, Hospital[] hospitals, boolean incremental) {
        // Each round of the kidney exchange works as follows:
        //     1. Each hospital creates a directed graph amongst ExchangePairs that it
        //        is aware of, where an edge from pair i to pair j indicates that pair
        //        i can be matched with the kidney being donated by pair j.
        //     2. Each hospital determines the cycles that it has the bandwidth to perform
        //        surgery on in this time step.
        //     3. For each cycle:
        //            3a. If the cycle contains only local patients, the surgeries
        //                are performed and the patients are removed from the hospital.
        //            3b. If the cycle contains remote patients, hospital will acquire
        //                remote patients if it outranks all of the hospitals in which
        //                the remote patients in this cycle reside.
        //     4. Each hospital informs higher ranked hospital of its unmatched pairs.

        for(int i=1; i < (numRounds + 1); i++) {
            System.out.println("\nRound: " + i + "\n");
            for(Hospital hospital : hospitals) {
                // INCREMENTAL SETTING #1: Add/remove random pair from hospital 2/5 of the time
                if(incremental) {
                    switch (KidneyExchangeHelper.rand.nextInt(5)) {
                        case 1: // add pair
                            KidneyExchangeHelper.addRandomExchangePair(hospital);
                            break;
                        case 2: // remove random pair that hospital is aware of
                            if (hospital.getSize() > 0) {
                                ExchangePair randPair = KidneyExchangeHelper.delRandomExchangePair(hospital);
                                for (Hospital h : hospitals) {
                                    h.removePair(randPair);
                                }
                            }
                            break;
                    }
                }
                // Print Hospital
                System.out.print(hospital);
                // Create directed graph from ExchangePairs
                DirectedGraph graph = KidneyExchangeHelper.createDirectedGraph(hospital);

                // Adjacency List should be printed when print level is verbose
                boolean printAdjList = false;
                if(printAdjList) {
                    System.out.println("Adjacency List for Hospital " + hospital.getHospitalId() + ":");
                    System.out.print(graph);
                }

                // Get list of cycles
                ArrayList<HashMap<ExchangePair, ExchangePair>> matches = KidneyExchangeHelper.greedyMatches(hospital,
                                                                                                            graph);

                // INCREMENTAL SETTING #2: Simulate matched pair dropping out 1/8 of the time
                if(matches != null && matches.size() > 0 && incremental &&
                        KidneyExchangeHelper.rand.nextInt(8) == 7) {
                    ExchangePair dropout = KidneyExchangeHelper.delRandomMatchedPair(matches);
                    System.out.println("\n" + dropout.toString() + " has dropped out of matching.");
                    // Remove pair from all hospitals that are aware of pair
                    for(Hospital h: hospitals) {
                        h.removePair(dropout);
                    }
                }

                System.out.println("\nMatches: ");
                // For each cycle in matching, perform surgeries if all patients are in this hospital,
                // otherwise acquire pairs if you are the highest ranking hospital in the cycle.
                if(matches != null) {
                    for(HashMap<ExchangePair, ExchangePair> cycle : matches) {
                        // Determine if this is a local cycle
                        boolean localCycle = true;
                        // If not a local cycle, determine if this hospital is highest ranking in the cycle.
                        boolean highestRanked = true;
                        for (ExchangePair pair : cycle.keySet()) {
                            if (pair.getCurrentHospital() != hospital.getHospitalId()) {
                                localCycle = false;
                                if (pair.getCurrentHospital() < hospital.getHospitalId()) {
                                    highestRanked = false;
                                }
                            }
                        }
                        if(localCycle) {
                            // Perform surgeries and remove pairs from all hospitals
                            System.out.println("----------------");
                            for (ExchangePair pair : cycle.keySet()) {
                                System.out.println(pair.toString() + " -> " + cycle.get(pair).toString());
                                for (Hospital h : hospitals) {
                                    h.removePair(pair);
                                }
                            }
                        } else if(highestRanked) {
                            // Acquire patients from lower ranked hospitals for matching next round.
                            System.out.println("----------------");
                            for(ExchangePair pair : cycle.keySet()) {
                                if(pair.getCurrentHospital() != hospital.getHospitalId()) {
                                    System.out.println("Moving " + pair.toString() + " from hospital " +
                                            pair.getCurrentHospital() + " to hospital " + hospital.getHospitalId());
                                    // Remove knowledge of pair from current hospital and move to new hospital
                                    Hospital currentHospital = hospitals[pair.getCurrentHospital() - 1];
                                    currentHospital.removePair(pair);
                                    pair.setCurrentHospital(hospital.getHospitalId());
                                }
                            }
                        }
                    }
                    System.out.println("----------------");
                }
                // Inform higher ranked peer of unmatched pairs to be accounted for in next round.
                // In other words, all peer information will be distributed after numHospital rounds.
                if(hospital.getHospitalId() != 1 && i < hospitals.length) {
                    Hospital peer = hospitals[hospital.getHospitalId() - 2];
                    for(ExchangePair pair: hospital.getPairs()) {
                        if(!peer.hasPair(pair)) {
                            peer.addPair(pair);
                        }
                    }
                }
                System.out.print("\n");
            }
        }
    }
}
package KidneyExchange;

import java.util.HashMap;

public class KidneyExchange {

    // Main function controlling behavior of kidney exchange
    public static void main(String[] args) {
        // Create Hospitals participating in Kidney Exchange
        // For now, hardcode 3 hospitals, though this may become a cl arg
        int numHospitals = 3;
        Hospital[] hospitals = new Hospital[numHospitals];
        for(int i=0; i < numHospitals; i++) {
            // Create each hospital with 10 Participants and the ability to perform
            // 10 simultaneous surgeries. This may become a command line arg.
            hospitals[i] = KidneyExchangeHelper.createHospital(5, 5);
        }

        // Print hospitals participating in KidneyExchange
        System.out.println("Hospitals participating in kidney exchange: ");
        for(int i=0; i < numHospitals; i++) {
            System.out.print(hospitals[i]);
        }

        // Run the kidney exchange for some fixed number of rounds
        // This may be a command line arg, but for now we will do one round,
        // i.e. local matching and no moving pairs.
        runKidneyExchange(1, hospitals);
    }

    private static void runKidneyExchange(int numRounds, Hospital[] hospitals) {
        // Each round of the kidney exchange works as follows:
        //     1. Each hospital creates a directed graph amongst ExchangePairs, where
        //        an edge from pair i to pair j indicates that pair i can be matched
        //        with the kidney being donated by pair j.
        //     2. Each hospital determines a feasible local matching and removes those
        //        matches, i.e. performs the surgeries in this time step.
        //     3. Each hospital tallies the kidney types that they need to make forward
        //        progress and sends that information to each peer.

        for(int i=0; i < numRounds; i++) {
            System.out.println("\nRound: " + i);
            for(Hospital hospital : hospitals) {
                // Create directed graph from ExchangePairs
                DirectedGraph graph = KidneyExchangeHelper.createDirectedGraph(hospital);
                System.out.println("\nAdjacency List for Hospital: " + hospital.getHospitalId());
                System.out.print(graph);

                // Return matched pairs
                HashMap<ExchangePair, ExchangePair> matches = KidneyExchangeHelper.greedyMatches(hospital, graph);
                // Print matches and remove matched pairs from hospital
                System.out.println("Matches: ");
                for(ExchangePair pair: matches.keySet()) {
                    System.out.println(pair.toString() + " -> " + matches.get(pair).toString());
                    hospital.removePair(pair);
                }
            }
        }
    }
}
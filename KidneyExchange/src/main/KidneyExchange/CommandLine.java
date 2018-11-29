package KidneyExchange;

import com.beust.jcommander.Parameter;

public class CommandLine {
    @Parameter( names={ "--seed", "-s" }, description = "RNG seed to use for kidney exchange" )
    Long seed = null;

    @Parameter( names={ "--num-hopitals", "-n" }, description = "Number of hospitals to create for kidney exchange" )
    Integer numHospitals;

    @Parameter( names={ "--num-pairs", "-p" }, description = "Number of pairs to initially assign each hospital" )
    Integer numPairs;

    @Parameter( names={ "--max-surgeries", "-m" }, description = "Max number of surgeries that can be performed by each hospital" )
    Integer maxSurgeries;

    @Parameter( names={ "--num-rounds", "-r" }, description = "Number of rounds to run kidney exchange" )
    Integer numRounds;

    @Parameter( names={ "--algorithm", "-a" }, description = "Matching algorithm to use. Must be either greedy or ilp." )
    MatchingAlgorithm matchingAlgorithm = MatchingAlgorithm.GREEDY;

    @Parameter( names={ "--quiet", "-q" }, description = "Disable console output" )
    boolean quiet = false;
}

package KidneyExchange.LP;

import KidneyExchange.MatchingAlgorithm;

public class WeightStrategyFactory {
    public static IWeightStrategy create( MatchingAlgorithm matchingAlgorithm ) {
        if( matchingAlgorithm == MatchingAlgorithm.ILP )
            return new EqualWeightStrategy();
        else if( matchingAlgorithm == MatchingAlgorithm.LOCAL_ILP )
            return new LocalPreferenceWeightStrategy( 3, 2 );
        return null;
    }
}

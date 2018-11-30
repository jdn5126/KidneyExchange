package KidneyExchange.LP;

import KidneyExchange.ExchangePair;
import KidneyExchange.Hospital;

public class LocalPreferenceWeightStrategy implements IWeightStrategy {
    private int localWeight;
    private int remoteWeight;

    public LocalPreferenceWeightStrategy( int localWeight, int remoteWeight ) {
        this.localWeight = localWeight;
        this.remoteWeight = remoteWeight;
    }

    @Override
    public int getWeight( Hospital currentHospital, ExchangePair fromPair, ExchangePair toPair ) {
        if( fromPair.getCurrentHospital() == currentHospital.getHospitalId() &&
            toPair.getCurrentHospital() == currentHospital.getHospitalId() )
            return localWeight;
        return remoteWeight;
    }
}

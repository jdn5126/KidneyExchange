package KidneyExchange.LP;

import KidneyExchange.ExchangePair;
import KidneyExchange.Hospital;

public class EqualWeightStrategy implements IWeightStrategy {

    @Override
    public int getWeight( Hospital currentHospital, ExchangePair fromPair, ExchangePair toPair ) {
        return 1;
    }
}

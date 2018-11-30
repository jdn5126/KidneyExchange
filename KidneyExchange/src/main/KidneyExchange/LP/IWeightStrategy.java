package KidneyExchange.LP;

import KidneyExchange.ExchangePair;
import KidneyExchange.Hospital;

public interface IWeightStrategy {
    int getWeight( Hospital currentHospital, ExchangePair fromPair, ExchangePair toPair );
}

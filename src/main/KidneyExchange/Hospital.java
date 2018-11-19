package KidneyExchange;

import java.util.ArrayList;

public class Hospital {
    private final int hospitalId;
    private final int maxSurgeries;
    private ArrayList<ExchangePair> pairs;

    public Hospital(int hospitalId, int maxSurgeries) {
        this.hospitalId = hospitalId;
        this.maxSurgeries = maxSurgeries;
        this.pairs = new ArrayList<ExchangePair>();
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public int getMaxSurgeries() {
        return maxSurgeries;
    }

    // Get reference to list of ExchangePairs
    public ArrayList<ExchangePair> getPairs() {
        return pairs;
    }

    public int getSize() {
        return pairs.size();
    }

    // Append pair to list
    public void addPair(ExchangePair pair) {
        pairs.add(pair);
    }

    // Remove pair from list
    public boolean removePair(ExchangePair pair) {
        return pairs.remove(pair);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Hospital: " + hospitalId + ", maxSurgeries=" + maxSurgeries + "\n");
        for(ExchangePair pair: pairs) {
            s.append(pair.toString() + "\n");
        }
        return s.toString();
    }
}
package KidneyExchange;

// ExchangePair represents a (donor, receiver) participating in kidney exchange
public class ExchangePair {
    private final Participant donor;
    private final Participant receiver;
    private final int pairId;
    // Hospital that the ExchangePair currently resides in
    private int currentHospital;

    public ExchangePair(KidneyType donorType, KidneyType receiverType, int pairId, int currentHospital) {
        this.donor = new Participant(donorType);
        this.receiver = new Participant(receiverType);
        this.pairId = pairId;
        this.currentHospital = currentHospital;
    }

    // Accessors and modifiers
    public KidneyType getDonorType() {
        return donor.getType();
    }

    public KidneyType getReceiverType() {
        return receiver.getType();
    }

    public int getCurrentHospital() {
        return currentHospital;
    }

    public void setCurrentHospital(int currentHospital) {
        this.currentHospital = currentHospital;
    }

    public int getPairId() {
        return pairId;
    }

    // Map operators
    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        ExchangePair ep = (ExchangePair)o;
        return (this.pairId == ep.getPairId());
    }

    @Override
    public int hashCode() {
        // Unique identifier is the ExchangePair's id
        return pairId;
    }

    // Matching functions for building directed graph
    public boolean canDonate(ExchangePair pair) {
        // return true if donor is compatible with other pair
        return donor.getType() == pair.getReceiverType();
    }

    public boolean canReceive(ExchangePair pair) {
        // return true if receiver is compatible with other pair
        return receiver.getType() == pair.getDonorType();
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("(" + donor.getType() + ", " + receiver.getType() + ")");
        return s.toString();
    }
}

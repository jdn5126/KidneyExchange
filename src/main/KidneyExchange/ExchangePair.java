package KidneyExchange;

// ExchangePair represents a (donor, receiver) participating in kidney exchange
public class ExchangePair {
    private final Participant donor;
    private final Participant receiver;
    private final int id;

    public ExchangePair(KidneyType donorType, KidneyType receiverType, int id) {
        this.donor = new Participant(donorType);
        this.receiver = new Participant(receiverType);
        this.id = id;
    }

    // Accessors
    public Participant getDonor() {
        return donor;
    }

    public KidneyType getDonorType() {
        return donor.getType();
    }

    public Participant getReceiver() {
        return receiver;
    }

    public KidneyType getReceiverType() {
        return receiver.getType();
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
        return (this.donor == ep.getDonor()) && (this.receiver == ep.getReceiver());
    }

    @Override
    public int hashCode() {
        // Unique identifier is the ExchangePair's id
        return id;
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
        s.append(id + ": (" + donor.getType() + ", " + receiver.getType() + ")");
        return s.toString();
    }
}

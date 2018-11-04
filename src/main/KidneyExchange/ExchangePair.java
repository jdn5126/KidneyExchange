package KidneyExchange;

public class ExchangePair {
    private Participant donor;
    private Participant receiver;

    public ExchangePair(Participant donor, Participant receiver) {
        this.donor = donor;
        this.receiver = receiver;
    }

    public ExchangePair(KidneyType donorType, KidneyType receiverType) {
        this.donor = new Participant(donorType);
        this.receiver = new Participant(receiverType);
    }

    // Accessors and modifiers
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

    public void setDonor(Participant donor) {
        this.donor = donor;
    }

    public void setDonorType(KidneyType type) {
        this.donor.setType(type);
    }

    public void setReceiver(Participant receiver) {
        this.receiver = receiver;
    }

    public void setReceiverType(KidneyType type) {
        this.receiver.setType(type);
    }

    // Matching functions
    public boolean canDonate(ExchangePair pair) {
        // return true if donor is compatible with other pair
        return donor.getType() == pair.getReceiverType();
    }

    public boolean canReceive(ExchangePair pair) {
        // return true if receiver is compatible with other pair
        return receiver.getType() == pair.getDonorType();
    }
}

package KidneyExchange;

// Enumerate list of possible kidney types
enum KidneyType {
    A, B, C, D;
}

public class Participant {
    private KidneyType type;

    public Participant(KidneyType type) {
        this.type = type;
    }

    public KidneyType getType() {
        return type;
    }

    public void setType(KidneyType type) {
        this.type = type;
    }
}

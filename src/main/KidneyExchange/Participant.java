package KidneyExchange;

// Enumerate list of possible kidney types
enum KidneyType {
    A, B, C, D;
}

public final class Participant {
    private final KidneyType type;

    public Participant(KidneyType type) {
        this.type = type;
    }

    public KidneyType getType() {
        return type;
    }
}

package KidneyExchange;

public final class Participant {
    private final KidneyType type;

    public Participant(KidneyType type) {
        this.type = type;
    }

    public KidneyType getType() {
        return type;
    }
}

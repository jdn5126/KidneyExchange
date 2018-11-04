package KidneyExchange;

public class KidneyExchange {
    public static void main(String[] args) {
        ExchangePair pair1 = new ExchangePair(KidneyType.A, KidneyType.B);
        ExchangePair pair2 = new ExchangePair(KidneyType.B, KidneyType.A);
        System.out.println(pair1.canDonate(pair2) && pair1.canReceive(pair2));
    }
}
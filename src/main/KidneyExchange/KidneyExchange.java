package KidneyExchange;

public class KidneyExchange {
    public static void main(String[] args) {
        // Create hospital that can perform at most one surgery
        Hospital hospital = new Hospital(1, 1);
        ExchangePair pair1 = new ExchangePair(KidneyType.A, KidneyType.B, 1);
        ExchangePair pair2 = new ExchangePair(KidneyType.B, KidneyType.A, 2);
        hospital.addPair(pair1);
        hospital.addPair(pair2);

        System.out.print(hospital);
        System.out.println(pair1.canDonate(pair2) && pair1.canReceive(pair2));
    }
}
import KidneyExchange.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class KidneyExchangeTest {

    // Test a single round of runKidneyExchange with single hospital
    public void runKidneyExchangeTest1(MatchingAlgorithm matchingAlgorithm) {
        int hospitalId = 0;
        int pairId = 0;
        // Create a hospital with 3 surgery slots and add a cycle of length 3
        Hospital hospital1 = new Hospital(++hospitalId, 3);
        hospital1.addPair(new ExchangePair(KidneyType.A, KidneyType.B, ++pairId, hospital1.getHospitalId()));
        hospital1.addPair(new ExchangePair(KidneyType.B, KidneyType.C, ++pairId, hospital1.getHospitalId()));
        hospital1.addPair(new ExchangePair(KidneyType.C, KidneyType.A, ++pairId, hospital1.getHospitalId()));

        // Run single round of kidney exchange and verify that all patients are removed
        Hospital[] hospitals = { hospital1 };
        KidneyExchange.runKidneyExchange(1, hospitals, matchingAlgorithm, false, false);
        Assertions.assertEquals(hospital1.getSize(), 0);

        // Create another hospital with 2 surgery slots and a cycle of length 3
        Hospital hospital2 = new Hospital(++hospitalId, 2);
        hospital2.addPair(new ExchangePair(KidneyType.A, KidneyType.B, ++pairId, hospital2.getHospitalId()));
        hospital2.addPair(new ExchangePair(KidneyType.B, KidneyType.C, ++pairId, hospital2.getHospitalId()));
        hospital2.addPair(new ExchangePair(KidneyType.C, KidneyType.A, ++pairId, hospital2.getHospitalId()));

        // Run single round of kidney exchange and verify that no patients are removed
        hospitals[0] = hospital2;
        KidneyExchange.runKidneyExchange(1, hospitals, matchingAlgorithm, false, false);
        Assertions.assertEquals(hospital2.getSize(), 3);
    }

    // Test three rounds of runKidneyExchange with two hospitals that need to collaborate
    public void runKidneyExchangeTest2(MatchingAlgorithm matchingAlgorithm) {
        int hospitalId = 0;
        int pairId = 0;

        // Hospital 1 and Hospital 2 contains pairs that cannot be locally matched, but can be globally matched
        Hospital hospital1 = new Hospital(++hospitalId, 4);
        hospital1.addPair(new ExchangePair(KidneyType.A, KidneyType.B, ++pairId, hospital1.getHospitalId()));
        hospital1.addPair(new ExchangePair(KidneyType.C, KidneyType.D, ++pairId, hospital1.getHospitalId()));

        Hospital hospital2 = new Hospital(++hospitalId, 4);
        hospital2.addPair(new ExchangePair(KidneyType.B, KidneyType.A, ++pairId, hospital2.getHospitalId()));
        hospital2.addPair(new ExchangePair(KidneyType.D, KidneyType.C, ++pairId, hospital2.getHospitalId()));

        // Neither will be able to match in Round 1, Hospital 1 will acquire pairs in Round 2,
        // pairs will be matched in Round 3
        Hospital[] hospitals = { hospital1, hospital2 };
        KidneyExchange.runKidneyExchange(3, hospitals, matchingAlgorithm, false, false);
        Assertions.assertEquals(hospital1.getSize(), 0);
        Assertions.assertEquals(hospital2.getSize(), 0);
    }

    // Test four rounds of runKidneyExchange with three hospitals and verify that pairs distribute
    public void runKidneyExchangeTest3(MatchingAlgorithm matchingAlgorithm) {
        int hospitalId = 0;
        int pairId = 0;

        // No matching can complete until Hospital 3 pair is acquired by Hospital 1
        Hospital hospital1 = new Hospital(++hospitalId, 4);
        hospital1.addPair(new ExchangePair(KidneyType.A, KidneyType.B, ++pairId, hospital1.getHospitalId()));

        Hospital hospital2 = new Hospital(++hospitalId, 4);
        hospital2.addPair(new ExchangePair(KidneyType.B, KidneyType.C, ++pairId, hospital2.getHospitalId()));

        Hospital hospital3 = new Hospital(++hospitalId, 4);
        hospital3.addPair(new ExchangePair(KidneyType.C, KidneyType.A, ++pairId, hospital3.getHospitalId()));

        // No one will be able to match in Round 1, Hospital 1 will acquire Hospital 2 pair in Round 2,
        // Hospital 1 will acquire Hospital 3 pair in round 3, pairs will be matched in round 4
        Hospital[] hospitals = { hospital1, hospital2, hospital3 };
        KidneyExchange.runKidneyExchange(4, hospitals, matchingAlgorithm, false, false);
        Assertions.assertEquals(hospital1.getSize(), 0);
        Assertions.assertEquals(hospital2.getSize(), 0);
        Assertions.assertEquals(hospital3.getSize(), 0);
    }

    // Test three rounds of runKidneyExchange and verify that information is distributed when no one can be matched
    public void runKidneyExchangeTest4(MatchingAlgorithm matchingAlgorithm) {
        int hospitalId = 0;
        int pairId = 0;

        // No matchings can complete due to pair incompatibility
        Hospital hospital1 = new Hospital(++hospitalId, 4);
        hospital1.addPair(new ExchangePair(KidneyType.A, KidneyType.B, ++pairId, hospital1.getHospitalId()));
        hospital1.addPair(new ExchangePair(KidneyType.A, KidneyType.B, ++pairId, hospital1.getHospitalId()));

        Hospital hospital2 = new Hospital(++hospitalId, 4);
        hospital2.addPair(new ExchangePair(KidneyType.C, KidneyType.D, ++pairId, hospital2.getHospitalId()));
        hospital2.addPair(new ExchangePair(KidneyType.C, KidneyType.D, ++pairId, hospital2.getHospitalId()));

        Hospital hospital3 = new Hospital(++hospitalId, 4);
        hospital3.addPair(new ExchangePair(KidneyType.B, KidneyType.D, ++pairId, hospital3.getHospitalId()));
        hospital3.addPair(new ExchangePair(KidneyType.B, KidneyType.D, ++pairId, hospital3.getHospitalId()));

        // No one will ever be able to match, but by round 3, information will have been fully distributed
        Hospital[] hospitals = { hospital1, hospital2, hospital3 };
        KidneyExchange.runKidneyExchange(3, hospitals, matchingAlgorithm, false, false);
        Assertions.assertEquals(hospital1.getSize(), 6);
        Assertions.assertEquals(hospital2.getSize(), 4);
        Assertions.assertEquals(hospital3.getSize(), 2);
    }

    // Build a more complicated example (two hospitals is sufficient) to test resiliency
    public void runKidneyExchangeTest5(MatchingAlgorithm matchingAlgorithm) {
        int hospitalId = 0;
        int pairId = 0;

        // Hospital 1 will complete one matching (C,D) <-> (D,C), but needs help breaking 4-cycle up
        Hospital hospital1 = new Hospital(++hospitalId, 3);
        hospital1.addPair(new ExchangePair(KidneyType.C, KidneyType.D, ++pairId, hospital1.getHospitalId()));
        hospital1.addPair(new ExchangePair(KidneyType.D, KidneyType.C, ++pairId, hospital1.getHospitalId()));
        hospital1.addPair(new ExchangePair(KidneyType.A, KidneyType.B, ++pairId, hospital1.getHospitalId()));
        hospital1.addPair(new ExchangePair(KidneyType.B, KidneyType.C, ++pairId, hospital1.getHospitalId()));
        hospital1.addPair(new ExchangePair(KidneyType.C, KidneyType.D, ++pairId, hospital1.getHospitalId()));
        hospital1.addPair(new ExchangePair(KidneyType.D, KidneyType.A, ++pairId, hospital1.getHospitalId()));

        // Hospital 2 can perform local matching (A,B) <-> (B,A), then can help Hospital 1 with two more matchings
        Hospital hospital2 = new Hospital(++hospitalId, 3);
        hospital2.addPair(new ExchangePair(KidneyType.A, KidneyType.B, ++pairId, hospital2.getHospitalId()));
        hospital2.addPair(new ExchangePair(KidneyType.B, KidneyType.A, ++pairId, hospital2.getHospitalId()));
        hospital2.addPair(new ExchangePair(KidneyType.D, KidneyType.C, ++pairId, hospital2.getHospitalId()));
        hospital2.addPair(new ExchangePair(KidneyType.C, KidneyType.B, ++pairId, hospital2.getHospitalId()));


        // Round 1: Hospital 1 should match (C,D) <-> (D,C) and be unable to do more
        //          Hospital 2 should match (A,B) <-> (B,A) and inform Hospital 1 of unmatched pairs
        // Round 2: Hospital 1 should pull one of (D,C), (C,B)
        //          Hospital 2 has one patient left and waits for it to be requested
        // Round 3: Hospital 1 can only perform two surgeries at a time, so will be left with four pairs
        //          Hospital 2 still waits for remaining pair to be requested
        Hospital[] hospitals = { hospital1, hospital2 };
        KidneyExchange.runKidneyExchange(3, hospitals, matchingAlgorithm, false, false);
        Assertions.assertEquals(hospital1.getSize(), 4);
        Assertions.assertEquals(hospital2.getSize(), 1);
    }

    @Test
    public void runKidneyExchangeTests() {
        // Suppress console output for tests
        ConsoleLogger.setQuiet(true);
        for(MatchingAlgorithm matchingAlgorithm: MatchingAlgorithm.values()) {
            runKidneyExchangeTest1(matchingAlgorithm);
            runKidneyExchangeTest2(matchingAlgorithm);
            runKidneyExchangeTest3(matchingAlgorithm);
            runKidneyExchangeTest4(matchingAlgorithm);
            runKidneyExchangeTest5(matchingAlgorithm);
        }
    }

    // Build a scale test to prove that algorithm can scale
    @Test
    public void runKidneyExchangeScaleTest() {
        // Suppress console output for testing
        ConsoleLogger.setQuiet(true);
        int hospitalId = 0;
        int pairId = 0;

        // Add pairs that will be matched in Hospital 1 only
        Hospital hospital1 = new Hospital(++hospitalId, 5);
        for(int i=0; i < 50; i++) {
            hospital1.addPair(new ExchangePair(KidneyType.A, KidneyType.B, ++pairId, hospital1.getHospitalId()));
            hospital1.addPair(new ExchangePair(KidneyType.B, KidneyType.C, ++pairId, hospital1.getHospitalId()));
            hospital1.addPair(new ExchangePair(KidneyType.C, KidneyType.A, ++pairId, hospital1.getHospitalId()));
        }

        // No dependencies between Hospital 1 and Hospital 2 so that information will still be distributed, but
        // patients will not be.
        Hospital hospital2 = new Hospital(++hospitalId, 4);
        for(int i=0; i < 50; i++) {
            hospital2.addPair(new ExchangePair(KidneyType.D, KidneyType.B, ++pairId, hospital2.getHospitalId()));
            hospital2.addPair(new ExchangePair(KidneyType.B, KidneyType.D, ++pairId, hospital2.getHospitalId()));
        }

        Hospital[] hospitals = { hospital1, hospital2 };
        // Hospital 1 will finish after 50 rounds, while Hospital 2 will finish in 25 rounds
        KidneyExchange.runKidneyExchange(50, hospitals, MatchingAlgorithm.GREEDY, false, false);
        Assertions.assertEquals(hospital1.getSize(), 0);
        Assertions.assertEquals(hospital2.getSize(), 0);
    }
}

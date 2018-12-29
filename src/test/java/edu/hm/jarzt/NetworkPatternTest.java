package edu.hm.jarzt;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


class NetworkPatternTest {

    private static final double TOLERANCE = 1e-10;
    private static final double THRESHOLD = 1e-2;

    @Test
    void aggregatesNetworkTrafficOdd() {
        File file = new File(System.getProperty("user.dir") + File.separator + "trafficPattern" + File.separator + "trafficPattern01Odd.csv");
        Records records = new Records(file);
        List<Long> periods = records.aggregatesNetworkTraffic(200, 2);

        assertThat(periods)
                .hasSize(9)
                .containsExactly(720L, 453L, 1718L, 1025L, 883L, 279L, 1104L, 441L, 479L);
    }

    @Test
    void aggregatesNetworkTrafficEven() {
        File file = new File(System.getProperty("user.dir") + File.separator + "trafficPattern" + File.separator + "trafficPattern01Even.csv");
        Records records = new Records(file);
        List<Long> periods = records.aggregatesNetworkTraffic(200, 2);

        assertThat(periods)
                .hasSize(10)
                .containsExactly(720L, 453L, 1718L, 1025L, 883L, 279L, 1104L, 441L, 479L, 750L);
    }

    @Test
    void differential() {
        List<Long> periods = new ArrayList<>();
        periods.add(250L);
        periods.add(300L);
        periods.add(600L);
        periods.add(1800L);
        periods.add(600L);

        List<Double> pattern = Utils.differential(periods);

        assertThat(pattern)
                .hasSize(5)
                .containsExactly(0.0, 0.2, 1.0, 2.0, -0.6666666666666666);
    }

    @Test
    void normalize() {
        List<Double> differential = new ArrayList<>();
        differential.add(0.0);
        differential.add(0.2);
        differential.add(1.0);
        differential.add(2.0);
        differential.add(-0.6666666666666666);

        List<Double> normalized = Utils.normalize(differential);

        assertThat(normalized)
                .hasSize(5);

        assertThat(Math.abs(normalized.get(0) - 0.5) <= TOLERANCE).isTrue();
        assertThat(Math.abs(normalized.get(1) - 0.549833997312478) <= TOLERANCE).isTrue();
        assertThat(Math.abs(normalized.get(2) - 0.7310585786300049) <= TOLERANCE).isTrue();
        assertThat(Math.abs(normalized.get(3) - 0.8807970779778823) <= TOLERANCE).isTrue();
        assertThat(Math.abs(normalized.get(4) - 0.33924363123418283) <= TOLERANCE).isTrue();
    }

    @Test
    void aggregateFileSizes() {
        File pathToTest = new File(System.getProperty("user.dir") + File.separator + "videos" + File.separator + "test");

        List<File> files = Arrays.asList(Objects.requireNonNull(pathToTest.listFiles()));
        Collections.sort(files);

        List<Long> results = Utils.getFileSizeInBytes(files);

        assertThat(results)
                .hasSize(10)
                .containsExactly(34354L, 27808L, 226974L, 241565L, 180024L, 143101L, 211129L, 124263L, 274893L, 194189L);
    }

    @Test
    void generateFingerprintTest() {
        List<Long> listA = new ArrayList<>();

        listA.add(7L);
        listA.add(2L);
        listA.add(3L);
        listA.add(5L);
        listA.add(10L);


        List<Double> listR = Utils.differential(listA);

        assertThat(listR)
                .hasSize(5);

        assertThat(Math.abs(listR.get(0) - 0.0) <= TOLERANCE).isTrue();
        assertThat(Math.abs(listR.get(1) - (-0.7142857142857143)) <= TOLERANCE).isTrue();
        assertThat(Math.abs(listR.get(2) - 0.5) <= TOLERANCE).isTrue();
        assertThat(Math.abs(listR.get(3) - 0.6666666666666667) <= TOLERANCE).isTrue();
        assertThat(Math.abs(listR.get(4) - 1) <= TOLERANCE).isTrue();

        List<Long> listResult = Utils.generateFingerPrintWithLSecondSegments(listA, 2);

        assertThat(listResult).hasSize(3).containsExactly(9L, 8L, 10L);

        List<Double> listDiff = Utils.differential(listResult);

        assertThat(listDiff).hasSize(3);

        assertThat(Math.abs(listDiff.get(0) - 0.0) <= TOLERANCE).isTrue();
        assertThat(Math.abs(listDiff.get(1) - (-0.1111111111111111)) <= TOLERANCE).isTrue();
        assertThat(Math.abs(listDiff.get(2) - 0.25) <= TOLERANCE).isTrue();

        List<Double> normalizedFingerprint = Utils.normalize(listDiff);

        assertThat(normalizedFingerprint).hasSize(3);

        assertThat(Math.abs(normalizedFingerprint.get(0) - 0.5) <= TOLERANCE).isTrue();
        assertThat(Math.abs(normalizedFingerprint.get(1) - (0.4722507650)) <= TOLERANCE).isTrue();
        assertThat(Math.abs(normalizedFingerprint.get(2) - (0.5621765008)) <= TOLERANCE).isTrue();

    }

    @Test
    void compareTwoSameFingerprints() {
        List<Double> list1 = Utils.generateFingerprint("test", 2);
        List<Double> list2 = Utils.generateFingerprint("test", 2);

        assertThat(Utils.partialMatchingPdtw(list1, list2)).isEqualTo(0.0);
    }

    @Test
    void compareTwoEqualFingerprintsShouldReturnZero() {
        List<Double> query = Utils.generateTrafficPattern("james.csv", 20000, 5);
        List<Double> template = Utils.generateTrafficPattern("james.csv", 20000, 5);
        List<Double> subSequence;
        List<Double> result = new ArrayList<>();
        query = query.subList(0, query.size() - 1);

        for (int i = 0; i < template.size(); i++) {
            for (int j = i; j < template.size(); j++) {
                subSequence = template.subList(i, j + 1);
                result.add(Utils.partialMatchingPdtwForTesing(template, query, subSequence));
            }
        }
        assertThat(Collections.min(result)).isEqualTo(0.0);
    }

    @Test
    void compareJames30fpsFingerprintWithJames30fpsPattern() {
        List<Double> queryJames = Utils.generateTrafficPattern("james.csv", 20000, 5);
        List<Double> templateJames = Utils.generateFingerprint("james", 5);
        List<Double> subSequence;
        List<Double> result = new ArrayList<>();

        for (int i = 0; i < templateJames.size(); i++) {
            for (int j = i; j < templateJames.size(); j++) {
                subSequence = templateJames.subList(i, j + 1);
                result.add(Utils.partialMatchingPdtwForTesing(templateJames, queryJames, subSequence));
            }
        }
        System.out.println(Collections.min(result));
        assertThat(Collections.min(result)).isLessThan(THRESHOLD);
    }

    @Test
    void compareKovacFingerprintWithKovacPattern() {
        List<Double> queryJames = Utils.generateTrafficPattern("kovac.csv", 20000, 6);
        List<Double> templateJames = Utils.generateFingerprint("kovac", 6);
        List<Double> subSequence;
        List<Double> result = new ArrayList<>();

        for (int i = 0; i < templateJames.size(); i++) {
            for (int j = i; j < templateJames.size(); j++) {
                subSequence = templateJames.subList(i, j + 1);
                result.add(Utils.partialMatchingPdtwForTesing(templateJames, queryJames, subSequence));
            }
        }
        System.out.println(Collections.min(result));
        assertThat(Collections.min(result)).isLessThan(THRESHOLD);
    }

    @Test
    void compareJimmyFingerprintWithJimmyPattern() {
        List<Double> queryJames = Utils.generateTrafficPattern("jimmy.csv", 20000, 5);
        List<Double> templateJames = Utils.generateFingerprint("jimmy", 5);
        List<Double> subSequence;
        List<Double> result = new ArrayList<>();

        for (int i = 0; i < templateJames.size(); i++) {
            for (int j = i; j < templateJames.size(); j++) {
                subSequence = templateJames.subList(i, j + 1);
                result.add(Utils.partialMatchingPdtwForTesing(templateJames, queryJames, subSequence));
            }
        }
        System.out.println(Collections.min(result));
        assertThat(Collections.min(result)).isLessThan(THRESHOLD);
    }

    @Test
    void checkSimilarityOfAllCombinations() {
        List<String> fingerprintNames = new ArrayList<>();
        List<String> trafficPatternNames = new ArrayList<>();
        List<Integer> segmentLenghts = new ArrayList<>();

        fingerprintNames.add("james");
        fingerprintNames.add("kovac");
        fingerprintNames.add("jimmy");

        trafficPatternNames.add("james.csv");
        trafficPatternNames.add("kovac.csv");
        trafficPatternNames.add("jimmy.csv");


        segmentLenghts.add(5);
        segmentLenghts.add(6);
        segmentLenghts.add(5);

        SimilarityChecker checker = new SimilarityChecker(fingerprintNames, trafficPatternNames, segmentLenghts, 20000);

        List<List<Double>> result = checker.calculateSimularity();

        assertThat(result.get(0).get(0)).
                as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(0))
                .isLessThan(THRESHOLD);
        assertThat(result.get(0).get(1)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(1))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(2)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(2))
                .isGreaterThan(THRESHOLD);

        assertThat(result.get(1).get(0)).
                as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(0))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(1)).
                as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(1))
                .isLessThan(THRESHOLD);
        assertThat(result.get(1).get(2)).
                as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(2))
                .isGreaterThan(THRESHOLD);


        assertThat(result.get(2).get(0)).
                as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(2), trafficPatternNames.get(0))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(2).get(1)).
                as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(2), trafficPatternNames.get(1))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(2).get(2)).
                as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(2), trafficPatternNames.get(2))
                .isLessThan(THRESHOLD);

    }

    @Test
    void checkSimilarityBetweenSameFingerprints() {
        for (int k = 1; k <= 8; k++) {
            List<Double> fingerprintAliasTrafficPattern1 = Utils.generateFingerprint("kovac", k);
            List<Double> fingerprint2 = Utils.generateFingerprint("kovac", k);
            List<Double> subSequence;
            List<Double> result = new ArrayList<>();

            for (int i = 0; i < fingerprint2.size(); i++) {
                for (int j = i; j < fingerprint2.size(); j++) {
                    subSequence = fingerprint2.subList(i, j + 1);
                    result.add(Utils.partialMatchingPdtwForTesing(fingerprint2, fingerprintAliasTrafficPattern1, subSequence));
                }
            }
            assertThat(Collections.min(result)).isEqualTo(0.0);
        }
    }
}





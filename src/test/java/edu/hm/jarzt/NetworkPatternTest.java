package edu.hm.jarzt;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


class NetworkPatternTest {

    private static final double TOLERANCE = 1e-10;
    private static final double THRESHOLD = 8e-3;
    private static final int LENGTH_30_FPS = 5;
    private static final int LENGTH_25_FPS = 6;

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
    void compareJames30fps480pFingerprintWithJames30fps480pPattern() {
        List<Double> queryJames = Utils.generateTrafficPattern("james30fps480p5s.csv", 20000, 5);
        List<Double> templateJames = Utils.generateFingerprint("james_480p", 5);
        List<Double> subSequence;
        List<Double> result = new ArrayList<>();

        for (Double d : templateJames) {
            System.out.println(d);
        }

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
    void compareJames30fpsFingerprintWithJames30fpsPattern() {
        List<Double> queryJames = Utils.generateTrafficPattern("james.csv", 20000, 5);
        List<Double> templateJames = Utils.generateFingerprint("james_720p", 5);
        List<Double> subSequence;
        List<Double> result = new ArrayList<>();

        for (Double d : templateJames) {
            System.out.println(d);
        }

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
    void compareKovac720pFingerprintWithKovacPattern() {
        List<Double> queryJames = Utils.generateTrafficPattern("kovac720p.csv", 20000, 6);
        List<Double> templateJames = Utils.generateFingerprint("kovac_720p", 6);
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
    void compareJimmy720pFingerprintWithJimmyPattern() {
        List<Double> queryJames = Utils.generateTrafficPattern("jimmy.csv", 20000, 5);
        List<Double> templateJames = Utils.generateFingerprint("jimmy_720p", 5);
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
    void checkSimilarityOfJames(){
        List<String> fingerprintNames = new ArrayList<>();
        List<String> trafficPatternNames = new ArrayList<>();
        List<Integer> segmentLengths = new ArrayList<>();

        fingerprintNames.add("james_720p");
        fingerprintNames.add("james_480p");

        trafficPatternNames.add("james720p.csv");
        trafficPatternNames.add("james480p.csv");
        trafficPatternNames.add("kovac720p.csv");
        trafficPatternNames.add("kovac480p.csv");
        trafficPatternNames.add("jimmy720p.csv");
        trafficPatternNames.add("jimmy480p.csv");
        trafficPatternNames.add("lampard720p.csv");
        trafficPatternNames.add("lampard480p.csv");
        trafficPatternNames.add("blind_fail720p.csv");
        trafficPatternNames.add("blind_fail480p.csv");
        trafficPatternNames.add("cake720p.csv");
        trafficPatternNames.add("cake480p.csv");
        trafficPatternNames.add("cats720p.csv");
        trafficPatternNames.add("cats480p.csv");
        trafficPatternNames.add("conan720p.csv");
        trafficPatternNames.add("conan480p.csv");
        trafficPatternNames.add("ski720p.csv");
        trafficPatternNames.add("ski480p.csv");
        trafficPatternNames.add("wind720p.csv");
        trafficPatternNames.add("wind480p.csv");

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);



        SimilarityChecker checker = new SimilarityChecker(fingerprintNames, trafficPatternNames, segmentLengths, 20000);

        List<List<Double>> result = checker.calculateSimilarity();

        assertionsUtils(fingerprintNames, trafficPatternNames, result);

    }

    @Test
    void checkSimilarityOfKovac(){
        List<String> fingerprintNames = new ArrayList<>();
        List<String> trafficPatternNames = new ArrayList<>();
        List<Integer> segmentLengths = new ArrayList<>();

        fingerprintNames.add("kovac_720p");
        fingerprintNames.add("kovac_480p");

        trafficPatternNames.add("kovac720p.csv");
        trafficPatternNames.add("kovac480p.csv");
        trafficPatternNames.add("james720p.csv");
        trafficPatternNames.add("james480p.csv");
        trafficPatternNames.add("jimmy720p.csv");
        trafficPatternNames.add("jimmy480p.csv");
        trafficPatternNames.add("lampard720p.csv");
        trafficPatternNames.add("lampard480p.csv");
        trafficPatternNames.add("blind_fail720p.csv");
        trafficPatternNames.add("blind_fail480p.csv");
        trafficPatternNames.add("cake720p.csv");
        trafficPatternNames.add("cake480p.csv");
        trafficPatternNames.add("cats720p.csv");
        trafficPatternNames.add("cats480p.csv");
        trafficPatternNames.add("conan720p.csv");
        trafficPatternNames.add("conan480p.csv");
        trafficPatternNames.add("ski720p.csv");
        trafficPatternNames.add("ski480p.csv");
        trafficPatternNames.add("wind720p.csv");
        trafficPatternNames.add("wind480p.csv");

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        SimilarityChecker checker = new SimilarityChecker(fingerprintNames, trafficPatternNames, segmentLengths, 20000);

        List<List<Double>> result = checker.calculateSimilarity();

        assertionsUtils(fingerprintNames, trafficPatternNames, result);
    }

    @Test
    void checkSimilarityOfJimmy(){
        List<String> fingerprintNames = new ArrayList<>();
        List<String> trafficPatternNames = new ArrayList<>();
        List<Integer> segmentLengths = new ArrayList<>();

        fingerprintNames.add("jimmy_720p");
        fingerprintNames.add("jimmy_480p");

        trafficPatternNames.add("jimmy720p.csv");
        trafficPatternNames.add("jimmy480p.csv");
        trafficPatternNames.add("james720p.csv");
        trafficPatternNames.add("james480p.csv");
        trafficPatternNames.add("kovac720p.csv");
        trafficPatternNames.add("kovac480p.csv");
        trafficPatternNames.add("lampard720p.csv");
        trafficPatternNames.add("lampard480p.csv");
        trafficPatternNames.add("blind_fail720p.csv");
        trafficPatternNames.add("blind_fail480p.csv");
        trafficPatternNames.add("cake720p.csv");
        trafficPatternNames.add("cake480p.csv");
        trafficPatternNames.add("cats720p.csv");
        trafficPatternNames.add("cats480p.csv");
        trafficPatternNames.add("conan720p.csv");
        trafficPatternNames.add("conan480p.csv");
        trafficPatternNames.add("ski720p.csv");
        trafficPatternNames.add("ski480p.csv");
        trafficPatternNames.add("wind720p.csv");
        trafficPatternNames.add("wind480p.csv");

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        SimilarityChecker checker = new SimilarityChecker(fingerprintNames, trafficPatternNames, segmentLengths, 20000);

        List<List<Double>> result = checker.calculateSimilarity();

        assertionsUtils(fingerprintNames, trafficPatternNames, result);
    }

    @Test
    void checkSimilarityOfLampard(){
        List<String> fingerprintNames = new ArrayList<>();
        List<String> trafficPatternNames = new ArrayList<>();
        List<Integer> segmentLengths = new ArrayList<>();

        fingerprintNames.add("lampard_720p");
        fingerprintNames.add("lampard_480p");

        trafficPatternNames.add("lampard720p.csv");
        trafficPatternNames.add("lampard480p.csv");
        trafficPatternNames.add("james720p.csv");
        trafficPatternNames.add("james480p.csv");
        trafficPatternNames.add("kovac720p.csv");
        trafficPatternNames.add("kovac480p.csv");
        trafficPatternNames.add("jimmy720p.csv");
        trafficPatternNames.add("jimmy480p.csv");
        trafficPatternNames.add("blind_fail720p.csv");
        trafficPatternNames.add("blind_fail480p.csv");
        trafficPatternNames.add("cake720p.csv");
        trafficPatternNames.add("cake480p.csv");
        trafficPatternNames.add("cats720p.csv");
        trafficPatternNames.add("cats480p.csv");
        trafficPatternNames.add("conan720p.csv");
        trafficPatternNames.add("conan480p.csv");
        trafficPatternNames.add("ski720p.csv");
        trafficPatternNames.add("ski480p.csv");
        trafficPatternNames.add("wind720p.csv");
        trafficPatternNames.add("wind480p.csv");

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        SimilarityChecker checker = new SimilarityChecker(fingerprintNames, trafficPatternNames, segmentLengths, 20000);

        List<List<Double>> result = checker.calculateSimilarity();

        assertionsUtils(fingerprintNames, trafficPatternNames, result);
    }

    @Test
    void checkSimilarityOfBlindFail(){
        List<String> fingerprintNames = new ArrayList<>();
        List<String> trafficPatternNames = new ArrayList<>();
        List<Integer> segmentLengths = new ArrayList<>();

        fingerprintNames.add("blind_fail_720p");
        fingerprintNames.add("blind_fail_480p");

        trafficPatternNames.add("blind_fail720p.csv");
        trafficPatternNames.add("blind_fail480p.csv");
        trafficPatternNames.add("james720p.csv");
        trafficPatternNames.add("james480p.csv");
        trafficPatternNames.add("kovac720p.csv");
        trafficPatternNames.add("kovac480p.csv");
        trafficPatternNames.add("jimmy720p.csv");
        trafficPatternNames.add("jimmy480p.csv");
        trafficPatternNames.add("lampard720p.csv");
        trafficPatternNames.add("lampard480p.csv");
        trafficPatternNames.add("cake720p.csv");
        trafficPatternNames.add("cake480p.csv");
        trafficPatternNames.add("cats720p.csv");
        trafficPatternNames.add("cats480p.csv");
        trafficPatternNames.add("conan720p.csv");
        trafficPatternNames.add("conan480p.csv");
        trafficPatternNames.add("ski720p.csv");
        trafficPatternNames.add("ski480p.csv");
        trafficPatternNames.add("wind720p.csv");
        trafficPatternNames.add("wind480p.csv");

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        SimilarityChecker checker = new SimilarityChecker(fingerprintNames, trafficPatternNames, segmentLengths, 20000);

        List<List<Double>> result = checker.calculateSimilarity();

        assertionsUtils(fingerprintNames, trafficPatternNames, result);
    }

    @Test
    void checkSimilarityOfCake(){
        List<String> fingerprintNames = new ArrayList<>();
        List<String> trafficPatternNames = new ArrayList<>();
        List<Integer> segmentLengths = new ArrayList<>();

        fingerprintNames.add("cake_720p");
        fingerprintNames.add("cake_480p");

        trafficPatternNames.add("cake720p.csv");
        trafficPatternNames.add("cake480p.csv");
        trafficPatternNames.add("james720p.csv");
        trafficPatternNames.add("james480p.csv");
        trafficPatternNames.add("kovac720p.csv");
        trafficPatternNames.add("kovac480p.csv");
        trafficPatternNames.add("jimmy720p.csv");
        trafficPatternNames.add("jimmy480p.csv");
        trafficPatternNames.add("lampard720p.csv");
        trafficPatternNames.add("lampard480p.csv");
        trafficPatternNames.add("blind_fail720p.csv");
        trafficPatternNames.add("blind_fail480p.csv");
        trafficPatternNames.add("cats720p.csv");
        trafficPatternNames.add("cats480p.csv");
        trafficPatternNames.add("conan720p.csv");
        trafficPatternNames.add("conan480p.csv");
        trafficPatternNames.add("ski720p.csv");
        trafficPatternNames.add("ski480p.csv");
        trafficPatternNames.add("wind720p.csv");
        trafficPatternNames.add("wind480p.csv");

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        SimilarityChecker checker = new SimilarityChecker(fingerprintNames, trafficPatternNames, segmentLengths, 20000);

        List<List<Double>> result = checker.calculateSimilarity();

        assertionsUtils(fingerprintNames, trafficPatternNames, result);
    }

    @Test
    void checkSimilarityOfCats(){
        List<String> fingerprintNames = new ArrayList<>();
        List<String> trafficPatternNames = new ArrayList<>();
        List<Integer> segmentLengths = new ArrayList<>();

        fingerprintNames.add("cats_720p");
        fingerprintNames.add("cats_480p");

        trafficPatternNames.add("cats720p.csv");
        trafficPatternNames.add("cats480p.csv");
        trafficPatternNames.add("james720p.csv");
        trafficPatternNames.add("james480p.csv");
        trafficPatternNames.add("kovac720p.csv");
        trafficPatternNames.add("kovac480p.csv");
        trafficPatternNames.add("jimmy720p.csv");
        trafficPatternNames.add("jimmy480p.csv");
        trafficPatternNames.add("lampard720p.csv");
        trafficPatternNames.add("lampard480p.csv");
        trafficPatternNames.add("blind_fail720p.csv");
        trafficPatternNames.add("blind_fail480p.csv");
        trafficPatternNames.add("cake720p.csv");
        trafficPatternNames.add("cake480p.csv");
        trafficPatternNames.add("conan720p.csv");
        trafficPatternNames.add("conan480p.csv");
        trafficPatternNames.add("ski720p.csv");
        trafficPatternNames.add("ski480p.csv");
        trafficPatternNames.add("wind720p.csv");
        trafficPatternNames.add("wind480p.csv");

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        SimilarityChecker checker = new SimilarityChecker(fingerprintNames, trafficPatternNames, segmentLengths, 20000);

        List<List<Double>> result = checker.calculateSimilarity();

        assertionsUtils(fingerprintNames, trafficPatternNames, result);
    }

    @Test
    void checkSimilarityOfConan(){
        List<String> fingerprintNames = new ArrayList<>();
        List<String> trafficPatternNames = new ArrayList<>();
        List<Integer> segmentLengths = new ArrayList<>();

        fingerprintNames.add("conan_720p");
        fingerprintNames.add("conan_480p");

        trafficPatternNames.add("conan720p.csv");
        trafficPatternNames.add("conan480p.csv");
        trafficPatternNames.add("james720p.csv");
        trafficPatternNames.add("james480p.csv");
        trafficPatternNames.add("kovac720p.csv");
        trafficPatternNames.add("kovac480p.csv");
        trafficPatternNames.add("jimmy720p.csv");
        trafficPatternNames.add("jimmy480p.csv");
        trafficPatternNames.add("lampard720p.csv");
        trafficPatternNames.add("lampard480p.csv");
        trafficPatternNames.add("blind_fail720p.csv");
        trafficPatternNames.add("blind_fail480p.csv");
        trafficPatternNames.add("cake720p.csv");
        trafficPatternNames.add("cake480p.csv");
        trafficPatternNames.add("cats720p.csv");
        trafficPatternNames.add("cats480p.csv");
        trafficPatternNames.add("ski720p.csv");
        trafficPatternNames.add("ski480p.csv");
        trafficPatternNames.add("wind720p.csv");
        trafficPatternNames.add("wind480p.csv");

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        SimilarityChecker checker = new SimilarityChecker(fingerprintNames, trafficPatternNames, segmentLengths, 20000);

        List<List<Double>> result = checker.calculateSimilarity();

        assertionsUtils(fingerprintNames, trafficPatternNames, result);
    }

    @Test
    void checkSimilarityOfSki(){
        List<String> fingerprintNames = new ArrayList<>();
        List<String> trafficPatternNames = new ArrayList<>();
        List<Integer> segmentLengths = new ArrayList<>();

        fingerprintNames.add("ski_720p");
        fingerprintNames.add("ski_480p");

        trafficPatternNames.add("ski720p.csv");
        trafficPatternNames.add("ski480p.csv");
        trafficPatternNames.add("james720p.csv");
        trafficPatternNames.add("james480p.csv");
        trafficPatternNames.add("kovac720p.csv");
        trafficPatternNames.add("kovac480p.csv");
        trafficPatternNames.add("jimmy720p.csv");
        trafficPatternNames.add("jimmy480p.csv");
        trafficPatternNames.add("lampard720p.csv");
        trafficPatternNames.add("lampard480p.csv");
        trafficPatternNames.add("blind_fail720p.csv");
        trafficPatternNames.add("blind_fail480p.csv");
        trafficPatternNames.add("cake720p.csv");
        trafficPatternNames.add("cake480p.csv");
        trafficPatternNames.add("cats720p.csv");
        trafficPatternNames.add("cats480p.csv");
        trafficPatternNames.add("conan720p.csv");
        trafficPatternNames.add("conan480p.csv");
        trafficPatternNames.add("wind720p.csv");
        trafficPatternNames.add("wind480p.csv");

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        SimilarityChecker checker = new SimilarityChecker(fingerprintNames, trafficPatternNames, segmentLengths, 20000);

        List<List<Double>> result = checker.calculateSimilarity();

        assertionsUtils(fingerprintNames, trafficPatternNames, result);
    }

    @Test
    void checkSimilarityOfWind(){
        List<String> fingerprintNames = new ArrayList<>();
        List<String> trafficPatternNames = new ArrayList<>();
        List<Integer> segmentLengths = new ArrayList<>();

        fingerprintNames.add("wind_720p");
        fingerprintNames.add("wind_480p");

        trafficPatternNames.add("wind720p.csv");
        trafficPatternNames.add("wind480p.csv");
        trafficPatternNames.add("james720p.csv");
        trafficPatternNames.add("james480p.csv");
        trafficPatternNames.add("kovac720p.csv");
        trafficPatternNames.add("kovac480p.csv");
        trafficPatternNames.add("jimmy720p.csv");
        trafficPatternNames.add("jimmy480p.csv");
        trafficPatternNames.add("lampard720p.csv");
        trafficPatternNames.add("lampard480p.csv");
        trafficPatternNames.add("blind_fail720p.csv");
        trafficPatternNames.add("blind_fail480p.csv");
        trafficPatternNames.add("cake720p.csv");
        trafficPatternNames.add("cake480p.csv");
        trafficPatternNames.add("cats720p.csv");
        trafficPatternNames.add("cats480p.csv");
        trafficPatternNames.add("conan720p.csv");
        trafficPatternNames.add("conan480p.csv");
        trafficPatternNames.add("ski720p.csv");
        trafficPatternNames.add("ski480p.csv");

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_30_FPS);
        segmentLengths.add(LENGTH_30_FPS);

        segmentLengths.add(LENGTH_25_FPS);
        segmentLengths.add(LENGTH_25_FPS);

        SimilarityChecker checker = new SimilarityChecker(fingerprintNames, trafficPatternNames, segmentLengths, 20000);

        List<List<Double>> result = checker.calculateSimilarity();

        assertionsUtils(fingerprintNames, trafficPatternNames, result);
    }



    private void assertionsUtils(List<String> fingerprintNames, List<String> trafficPatternNames, List<List<Double>> result) {
        assertThat(result.get(0).get(0)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(0))
                .isLessThan(THRESHOLD);
        assertThat(result.get(0).get(1)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(1))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(2)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(2))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(3)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(3))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(4)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(4))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(5)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(5))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(6)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(6))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(7)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(7))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(8)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(8))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(9)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(9))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(10)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(10))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(11)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(11))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(12)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(12))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(13)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(13))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(14)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(14))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(15)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(15))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(16)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(16))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(17)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(17))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(18)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(18))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(0).get(19)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(0), trafficPatternNames.get(19))
                .isGreaterThan(THRESHOLD);

        assertThat(result.get(1).get(0)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(0))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(1)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(1))
                .isLessThan(THRESHOLD);
        assertThat(result.get(1).get(2)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(2))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(3)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(3))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(4)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(4))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(5)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(5))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(6)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(6))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(7)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(7))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(8)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(8))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(9)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(9))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(10)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(10))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(11)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(11))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(12)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(12))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(13)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(13))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(14)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(14))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(15)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(15))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(16)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(16))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(17)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(17))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(18)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(18))
                .isGreaterThan(THRESHOLD);
        assertThat(result.get(1).get(19)).as("%s Fingerprint compare with %s Trafficpattern", fingerprintNames.get(1), trafficPatternNames.get(19))
                .isGreaterThan(THRESHOLD);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 20; j++) {
                System.out.println(String.format("%s Fingerprint compare with %s Trafficpattern = %.16f", fingerprintNames.get(i), trafficPatternNames.get(j), result.get(i).get(j)));
            }
        }
    }

    @Test
    void checkSimilarityBetweenSameFingerprints() {
        for (int k = 1; k <= 8; k++) {
            List<Double> fingerprintAliasTrafficPattern1 = Utils.generateFingerprint("kovac_720p", k);
            List<Double> fingerprint2 = Utils.generateFingerprint("kovac_720p", k);
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





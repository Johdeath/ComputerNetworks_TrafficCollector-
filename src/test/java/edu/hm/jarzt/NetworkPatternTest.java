package edu.hm.jarzt;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class NetworkPatternTest {

    private static final double TOLERANCE = 1e-10;

    @Test
    void aggregatesNetworkTrafficOdd() {
        File file = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "trafficPattern01Odd.csv");
        Records records = new Records(file);
        List<Long> periods = records.aggregatesNetworkTraffic(200, 2);

        assertThat(periods)
                .hasSize(9)
                .containsExactly(720L, 453L, 1718L, 1025L, 883L, 279L, 1104L, 441L, 479L);

    }

    @Test
    void aggregatesNetworkTrafficEven() {
        File file = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "trafficPattern01Even.csv");
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
        List<String> files = new ArrayList<>();
        files.add("test_001.mp4");
        files.add("test_002.mp4");
        files.add("test_003.mp4");
        files.add("test_004.mp4");
        files.add("test_005.mp4");
        files.add("test_006.mp4");
        files.add("test_007.mp4");
        files.add("test_008.mp4");
        files.add("test_009.mp4");
        files.add("test_010.mp4");

        List<Long> results = Utils.getFileSizeInBytes(files, "test");

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


        List<Double> listR = Utils.differential(listA);

        assertThat(listR)
                .hasSize(4);

        assertThat(Math.abs(listR.get(0) - 0.0) <= TOLERANCE).isTrue();
        assertThat(Math.abs(listR.get(1) - (-0.7142857142857143)) <= TOLERANCE).isTrue();
        assertThat(Math.abs(listR.get(2) - 0.5) <= TOLERANCE).isTrue();
        assertThat(Math.abs(listR.get(3) - 0.6666666666666667) <= TOLERANCE).isTrue();

        List<Long> listResult = Utils.generateFingerprint(listA,2);

        assertThat(listResult).hasSize(2).containsExactly(9L, 8L);

        List<Double> listDiff = Utils.differential(listResult);

        assertThat(listDiff).hasSize(2);

        assertThat(Math.abs(listDiff.get(0) - 0.0) <= TOLERANCE).isTrue();
        assertThat(Math.abs(listDiff.get(1) - (-0.1111111111111111)) <= TOLERANCE).isTrue();

        List<Double> normalizedFingerprint = Utils.normalize(listDiff);

        assertThat(normalizedFingerprint).hasSize(2);

        assertThat(Math.abs(normalizedFingerprint.get(0) - 0.5) <= TOLERANCE).isTrue();
        assertThat(Math.abs(normalizedFingerprint.get(1) - (0.4722507650)) <= TOLERANCE).isTrue();

    }
}
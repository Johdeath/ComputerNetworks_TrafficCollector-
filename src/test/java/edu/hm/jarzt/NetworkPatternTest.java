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
        List<Integer> periods = records.aggregatesNetworkTraffic(200, 2);

        assertThat(periods)
                .hasSize(9)
                .containsExactly(720, 453, 1718, 1025, 883, 279, 1104, 441, 479);

    }

    @Test
    void aggregatesNetworkTrafficEven() {
        File file = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "trafficPattern01Even.csv");
        Records records = new Records(file);
        List<Integer> periods = records.aggregatesNetworkTraffic(200, 2);

        assertThat(periods)
                .hasSize(10)
                .containsExactly(720, 453, 1718, 1025, 883, 279, 1104, 441, 479, 750);

    }

    @Test
    void differential() {

        List<Integer> periods = new ArrayList<>();
        periods.add(250);
        periods.add(300);
        periods.add(600);
        periods.add(1800);
        periods.add(600);


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

        List<Long> results = Utils.getFileSizeInBytes(files);

        assertThat(results)
                .hasSize(10)
                .containsExactly(34354L, 27808L, 226974L, 241565L, 180024L, 143101L, 211129L, 124263L, 274893L, 194189L);
    }
}
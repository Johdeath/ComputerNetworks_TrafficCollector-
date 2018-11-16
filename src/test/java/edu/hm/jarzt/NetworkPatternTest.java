package edu.hm.jarzt;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class NetworkPatternTest {



    @Test
    void aggregatesNetworkTrafficOdd() {
        File file = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "trafficPattern01Odd.csv");
        Records records = new Records(file);
        List<Integer> periods = records.aggregatesNetworkTraffic(200,2);

        assertThat(periods)
                .hasSize(9)
                .containsExactly(720,453,1718,1025,883,279,1104,441,479);

    }

    @Test
    void aggregatesNetworkTrafficEven() {
        File file = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "trafficPattern01Even.csv");
        Records records = new Records(file);
        List<Integer> periods = records.aggregatesNetworkTraffic(200,2);

        assertThat(periods)
                .hasSize(10)
                .containsExactly(720,453,1718,1025,883,279,1104,441,479,750);

    }

    @Test
    void differential() {

        List<Integer> periods = new ArrayList<>();
        periods.add(250);
        periods.add(300);
        periods.add(600);
        periods.add(1200);
        periods.add(600);


        List<Double> pattern = Utils.differential(periods);

        assertThat(pattern)
                .hasSize(5)
                .containsExactly(0.0, 0.2, 1.0, 1.0, -0.5);

    }
}
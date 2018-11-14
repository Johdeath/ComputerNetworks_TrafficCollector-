package edu.hm.jarzt;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class RecordsTest {



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
}
package edu.hm.jarzt;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class RecordsTest {

    @Test
    void aggregatesNetworkTraffic() {
        File file = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "trafficPattern01 - Kopie.csv");
        Records records = new Records(file);
        List<Integer> periods = records.aggregatesNetworkTraffic(200,2);

        assertThat(periods)
               .hasSize(9)
               .containsExactly(720,453,1718,1025,883,279,1104,441,479);

    }
}
package edu.hm.jarzt;


import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Main {


    public static void main(String[] args) {
        File file = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "trafficPattern01.csv");
        Records records = new Records(file);

        List<Integer> periods = records.aggregatesNetworkTraffic(200,2);

        Utils.differential(periods).forEach(System.out::println);

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

        Utils.getFileSizeInBytes(files);

        for (Integer period:periods) {
            //System.out.println(period);
        }
    }
}

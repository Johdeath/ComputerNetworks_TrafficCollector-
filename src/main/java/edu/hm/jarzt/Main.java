package edu.hm.jarzt;


import java.io.*;
import java.util.List;


public class Main {


    public static void main(String[] args) {
        File file = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "trafficPattern01.csv");
        Records records = new Records(file);



        List<Integer> periods = records.aggregatesNetworkTraffic(200,2);

        for (Integer period:periods) {
            System.out.println(period);
        }
    }
}

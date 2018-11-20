package edu.hm.jarzt;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Main {


    public static void main(String[] args) {
        File file = new File(System.getProperty("user.dir") + File.separator + "trafficPattern" + File.separator + "enp0s_NightSchoolAllinBytes.csv");

        File file1 = new File(System.getProperty("user.dir") + File.separator + "videos" + File.separator +"james" + File.separator);

        new ArrayList<File>(Arrays.asList(Objects.requireNonNull(file1.listFiles()))).forEach(System.out::println);







        /*
        Records records = new Records(file);



        //Utils.differential(periods).forEach(System.out::println);

        List<String> files = new ArrayList<>();

        // Original --> 0.014002053987111884
        // Nation --> 0.02743390741768184
        // James --> 0.022346840972625116

        for (int i = 1; i <= 450; i++) {
            files.add("james_" + String.format("%03d", i) + ".mp4");
            //System.out.println("test_" + String.format("%03d",i) + ".mp4");
        }

        List<Long> list1 = Utils.getFileSizeInBytes(files, "james");
        //List<Double> list2 = Utils.differential(list1);
        List<Long> listResult = Utils.generateFingerprint(list1, 6);
        List<Double> listDiff = Utils.differential(listResult);
        List<Double> fingerprint = new ArrayList<> (Utils.normalize(listDiff));

        System.out.println("ASDFASDFASDFASDFASDFASDFSADFASDFASDFASDFASDFj-lkiosdöajsidojasdfijasdfiasdfjiasdfgj");

        // Traffic pattern CSV - File
        List<Long> periods = records.aggregatesNetworkTraffic(20000, 6);
        List<Double> pattern = Utils.differential(periods);
        List<Double> trafficPattern = new ArrayList<> (Utils.normalize(pattern));

        System.out.println(Utils.pdtw(fingerprint,trafficPattern));

*/
    }
}

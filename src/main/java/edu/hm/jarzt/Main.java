package edu.hm.jarzt;


import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Main {


    public static void main(String[] args) {
        File file = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "enp0s_NightSchoolAllinBytes.csv");
        Records records = new Records(file);


        //Utils.differential(periods).forEach(System.out::println);

        List<String> files = new ArrayList<>();

        for (int i = 1; i <= 141; i++) {
            files.add("trailer_" + String.format("%03d", i) + ".mp4");
            //System.out.println("test_" + String.format("%03d",i) + ".mp4");
        }
        List<Long> list1 = Utils.getFileSizeInBytes(files, "trailer");
        //List<Double> list2 = Utils.differential(list1);
        List<Long> listResult = Utils.generateFingerprint(list1, 6);
        List<Double> listDiff = Utils.differential(listResult);
        Utils.normalize(listDiff).forEach(System.out::println);

        System.out.println("ASDFASDFASDFASDFASDFASDFSADFASDFASDFASDFASDFj-lkiosdöajsidojasdfijasdfiasdfjiasdfgj");

        // Traffic pattern CSV - File
        List<Long> periods = records.aggregatesNetworkTraffic(20000, 6);
        List<Double> pattern = Utils.differential(periods);
        Utils.normalize(pattern).forEach(System.out::println);

    }
}

package edu.hm.jarzt;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Main {


    public static void main(String[] args) {
        File file = new File(System.getProperty("user.dir") + File.separator + "trafficPattern" + File.separator + "bigbugbunny1.csv");

        File pathToJamesVideoFiles = new File(System.getProperty("user.dir") + File.separator + "videos" + File.separator +"james" + File.separator);

        List<File> jamesVideoFiles = Arrays.asList(Objects.requireNonNull(pathToJamesVideoFiles.listFiles()));





        Records records = new Records(file);

        // BBB ohne audio
        // Original 0.014655912957392868
        // trailer 0.022610747772891644
        // james 0.02210123016744051



        // BIG bug bunny 0.03086809395955986
        // james 0.024741583421318682


        //Utils.differential(periods).forEach(System.out::println);

        List<String> files = new ArrayList<>();

        // Original --> 0.014002053987111884
        // Nation --> 0.02743390741768184
        // James --> 0.022346840972625116


        //ToDo move into method in Utils
        List<Long> list1 = Utils.getFileSizeInBytes(jamesVideoFiles);
        //List<Double> list2 = Utils.differential(list1);
        List<Long> listResult = Utils.generateFingerprint(list1, 6);


        List<Double> listDiff = Utils.differential(listResult);

        List<Double> fingerprint = new ArrayList<> (Utils.normalize(listDiff));
        //fingerprint.forEach(System.out::println);


        System.out.println("ASAaadsflkjasdfjklöasdljkfsajklfdasjklfjkldöfjklödf");

        //ToDo move into method in Utils
        // Traffic pattern CSV - File
        List<Long> periods = records.aggregatesNetworkTraffic(20000, 6);


        List<Double> pattern = Utils.differential(periods);

        List<Double> trafficPattern = new ArrayList<> (Utils.normalize(pattern));
        //trafficPattern.forEach(System.out::println);

        System.out.println(Utils.pdtw(fingerprint,trafficPattern));


    }
}

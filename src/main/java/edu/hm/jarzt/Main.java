package edu.hm.jarzt;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Main {


    public static void main(String[] args) {
        File file = new File(System.getProperty("user.dir") + File.separator + "trafficPattern" + File.separator + "bigbugbunny1.csv");
        File pathToJamesVideoFiles = new File(System.getProperty("user.dir") + File.separator + "videos" + File.separator +"bbb_ohne_audio" + File.separator);
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



        List<Double> fingerprint = Utils.generateFingerprint(jamesVideoFiles,6);
        System.out.println("ASAaadsflkjasdfjklöasdljkfsajklfdasjklfjkldöfjklödf");
        List<Double> trafficPattern = Utils.generateTrafficPattern(records,2000,6);


        System.out.println(Utils.pdtw(fingerprint,trafficPattern));


    }
}

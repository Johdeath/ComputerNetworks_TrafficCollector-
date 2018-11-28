package edu.hm.jarzt;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Main {


    public static void main(String[] args) {


        // BBB ohne audio
        // Original 0.014655912957392868
        // trailer 0.022610747772891644
        // james 0.02210123016744051



        // BIG bug bunny 0.03086809395955986
        // james 0.024741583421318682


        //Utils.differential(periods).forEach(System.out::println);


        // Original --> 0.014002053987111884
        // Nation --> 0.02743390741768184
        // James --> 0.022346840972625116





        List<Double> fingerprint = Utils.generateFingerprint("bbb_ohne_audio",6);
        System.out.println("-----------");
        List<Double> trafficPattern = Utils.generateTrafficPattern("bigbugbunny1.csv",2000,6);


        System.out.println(Utils.pdtw(fingerprint,trafficPattern));


    }
}

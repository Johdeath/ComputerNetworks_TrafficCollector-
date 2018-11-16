package edu.hm.jarzt;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static List<Double> differential(List<Integer> list) {
        List<Double> diffList = new ArrayList<>();
        diffList.add(0.0);

        for (int i = 1; i < list.size(); i++) {
            double result = (double) (list.get(i) - list.get(i - 1)) / list.get(i - 1);
            diffList.add(result);
        }

        return diffList;
    }

    public static List<Double> normalize(List<Double> list) {
        return list.stream().map(n -> (1 / (1 + Math.exp(-n)))).collect(Collectors.toList());
    }

    public static List<Long> getFileSizeInBytes(List<String> files) {

        List<Long> filesSizes = new ArrayList<>();


        filesSizes = files.stream().map(n -> {
            return new File(System.getProperty("user.dir") + File.separator + "videos" + File.separator + n).length();
        }).collect(Collectors.toList());

        return filesSizes;
    }
}

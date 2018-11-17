package edu.hm.jarzt;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static List<Double> differential(List<Long> list) {
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
        return files.stream().map(n -> new File(System.getProperty("user.dir") + File.separator + "videos" + File.separator + n)
                .length())
                .collect(Collectors.toList());
    }

    public static List<Long> generateFingerprint(List<Long> list, int segmentLength) {

        List<Double> listR = Utils.differential(list);
        List<Long> listResult = new ArrayList<>();

        //Todo perhaps cut the value (listR.size()/segmentLength) ends with trouble
        for(int i = 1; i<=listR.size()/segmentLength; i++) {
            double sum=0;
            for (int j = 1; j <= segmentLength; j++) { //sum
                int k = 1;
                double prod = 1;
                while (k <= (i - 1)*segmentLength+j) { //product
                    prod *= (listR.get(k-1)+1);
                    k++;
                }
                sum+=prod;
            }
            listResult.add((long)(sum*list.get(0)));
        }
        return listResult;
    }
}

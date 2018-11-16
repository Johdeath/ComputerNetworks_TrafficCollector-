package edu.hm.jarzt;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<Double> differential(List<Integer> list) {
        List<Double> diffList = new ArrayList<>();
        diffList.add(0.0);

        for (int i = 1; i < list.size(); i++) {
            double result = (double) (list.get(i) - list.get(i-1)) / list.get(i-1);
            diffList.add(result);
        }

        return diffList;
    }

}

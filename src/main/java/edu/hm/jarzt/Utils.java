package edu.hm.jarzt;



import java.io.File;
import java.util.*;
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
        return list.parallelStream().map(n -> (1 / (1 + Math.exp(-n)))).collect(Collectors.toList());
    }

    public static List<Long> getFileSizeInBytes(List<File> files) {
        return files.stream().map(File::length)
                .collect(Collectors.toList());
    }

    public static List<Long> generateFingerprint(List<Long> list, int segmentLength) {

        List<Double> listR = Utils.differential(list);
        List<Long> listResult = new ArrayList<>();

        //Todo perhaps cut the value (listR.size()/segmentLength) ends with trouble
        for (int i = 1; i <= listR.size() / segmentLength; i++) {
            double sum = 0;
            for (int j = 1; j <= segmentLength; j++) { //sum
                int k = 1;
                double prod = 1;
                while (k <= (i - 1) * segmentLength + j) { //product
                    prod *= (listR.get(k - 1) + 1);
                    k++;
                }
                sum += prod;
            }
            listResult.add((long) (sum * list.get(0)));
        }
        return listResult;
    }


    public static double pdtw(List<Double> template, List<Double> query) {

        Set<Double> distances = new TreeSet<>();

        for (int i = 0; i < template.size(); i++) {
            for (int j = i+1; j < template.size()-2; j++) { //Todo j < template.size() war mal j <= template.size()
                List<Double> subsequence = template.subList(i, j+2); //Todo (j+1) nachträglich hinzugefügt nicht sicher

                int n = query.size()+1;
                int m = subsequence.size()+1;

                double[][] matrix = new double[n][m];

                matrix[0][0]=0;

                //was zum Teufel!!!!!
                for (int k = 1; k < n; k++) {
                    matrix[k][0] = Double.MAX_VALUE;
                }
                for (int l = 1; l < m; l++) {
                    matrix[0][l] = Double.MAX_VALUE;
                }

                for (int k = 1; k < n; k++) {
                    for (int l = 1; l < m; l++) {
                        double cost = Math.abs(query.get(k-1)-subsequence.get(l-1));
                        if(l >= 2) {
                            matrix[k][l] = cost + Math.min(Math.min(matrix[k - 1][l] , matrix[k - 1][l - 1]), matrix[k - 1][l - 2]);
                        }else {
                            matrix[k][l] = cost + Math.min(matrix[k - 1][l], matrix[k - 1][l - 1]);
                        }
                    }
                }
                System.out.println("du sau1");
                distances.add(matrix[n-1][m-1]/n);

            }
        }

        return Collections.min(distances);
    }
}

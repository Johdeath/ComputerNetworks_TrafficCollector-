package edu.hm.jarzt;


import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    static List<Double> differential(List<Long> list) {
        List<Double> diffList = new ArrayList<>();
        diffList.add(0.0);

        for (int i = 1; i < list.size(); i++) {
            double result = (double) (list.get(i) - list.get(i - 1)) / list.get(i - 1);
            diffList.add(result);
        }

        return diffList;
    }


    static List<Double> normalize(List<Double> list) {
        return list.parallelStream().map(n -> (1 / (1 + Math.exp(-n)))).collect(Collectors.toList());
    }

    static List<Long> getFileSizeInBytes(List<File> files) {
        return files.stream().map(File::length)
                .collect(Collectors.toList());
    }

    static List<Long> generateFingerPrintWithLSecondSegments(List<Long> list, int segmentLength) {

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


    static double partialMatchingPdtw(List<Double> template, List<Double> query) {

        Set<Double> distances = new TreeSet<>();

        for (int q = 1; q <= template.size() ; q++) { //Todo j < template.size() war mal j <= template.size()
            List<Double> subSequence = template.subList(0, q); //Todo (j+1) nachträglich hinzugefügt nicht sicher

            int n = query.size();
            int m = subSequence.size();

            double[][] matrix = new double[n+1][m+1];

            matrix[0][0] = 0;

            //was zum Teufel!!!!!
            for (int i = 1; i < n; i++) {
                matrix[i][0] = Double.MAX_VALUE;
            }
            for (int i = 1; i < m; i++) {
                matrix[0][i] = Double.MAX_VALUE;
            }

            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= m; j++) {
                    double cost = Math.abs(query.get(i - 1) - subSequence.get(j - 1));
                    if (j >= 2) {
                        matrix[i][j] = cost + Math.min(Math.min(matrix[i - 1][j], matrix[i - 1][j - 1]), matrix[i - 1][j - 2]);
                    } else {
                        matrix[i][j] = cost + Math.min(matrix[i - 1][j], matrix[i - 1][j - 1]);
                    }
                }
            }

            distances.add(matrix[n][m] / n);
        }

        return Collections.min(distances);
    }

    public static List<Double> generateFingerprint(String videoFolderName, int segmentLength) {


        File pathToVideoFiles = new File(System.getProperty("user.dir") + File.separator + "videos" + File.separator + videoFolderName + File.separator);
        List<File> videoFiles = Arrays.asList(Objects.requireNonNull(pathToVideoFiles.listFiles()));

        List<Long> fingerPrintWithOneSecondSegments = Utils.getFileSizeInBytes(videoFiles);
        List<Long> fingerPrintWithLSecondSegments = Utils.generateFingerPrintWithLSecondSegments(fingerPrintWithOneSecondSegments, segmentLength);
        List<Double> differentialFingerprint = Utils.differential(fingerPrintWithLSecondSegments);
        return (Utils.normalize(differentialFingerprint));
    }

    public static List<Double> generateTrafficPattern(String trafficPatternCSVName, int threshold, int segmentLenght) {

        File file = new File(System.getProperty("user.dir") + File.separator + "trafficPattern" + File.separator + trafficPatternCSVName);
        Records records = new Records(file);
        List<Long> traffic = records.aggregatesNetworkTraffic(20000, 6);
        List<Double> differentialTraffic = Utils.differential(traffic);
        return Utils.normalize(differentialTraffic);
    }


}

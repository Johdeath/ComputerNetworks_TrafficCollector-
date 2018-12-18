package edu.hm.jarzt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimilarityChecker {
    private List<String> finterprintNames;
    private List<String> trafficpatternNames;
    private int segmentLength;
    private int threshold;

    public SimilarityChecker(List<String> finterprintNames, List<String> trafficpatternNames, int segmentLength, int threshold) {
        this.finterprintNames = finterprintNames;
        this.trafficpatternNames = trafficpatternNames;
        this.segmentLength = segmentLength;
        this.threshold = threshold;
    }

    public void setSegmentLength(int segmentLength) {
        this.segmentLength = segmentLength;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setFinterprintNames(List<String> finterprintNames) {
        this.finterprintNames = finterprintNames;
    }

    public void setTrafficpatternNames(List<String> trafficpatternNames) {
        this.trafficpatternNames = trafficpatternNames;
    }

    public List<List<Double>> calculateSimularity() {
        List<List<Double>> endResult = new ArrayList<>();

        for (String fingerprintName : finterprintNames) {
            List<Double> partResult = new ArrayList<>();
            for (String trafficpatternName : trafficpatternNames) {
                List<Double> queryJames = Utils.generateTrafficPattern(trafficpatternName, threshold, segmentLength);
                List<Double> templateJames = Utils.generateFingerprint(fingerprintName, segmentLength);
                List<Double> subseqenz;
                List<Double> result = new ArrayList<>();

                for (int i = 0; i < templateJames.size(); i++) {
                    for (int j = i; j < templateJames.size(); j++) {
                        subseqenz = templateJames.subList(i, j + 1);
                        result.add(Utils.partialMatchingPdtwForTesing(templateJames, queryJames, subseqenz));
                    }
                }
                partResult.add(Collections.min(result));

            }

            endResult.add(partResult);

        }

        return endResult;
    }

}


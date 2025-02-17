package edu.hm.jarzt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimilarityChecker {
    private List<String> finterprintNames;
    private List<String> trafficpatternNames;
    private List<Integer> segmentLenghts;
    private int threshold;

    public SimilarityChecker(List<String> finterprintNames, List<String> trafficpatternNames, List<Integer> segmentLengths, int threshold) {
        this.finterprintNames = finterprintNames;
        this.trafficpatternNames = trafficpatternNames;
        this.segmentLenghts = segmentLengths;
        this.threshold = threshold;
    }

    public void setSegmentLength(List<Integer> segmentLenghts) {
        this.segmentLenghts = segmentLenghts;
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

    public List<List<Double>> calculateSimilarity() {
        List<List<Double>> endResult = new ArrayList<>();
        int indexOuter = 0;
        for (String fingerprintName : finterprintNames) {
            List<Double> partResult = new ArrayList<>();
            int indexInner = 0;
            for (String trafficPatternName : trafficpatternNames) {
                List<Double> trafficPattern = Utils.generateTrafficPattern(trafficPatternName, threshold, segmentLenghts.get(indexInner));
                List<Double> fingerprints = Utils.generateFingerprint(fingerprintName, segmentLenghts.get(indexOuter));

                partResult.add(Utils.partialMatchingPdtw(fingerprints, trafficPattern));

                indexInner++;
            }
            indexOuter++;
            endResult.add(partResult);
        }
        return endResult;
    }

}


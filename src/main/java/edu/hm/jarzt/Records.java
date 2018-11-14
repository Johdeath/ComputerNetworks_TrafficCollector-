package edu.hm.jarzt;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Records {
    private List<Record> records = new ArrayList<>();

    public Records(List<Record> records) {
        this.records = records;
    }

    Records(File csvFile) {
        Reader in;

        System.out.println(csvFile.toString());
        CSVParser csvRecords;
        try {
            in = new FileReader(csvFile);
            csvRecords = CSVFormat.EXCEL.withHeader().parse(in);
            for (CSVRecord record : csvRecords) {
                int time = Integer.parseInt(record.get("Interval start"));
                int dataAmount = Integer.parseInt(record.get("Alle Pakete"));
                this.records.add(new Record(time, dataAmount));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    List<Integer> aggregatesNetworkTraffic(int threshold, int maxInterval) {

        MyConsumer consumer =records.stream()
                .map(Record::getDataAmount)
                .collect(() -> new MyConsumer(maxInterval,threshold),MyConsumer::accept,MyConsumer::combine);
        return consumer.toList();
    }
}


/*
List<Integer> aggregatesNetworkTraffic(int threshold, int maxInterval) {
        List<Integer> periods = new ArrayList<Integer>();
        int startTime = 0;
        int sum = 0;
        for (int currentTime = 0; currentTime < records.size(); currentTime++) {
            if (records.get(currentTime).getDataAmount() > threshold) {
                if (currentTime - startTime >= maxInterval) {
                    startTime = currentTime;
                    periods.add(sum);
                    sum = records.get(currentTime).getDataAmount();
                } else {
                    sum += records.get(currentTime).getDataAmount();
                }
            }
        }
        periods.add(sum);
        return periods;
    }
 */
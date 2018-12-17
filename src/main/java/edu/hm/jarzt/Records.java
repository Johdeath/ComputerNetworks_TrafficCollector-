package edu.hm.jarzt;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;

public class Records {
    private List<Record> records = new ArrayList<>();

    public Records(List<Record> records) {
        this.records = records;
    }

    Records(File csvFile) {
        Reader in;
        CSVParser csvRecords;
        try {
            in = new FileReader(csvFile);
            csvRecords = CSVFormat.EXCEL.withHeader().parse(in);
            for (CSVRecord record : csvRecords) {
                int time = Integer.parseInt(record.get("Interval start"));
                //long dataAmount = Long.valueOf(record.get("Alle Pakete"));
                long dataAmount = new BigDecimal(record.get("Alle Pakete")).longValueExact();

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

    List<Long> aggregatesNetworkTraffic(int threshold, int segmentLength) {
        List<Long> periods = new ArrayList<>();
        int startTime = 0;
        long sum = 0L;
        for (int currentTime = 0; currentTime < records.size(); currentTime++) {
            if (currentTime - startTime >= segmentLength-1) {
                if (records.get(currentTime).getDataAmount() > threshold) {
                    sum += records.get(currentTime).getDataAmount();
                }
                startTime = currentTime+1;
                periods.add(sum);
                sum = 0;
            } else {
                if (records.get(currentTime).getDataAmount() > threshold) {
                    sum += records.get(currentTime).getDataAmount();
                }
            }
        }
        periods.add(sum);
        return periods;
    }
}

package edu.hm.jarzt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class MyConsumer implements IntConsumer {


    private int currentTime  = 0;
    private int startTime = 0;
    private int maxInterval;
    private int sum =0;
    private List<Integer> list = new ArrayList<>();
    private int threshold;


     MyConsumer(int maxInterval, int threshold) {
        this.maxInterval = maxInterval;
        this.threshold = threshold;
    }

    @Override
    public void accept(int value) {
        if (value > threshold) {
            if (currentTime - startTime >= maxInterval) {
                startTime = currentTime;
                list.add(sum);
                sum = value;
            } else {
                sum += value;
            }
        }
      currentTime++;

    }


    @Override
    public IntConsumer andThen(IntConsumer after) {
        return after;
    }

     void combine(MyConsumer other) {
        System.out.println("Hello!");
        //for parralel Stream

    }

     List<Integer> toList() {
        if(sum != 0){
            list.add(sum);
            sum = 0;
        }
        return  list;
    }
}

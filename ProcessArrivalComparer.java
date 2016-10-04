package com.getcodesmart;

import java.util.Comparator;

/**
 * Created by scottanderson on 10/2/16.
 */
public class ProcessArrivalComparer implements Comparator<process> {
    @Override
    public int compare(process o1, process o2) {
        return o1.getArrivalTime() - o2.getArrivalTime(); // Ascending Order
    }
}

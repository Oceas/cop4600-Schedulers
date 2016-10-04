package com.getcodesmart;

import java.util.Comparator;

/**
 * Created by scottanderson on 10/2/16.
 */
public class ProcessComparer implements Comparator<CustomProcess> {
    @Override
    public int compare(CustomProcess o1, CustomProcess o2) {
        return o1.getArrivalTime() - o2.getArrivalTime(); // Ascending Order
    }
}

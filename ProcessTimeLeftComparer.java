package com.getcodesmart;

import java.util.Comparator;

/**
 * Created by scottanderson on 10/2/16.
 */
public class ProcessTimeLeftComparer implements Comparator<CustomProcess> {
    @Override
    public int compare(CustomProcess o1, CustomProcess o2) {
        return o1.getTimeLeft() - o2.getTimeLeft(); // Ascending Order
    }
}

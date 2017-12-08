package com.org.nagradcore;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 2017/12/1/001.
 */

public class Lists {
    private Lists() {
    }
    public static <E> ArrayList<E> newArrayList(E... elements) {
        int capacity = computeArrayListCapacity(elements.length);
        ArrayList list = new ArrayList(capacity);
        Collections.addAll(list, elements);
        return list;
    }
    static int computeArrayListCapacity(int arraySize) {
        return Ints.saturatedCast(5L + (long)arraySize + (long)(arraySize / 10));
    }
}

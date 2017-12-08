package com.org.nagradcore;

/**
 * Created by Administrator on 2017/12/1/001.
 */

public class Ints {
    public static final int BYTES = 4;
    public static final int MAX_POWER_OF_TWO = 1073741824;

    private Ints() {
    }
    public static int saturatedCast(long value) {
        return value > 2147483647L?2147483647:(value < -2147483648L?-2147483648:(int)value);
    }
}

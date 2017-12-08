package com.org.nagradcore;

/**
 * Created by Administrator on 2017/12/1/001.
 */

public class SpatialException extends RuntimeException{
    public SpatialException() {
    }

    public SpatialException(String message) {
        super(message);
    }

    public SpatialException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpatialException(Throwable cause) {
        super(cause);
    }
}

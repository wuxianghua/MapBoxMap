package com.org.mylibrary.yichemap.http;

import java.io.Serializable;

/**
 * Created by stone on 2017/7/13.
 */

public class HttpResult implements Serializable{
    public int State;
    public String Data;

    public HttpResult(){
        Data = "";
    }
}

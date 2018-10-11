package com.joe.app.outbound.data.event;

/**
 * Created by Joe on 2016/6/9.
 * Email-joe_zong@163.com
 */
public class ScanResultEvent {
    public String result;

    public ScanResultEvent(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}

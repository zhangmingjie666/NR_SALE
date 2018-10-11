package com.joe.app.outbound.data.model;

import java.io.Serializable;

/**
 * Created by Joe on 2016/6/8.
 * Email-joe.zong@xiaoniubang.com
 */
public class EmployeeBean implements Serializable{
    public String id;
    public String name;

    public EmployeeBean(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

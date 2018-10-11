package com.joe.app.outbound.data.model;

import java.io.Serializable;

/**
 * Created by Joe on 2016/6/7.
 * Email-joe_zong@163.com
 */
public class SaleSendOrderBean implements Serializable{

    /**
     * id : 20
     * billdate : 2016-06-06
     * customcode : 20151113-1274
     * plan_quantity : 1000
     * style : Y001
     * customer_name : 顺嘉
     * material : 0007／20D400T涤塔夫
     * color : H0005／天蓝
     * craft : 染色+涂层
     */

    public String id;
    public String billdate;
    public String customcode;
    public String plan_quantity;
    public String style;
    public String customer_name;
    public String material;
    public String quantity_string;
    public String color;
    public String craft;
}

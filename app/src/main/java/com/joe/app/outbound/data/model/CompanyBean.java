package com.joe.app.outbound.data.model;

import java.io.Serializable;

/**
 * @author MJ@ZHANG
 * @package: com.example.nrbzms17.data.model
 * @filename CompanyBean
 * @date on 2018/8/27 9:56
 * @descibe TODO
 * @email zhangmingjie@huansi.net
 */
public class CompanyBean implements Serializable {
    public String id;
    public String fullname;

    public CompanyBean(String id, String fullname) {
        this.id = id;
        this.fullname = fullname;
    }
}

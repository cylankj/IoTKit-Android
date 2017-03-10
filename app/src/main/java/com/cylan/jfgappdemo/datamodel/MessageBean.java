package com.cylan.jfgappdemo.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lxh on 16-8-9.
 */
public class MessageBean implements Serializable {
    public ArrayList<String> urls;
    public long time;
    public String date;
    public long version;
}

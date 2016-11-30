package com.cylan.jfgappdemo.datamodel;

import java.io.Serializable;

/**
 * Created by lxh on 16-8-4.
 */
public class BindDevBean implements Serializable {
    /**
     * The Ssid.
     */
    public String ssid = "";
    /**
     * The Cid.
     */
    public String cid = "";
    /**
     * The Mac.
     */
    public String mac = "";
    /**
     * The Version.
     */
    public String version = "";
    /**
     * The Ip.
     */
    public String ip;
    /**
     * The Langage.
     */
    public int langage;
    /**
     * The Net id.
     */
    public int netId;

    /**
     * The Bind code.
     */
    public String bindCode;


    public int devNetType;

}

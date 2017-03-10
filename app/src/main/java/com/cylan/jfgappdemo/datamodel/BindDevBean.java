package com.cylan.jfgappdemo.datamodel;

import java.io.Serializable;

/**
 * 添加设备界面。
 * 简单描述下逻辑。
 * 1.扫描出设备发出的SSID，我司设备发出的SSID前缀为 DOG- 。
 * 2.连接到此设备上。
 * 3.连接成功后，发送配置命令,命令回应在 onLocalMessage 接口返回,需要用 msgpack 包来解析byte[]。
 * 4.发送命令如下：
 * send: ping , recv: ping_ack; 确认收到回复消息,否则1s 发一次，直到收到，才发送下一个命令。
 * send: fping ,recv: fping_ack; 确认收到回复消息,否则1s 发一次，直到收到，才发送下一个命令。
 * send: setLanguage, send: setServerAddress.
 * 最后是 send: setWifiCfg .注意 WifiCfg 一定要最后发。因为设备端收到后就会关闭ap，与手机断开连接。
 * 5. 连上正常可以上网的wifi 。
 * 6. 登陆成功后发送   JfgAppCmd.getInstance().bindDevice(cid,code);
 * 7. 在onResult 的回调中判断绑定是否成功。
 * <p>
 * 可以根据各自的界面实现逻辑
 * Created by lxh on 16-8-4.
 */
public class BindDevBean implements Serializable {
    /**
     * The Ssid.
     */
    public String ssid = "";
    /**
     * The sn.
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

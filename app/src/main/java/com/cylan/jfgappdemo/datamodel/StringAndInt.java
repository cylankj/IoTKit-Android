package com.cylan.jfgappdemo.datamodel;

import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;

/**
 * Created by lxh on 16-7-27.
 */
@Message
public class StringAndInt extends BaseDataPoint{
    /**
     * The String value.
     */
    @Index(0)
    public String strValue;
    /**
     * The Int value.
     */
    @Index(1)
    public int intValue;
}

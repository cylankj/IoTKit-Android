package com.cylan.jfgappdemo.datamodel;

import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;

/**
 * Created by lxh on 16-7-27.
 */
@Message
public class IntAndString extends BaseDataPoint{

    /**
     * The Int value.
     */
    @Index(0)
    public int intValue;
    /**
     * The String value.
     */
    @Index(1)
    public String strValue;
}

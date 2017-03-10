package com.cylan.jfgappdemo.datamodel;

import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;

/**
 * Created by lxh on 16-7-26.
 */
@Message
public class SingleString extends BaseDataPoint{
    /**
     * The String Value.
     */
    @Index(0)
    public String value;
}

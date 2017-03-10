package com.cylan.jfgappdemo.datamodel;

import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;

/**
 * Created by lxh on 16-8-11.
 */
@Message
public class MsgWarningInfo extends BaseDataPoint {

    /**
     * The Time.
     */
    @Index(0)
    public long time;
    /**
     * The Is recode.
     */
    @Index(1)
    public int isRecode;
    /**
     * The Files.
     */
    @Index(2)
    public int files;
    /**
     * The Type.
     */
    @Index(3)
    public int type;


}

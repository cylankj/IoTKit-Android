package com.cylan.jfgappdemo.datamodel;

import org.msgpack.MessagePack;
import org.msgpack.annotation.Ignore;
import org.msgpack.annotation.Message;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by lxh on 16-8-10.
 */
@Message
public class BaseDataPoint implements Serializable {

    /**
     * To bytes byte [ ].
     *
     * @return the byte [ ]
     */
    @Ignore
    public byte[] toBytes() {
        try {
            MessagePack msgpack = new MessagePack();
            return msgpack.write(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

package com.cylan.jfgappdemo.datamodel;

import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;

import java.io.Serializable;

/**
 * Created by tim on 17-1-16.
 */

@Message
public class PlayMultVideoData implements Serializable {
    @Index(0)
    public int isPlay;
    @Index(1)
    public int camId;
    @Index(2)
    public int ssrc;

    public PlayMultVideoData(int isPlay, int camId, int ssrc) {
        this.isPlay = isPlay;
        this.camId = camId;
        this.ssrc = ssrc;
    }


    public PlayMultVideoData(int isPlay, int camId) {
        this.isPlay = isPlay;
        this.camId = camId;
    }


    public PlayMultVideoData() {
    }

    @Override
    public String toString() {
        return "[" + isPlay + ":" + camId + ":" + ssrc + "]";
    }
}

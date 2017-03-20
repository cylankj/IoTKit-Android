package com.cylan.jfgappdemo.ui;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.cylan.socket.JFGSocket;
import com.cylan.utils.JfgMsgPackUtils;
import com.superlog.SLog;

import org.msgpack.annotation.Index;

import java.io.IOException;

/**
 * Created by Tim on 2017/3/17.
 */

public class JFGSocketImpl implements JFGSocket.JFGSocketCallBack {


    @org.msgpack.annotation.Message
    public class KeepAlive {
        @Index(0)
        public long id = 2502;
        @Index(1)
        public String caller="";
        @Index(2)
        public String callee="";
        @Index(3)
        public long seq = 9;

    }

    @Override
    public void OnConnected() {
        SLog.e("OnConnected");
        handler.sendEmptyMessage(103);

    }

    @Override
    public void OnDisconnected() {
        SLog.e("OnDisconnected");
            handler.sendEmptyMessage(104);
    }

    @Override
    public void OnMsgpackBuff(byte[] data) {
        SLog.e("OnMsgpackBuff "+new String(data));
        handler.sendEmptyMessage(102);
    }


    public long obj ;

    Handler handler;
    public JFGSocketImpl() {
        HandlerThread thread = new HandlerThread("jfgsocket");
        thread.start();
        handler = new Handler(thread.getLooper(),new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 100:
                         obj =  JFGSocket.InitSocket(JFGSocketImpl.this);
                        SLog.w("init-> "+obj);
                        handler.sendEmptyMessage(101);
                        break;
                    case 101:
                        SLog.w("Connect-> "+obj);
                        JFGSocket.Connect(obj,"yf.jfgou.com", (short) 443,true);
                        break;
                    case 102:
                        JFGSocket.Disconnect(obj);
                        break;
                    case 103:
                        try {
                            JFGSocket.SendMsgpackBuff(obj,JfgMsgPackUtils.pack(new KeepAlive()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 104:
                        JFGSocket.Release(obj);
                        break;

                }
                return false;
            }
        });
        handler.sendEmptyMessage(100);
    }
}

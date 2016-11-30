package com.cylan.jfgappdemo;

import com.cylan.entity.jniCall.JFGDPMsg;
import com.cylan.entity.jniCall.JFGDPMsgRet;

import java.util.ArrayList;

/**
 * Created by lxh on 16-7-7.
 */
public class JfgEvent {


    /**
     * use phone register
     */
    public static final int REGISTER_TYPE_PHONE = 0;
    /**
     * user mail register
     */
    public static final int REGISTER_TYPE_MAIL = 1;


    /**
     * The type On line state.
     */
    public static class OnLineState {
        /**
         * The Online.
         */
        public boolean online;

        /**
         * Instantiates a new On line state.
         *
         * @param online the online
         */
        public OnLineState(boolean online) {
            this.online = online;
        }
    }


    /**
     * The type Result event.
     */
    public static class ResultEvent {
        /**
         * The constant JFG_RESULT_VERIFY_SMS.
         */
        public static final int JFG_RESULT_VERIFY_SMS = 0;
        /**
         * The constant JFG_RESULT_REGISTER.
         */
        public static final int JFG_RESULT_REGISTER = 1;
        /**
         * The constant JFG_RESULT_LOGIN.
         */
        public static final int JFG_RESULT_LOGIN = 2;
        /**
         * The constant JFG_RESULT_BINDDEV.
         */
        public static final int JFG_RESULT_BINDDEV = 3;

        /**
         * The constant JFG_RESULT_UNBINDDEV.
         */
        public static final int JFG_RESULT_UNBINDDEV = 4;


        /**
         * The constant JFG_RESULT_UPDATE_ACCOUNT.
         */
        public static final int JFG_RESULT_UPDATE_ACCOUNT = 5;

        /**
         * 删除好友的结果
         */
        public static final int JFG_RESULT_DEL_FRIEND = 6;
        /**
         * 同意添加好友的结果
         */
        public static final int JFG_RESULT_CONSENT_ADD_FRIEND = 7;

        /**
         * 设置好友备注名
         */
        public static final int JFG_RESULT_SET_FRIEND_MARKNAME = 8;

        /**
         * 分享设备
         */
        public static final int JFG_RESULT_SHARE_DEVICE = 9;
        /**
         * 取消分享
         */
        public static final int JFG_RESULT_UNSHARE_DEVICE = 10;
        /**
         * 设置别名
         */
        public static final int JFG_RESULT_SET_DEVICE_ALIAS = 11;
        /**
         * 发送反馈的结果
         */
        public static final int JFG_RESULT_SEND_FEEDBACK = 12;
        /**
         * 设置DeviceToken的返回结果
         */
        public static final int JFG_RESULT_SET_DEVICE_TOKEN = 13;


        //------------------------------------------------------------------------

//        /**
//         * The Code.
//         */
//        public int code;
//        /**
//         * The Event.
//         */
//        public int event;

//        /**
//         * Instantiates a new Result event.
//         *
//         * @param event the event
//         * @param code  the code
//         */
//        public ResultEvent(int event, int code) {
//            this.code = code;
//            this.event = event;
//        }
    }


    /**
     * The type Del dp result.
     */
    public static class DelDpResult {
        /**
         * The Seq.
         */
        public long seq;
        /**
         * The Ret.
         */
        public int ret;

        /**
         * Instantiates a new Del dp result.
         *
         * @param seq the seq
         * @param ret the ret
         */
        public DelDpResult(long seq, int ret) {
            this.seq = seq;
            this.ret = ret;
        }
    }


    /**
     * The type Robot msg ack.
     */
    public static class RobotMsgAck {
        /**
         * The Sn.
         */
        public int sn;

        /**
         * Instantiates a new Robot msg ack.
         *
         * @param sn the sn
         */
        public RobotMsgAck(int sn) {
            this.sn = sn;
        }
    }


    /**
     * The type Local msg.
     */
    public static class LocalMsg {
        /**
         * The Ip.
         */
        public String ip;
        /**
         * The Port.
         */
        public int port;
        /**
         * The Data.
         */
        public byte[] data;

        /**
         * Instantiates a new Local msg.
         *
         * @param ip   the ip
         * @param port the port
         * @param data the data
         */
        public LocalMsg(String ip, int port, byte[] data) {
            this.ip = ip;
            this.port = port;
            this.data = data;
        }

        @Override
        public String toString() {
            return ip + ":" + port + "[" + new String(data) + "]";
        }
    }

    /**
     * The type Sms code result.
     */
    public static class SmsCodeResult {
        /**
         * The Error.
         */
        public int error;
        /**
         * The Token.
         */
        public String token;

        /**
         * Instantiates a new Sms code result.
         *
         * @param error the error
         * @param token the token
         */
        public SmsCodeResult(int error, String token) {
            this.error = error;
            this.token = token;
        }
    }


    /**
     * The type Roboto sync data.
     */
    public static class RobotoSyncData {
        /**
         * The From dev.
         */
        public boolean fromDev;
        /**
         * The Identity.
         */
        public String identity;
        /**
         * The List.
         */
        public ArrayList<JFGDPMsg> list;

        /**
         * Instantiates a new Roboto sync data.
         *
         * @param fromDev  the from dev
         * @param identity the identity
         * @param list     the list
         */
        public RobotoSyncData(boolean fromDev, String identity, ArrayList<JFGDPMsg> list) {
            this.fromDev = fromDev;
            this.identity = identity;
            this.list = list;
        }
    }

    public static class RobotoSetDataRsp {
        public long seq;
        public ArrayList<JFGDPMsgRet> dataList;

        public RobotoSetDataRsp(long seq, ArrayList<JFGDPMsgRet> dataList) {
            this.seq = seq;
            this.dataList = dataList;
        }
    }
}

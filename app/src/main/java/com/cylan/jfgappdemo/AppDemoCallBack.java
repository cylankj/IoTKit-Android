package com.cylan.jfgappdemo;

import android.os.SystemClock;
import android.widget.Toast;

import com.cylan.entity.jniCall.JFGAccount;
import com.cylan.entity.jniCall.JFGDPMsg;
import com.cylan.entity.jniCall.JFGDPMsgCount;
import com.cylan.entity.jniCall.JFGDPMsgRet;
import com.cylan.entity.jniCall.JFGDevice;
import com.cylan.entity.jniCall.JFGDoorBellCaller;
import com.cylan.entity.jniCall.JFGFeedbackInfo;
import com.cylan.entity.jniCall.JFGFriendAccount;
import com.cylan.entity.jniCall.JFGFriendRequest;
import com.cylan.entity.jniCall.JFGHistoryVideo;
import com.cylan.entity.jniCall.JFGHistoryVideoErrorInfo;
import com.cylan.entity.jniCall.JFGMsgHttpResult;
import com.cylan.entity.jniCall.JFGMsgVideoDisconn;
import com.cylan.entity.jniCall.JFGMsgVideoResolution;
import com.cylan.entity.jniCall.JFGMsgVideoRtcp;
import com.cylan.entity.jniCall.JFGResult;
import com.cylan.entity.jniCall.JFGServerCfg;
import com.cylan.entity.jniCall.JFGShareListInfo;
import com.cylan.entity.jniCall.RobotMsg;
import com.cylan.entity.jniCall.RobotoGetDataRsp;
import com.cylan.jfgapp.interfases.AppCallBack;
import com.cylan.utils.JfgUtils;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 所有JFGSDK的回调都在此类中接收
 * demo中将收到的回调，通过EventBus进行分发。
 * 在需要接收的消息中注册EventBus事件即可。
 * <p>
 * Created by lxh on 16-7-7.
 */
public class AppDemoCallBack implements AppCallBack {


    @Override
    public void OnLocalMessage(String ip, int port, byte[] data) {
        EventBus.getDefault().post(new JfgEvent.LocalMsg(ip, port, data));
        SLog.d("");
    }

    @Override
    public void OnReportJfgDevices(JFGDevice[] devs) {
        SLog.d("");
        SystemClock.sleep(1000);
        EventBus.getDefault().post(devs);
    }

    @Override
    public void OnUpdateAccount(JFGAccount account) {
        EventBus.getDefault().post(account);
        SLog.d("");
    }

    @Override
    public void OnUpdateHistoryVideoList(JFGHistoryVideo video) {
        EventBus.getDefault().post(video.list);
        SLog.d("");
    }

    @Override
    public void OnServerConfig(JFGServerCfg cfg) {
        EventBus.getDefault().post(cfg);
        SLog.d("");
    }

    @Override
    public void OnUpdateHistoryErrorCode(JFGHistoryVideoErrorInfo info) {
        EventBus.getDefault().post(info);
        SLog.d("");
    }


    @Override
    public void OnLogoutByServer(int code) {
        SLog.d("");
    }

    @Override
    public void OnVideoDisconnect(JFGMsgVideoDisconn msg) {
        SLog.d("");
        EventBus.getDefault().post(msg);
    }


    @Override
    public void OnVideoNotifyResolution(JFGMsgVideoResolution msg) {
        SLog.d("");
        EventBus.getDefault().post(msg);
    }

    @Override
    public void OnVideoNotifyRTCP(JFGMsgVideoRtcp msg) {
        EventBus.getDefault().post(msg);
    }

    @Override
    public void OnHttpDone(JFGMsgHttpResult msg) {
        EventBus.getDefault().post(msg);
        SLog.d("");
    }

    @Override
    public void OnRobotTransmitMsg(RobotMsg msg) {
        EventBus.getDefault().post(msg);
        SLog.d("");
    }

    @Override
    public void OnRobotMsgAck(int sn) {
        SLog.d("");
        EventBus.getDefault().post(new JfgEvent.RobotMsgAck(sn));
    }


    @Override
    public void OnRobotDelDataRsp(long seq, String peer, int ret) {
        SLog.d(peer);
        EventBus.getDefault().post(new JfgEvent.DelDpResult(seq, ret));
    }

    @Override
    public void OnRobotGetDataRsp(RobotoGetDataRsp dataRsp) {
        SLog.d("");
        EventBus.getDefault().post(dataRsp);
    }

    @Override
    public void OnRobotSetDataRsp(long seq, ArrayList<JFGDPMsgRet> dataList) {
        SLog.d("");
        EventBus.getDefault().post(new JfgEvent.RobotoSetDataRsp(seq, dataList));
    }

    @Override
    public void OnRobotGetDataTimeout(long seq) {
        SLog.d("time out:" + seq);
    }

    @Override
    public ArrayList<JFGDPMsg> OnQuerySavedDatapoint(String identity, ArrayList<JFGDPMsg> dps) {
        SLog.d(identity);
        return null;
    }

    @Override
    public void OnlineStatus(boolean online) {
        SLog.d("" + online);
        EventBus.getDefault().post(new JfgEvent.OnLineState(online));
    }


    @Override
    public void OnDoorBellCall(JFGDoorBellCaller caller) {
        SLog.d("call form: " + caller.cid + " , url: " + caller.url);
        EventBus.getDefault().post(caller);
    }

    @Override
    public void OnOtherClientAnswerCall() {
        SLog.d("");
    }


    @Override
    public void OnRobotCountDataRsp(long seq, String peer, ArrayList<JFGDPMsgCount> list) {
        SLog.d(peer);
    }

    @Override
    public void OnResult(JFGResult result) {
        SLog.d("");
        EventBus.getDefault().post(result);
    }

    @Override
    public void OnRobotSyncData(boolean fromDev, String identity, ArrayList<JFGDPMsg> list) {
        SLog.d("");
        EventBus.getDefault().post(new JfgEvent.RobotoSyncData(fromDev, identity, list));
    }

    @Override
    public void OnSendSMSResult(int error, String token) {
        SLog.d("");
        EventBus.getDefault().post(new JfgEvent.SmsCodeResult(error, token));
    }

    @Override
    public void OnGetFriendListRsp(int ret, ArrayList<JFGFriendAccount> list) {
        EventBus.getDefault().post(list);
    }

    @Override
    public void OnGetFriendRequestListRsp(int ret, ArrayList<JFGFriendRequest> list) {
        EventBus.getDefault().post(list);
    }

    @Override
    public void OnGetFriendInfoRsp(int ret, JFGFriendAccount friendAccount) {
        EventBus.getDefault().post(friendAccount);
    }

    @Override
    public void OnCheckFriendAccountRsp(int ret, String targetAccount, String alias, boolean isFriend) {
        SLog.d("");
    }

    @Override
    public void OnShareDeviceRsp(int ret, String cid, String account) {
        SLog.d("");
    }

    @Override
    public void OnUnShareDeviceRsp(int ret, String cid, String account) {
        SLog.d("");
    }

    @Override
    public void OnGetShareListRsp(int ret, ArrayList<JFGShareListInfo> list) {
        SLog.d("");
    }

    @Override
    public void OnGetUnShareListByCidRsp(int ret, ArrayList<JFGFriendAccount> list) {
        SLog.d("");
    }

    @Override
    public void OnUpdateNTP(int unixTimestamp) {
        SLog.d("unixTimestamp : " + JfgUtils.DetailedDateFormat.format(unixTimestamp * 1000L));
    }

    @Override
    public void OnEfamilyMsg(byte[] bytes) {
        SLog.d("");
    }

    @Override
    public void OnForgetPassByEmailRsp(int ret, String email) {
        SLog.d("");
    }

    @Override
    public void OnGetAliasByCidRsp(int ret, String alias) {
        SLog.d("");
    }

    @Override
    public void OnGetFeedbackRsp(int ret, ArrayList<JFGFeedbackInfo> list) {
        SLog.d("");
    }

    @Override
    public void OnCheckDevVersionRsp(boolean hasNew, String url, String version, String tip, String md5) {
        SLog.d("");
    }
}

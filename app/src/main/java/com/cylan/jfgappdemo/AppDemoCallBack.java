package com.cylan.jfgappdemo;

import android.os.SystemClock;

import com.cylan.entity.JfgEvent;
import com.cylan.entity.jniCall.DevUpgradeInfo;
import com.cylan.entity.jniCall.JFGAccount;
import com.cylan.entity.jniCall.JFGDPMsg;
import com.cylan.entity.jniCall.JFGDPMsgCount;
import com.cylan.entity.jniCall.JFGDPMsgRet;
import com.cylan.entity.jniCall.JFGDPValue;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    public void OnRobotSetDataRsp(long seq, String peer, ArrayList<JFGDPMsgRet> dataList) {
        SLog.d("");
        EventBus.getDefault().post(new JfgEvent.RobotoSetDataRsp(seq, peer, dataList));
    }

    @Override
    public void OnRobotGetDataTimeout(long seq, String peer) {
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
        SLog.d("call form: " + caller.cid);
        EventBus.getDefault().post(caller);
    }

    @Override
    public void OnOtherClientAnswerCall(String cid) {
        SLog.d(cid);
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
    public void OnCheckDevVersionRsp(boolean hasNew, String url, String version, String tip, String md5, String cid) {
        SLog.d("");
    }

    @Override
    public void OnNotifyStorageType(int type) {
        SLog.i("storage type: " + type);
    }

    @Override
    public void OnRobotGetDataExRsp(long seq, String idtity, ArrayList<JFGDPMsg> dps) {
        SLog.i("OnRobotGetDataExRsp: seq = [" + seq + "], idtity = [" + idtity + "], dps = [" + dps.size() + "]");
    }

    @Override
    public void OnBindDevRsp(int ret, String cid) {
        SLog.i("Bind dev: " + ret + " , cid: " + cid);
    }


    @Override
    public void OnUnBindDevRsp(int ret, String cid) {
        SLog.i("UnBind dev: " + ret + " , cid: " + cid);
    }

    @Override
    public void OnGetVideoShareUrl(String url) {
        SLog.i("OnGetVideoShareUrl: url = [" + url + "]");
    }

    @Override
    public void OnForwardData(byte[] data) {

    }

    @Override
    public void OnMultiShareDevices(int ret, String device, String account) {

    }

    @Override
    public void OnCheckClientVersion(int ret, String url, int coerceUpgrade) {

    }

    @Override
    public void OnRobotCountMultiDataRsp(long seq, Object obj) {
        HashMap<String, JFGDPMsgCount[]> map = (HashMap<String, JFGDPMsgCount[]>) obj;
        if (map.isEmpty()) return;
        Iterator<Map.Entry<String, JFGDPMsgCount[]>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, JFGDPMsgCount[]> entry = it.next();
            JFGDPMsgCount[] dpc = entry.getValue();
            for (JFGDPMsgCount o : dpc) {
                SLog.i("[%s,%s]", entry.getKey(), o.toString());
            }
        }
    }

    @Override
    public void OnRobotGetMultiDataRsp(long seq, Object obj) {
        HashMap<String, HashMap<Long, JFGDPValue[]>> map = (HashMap<String, HashMap<Long, JFGDPValue[]>>) obj;
        if (map.isEmpty()) return;
//        Map.Entry<String,HashMap<Long,JFGDPValue[]>> entry = map.entrySet();
        for (Map.Entry<String, HashMap<Long, JFGDPValue[]>> entry : map.entrySet()) {
            String key = entry.getKey();
            for (Map.Entry<Long, JFGDPValue[]> e : entry.getValue().entrySet()) {
                Long dpid = e.getKey();
                for (JFGDPValue v : e.getValue()) {
                    SLog.i(key + " dpid: %d , dpv: %s",dpid , v.toString());
                }
            }
        }
    }

    @Override
    public void OnGetAdPolicyRsp(int i, long l, String s, String s1) {

    }

    @Override
    public void OnCheckTagDeviceVersionRsp(int ret, String cid, String tagVersion, String content, ArrayList<DevUpgradeInfo> resultList) {

    }

    @Override
    public void OnUniversalDataRsp(long seq, int mid, byte[] data) {

    }
}

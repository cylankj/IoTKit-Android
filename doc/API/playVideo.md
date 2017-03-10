# 播放录像
1.获取设备历史录像。
```java
JfgAppCmd.getInstance().getVideoList(device.uuid);
```
如果有历史录像返回，则在
```java
void OnUpdateHistoryVideoList(JFGHistoryVideo video);
```
中返回。

2.使用playVideo来播放在线视频。
```java
JfgAppCmd.getInstance().playVideo(device.uuid);
```

3.使用playHistVideo来播放历史视频,传入一个时间点。
```java
 JfgAppCmd.getInstance().playHistoryVideo(device.uuid, time);
```
4.使用setAudio来控制设备与手机的mic和声音的播放。
```java
JfgAppCmd.getInstance().setAudio(true, enableMic, enableVoice);
```
6.使用screenshot来截取当前视频画面。
```java
 JfgAppCmd.getInstance().screenshot(false, new CallBack() {
     @Override
     public void onSucceed(Object... objs) {
         //save pic
         Bitmap bm = (Bitmap) objs[0];
         savePic(bm);
    }

     @Override
     public void onFailure(Object... objs) {
         SLog.w("screenshot failure!");
    }
});
```
7.使用stopPlay来停止播放视频。
```java
JfgAppCmd.getInstance().stopPlay(device.uuid);
```
8.视频可以播放的回调。包含分辨率信息。
```java
void OnVideoNotifyResolution(JFGMsgVideoResolution msg);
```
收到此回调后，需要传入一个view来接收图像。
```java
JfgAppCmd.getInstance().setRenderRemoteView(videoView);
```

9.视频帧率信息的回调。
```java
 void OnVideoNotifyRTCP(JFGMsgVideoRtcp msg);
```

10.视频断开连接的回调。
```java
 void OnVideoDisconnect(JFGMsgVideoDisconn msg);
```

注意事项：
  播放前注意判断设备网络状态，和手机当前网络状态。
  
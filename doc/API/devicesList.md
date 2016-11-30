# 设备列表

1.接收服务器返回的此账号下的设备。

```java
void OnReprotJfgDevices(JFGDevice[] devices);
```

2.此时设备还没有属性，需要自行查询所需的属性。此处简单介绍查询与接收设备属性。

```java
 /** Gets data point. 
   * @param peer the peer
   */
 private void getDataPoint(String peer) {
    ArrayList<JFGDPMsg> dp = new ArrayList<>();
    JFGDPMsg msg = new JFGDPMsg(201, 0); // query dev network
    dp.add(msg);
    long seq = JfgAppCmd.getInstance().robotGetData(peer, dp, 1, false, 0);
    SLog.i(peer + " seq:" + seq);
 }

```

说明:

1.此处查询ID为201的消息。我司规定201为设备端网络信息。0代表查询的是当前的状态。

2.可以一次查询多条设备状态，所以第2个参数传入一个ArrayList。

3.参数1代表每个设备状态查询一条记录。

4.false为是否倒叙返回。

5.是否需要超时。

6.此api返回一个请求序列号。届时服务器异步返回也会带上请求序列号。

---

3.查询数据的返回

```java
void OnRobotGetDataRsp(long seq, String identity, ArrayList<ArrayList<JFGDPMsg>> idDataList);
```

此处简单的解析了201的数据。

```java
 public void OnRobotGetDataRsp(JfgEvent.RobotDataRspEvent event){
     SLog.i(event.identity + " seq: " + event.seq);
     int index = adapter.getPositionBySn(event.identity);
     if (index == -1) return;
     JFGDevice dev = adapter.getDevice()[index];
     if (dev == null) return;
     int len = event.list.size();
     for (int i = 0; i < len; i++) {
         ArrayList<JFGDPMsg> dpList = event.list.get(i);
         int size = dpList.size();
         for (int j = 0; j < size; j++) {
             JFGDPMsg dp = dpList.get(j);
             switch (dp.id) {
                 case 201: // net info IntAndString;
                 if (dp.packValue == null || dp.packValue.length == 0) break;
                 MessagePack msgpack = new MessagePack();  
             try {
                 IntAndString values = msgpack.read(dp.packValue, IntAndString.class);
                 SLog.i("intValue:" + values.intValue + " , strValue:" + values.strValue);
                 // baseValue
                 dev.base = new JFGDevBaseValue(); // 判断base 是否为空。
                 dev.base.netType = values.intValue;
                 dev.base.netName = values.strValue;
                 adapter.notifyItemChanged(index);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             break;
             }
        }
     }
 }

```



# JFG Android SDK Document

  此帮助文档主要介绍加菲狗Android SDK的大致逻辑。

#[接入流程](doc/API/SDK.md)必看,非常重要！！！

###[API目录](doc/API/SUMMARY.md)
---

###更新说明

#### 3.0.179
  1. setAudio(local,mic,speaker);
  2. 兼容多语言版本，一些接口添加language参数。
  3. 直接连接AP播放。

#### 3.0.169
  1. 避免线程不同步的问题，更改初始化方式。参考demo。[初始化](doc/API/SDK.md)
  2. 修复调用API时，seq为负数的问题。
  3. 获取签名的URL需要自行拼接（不需要带bucket），然后再调用签名的API。 getSignedCloudUrl(regionType,url)。
  详情看[服务器定义](https://github.com/cylankj/IoTKit-MsgDefine/blob/master/dpid_msg_define/server_cfg_msg_define.md#dpidcloudstorage--3)
  4. JfgDevice 添加regionType，vid 属性。
  5. 优化由于在SDK的回调线程中调用媒体API导致的线程卡住的问题。
  6. 由于和其他项目冲突，删除数据库。
  7. 修复setAudio()参数顺序错误的问题。

#### 3.0.160
  1. 新增接口，获取分享视频的url。
  2. onResult回调中添加消息序号。
  3. 绑定设备添加回调接口。

#### 3.0.159
  1. 新增一个接口，可以传入在android Manifests 中写入的三个meta 值，按需使用。如果没能从此接口读取到内容，将继续去读取Manifests中的meta.
  2. 新增一个检查设备是否有新版本的接口。
  3. 新增两个赛蓝特殊需求的查询接口。

#### 3.0.158
  1. 修复3.0.157 中绑定设备时的兼容性问题。
  2. 鉴于使用自动连接wifi的成功较低，demo中使用新的绑定逻辑。具体可以参考BindDevFragment这个类。

#### 3.0.157
  1. 为兼容多路视频预览，新加接口。
     enableRenderMultiRemoteView(boolean enable,int ssrc,Object view,JFGVideoRect rect);

 2. 单路视频预览接口改为
    enableRenderSingleRemoteView(boolean enable, Object view);

 3. JFGDevice 类加入vid 变量。获取报警图片的URL时，需要传入设备的VID。

#### 3.0.154
  1. 获取云存储路径时，需要传入VID
  2. 删除过时的openLogin接口，使用具有3个参数的openLogin接口。
  3. 预览多路远程视频时，需要传入ssrc标志。如果只有单路，ssrc，填0 即可。
  4. 精简接口，使用参数来区分是预览视频，还是取消预览。(enableRenderRemoteView)
  5. 修复已知bug。

#### 3.0.151
  1. 修复bug。
  2. 绑定第三方账号设置密码。
  3. ndk 优化。

#### 3.0.149 
  1. 添加检查账号是否注册的接口。
  2. 删除添加你为好友的信息。
  3. 修复已知的bug。

#### 3.0.148
  1. 只能在子线程初始化。
  2. 兼容3.0.147中的问题。

#### 3.0.147
  1. 添加一个JfgException
  2. openLogin 添加登录类别，兼容以前的接口
  3. 登录时判断sdk是否初始化，否则返回相应的错误号。稍后继续调用登录接口即可。
  4. 添加上传文件到云存储的接口。
  5. 修复已知的bug。 

#### 3.0.140
  1. 修复NTP时间问题。
  2. KV存储问题。
  
#### 3.0.139
  1. 优化底层代码。 
  
#### 3.0.138
  1. 兼容2.x版本的获取图片的URL
  2. 优化jni 代码。
  3. 修复一些已知bug。

####3.0.137
  1. 重命名获取云存储URL的API，getCloudUrlByType(int type ,int flag ,String fileName,String belong);
   第一个参数，查看JfgEnum.JFG_URL类中的定义。第二个flag，代表着服务器存储的位置（国内外）,由服务器下发。第三个fileName是具体的文件名，必须带后缀。
   第四个belong 为该资源归属哪个设备，如果有就填上设备ID，如果不归属设备，则填空字符串即可。
  2. 修复136版本中存在的bug。 

#### 3.0.136
  1. 更新用户头像。 (流程：使用updateAccountPortrait 接口更新头像成功后，
  调用JFGAccount类中的setPhoto方法，然后发送setAccount.最后服务器会更新JFGAccount中的photoUrl)
  2. 更改设备别名。 (流程：使用setAliasByCid设置设备名后，在OnReportJfgDevices查看具体的设备别名。)
  3. DP空值也会返回到上层。


#### 3.0.134 
  1. 消息透传添加消息来源，caller。
  2. JFGMsgHttpResult 的结果改为byte[]类型。

#### 3.0.133
  1. JFGAccount 类添加一个 resetFlag() 方法。用来清空内部的一个标志。需要在setAccount之后调用。
  2. 因为修改手机号必须要有token ，所以将两个参数合并。 setPhone(String phone,String token);

#### 3.0.132
  1. 添加一个MessagePack的工具类 ‘JfgMessagePackUtils’。 
   
#### 3.0.131
  1. 将DatePoint 中 id 的数据类型为 long 类型。

#### 3.0.130
  1. 修复setAccount中因为空值导致的崩溃。

#### 3.0.118
  1. 为了便于以后扩展，修改onResult回调接口，参数改为JFGResult 类。
  2. 忘记密码，重置密码，修改密码接口。
  3. 分享设备，分享列表。---分享类接口。

  

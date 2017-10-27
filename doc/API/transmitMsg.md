#消息透传

  1.主动发送透传消息

  ```java
    RobotMsg msg = new RobotMsg();
    JfgAppCmd.getInstance().robotTransmitMsg(msg);
  ```
  2.收到透传消息

  实现 AppCallBack 类；
  ```java
      OnRobotTransmitMsg(RobotMsg msg);
  ```

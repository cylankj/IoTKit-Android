#账号设置

描述：设置账号带有的哪些功能。

获取账号属性：
```java
    JfgAppCmd.getInstance().getAccount();
 ```
 账号回调：
 ```java
    void OnUpdateAccount(JFGAccount account);
 ```

 更改账号属性：
 注意，不需要new JFGAccount();
 使用 getAccount() 得到的对象进行改变。

 ```java
    account.setPhone("13800138000","toke").setAlias("中国移动");
    JfgAppCmd.getInstance().setAccount(account);
    account.resetFlag(); // 清空flag 。原因为有个内部变量flag，标志着需要改变某个属性。如果不清空，则每次都更改上次修改的内容。
    account.setEmal("13800138000@139.com").setEnablePush(ture);
    JfgAppCmd.getInstance().setAccount(account);  
    // 当需要改变多个属性的时候，可以多次设置。但不建议这样处理。最好是一次设置完所有属性后，在调用一次setAccount；
 ```

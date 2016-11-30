# User Login

登陆主要有两种方式，标准加菲狗客户端账号登陆和第三方账号登陆。

1.标准账号

```java
  JfgAppCmd.getInstance().login(String userName, String pwd);
```


2.第三方登录接口

```java
  JfgAppCmd.getInstance().openLogin(String openId,String token);
```

登陆的回调结果,判断result event为JfgEvent.ResultEvent.JFG_RESULT_LOGIN，接着判断code为0即为成功。

```java
 public void OnResult(JFGResult result);
```
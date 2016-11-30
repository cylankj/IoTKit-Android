# User Register

1.注册主要有两种方式。手机注册和邮箱注册。

2.手机注册：

（1） 首先需要获取到短信验证码。

```java
  JfgAppCmd.getInstance().sendCheckCode(account, JfgEnum.JFG_SMS_REGISTER);
```

 然后短信验证码的发送情况在此回调中.判断error是否为0.和token是否为空.
 ```java
     void OnSendSMSResult(int error, String token);
 ```

(2) 校验验证码是否有效，传入账号，验证码，和刚刚回调的token.
```java
 JfgAppCmd.getInstance().verifySMS(account, code, token);
```

 在OnResult中判断result event为 JfgEvent.ResultEvent.JFG_RESULT_VERIFY_SMS。接着判断code 为0即为验证码有效。

```java
public void OnResult(JFGResult result);
```

（3）注册，传入用户名，密码，注册方式，token。

```java
   JfgAppCmd.getInstance(). register(account, pwd, JfgEvent.REGISTER_TYPE_PHONE, token);
```

3.邮箱注册，只需改变注册方式为邮箱，验证码为空即可。


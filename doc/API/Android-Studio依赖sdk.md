###接入Sdk

###强势推荐使用Android Studio

>	此改动,是接入sdk流程的第二种选择,不需要,可停止阅读.


工程目录
```
Project
--build
--app
  --build
  --src
  --build.gradle(module的build.gradle)
  ...
build.gradle(工程的build.gradle)
...
...
```

*   编辑Project/build.gradle文件.

```
allprojects {
    repositories {
        jcenter()
        maven {
            url 'https://dl.bintray.com/cylan/Iot'
        }
    }
}
```

*   编辑Project/app/build.gradle文件

```
    //1.
    defaultConfig {
      ...//
        ndk {
            // 设置支持的SO库架构,
            abiFilters 'armeabi-v7a'//, 'x86'//, 'armeabi-v7a', 'x86_64', 'arm64-v8a'
        }
    }
    //2.
    compile 'com.cylan.iot.sdk:sdkLib:1.0.1'
```

*   代码中使用

```
   //在Application#onCreate后调用,不能在Application类中的static块使用.
    System.loadLibrary("jfgsdk");
```

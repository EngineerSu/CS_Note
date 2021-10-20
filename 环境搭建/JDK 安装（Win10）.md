# JDK 安装（Win10）

## 下载 JDK

**下载地址：**https://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html

点击下载按钮：

![img](D:\resource\notePics\141837092333398.png)

![img](D:\resource\notePics\60445A05-936A-4E82-9DCC-B629232DB657.jpg)

开始安装JDK：

![img](D:\resource\notePics\141837143894724.jpg)

可以设置为你想安装的路径。



## 环境变量配置

### 打开环境变量窗口

右键 **This PC(此电脑) -> Properties（属性） -> Advanced system settings（高级系统设置） -> Environment Variables（环境变量）...**

![img](D:\resource\notePics\1534410870-6587-3932295-dd57a38934b0c1af.png)

![img](D:\resource\notePics\1534410904-3787-3932295-4ab4c5e4655e83ff.png)

![img](D:\resource\notePics\1534410952-9821-3932295-53c3abde864d63a7.png)

![img](D:\resource\notePics\1534411011-6006-3932295-f3fe252e7a837300.png)



### 新建 JAVA_HOME 变量

### 点击 **New（新建）...** 按钮

![img](D:\resource\notePics\1534411060-2131-3932295-ef08fb7d0ef0760f.png)

输入:

```
变量名：JAVA_HOME
变量值：电脑上JDK安装的绝对路径
```

输入完毕后点击 OK。

![img](D:\resource\notePics\1534411114-3840-3932295-73be617cffdac223.png)

JDK 路径下必须能够看到如下的文件。

![img](D:\resource\notePics\1534411157-4157-3932295-b821f0e17887bcdf.png)



### 新建/修改 CLASSPATH 环境变量

/在已有的变量值后面添加：

```
变量名：CLASSPATH
变量值：.;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar;
```

点击 OK 保存。

![img](D:\resource\notePics\1534411198-2259-3932295-afe57d1caf93277c.png)



### 修改 Path 变量

由于 win10 的不同，当选中 **Path** 变量的时候，系统会很方便的把所有不同路径都分开了，不会像 win7 或者 win8 那样连在一起。

![img](D:\resource\notePics\1534411240-7448-3932295-515fd020c1e2fc4a.png)

新建两条路径：

```
%JAVA_HOME%\bin
%JAVA_HOME%\jre\bin
```

![img](D:\resource\notePics\1534411281-6565-3932295-08d84d125c71d0a8.png)



## 验证

快捷键：Win + R，输入 cmd 调起命令窗口，输入 java ，出现一连串的指令提示，说明配置成功。

![img](D:\resource\notePics\1534411321-5420-3932295-192d3a3152de471e.png)



输入 `java -version` 可以看到当前系统安装的 JDK 版本号

![image-20211009113131904](D:\resource\notePics\image-20211009113131904.png)


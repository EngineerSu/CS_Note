# Maven 的安装和使用（Win10）

## 安装

首先确保已安装 JDK，并且配置了 JAVA_HOME 的环境变量。

访问 [Maven官网]([Maven – Download Apache Maven](http://maven.apache.org/download.cgi)) 进行下载 zip 文件，解压到指定目录下，解压后相当于已安装。

比如下面装在 D:/dev/ 下

![image-20201206224949703](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206224949703.png)



## 设置环境变量

### M2_HOME 和 MAVEN_HOME

添加 M2_HOME 和 MAVEN_HOME 环境变量到 Windows 环境变量，并将其指向你的 Maven 文件夹，如下：

![image-20201206225056539](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206225056539.png)

（Maven 说只是添加 M2_HOME , 但一些项目仍引用 Maven 的文件夹 MAVEN_HOME, 因此，为了安全也把它添加进去。)



### 添加到环境变量 PATH

以上其实只是用两个环境变量代替 Maven 的安装路径，并没有把 Maven 的 bin 文件添加到系统路径里。

更新 PATH 变量，添加 Maven bin 文件夹到 PATH 中，如下：

![image-20201206225410924](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206225410924.png)



## 验证

快捷键：Win + R，输入 cmd 调起命令窗口，输入 ` mvn –version` ，出现 Maven 的版本信息则说明安装成功。



## 修改 settings.xml

Maven 管理了项目的所有依赖，因此在它的配置文件中，从哪里下载依赖，以及下载的依赖存放在什么位置，这两个设置是需要我们自定义指定的。

在 Maven 的安装目录下，进入 conf 目录，选择编辑 settings.xml 

### 本地仓库

在非第一行的位置添加以下标签：

```xml
<localRepository>D:\repository</localRepository>
```

它声明了 Maven 的本地仓库，Maven 下载的所有依赖包都能在这个目录里找到。建议这个目录后续不要用作其它。

### 远程仓库

在非第一行的位置添加以下标签：

```xml
<mirror>
    <id>alimaven</id>
    <mirrorOf>central</mirrorOf>
    <name>aliyun maven</name>
    <url>http://maven.aliyun.com/nexus/content/repositories/central/</url>
</mirror>
```

它声明了 Maven 的远程仓库，即 Maven 发现项目中的依赖文件在本地仓库找不到时，会访问远程仓库寻找并下载到本地仓库。



## IDEA 中设置 Maven 属性

### 修改当前 IDEA 项目的 Maven 配置

菜单栏 File - Settings - Build, Execution, Deployment - Build Tools - Maven，需要更改三项配置：

![image-20201206231118658](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206231118658.png)

分别是 Maven 的安装目录位置、settings.xml 的文件位置、Maven 本地仓库的位置



### 修改 IDEA 新项目的 Maven 配置

以上修改只对当前项目生效，当新建或新导入一个项目时，仍会发现 Maven 的配置还是默认配置。

菜单栏 File - New Projects Settings - Settings for New Projects

![image-20201206231406025](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206231406025.png)



在这里进行上一步提到的配置即可


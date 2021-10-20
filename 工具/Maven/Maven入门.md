### Maven入门

---

#### 1.基本概念

> MAVEN是基于POM(项目对象模型),以项目为对象,高效实现程序的可重用性.
>
> Maven项目相比于传统项目更"智能省心",它利用仓库一次性管理所有的jar包,不用每个项目单独搜索jar包

```
Maven的依赖管理:jar包单独仓库管理,会给每一个jar包提供一个"坐标"(索引/指针)
```

#### 2.Maven使用

##### (1)环境配置

````
JDK版本1.7+
1.增加系统变量
	变量名:MAVEN_HOME
	变量值:解压目录的路径
2.将配置的系统变量加入到环境变量
	%MAVEN_HOME%\bin
	%MAVEN_HOME%\lib
3.完成后,在安装目录的bin目录下,cmd命令"mvn -v"可以查看maven版本
````

##### (2)MAVEN的仓库配置

```
maven的仓库分为本地仓库 远程仓库(私服) 中央仓库(互联网共享)
```

##### 本地仓库的配置

```
修改maven解压目录/conf/settings.xml配置文件,添加本地仓库标签
	<localRepository>/path/to/local/repo</localRepository>
	// 将/path/to/local/repo修改为仓库的路径
	如:<localRepository>D:\resource\repository</localRepository>
```

##### 修改默认下载路径

```
因为默认的网址服务器在国外,推荐使用阿里云下载地址
修改maven解压目录/conf/settings.xml配置文件,160-165行,添加一个mirror标签

<mirror>
            <id>alimaven</id>
            <mirrorOf>central</mirrorOf>
            <name>aliyun maven</name>
            <url>http://maven.aliyun.com/nexus/content/repositories/central/</url>
</mirror>
```

##### (3)MAVEN项目目录结构

![maven目录结构](d:/resource/notePic/maven-dictionary.jpg)

#### 3.Maven命令

```
clean(清理,删除target目录)
compile(编译)
test(测试,并在cmd窗口输出测试结果) 
package(将项目打包并存储到target目录)
install(将项目打包并发布到本地仓库)
  如果要引用自己的maven项目包,就需要将其intall到仓库中才能依赖
deploy:将项目打包部署到远程仓库中
```

**打依赖树到本地**

```
mvn dependency:tree >/Users/jacksu/Downloads/dependencyTree.txt
```

作用：通过在依赖树txt中，可以搜索到一些引用的jar包版本，也可以查看每个依赖编译后的子依赖有哪些

#### 4.maven in eclipse

##### (1)m2e插件(maven to eclipse)

```
eclipse版本较高,自带这个插件
```

##### (2)在eclipse中配置maven版本

##### ![1546938364335](/d:/resource/notePic/1546938364335.png)(3)在eclipse中配置maven的settings.xml和本地仓库

![1546938555864](/d:/resource/notePic/1546938555864.png)

##### (4)构建索引

显示maven的仓库窗口

![1546938662727](/d:/resource/notePic/1546938662727.png)

将仓库视图中的本地仓库rebuild index

##### (5)创建maven工程

![1546942940462](/d:/resource/notePic/1546942940462.png)

创建的时候,勾选create a simple ... (保证生成正常的maven目录结构)

![1546943552384](/d:/resource/notePic/1546943552384.png)

注意:如果Packaging选择的是"war",则是javaweb项目.需要手动创建scr/main/webapp/WEB-INF/web.xml,并编写基本的web.xml

##### (6)在maven工程中导入jar包(添加jar包"坐标")

##### 编辑pom.xml文件:在"Dependencies"窗口,可视化搜索和添加jar包

![1546944151793](/d:/resource/notePic/1546944151793.png)

```
添加完jar包后,会自动在pom.xml中生成对应的标签(公司,项目,版本)
如果知道要导入哪些jar包,也可以直接复制已有的pom.xml中的标签内容

pom.xml中还需要一个<build>标签来调整配置JDK

注意添加jar包时要选择合适的scope,对于包名有冲突的jar包,scope选择"provided"
```

![1546947169808](/d:/resource/notePic/1546947169808.png)

##### (7)MAVEN与Struts2的结合

```
0.在webapp目录下创建WEB-INF/web.xml目录
1.在pom.xml中添加struts2需要的jar包
	导入一个struts-core.jar会关联导入一些依赖包(导入jar包时注意scope选择,有命名冲突的选择"provided",junit包scope选择"test")
	设置编译版本标签:调整项目Java Resources/Libraries下的JRE系统版本
2.在web.xml中配置Struts2需要的核心过滤器
3.相关代码正常编写在src/main/java目录中;web相关的jsp等内容在webapp目录中编写
4.struts.xml配置文件写在src/main/resources中
	在struts.xml中配置Action类
```

##### (8)项目执行

```
选择run as mavenbuild,goal命令:  tomcat:run;然后run即可
```



#### maven in IDEA

##### 设置maven路径

```
File->Settings->Build, Execute, Deployment->Build Tools->Maven
修改 Maven home directory: d:/software/apache-maven-3.5.0
修改 User settings file: D:\software\apache-maven-3.5.0\conf\settings.xml
Local repository 会被自动识别为 d:\maven\repository
```

##### pom.xml

```
maven项目的配置文件,导入jar包需要增加一个<dependency>标签(可以手动,但不建议)
在修改了pom.xml之后，IDEA上会弹出这么一个 Maven projects need to be imported，点击Import Changes,导入的jar包生效

每次新建Maven项目，或者pom.xml有改动，都会有这个提示，可以点击Enable Auto-Import，自动导入，省掉麻烦
```

##### 新建maven-web项目

```
1. 左边选中Maven
2. 勾选Create from archetype
3. 选中 org.apache.maven.archetypes:maven-archetype-webapp
4. 一直Next

maven web项目默认是没有java源代码目录的，所以需要手动创建，并设置其为源代码目录
右键main目录-> New->Directory->输入java->右键java->Mark Directory as-> Sources Root
这样就创建了存放java源文件的目录了
```

##### 配置tomcat

```
点击状态栏上的 Edit Configurations
然后点击+号,选择more,找到tomcat Servet,选择Local
然后在弹出来的tomcat配置框中,配置: 输入name和指定tomcat路径

还是在刚才那个tomcat配置框中,部署项目到tomcat中
1. 点击Deployment
2. 点击加号
3. 点击Artifact...
4. 自动生成 j2ee.war
然后确定,可以试着运行tomcat,查看命令窗口是否出现成功信息
```

##### IDEA导入maven项目

```
File->New->Project from existing sources 选择项目文件夹中的pom.xml文件导入. 然后一直next
	注意是选择pom.xml文件导入项目!!! 不是选择文件夹
```

##### IDEA对tomcat的调试设置

```
1.在Tomcat的Deploy对应的 Artifacts这里，需要选择tmall_ssm:war exploded, 不要选择第一个tmall_ssm:war.
因为选war的话，每次修改了jsp都要重新打包成war才能看到效果，不便于观察jsp修改后的效果
2.Tomcat的配置界面 On 'Update' action 是手动更新,On frame deactivation 是当idea失去焦点,这两个下拉选都选择Update classes and resources
3.运行Tomcat的时候，采用debug模式，这样 勾上 Update classes and resources 这一步导致的类自动更新就会引起Tomcat的reload，那么就不需要重新启动Tomcat也能看到效果了，便于修改代码和观察效果
```



#### Maven工程的父子目录层次

如图是vivo-schedule项目的Maven工程, 父工程中不需要src目录, 每个文件夹的名字可以和maven模块的项目名不同, 比如vivo-schedule是文件夹名, schedule-all是其maven模块的项目名. maven模块的项目名, 在pom.xml中即是<artifactId>标签的值.

![1560217705458](/d:/resource/notePic/vivo-schedule项目目录.png)



下图是maven子模块的包结构, 可以看到它们具有共同包层次"com.vivo.internet.schedule", 这是由父maven模块的groupId和artifactId决定. 而每个子模块的顶层包名就是它的模块文件夹名, 比如"common" 和 "console"

![1560217963979](/d:/resource/notePic/maven子模块的包结构.png)



下图是父maven模块的pom.xml文件中关于模块的配置,  artifactId标签就是模块名, 注意packaging是pom方式, module标签是maven子模块的文件夹名, 另外作为父模块它也是有parent标签的, 是继承spring的父模块(SpringBoot项目).

![1560218137011](/d:/resource/notePic/父maven模块的pom配置.png)



下图是maven子模块console关于模块的配置, 可以看到当继承了本地的父模块后, 它本身不再需要groupId和version标签, 一般子模块的packaging是jar, 对于web层(传统SSM,非SpringBoot), packaging是war.

![1560218426944](/d:/resource/notePic/子模块console的模块配置.png)


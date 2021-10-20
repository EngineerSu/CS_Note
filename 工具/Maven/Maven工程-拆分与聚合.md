### Maven工程-拆分与聚合

---

#### 1.父工程与子模块的拆分与聚合

```
父工程(maven project)
模块(maven module)
```

##### (1)父工程

```
父工程创建时,打包方式选择pom打包方式(表明是父工程)
父工程的目录结构不同于一般的maven模块项目,父工程中不需要编写代码
它的pom.xml文件导入的公共jar包,可能被继承它的模块使用
	继承模块能不能用父工程的jar包,取决于在父工程中引入jar包时选择的scope
```

##### (2)模块

```
需要选择"Parent Project"(父工程)
打包方式注意,不同模块方式不一样
	web层打war包
	其他层(如dao,service)打jar包
(模块创建完毕后,在父工程的pom.xml中会出现一个新的module标签,模块的pom.xml中会出现一个parent标签;也会出现一个子模块的文件夹)
```

#### 2.解决jar包冲突问题

```
引入的struts2包和hibernate包都含有(依赖)javasist包,但是它们依赖的版本不同.
一般保留高版本jar,对于低版本的冲突jar包,右键选择exclude(在pom.xml中会对应出现一个exclusion标签)
```

##### 依赖调节原则

> 没有手动处理冲突jar包时,项目会自动采取一定策略处理冲突

```
1.第一声明者优先原则:在pom.xml中先出现的jar包优先采用
2.路径近者优先原则:A依赖于B和C,C又依赖于B.1;则A采用B
```

##### 锁定版本

```
dependManagement标签中声明的dependencies标签中含有的jar包,即为声明jar包应该使用的版本.
(注意dependManagement标签只是声明锁定版本信息,但是不是jar包的引入)
还需要用dependencies标签引入jar包(对于已经声明锁定版本的jar包不需要再提供版本信息标签)
```

#### 3.在Maven中实现SSH的聚合

> 相关的实例代码和配置文件可以在java/resource/project/maven_ssh_crm中查看

##### (1)创建ssh_parent的maven project

```
创建父工程,打包方式选择pom,并引入通用的jar包
```

##### (2)创建ssh_dao的maven module

```
创建dao模块,打包方式选择jar,并且选择父工程:ssh_parent
它的类在src/main/java中,但是po(可持久化类)的hbm.xml配置文件则放在src/main/resource中,并且这些配置文件发包名与po类的包名保持一致;
hibernate.cfg.xml的配置文件放在src/main/resource中
添加applicationContext_dao.xml,放在src/main/resource中;Spring在dao中的部分(这部分配置文件需要读取hibernate.cfg.xml配置文件,用于整合hibernate)

备注:
1.如果在dao模块中测试,需要单独导入junit包(父工程导入的在这里不能传递)
2.在dao模块中测试,applicationContext_dao.xml的IOC运用,需要自己创建工厂,通过工厂获取实例
```

> applicationContext-dao.xml配置文件

![applicationContext-dao.xml配置文件](/Users/jacksu/Desktop/File/resource/image/notePics/applicationContext_dao.xml截图.jpg)

##### (3)创建ssh_service的maven module

```
创建service模块,打包jar方式,选择父工程:ssh-parent
因为service层需要使用dao层中的一些类(比如domain),因此在service层的pom.xml中导入dao的jar包(这里就体现了POM思想)
需要创建applicationContext-service.xml配置文件,配置文件中注入customerDao时,不需要再配置customerDao类的bean标签,因为在dao层已经配置过,它的jar包已经导入
```

> applicationContext-service.xml配置文件

![applicationContext-service.xml配置文件](/Users/jacksu/Desktop/File/resource/image/notePics/applicationContext-service.xml配置文件.png)

##### (4)创建ssh_web的maven module

```
创建service模块,打包war方式,选择父工程:ssh-parent
	因为是war打包,要在webapp目录下创建WEB-INF/web.xml
引入ssh-service的jar包
创建Action类(src/main/java)
创建applicationContext-action.xml(src/main/resources):Spring在Struts2(web层)的配置文件部分
创建struts.xml(src/main/resources)
	注意因为Spring已经接管了Action类,这里的bean标签的class值不再是类的全限定名,而是id
创建applicationContext.xml(src/main/resources):用import标签将dao,service和action三个Spring的配置文件分别引入,创建一个统一的Spring配置文件
在web.xml中加载applicationContext.xml配置文件的监听器和过滤器,Struts2的过滤器
```

```xml
<!-- applicationContext.xml整合 -->
<import resource="classpath:applicationContext-action.xml"/>
<import resource="classpath*:applicationContext-service.xml"/>
<import resource="classpath*:applicationContext-dao.xml"/>

<!-- classpath加*表示,这个配置文件可能再引用的jar包中 -->
```

##### (5)在自己tomcat中启动maven-ssh项目(而不是通过maven的插件启动)

```
和普通javaweb项目一样,首先将项目add到tomcat中,然后run即可
```

##### (6)Maven实现SSH模块聚合的总结

```
父工程ssh-parent + 模块(ssh-dao,ssh-service,ssh-web)
父工程的打包方式选择pom,dao和service模块打包选择jar,web模块打包选择war;并且模块在创建时要选择父工程
service模块引用依赖dao模块的jar包,web模块引用依赖service模块的jar包(依赖后,不仅是可以使用依赖包中的类,还可以使用依赖包中的配置文件)
关于配置文件:
	dao模块中需要po类的cfg.xml配置文件,hibernate.cfg.xml配置文件以及application-dao.xml配置文件
	service模块中只需要application-service.xml配置文件
	web模块中需要application-action.xml配置文件,struts.xml配置文件和application.xml配置文件(整合导入前三个分开的配置文件),以及web.xml配置文件(其中引入的Spring配置文件是合并后的配置文件;引入方式与之前的普通SSH项目一样)
```

#### 4.私服搭建和访问

> 相当于局域网的Maven仓库

私服搭建一般由运维完成,这里省略.详情见word


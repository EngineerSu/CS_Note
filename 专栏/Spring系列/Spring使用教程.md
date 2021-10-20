



# Spring使用教程

介绍实际开发工作中使用到的 Spring 功能与特性。



## Project 创建

Project 使用 IDEA 创建，后面章节实践的 Project 都基于此。

### 创建 Maven 工程

IDEA 菜单栏 File - New - Project，出现以下界面，选择 Maven 工程：

![image-20201205164517381](/d:/resource/notePic/image-20201205164517381.png)

Next 后，输入项目名称 Name，会自动生成 Artifact 的信息，选择项目位置 Location，如下：

![image-20201205164650128](/d:/resource/notePic/image-20201205164650128.png)

Finish 后，进入创建的工程，如下：

![image-20201205164755287](/d:/resource/notePic/image-20201205164755287.png)

其中进行了默认分包，pom.xml 文件干净，没有任何依赖



### pom.xml

在原有 pom.xml 文件的基础上，需要引入一些基础的依赖。pom.xml 文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>demo-spring-ioc</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <!-- junit测试 -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>

        <!-- google guava 工具包 -->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>30.0-jre</version>
        </dependency>

        <!-- lombok 工具，使用还需要安装lombok插件 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.16.20</version>
        </dependency>

        <!-- slf4j + logback -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.6.6</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.0.6</version>
            <scope>runtime</scope>
        </dependency>
     
    </dependencies>
</project>
```



### lombok 插件

pom.xml 中包含了 lombok 依赖，可以使用注解节省 POJO 类的 get/set/toString 等基础代码。但使用时需要在 IDEA 下载 lombok 插件。

IDEA 菜单栏选择 Preferences - Plugins，在插件界面选择 MarketPlace，搜索 lombok，点击 install 即可。

![image-20201206145634240](/d:/resource/notePic/image-20201206145634240.png)



### logback.xml

工程采用 slf4j + logback 作为日志组件，需要在 main.resources 目录下创建 logback.xml 配置文件，如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">
    <property name="APP_NAME" value="demo-spring-ioc" />
    <!--定义日志文件的存储地址 勿在 LogBack 的配置中使用相对路径-->
    <!-- user.home表示执行该程序时的用户目录 -->
    <property name="LOG_PATH" value="${user.home}/demo/${APP_NAME}/logs" />

    <!-- 控制台输出 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %thread [%-5level] %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 全项目输出 -->
    <appender name="PROJECT" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %thread [%-5level] %logger{50} - %msg%n</pattern>
        </encoder>
        <file>${LOG_PATH}/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
            <!--日志文件输出的文件名,需要包含%i-->
            <fileNamePattern>${LOG_PATH}/test.%i.log</fileNamePattern>
            <!--固定窗口：10份-->
            <minIndex>1</minIndex>
            <maxIndex>10</maxIndex>
        </rollingPolicy>
        <!--单份日志文件最大的大小-->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <MaxFileSize>10MB</MaxFileSize>
        </triggeringPolicy>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 根日志 -->
    <root level="INFO">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="PROJECT" />
    </root>

</configuration>
```

其中 APP_NAME 需要根据不同的项目名称自行更改，也可以自行添加 appender 和 logger 标签。

默认的日志存储在 `${user.home}/demo/${APP_NAME}/logs` 目录下，其中 `user.home` 代表计算机用户目录



## IOC 容器使用

IOC 容器是 Spring 必使用的基础组件之一。

### pom 依赖

新增 Spring 的以下依赖：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>${spring_version}</version>
</dependency>

<!-- spring-core container -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-beans</artifactId>
    <version>${spring_version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>${spring_version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>${spring_version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-expression</artifactId>
    <version>${spring_version}</version>
</dependency>
```

Spring 版本号使用 properties 标签统一控制：

```xml
<properties>
    <spring_version>5.1.9.RELEASE</spring_version>
</properties>
```



### applicationContext.xml

SpringBoot 大部分功能都可以通过注解使用，包括配置。但 Spring 中的基本配置还是需要 xml 配置文件的。

在 main.resources 目录下新建 applicationContext.xml 文件。我们使用注解进行 bean 声明，因此在 applicationContext.xml 文件中只用声明 context 扫描包即可，如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:context="http://www.springframework.org/schema/context"
	   xmlns:aop="http://www.springframework.org/schema/aop"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
	http://www.springframework.org/schema/context
	http://www.springframework.org/schema/context/spring-context-3.1.xsd http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd"
	   default-autowire="byName">

	<context:component-scan base-package="org.example"/>
</beans>
```



### bean - Admin

创建 Admin 类，并使用 Component 注解将其作为单例对象注入到 IOC 容器中，用 Value 注解注入属性。如下：

```java
package org.example.pojo;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@ToString
@Component("admin")
public class Admin {
    /**
     * 登录名称
     */
    @Value("admin")
    private String name;
    /**
     * 登录密码
     */
    @Value("123456")
    private String password;
}
```

注意，Getter 注解依赖 lombok 插件和 lombok 的 pom 依赖



### main 方法测试 IOC 容器

创建 Application 类，其 main 方法中启动 IOC 容器，并获取 Admin 的单例对象进行使用。

```java
package org.example.start;

import org.example.pojo.Admin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Application {

    public static void main(String[] args) {
        // IOC 容器初始化
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Admin admin = (Admin) applicationContext.getBean("admin");
        String msg = String.format("admin name:[%s], password:[%s]", admin.getName(), admin.getPassword());
        System.out.println(msg);
    }
}
```

ApplicationContext 用于 IOC 容器的初始化，其中 classpath 表示 resource 所在的相对目录。

执行 main 方法，控制台将会输出：

```
admin name:[admin], password:[123456]
```



### Junit 测试 IOC 容器

前面测试是使用启动类进行测试，当项目非常大的时候，启动整个项目可能会比较耗时，此时可以借助 Junit 的能力进行测试。

创建 IocTest 测试类如下：

```java
package org.example.test;

import org.example.pojo.Admin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations= "classpath:applicationContext.xml")
public class IocTest {
    // 测试类不用打开扫描开关，因为测试环境中会自动扫描该类
    @Resource
    private Admin admin;

    @Test
    public void test() {
        String msg = String.format("admin name:[%s], password:[%s]", admin.getName(), admin.getPassword());
        System.out.println(msg);
    }

}
```

其中 RunWith 注解中使用 SpringRunner.class 作为参数，表示启动 Spring环境，SpringRunner 注解与 SpringJUnit4ClassRunner 同义，前者相当于缩写。

ContextConfiguration 注解声明了 Spring 去哪里加载 context。

执行 test 方法，控制台将会输出：

```
admin name:[admin], password:[123456]
```



### 打 jar 包测试 IOC 容器

Java 程序可以打成 jar 包，在任何装有 JVM 的平台上运行，下面介绍如何将以上项目打成 jar 包。

IDEA 中选择 File - Project Structure - Artifacts，点击 + ，如下：

![image-20201205172849508](/d:/resource/notePic/image-20201205172849508.png)

再进行如下选择：

![image-20201205172916336](/d:/resource/notePic/image-20201205172916336.png)

在弹出的交互框中，进行一些配置，主要配置介绍：

- Main Class：jar 包的启动类，该类需要有 main 方法，在执行 `java -jar` 时，就会执行启动类的 main 方法
- JAR files from libraries：第一个选项指把项目的依赖包都一起压缩进 jar 包中；第二个选项指将依赖包也都复制到 jar 包的输出目录，并在输出 jar 包中建立超链接。（建议选择第一种，如果打出来的 jar 包执行报错，就再执行以上步骤，选择第二种）
- Directory for META-INF/MANIGEST.MF：我们执行这些配置其实就是为了生成一个 META-INF/MANIGEST.MF 文件，这里是配置 META-INF/MANIGEST.MF 文件的输出位置，一般至于 resource 下
- include tests：打的 jar 包是否包含测试包（test）里的类，建议不勾选

![image-20201205173018652](/d:/resource/notePic/image-20201205173018652.png)



我的配置如下：

![image-20201205173734559](/d:/resource/notePic/image-20201205173734559.png)

点击确定，注意这里的 Output directory ，它是打的 jar 包的存放位置。

![image-20201205174058696](/d:/resource/notePic/image-20201205174058696.png)

点击确定，可以发现 resources 目录下新增了文件，如下：

![image-20201205173830695](/d:/resource/notePic/image-20201205173830695.png)

IDEA 选择菜单栏 Build - Build Artifacts，使用终端切到前面提到的 Output directory  目录下，可以看到打出的jar包，执行 `java -jar demo-spring-ioc.jar` 命令，可以看到输出：

```
admin name:[admin], password:[123456]
```



## AOP 使用

Java 是面向对象设计的语言，面向对象的继承特性是纵向的扩展，而 AOP（Object Oriented Programming，面向对象编程）可以认为是横向的扩展，Spring 提供的 AOP 能力与 「SOLID」 原则中的 OCP 原则（面向拓展开放，面向修改关闭）也是非常契合的。

工程创建和基本依赖参考 IOC 容器使用，参考：[Spring使用教程（一）： IOC 容器使用](https://juejin.cn/post/6902707176553316366)

### pom 依赖

使用 Spring 的 AOP，需要在 IOC 容器的依赖基础上，再加入以下依赖：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
    <version>${spring_version}</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
    <version>${spring_version}</version>
</dependency>
```



### applicationContext.xml

applicationContext.xml 中需要加配置，如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:context="http://www.springframework.org/schema/context"
	   xmlns:aop="http://www.springframework.org/schema/aop"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
	http://www.springframework.org/schema/context
	http://www.springframework.org/schema/context/spring-context-3.1.xsd http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd"
	   default-autowire="byName">

<!--	proxy-target-class默认为false，表示织入模式是基于接口的动态代理；为true表示织入模式是基于CGLib-->
<!--	当然，如果被增强的类没有实现接口，Spring将自动使用CGLib动态代理-->
	<aop:aspectj-autoproxy proxy-target-class="true"/>
	<context:component-scan base-package="org.example"/>
</beans>

```

CGLib 是基于字节码增强技术的动态代理技术，有兴趣可以看下[Java 代理模式介绍](https://cloud.tencent.com/developer/article/1461796)



### 基本概念

AOP 中有两个重要的概念：切面和切入点。

> 切面，即一个横跨多个核心逻辑的功能，或者称之为系统关注点

通俗来说，切面是一个类，这个类中定义了若干增强方法，并借助切入点去声明这些增强方法作用的对象。

> 切入点，即定义在应用程序流程的何处插入切面的执行

切入点的定义方式有 「execution 表达式」和「注解」两种，execution 是使用正则表达式去定义切面中增强方法作用的范围，比如 `execution(* org.example.*DAO.*(..))` 表达式定义了在 org.example 包下，所有 XxxDAO 类的所有方法都会被增强。

它看起来是个一劳永逸的方法，但正因为这样，它的危害也是很大的。比如一个项目的维护人换了，后来的同学可能并不知道有这样的配置，也许他并不想他新增的DAO被代理，这样可能就会带来一些意想不到的效果。

因此，这里也只介绍如何结合注解使用 AOP，这是一种强感知的方法（就像 Spring 中对于需要事务的方法，需要显式使用 Transactionnal 注解，而不是默认给方法都增加事务），不会出现上面的问题。



### 注解 AOP 实战

假设现在需要新增一个切面，它的功能是：统计方法的执行时间。

#### 注解 Metric

为此，我们先定义一个监控注解 Metric 如下：

```java
package org.example.aop.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Metric {
    String methodName();
}
```

其中 Target 注解声明了 Metric 注解是作用于方法上的，Retention 注解声明了 Metric 注解是运行时生效。

methodName 属性要求使用该注解的地方，需要告知它的方法名称。



#### 切面类 MetricAspect

再定义切面类 MetricAspect 如下：

```java
package org.example.aop.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.aop.annotations.Metric;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MetricAspect {

    /**
     * Before 注解中的 value 属性表示切入点
     */
    @Before(value = "@annotation(metric)")
    public void metricBefore(Metric metric) {
        System.out.printf("method:[%s] begin metric !%n", metric.methodName());
    }

    /**
     * Around 注解中的 value 属性表示切入点
     */
    @Around(value = "@annotation(metric)")
    public Object metricAround(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long spend = System.currentTimeMillis() - start;
            String msg = String.format("method:[%s], spendTime:[%s]ms", metric.methodName(), spend);
            System.out.println(msg);
            // 可以输出到监控日志
        }
    }
}
```

注意切面类也需要注入到 IOC 容器中，因此 MetricAspect 有 Component 注解声明，另外再使用 Aspect 注解声明这是一个切面类。

Before 注解声明该方法增强的切入时机是在方法执行前，它的 value 表明该增强的切入点是具有 Metric 注解声明的方法。

Around 注解声明该方法增强的切入时机包括之前和之后，被增强的方法执行的时间是由 `joinPoint.proceed()` 控制。因此 metricAround 方法逻辑就是在方法执行前记录系统时间，在方法执行后统计方法执行时间并打印出来。



#### 目标对象 Calculator

定义一个类 Calculator，如下：

```java
package org.example.pojo;

import org.example.aop.annotations.Metric;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component("calculator")
public class Calculator {
    @Metric(methodName = "Calculator.add")
    public int add(int a, int b) throws InterruptedException {
        // 模拟方法耗时
        Thread.sleep(new Random().nextInt(3000));
        return a + b;
    }
}
```

Calculator 提供了一个 add 方法，提供计算两数之和的功能，add 方法具有 Metric 注解声明，表明该方法需要监控计算时长。



#### main 方法测试 AOP

创建 Application 类如下：

```java
package org.example.start;

import org.example.pojo.Calculator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {
    
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Calculator calculator = (Calculator) applicationContext.getBean("calculator");
        calculator.add(5, 5);
    }
    
}
```

ApplicationContext 用于启动 IOC 容器装载上下文，这在前面的 IOC 容器使用中有介绍。

执行 main 方法如下：

```
method:[Calculator.add] begin metric !
method:[Calculator.add], spendTime:[451]ms
```

第一行是切面类 MetricAspect.metricBefore 方法的效果，第二行是 MetricAspect.metricAround 方法的效果。

Junit 测试和打 jar 包测试方法与 IOC 容器使用章节类似，这里就不再介绍。



### 总结

- Spring 中 AOP 的使用需要新增 pom 依赖，并需要在 xml 配置文件中开启 AOP 开关。
- 推荐使用注解 AOP，这是一种显式且安全的使用形式。



## JDBC 使用

JDBC（Java Dara Base Connectivity，Java 数据库连接）是一种用于执行 SQL 语句的 Java API，可以为多种数据库提供统一访问，为数据库开发人员提供了一个标准的 API，它由一组用 Java 语言编写的类和接口组成。

### 纯 Java 程序使用 JDBC

生产环境几乎不会直接使用 JDBC，因此下面只简单介绍使用 JDBC 连接数据库和执行 SQL 的流程，不做编码实现。

- 在开发环境中加载指定数据库的驱动程序。比如将 mysql-connector-java-5.1.18-bin.jar 放置在项目的 lib 包下

- 在 Java 程序中加载驱动程序：

  ```java
  Class.forName("com.mysql.jdbc.Driver");
  ```

- 创建数据连接对象 Connection：

  ```java
  Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "123456");
  ```

- 创建执行 SQL 的对象 Statement：

  ```java
  Statement statement = connection.createStatement();
  ```

- 调用 Statement 对象相关方法执行 SQL 语句。`executeUpdate()` 方法用于执行增删改的 SQL，`executeQuery` 方法用于执行查询，查询结果对象为 ResultSet，通过该对象的 `next()` 方法，可以一行行获取结果，通过 `getXxx` 方法可以取出每行的列结果。

  ```java
  statement.executeUpdate("insert into user(name, sex, age) values('张三', '男', 18)");
  
  ResultSet resultSet = statement.executeQuery("select * from user");
  ```

- 关闭数据库连接：

  ```java
  connection.close();
  ```



### Spring 使用 JDBC

首先需要保证本机按照了 MySQL，参考：[MySQL的安装和使用教程（Mac OS）超详细](https://juejin.cn/post/6903070115823419399)

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>demo-spring-jdbc</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <spring_version>5.1.9.RELEASE</spring_version>
    </properties>

    <dependencies>
        <!-- junit测试 -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>

        <!-- google guava 工具包 -->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>30.0-jre</version>
        </dependency>

        <!-- lombok 工具，需要安装lombok插件 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.16.20</version>
        </dependency>

        <!-- slf4j + logback -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.6.6</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.0.6</version>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>${spring_version}</version>
        </dependency>

        <!-- spring-core container -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>${spring_version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring_version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring_version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-expression</artifactId>
            <version>${spring_version}</version>
        </dependency>

        <!-- spring + jdbc 的系列依赖 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>${spring_version}</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.22</version>
        </dependency>
        <dependency>
            <groupId>commons-dbcp</groupId>
            <artifactId>commons-dbcp</artifactId>
            <version>20030825.184428</version>
        </dependency>
        <dependency>
            <groupId>commons-pool</groupId>
            <artifactId>commons-pool</artifactId>
            <version>1.6</version>
        </dependency>

    </dependencies>

</project>
```



#### applicationContext.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:context="http://www.springframework.org/schema/context"
	   xmlns:aop="http://www.springframework.org/schema/aop"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
	http://www.springframework.org/schema/context
	http://www.springframework.org/schema/context/spring-context-3.1.xsd http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd"
	   default-autowire="byName">

	<context:component-scan base-package="org.example"/>

	<!-- 数据库连接池配置 -->
	<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
		<property name="driverClassName" value="com.mysql.jdbc.Driver"/>
		<!-- 最后的 demo 是数据库名称 -->
		<!-- characterEncoding=utf-8 需要与数据库的编码格式保持一致，不然可能会出现中文乱码 -->
		<property name="url" value="jdbc:mysql://localhost:3306/demo?characterEncoding=utf-8"/>
		<!-- 账号和密码 -->
		<property name="username" value="root"/>
		<property name="password" value="123456"/>
	</bean>

	<!-- 将数据库连接池注入到 jdbcTemplate 中 -->
	<bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
		<property name="dataSource" ref="dataSource"/>
	</bean>
</beans>
```



#### 表的行数据映射

首先定义一个类 User，对应 user 表中每行数据：

```java
package org.example.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
    private int id;
    private String name;
    private String sex;
    private int age;
}
```

然后定义 UserRowMapper，该类需要实现 `RowMapper<User>` 接口，用于将 JDBC 中执行查询 SQL 后得到的 ResultSet 映射成 User，如下：

```java
package org.example.mapper;

import org.example.pojo.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User res = new User();
        res.setId(resultSet.getInt("id"));
        res.setName(resultSet.getString("name"));
        res.setSex(resultSet.getString("sex"));
        res.setAge(resultSet.getInt("age"));
        return res;
    }
}
```



#### 原生 DAO

定义 UserDAO 接口，它提供了落库和查询两个方法：

```java
package org.example.dao;

import org.example.pojo.User;

import java.util.List;

public interface UserDAO {
    /**
     * 落库保存
     */
    void save(User user);

    /**
     * 查询所有的User
     */
    List<User> queryAll();
}
```

定义 UserDAOImpl，它借助 JdbcTemplate 实现了 UserDAO 接口。JdbcTemplate 正是 Spring 对 JDBC 的封装（需要在配置文件中为 JdbcTemplate 注入数据库连接池）。

可以看到，借助 JdbcTemplate 后，执行 SQL 不再需要创建那么多对象，近似于直接写 SQL 。

```java
package org.example.dao.impl;

import org.example.dao.UserDAO;
import org.example.mapper.UserRowMapper;
import org.example.pojo.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.Types;
import java.util.List;

@Repository("userDAO")
public class UserDAOImpl implements UserDAO {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public void save(User user) {
        jdbcTemplate.update("insert into user(name,sex,age) values(?,?,?)",
                new Object[]{user.getName(), user.getSex(), user.getAge()},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER});
    }

    public List<User> queryAll() {
        return jdbcTemplate.query("select * from user", new UserRowMapper());
    }
}
```



#### 测试 Spring JDBC

编写 Application 类，对 Spring JDBC 进行测试。

```java
package org.example.start;

import org.example.dao.UserDAO;
import org.example.pojo.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.List;


public class Application {

    public static void main(String[] args) {
        // IOC 容器初始化
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        UserDAO userDAO = (UserDAO) applicationContext.getBean("userDAO");
        User user = new User();
        user.setName("王五");
        user.setSex("男");
        user.setAge(30);
        userDAO.save(user);
        List<User> users = userDAO.queryAll();
        if (CollectionUtils.isEmpty(users)) {
            System.out.println("users is empty !");
        }
        else {
            for (User each : users) {
                System.out.println(each);
            }
        }
    }
}
```

执行 main 方法，输出：

```
User(id=3, name=张三, sex=男, age=18)
User(id=4, name=李四, sex=女, age=20)
User(id=8, name=王五, sex=男, age=30)
```



#### 常见问题

Q：在执行 main 方法时，出现了 ClassNoDefException

A：这是因为相关依赖 jar 包版本不对，依赖请与前面的 pom.xml 文件保持一致

Q：执行 Application.main 方法，输出中的中文出现了乱码

A：数据库连接池配置没有指定字符编码方式，默认的编码方式与数据库不一致。参考前面的 applicationContext.xml 配置



## MyBatis 使用

MyBatis 是优秀的持久层框架，它旨在简化 JDBC 的使用，并且与 Spring 可以很好的融合。

Spring 融合 Mybatis 的使用需要分成以下四步：

- 在 IOC 容器中注入 SqlSessionFactoryBean 作为 sqlSessionFactory 
- 编写 Mapper 接口，并在 IOC 容器中将其声明为 MapperFactoryBean 对象
- 编写 Mapper 接口对应的 SQL 文件（可以用注解，也可以用 xml）
- 添加配置文件 mybatis-config.xml 声明有哪些映射器



### 1. SqlSessionFactoryBean

SqlSessionFactoryBean 有两个必须注入的属性：dataSource 与 configLocation，它们的含义如下：

- dataSource：声明了数据库的详细连接信息（包括驱动、地址、用户名和密码）
- configLocation：声明了 mybatis-config.xml 配置文件的位置

这里采用注解类的形式进行 bean 注入：

```java
package org.example.mybatis.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
public class MybatisConfiguration {

    @Bean(name = "dataSource")
    public BasicDataSource getBasicDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/demo?characterEncoding=utf-8&serverTimezone=GMT");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        return dataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean getSqlSessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean res = new SqlSessionFactoryBean();
        res.setDataSource(dataSource);
        // mybatis-config.xml 就是 MyBatis 的配置文件
        res.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
        return res;
    }    
}
```

简单解释一下注解形式注入 bean，首先 MybatisConfiguration 类本身被 Configuration 注解修饰，说明它是一个配置类。

它的 getBasicDataSource 方法被 Bean 注解修饰，表明该方法的返回值会作为一个单例对象注入到 IOC 容器，该方法相当于 xml 文件中的一个 bean 标签。

它的 getSqlSessionFactoryBean 方法同样被 Bean 注解修饰，并且方法还有 DataSource 的参数，被 Bean 注解修饰方法的参数，Spring 会自动去 IOC 容器中寻找（相当于 xml 文件中 bean 标签的 ref ）。



### 2. UserMapper

针对每一张表，可以建一个 Mapper 接口，类似于 JDBC 中的 DAO。创建 UserMapper 如下：

```java
package org.example.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.mybatis.dataobject.UserDO;

import java.util.List;

@Mapper
public interface UserMapper {
    Long insert(UserDO userDO);

    List<UserDO> selectByName(String name);

    List<UserDO> selectAll();
}
```

其中 Mapper 注解可以用于 Mapper 类自动搜索的标识。需要在启动配置类上增加 MapperScan 注解，声明应该去应用中哪些位置寻找 Mapper。

涉及的 UserDo 如下：

```java
package org.example.mybatis.dataobject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDO {
    private Long id;
    private String name;
    private String sex;
    private int age;

    public UserDO(String name, String sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }
}
```



### 3. user_mapper.xml

MyBatis 写 SQL 有基于注解和 xml 两种方式，个人习惯使用 xml，如下为 user_mapper.xml：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
		PUBLIC "-//database.org//DTD Mapper 3.0//EN"
		"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.example.mybatis.mapper.UserMapper">

	<resultMap id="userDo" type="org.example.mybatis.dataobject.UserDO">
		<result property="id" column="id"/>
		<result property="name" column="name"/>
		<result property="sex" column="sex"/>
		<result property="age" column="age"/>
	</resultMap>

	<parameterMap id="userDo" type="org.example.mybatis.dataobject.UserDO">

	</parameterMap>

	<sql id="sqlColumn">
		id,
		name,
		sex,
		age
	</sql>

	<select id="selectByName" resultMap="userDo">
		SELECT
			<include refid="sqlColumn"></include>
		FROM
			user
		WHERE name = #{name}
	</select>

	<insert id="insert" parameterMap="userDo">
		INSERT INTO user(
			id,
			name,
			sex,
			age
		) VALUES (
			#{id},
			#{name},
			#{sex},
			#{age}
		)
		<selectKey resultType="java.lang.Long" keyProperty="id">
			select last_insert_id() as id from user limit 1
		</selectKey>
	</insert>

	<select id="selectAll" resultMap="userDo">
		SELECT
		<include refid="sqlColumn"></include>
		FROM
		user
	</select>

</mapper>
```



### 4. mybatis-config.xml

第三步编写的 SQL xml 文件位置需要通过 mybatis-config.xml 配置，如下：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!-- mybatis的配置文件 -->
<!DOCTYPE configuration
        PUBLIC "-//database.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
	<mappers>
        <mapper resource="mapper/user_mapper.xml"/>
    </mappers>
</configuration>

```



### 测试

以上便完成了 MyBatis 的基本配置。如果有多张表，增加对应的 XxxMapper  和 xxx_mapper.xml 即可，并在 mybatis-config.xml 中增加 mapper 标签，声明 xxx_mapper.xml 的位置。

Application 类用于测试，如下：

```java
package org.example.mybatis.start;

import org.example.mybatis.BootstrapConfig;
import org.example.mybatis.dataobject.UserDO;
import org.example.mybatis.mapper.UserMapper;
import org.example.mybatis.service.UserServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BootstrapConfig.class);
        insertTest(context);
    }

    /**
     * 基本的 Mapper 执行 SQL 测试
     */
    private static void insertTest(AnnotationConfigApplicationContext context) {
        UserMapper userMapper = context.getBean(UserMapper.class);
        
        UserDO user1 = new UserDO("user1", "girl", 18);
        userMapper.insert(user1);
        System.out.println("====插入user1后====");
        userMapper.selectAll().forEach(System.out::println);

        UserDO user2 = new UserDO("user2", "boy", 19);
        userMapper.insert(user2);
        System.out.println("====插入user2后====");
        userMapper.selectAll().forEach(System.out::println);
    }

}
```

Application 类用到了 AnnotationConfigApplicationContext，从名字也可以看出它是基于注解配置的Spring上下文注册器，其中注解配置类是它的构造参数，为 BootstrapConfig 如下：

```java
package org.example.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 系统启动的注解配置
 *
 * @author yangsu
 * @date 2020/12/11
 */
@MapperScan(basePackages = "org.example.mybatis.mapper")
@ComponentScan(basePackages = {"org.example.mybatis"})
@Configuration
public class BootstrapConfig {

}
```

BootstrapConfig 是三个注解的结合：Configuration + ComponentScan + MapperScan，其作用为：

- Configuration：声明 BootstrapConfig 为配置类
- ComponentScan：声明 Spring 在创建 IOC 容器时，需要扫描的组件包
- MapperScan：声明 MyBatis 的 Mapper 接口所在位置

启动 Application 的 main 函数，可以看到输出：

```
====插入user1后====
UserDO(id=22, name=user1, sex=girl, age=18)
====插入user2后====
UserDO(id=22, name=user1, sex=girl, age=18)
UserDO(id=23, name=user2, sex=boy, age=19)
```

表明 Spring 与 MyBatis 结合成功！

### 事务

Spring 中事务是默认关闭的，如果要通过注解开启事务，需要做以下两步：

- 注入 transactionManager 对象到 IOC 容器中
- 在 Spring 的配置文件中声明开启注解事务

以上两步，我们都使用注解在 BootstrapConfig 中进行配置，如下：

````java
package org.example.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 系统启动的注解配置
 *
 * @author yangsu
 * @date 2020/12/11
 */
@MapperScan(basePackages = "org.example.mybatis.mapper")
@EnableTransactionManagement(proxyTargetClass = false)
@ComponentScan(basePackages = {"org.example.mybatis"})
@Configuration
public class BootstrapConfig {

    /**
     * 用于事务
     */
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager res = new DataSourceTransactionManager();
        res.setDataSource(dataSource);
        return res;
    }
}
````

增加的 EnableTransactionManagement 注解即表示开启注解事务，proxyTargetClass 属性用于设置使用 CGLib 还是 动态代理实现注解。



## Spring MVC 使用

Spring MVC 定制了 Spring Web 应用的开发规范：按要求写 Controller（控制器）即可。Spring MVC 负责将接收到的请求分配给指定控制器中的指定处理器（控制器中被 RequestMapping 修饰的方法）

### 配置文件

demo-spring-mvc 其实就是 SSM 的整合，涉及了较多的配置，在这里做简单的介绍。

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>demo-spring-mvc</artifactId>
    <version>1.0-SNAPSHOT</version>

    <packaging>war</packaging>

    <properties>
        <spring_version>5.1.9.RELEASE</spring_version>
    </properties>

    <dependencies>
        <!-- junit测试 -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>

        <!-- google guava 工具包 -->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>30.0-jre</version>
        </dependency>

        <!-- lombok 工具，需要安装lombok插件 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.16.20</version>
        </dependency>

        <!-- slf4j + logback -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.6.6</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.0.6</version>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>${spring_version}</version>
        </dependency>

        <!-- spring-core container -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>${spring_version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring_version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring_version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-expression</artifactId>
            <version>${spring_version}</version>
        </dependency>

        <!-- spring + jdbc 的系列依赖 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>${spring_version}</version>
        </dependency>

        <!-- mysql 驱动和连接池 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.22</version>
        </dependency>
        <dependency>
            <groupId>commons-dbcp</groupId>
            <artifactId>commons-dbcp</artifactId>
            <version>1.4</version>
        </dependency>
        <dependency>
            <groupId>commons-pool</groupId>
            <artifactId>commons-pool</artifactId>
            <version>1.6</version>
        </dependency>

        <!-- mybatis -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.4.6</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>1.3.0</version>
        </dependency>

        <!--servlet相关依赖 begin-->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>4.0.1</version>
            <scope>provided</scope>
        </dependency>
        <!--servlet相关依赖 end-->

        <!--spring web -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>${spring_version}</version>
        </dependency>

        <!-- spring-webmvc -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${spring_version}</version>
        </dependency>

        <!-- fastjson -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.59</version>
        </dependency>

        <!-- Controller 使用 RequestBody 注解时，对参数的 convert 依赖 jackson （版本也要对应） -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.9</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!--其他配置-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.2.3</version>
                <configuration>
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>6</source>
                    <target>6</target>
                </configuration>
            </plugin>
        </plugins>

    </build>

</project>
```



#### WebApplication

Spring Web 应用要做到上述的功能，依赖两个核心配置：

- DispatcherServlet 配置：Spring Web 应用要声明 DispatcherServlet 为它的第一个 Servlet，它负责拦截所有请求进行分发
- 上下文：Spring Web 应用要将 Spring 的上下文与 Servlet 的上下文结合

WebApplication 中需要设置上述的两个核心配置。

传统 Java 应用（打 jar 包使用的那种）的启动入口是： META-INF 中的 Main Class（启动类）的 main 方法，执行 `java -jar xxx.jar` 命令，即会在 JVM 中执行启动类的 main 方法。

Spring Web 应用本身并不具有启动入口，因为 Spring Web 应用其实是系列 Servlet 的集合，它需要配合 Web 容器（如 Tomcat）一起使用，Web 容器负责接收网络请求，并将其转换成 Spring Web 应用能够处理的请求，然后将 Spring Web 应用处理的结果转换成网络请求响应进行回复。

Spring MVC 的基本思想是，使用 DispatcherServlet 拦截所有应用能处理的 Web 请求，然后按规则分发给指定的执行器。因此 Spring Web 应用的特征是它的 web.xml 配置文件中声明了 DispatcherServlet，除此之外， web.xml 还至少需要声明 Spring 的上下文，这样才能将 MVC 的上下文一起结合，处理请求。

Spring 4.x 以后支持使用类代替 web.xml，在项目中增加 WebApplicationInitializer 接口的实现类，Web 容器会扫描这样的类，并执行它的 onStartup 方法。WebApplicationInitializer 实现类如下：

```java
package org.example.mvc.start;

import org.example.mvc.config.BootstrapConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * 这个类相当于 web.xml 的替代，作为 Spring Web 应用的启动类存在
 *
 * @author yangsu
 * @date 2020/12/12
 */
public class WebApplication implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        // 引入 Spring Web 应用的上下文
        context.register(BootstrapConfig.class);
        // 声明 DispatcherServlet，它是 SpringMVC 的前端控制器
        context.setServletContext(servletContext);
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(context));
        // 声明 DispatcherServlet 匹配所有请求
        servlet.addMapping("/");
        servlet.setLoadOnStartup(1);
    }
}
```



#### BootstrapConfig

BootstrapConfig 是启动配置类，它可以认为是 springContext.xml 的替代。

```java
package org.example.mvc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 系统启动的注解配置
 *
 * @author yangsu
 * @date 2020/12/11
 */
@ComponentScan(basePackages = {"org.example.mvc"})
@Configuration
public class BootstrapConfig {

}
```

这里它只做了一件事， 声明基础扫描包。

为了模块化的管理配置，在 org.example.mvc.config 包下拆分了多个配置类，它们分别负责 MyBatis 和 Spring MVC 的配置。（Spring IOC 的配置基本可以认为是 BootstrapConfig 的 ComponentScan 注解）



#### MybatisConfig

它负责了数据库相关 bean 的注入，以及与 MyBatis 相关的配置（如 Mapper 接口的声明等），有了 MybatisConfig，数据库的 CURD 就有了保障。

```java
package org.example.mvc.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@MapperScan(basePackages = {"org.example.mvc.mapper"})
@EnableTransactionManagement(proxyTargetClass = false)
@Configuration
public class MybatisConfig {

    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        // serverTimezone 用于数据库与系统时区不一致时，进行声明
        dataSource.setUrl("jdbc:mysql://localhost:3306/demo?characterEncoding=utf-8&serverTimezone=GMT");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
        SqlSessionFactoryBean res = new SqlSessionFactoryBean();
        res.setDataSource(dataSource);
        res.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
        return res;
    }

    /**
     * 用于事务
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager res = new DataSourceTransactionManager();
        res.setDataSource(dataSource);
        return res;
    }

}
```



#### SpringMvcConfig

SpringMvcConfig 是 springMvc-context.xml 的替代，它具有 EnableWebMvc 注解，它作用是导入 DelegatingWebMvcConfiguration，而 DelegatingWebMvcConfiguration 作用可理解为提供缺省的 Spring MVC 配置。

如果想对缺省的 Spring MVC 配置进行定制，则可以让 SpringMvcConfig 实现 WebMvcConfigurer 接口定义的方法。

```java
package org.example.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@EnableWebMvc
@Configuration
public class SpringMvcConfig {
    /**
     * 视图解析器配置
     * 方法名 viewResolver 相当于 bean 标签的 id
     * 也可以在 Bean 注解中设置 name 属性，来指定 Bean 的 id
     */
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver res = new InternalResourceViewResolver();
        res.setPrefix("/WEB-INF/classes/views/");
        res.setSuffix(".jsp");
        res.setViewClass(JstlView.class);
        return res;
    }
}
```

SpringMvcConfig 还注入了一个 viewResolver，它设置了系列属性，用于声明如何解析 jstl 视图。

比如 Controller 返回的视图名称为 index，则经过该 viewResolver 解析后，会按 `/WEB-INF/classes/views/index.jsp` 路径去寻找视图文件。



### Controller

Spring MVC 将 Servlet 简化成 Controller（控制器），一组控制器具有相同的访问路径前缀，每个 Controller 中包含若干方法，每个方法都可以匹配一个请求路径。

如下是 IndexController 示例：

```java
package org.example.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    /**
     * 基本的 index 页面
     */
    @RequestMapping("index")
    public String index0() {
        // 不使用 ResponseBody 注解时，默认返回字符串是逻辑视图名
        return "index0";
    }

}
```

当请求url为：localhost:8080/app/index 时，Web 容器会将这个请求分给 `app` 这个 Web 应用，`app` 这个以 Spring MVC 构建的 Web 应用会根据 `/index` 访问路径定位到 IndexController 的 index0 方法，该方法返回值为 "index0"，没有声明返回值类型时，默认该值为视图名称。

后续经过视图解析器处理，找到相对路径为 `/WEB-INF/classes/views/index0.jsp` 的页面资源，返回给 Web 容器，Web 容器再处理 jsp 文件，最终返回 html 文件资源给浏览器。

下面介绍写 Controller 会用到的一些注解：

#### Controller

声明该类是一个 Spring MVC 的控制器，一个控制器包含若干个处理器（每一个方法可以认为是一个处理器），处理器才是最终处理请求的逻辑体。

#### RequestMapping

声明路径，用于 DispatcherServlet 定位该用什么处理器处理请求。RequestMapping 可以作用在 Controller 类上，此时该类下所有处理器的路径都会增加声明的前缀。

```java
package org.example.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("index")
public class IndexController {

    @RequestMapping("0")
    public String index0() {
        // 不使用 ResponseBody 注解时，默认返回字符串是逻辑视图名
        return "index0";
    }

    
	@RequestMapping("1")
    public String index1() {
        // 不使用 ResponseBody 注解时，默认返回字符串是逻辑视图名
        return "index1";
    }
}
```

比如以上的情况，对应的访问路径分别是 `/index/0` 和 `/index/1` （都不包含请求前缀）

RequestMapping 还有一些常用属性设置：

- method：声明该方法的类型，一般使用 RequestMethod 设置，比如 `method = RequestMethod.GET`
- produces：声明交换数据的格式，一般使用 MediaType 设置，你如 `produces = MediaType.APPLICATION_JSON_UTF8_VALUE`

注意，produces 设置为 json 时需要依赖正确版本的 jackson



#### PathVariable

用于声明访问路径中的变量，比如上面如果有 10 个 index 页面，对应写 10 个方法未免太麻烦，可以这样使用：

```java
package org.example.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    /**
     * 演示 PathVariable 使用
     */
    @RequestMapping("index/{num}")
    public String index(@PathVariable("num") String num) {
        // 不使用 ResponseBody 注解时，默认返回字符串是逻辑视图名
        return "index" + num;
    }

}
```

比如访问路径是 `/index/10` 时，就会返回 `index10` 对应的视图资源



#### ResponseBody

控制器中被 RequestMapping 注解修饰的方法默认返回字符串都是视图名称，但有时候 Servlet 只是为了获取数据，并不需要视图信息，此时可以在方法上加 ResponseBody 注解，声明该处理器返回的字符串是数据而不是视图名称。

ResponseBody 也可以在控制器上声明，表示该控制器下所有处理器返回值都是数据而不是视图。

当然处理器被 ResponseBody 声明后，也可以返回对象，如下：

```java
@RequestMapping(path = "testResponseBody", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public UserDO testResponseBody() {
    UserDO userDO = new UserDO();
    userDO.setName("user");
    userDO.setAge("18");    
    return userDO;
}
```



#### RestController

RestController 是两个注解的组合（简写）

RestController = ResponseBody + Controller



#### RequestParam

请求的 URL 中可以携带一些参数，对 URL 中参数的解析使用 RequestParam 注解。

比如 URL 为：http://localhost:8080/app/user/queryByUserId?id=31

对应的处理器可以这么写：

```java
@RequestMapping(path = "queryByUserId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public UserDO queryByUserId(@RequestParam("id") Long id) {
    UserDO userDO = userMapper.selectById(id);
    return userDO;
}
```



#### RequestBody

当请求的参数不在 URL 中，而在请求体中时，对请求体参数的解析使用 RequestBody 注解。

比如用 Postman 发送这样一个 Put 请求时：

![image-20201213140302872](/d:/resource/notePic/image-20201213140302872.png)

对应的处理器可以这样写：

```java
@RequestMapping(path = "insert", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public String insertUser(@RequestBody UserDO userDO) {
    logger.info("insertUser userDO:{}", userDO);
    userMapper.insert(userDO);
    return "insert userDO return id:" +  userDO.getId();
}
```

注意，对于特殊的请求参数到对象的转换，仅有 RequestBody 声明还不够，可能还需要声明使用什么 convert



### Tomcat

完成了配置和 Controller 的编写后，Spring Web 应用就相当于完成了。但是 Spring Web 应用并不能单独使用，它需要配合 Web 容器，比如 Tomcat。

打个不恰当的比方，Web 容器相当于手机的操作系统，Spring Web 应用相当于 App，没有操作系统存在时，App 是不能处理用户请求的。操作系统又类似于一个翻译官，负责将用户的请求翻译成 App 能够处理的请求，交给指定的 App，等 App 处理完成后，又将 App 的响应翻译成用户能接受的响应。

#### 打 war 包

App 安装到操作系统中是需要安装包的，同样 Spring Web 应用「安装」到 Web 容器中也需要「安装包」，一般是 war 包的形式（war：winrar）。

IDEA 中打开 Project Structure，选择 Artifacts 点击 + ，推荐选择 Web Application: Exploded （Web Application 不支持热部署，Exploded 表示已文件夹形式部署），再选择 Form Modules，选择自己的项目，点击确定即可。

![image-20201213131112668](/d:/resource/notePic/image-20201213131112668.png)

然后点击 Maven，选到需要打 war 包的 module，在 LifeCycle 中依次点击 clean 和 package 命令，从 Run 的窗口中可以看到打出 war 包的位置

![image-20201213131621464](/d:/resource/notePic/image-20201213131621464.png)

#### 安装 Tomcat

Tomcat 的安装非常简单，[下载](https://tomcat.apache.org/download-90.cgi) 后到安装目录下解压即可。

![image-20201213131727517](/d:/resource/notePic/image-20201213131727517.png)

#### 启动和关闭

使用终端切换到 Tomcat 的安装目录的 bin 目录下，比如我的是：

```shell
cd /Library/apache-tomcat-9.0.41/bin
```

执行命令即可开启和关闭 Tomcat：

```shell
# 开启
sh ./startup.sh

# 关闭
sh ./shutdown/sh
```

开启后，访问 `localhost:8080` 可以看到 Tomcat 的说明页面表明启动成功

![image-20201213132129426](/d:/resource/notePic/image-20201213132129426.png)

#### 部署 Spring Web 应用

Tomcat 的安装目录下有一个 webapps 目录，它就类似于操作系统的「已安装应用仓库」，将打好的 war 复制到 webapps 目录下，它会自动解压 war 包，并根据 war 包中的内容创建容器。就这么简单，Spring Web 应用就部署好了。

比如 war 包解压后的目录名为 demo-spring-mvc-1.0-SNAPSHOT，按照前面创建的应用中 Controller，访问以下网址即可访问到 index 页面：

```http
http://localhost:8080/demo-spring-mvc-1.0-SNAPSHOT/index
```

可以看一下 Spring Web 应用部署后的目录（其实也就是打 war 包后的目录结构）

![image-20201213133017285](/d:/resource/notePic/image-20201213133017285.png)

- classes 与普通 jar 包结构基本一致，没有特殊配置的情况下，resources 目录下所有目录和文件都直接复制到 calsses 目录下
- lib 中是所有依赖的 jar 包

通过以上目录结构可以理解 SpringMvcConfig 中配置视图解析器时，有以下代码：

```java
res.setPrefix("/WEB-INF/classes/views/");
```

它声明了视图位置，其相对路径是相当于 demo-spring-mvc-1.0-SNAPSHOT 这个 webapp 的根目录



### 测试

按照上述步骤，每次修改代码后都要重新打包并且复制到 Tomcat 的 webapps 目录下，未免太过麻烦，IDEA 提供了较为方便的测试方法。

点击项目启动左边的配置，选择 Edit Configuration

![image-20201213133518389](/d:/resource/notePic/image-20201213133518389.png)

再点击左上角的 +，并选择 Tomcat - Local

![image-20201213133627400](/d:/resource/notePic/image-20201213133627400.png)

配置 Local 的 Tomcat：Name 可以随意设置，主要设置点如下：

- 设置 Application server，选择前面 Tomcat 的解压目录

  ![image-20201213133835289](/d:/resource/notePic/image-20201213133835289.png)

- Open browser：表示 Tomcat 启动后会自动打开浏览器，访问下面设置的 URL

- 后面 VM options 中有两个配置，在 `On Update action` 中选择 `Restart server` 表示更新时重启服务器

- Tomcat Server Settings 中可以设置 HTTP port，表示 Tomcat 启动后将会监听的端口，没有特殊需要时就选择默认的 8080。注意 IDEA 启动的 Tomcat 和前面提到用终端命令启动 Tomcat 是两个进程，建议使用 IDEA Tomcat 时关掉终端启动的 Tomcat，不然 IDEA Tomcat 启动时会报端口冲突。或者在这里修改 IDEA Tomcat 监听的端口

- 设置部署的 webapp：选择 Deployment，点击 + 选择前面 Artifacts 中创建的 war exploded，还可以设置 Application context 路径，表示 webapp 的前置访问路径，其实就是前面直接复制 war 包部署时的解压后目录名，这里可以自定义

  ![image-20201213134512016](/d:/resource/notePic/image-20201213134512016.png)

设置好后，点击 OK，然后启动项目，可以在状态栏看到启动日志

![image-20201213134630880](/d:/resource/notePic/image-20201213134630880.png)

显示部署成功后，通过浏览器访问 http://localhost:8080/app/index 即可看到 index 页面

当修改代码后，可以点击上图中箭头指向的 deploy all，会重新部署，这样就可以很方便的测试了。








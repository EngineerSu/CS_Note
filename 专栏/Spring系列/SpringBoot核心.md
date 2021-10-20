## SpringBoot核心

**参考**

[快速搭建一个SpringBoot项目](http://tengj.top/2017/02/26/springboot1/)



----

### 基本配置

#### 入口类和@SpringBootApplication

SpringBoot项目都有一个XxxApplication的入口类, 它有一个main方法, 其中执行`SpringApplication.run(XxxApplication.class, arg)`, 用于启动SpringBoot项目

入口类会被@SpringBootApplication注解修饰, 这个注解是一个组合注解, 其中最重要的三个注解是@Configuration @ComponentScan @EnableAutoConfiguration.

- @Configuration 修饰的类是JavaConfig的配置类, 用于代替beans.xml配置文件.

- @ComponentScan 修饰的类, 若不指定扫描范围, 默认其同级包及其内层的Bean会被扫描(比如被@Component, @Service等修饰的类), 进入IOC容器. 所以入口类一般放置在groupId+artifactID组合的包名下.

- @EnableAutoConfiguration 也是一个组合注解, 其中最重要的是==@Import==注解导入的配置功能, EnableAutoConfigurationSelector使用SpringFactoriesLoader.loadFactoryNames方法来扫描具有META-INF/spring.factories文件的jar包, 此文件中声明了具有哪些自动配置类. 然后执行这些自动配置类进行自动配置, 比如项目导入了spring-boot-starter-web的依赖, 会自动添加对Tomcat和SpringMVC依赖, 并对其进行自动配置.

自动配置类是一个用@Configuration修饰的JavaConfig配置类, 其中会有一些@Condition的组合注解, 用来判断某个类是否需要自动配置, 自动配置的值可以用属性类导入, 属性类中的属性往往有默认值, 如果不在application.properties中添加这些属性, 那么自动配置就使用这些默认值

#### 关闭特定的自动配置

使用@SpringBootApplication的exclude属性, 比如

```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
```

#### 查看开启的自动配置

使用`java -jar xxx.jar --debug`启动, 或在application.properties中配置debug=true属性, SpringBoot项目启动时, 即可在控制台看到已启用和未启动的自动配置.

#### 全局配置文件

application.properties 或 application.yml文件是Spring Boot的全局配置文件,  作用是对自动配置的默认值进行修改. (.yml文件具有面向对象的特点). 其位置一般置于classpath根目录或classpath:/config目录下(这里的classpath指的是resources根目录), 而且后者优先级较高.

比如可以修改Tomcat自动配置的默认端口号, 如下

```properties
server.port=8081
# 配置文件中${random} 可以用来生成各种不同类型的随机值
dudu.secret=${random.value}
dudu.number=${random.int}
dudu.bignumber=${random.long}
dudu.uuid=${random.uuid}
dudu.number.less.than.ten=${random.int(10)}
dudu.number.in.range=${random.int[1024,65536]}
```

#### starter pom

starter pom是SpringBoot官方提供的pom依赖, 并且对大多数使用场景进行了自动化配置, 比如导入spring-boot-starter-web, 就会自动导入web开发相关的依赖, 并对其进行自动配置. (也有第三方提供或自定义的starter pom, 其核心就是对依赖进行自动化配置)

比如spring-boot-starter-parent依赖是SpringBoot父级依赖, 有了这个依赖即表明这个maven项目是SpringBoot项目, 使用它之后, 常用的包依赖可以省去version标签. 如果不想使用默认的版本, 通过在properties标签中添加属性标签来覆盖默认版本属性. 

#### xml配置

SpringBoot建议无xml配置, 但是也支持使用xml配置. 在入口类中使用@ImportResource即可导入xml配置.

如下, 一般只导入一个xml配置文件, 在这个文件中可以导入其他xml配置文件.

```java
@ImportResource({"classpath:spring.xml", "classpath:other-context.xml"})
```



### 外部配置

#### 命令行参数配置

SpringBoot项目打成的jar包, 可以直接使用`java -jar xxx.jar --server.port=8081`运行(需要在项目中依赖相关插件), 其中`--server.port=8081`就是命令行配置. 其作用和在application.properties声明是一样的, 但是优先级比application.properties高.

#### 常规属性配置

在application.properties全局配置文件中, 除了覆盖自动配置的默认值, 还可以自定义配置项. 自定义的配置项在类中通过@Value注入. 如下:

```properties
# 自定义的配置项
sc.name=suchanginclasspath
```

```java
// 注入配置项
@Value("${sc.name}")
private String name;
```

#### 类型安全的配置

当常规属性太多或这些属性可以归于一个对象时, 可以用一个Bean去关联这些属性值, 使用时在类中注入Bean, 通过get方法获取属性值, 避免了过多@Value注解. Bean需要用@ConfigurationProperties声明它是一个配置值类, 并用prefix属性声明配置项前缀. 用locations指定配置文件的路径.

如果添加location属性失效, 可以添加@Configuration和@PropertySource(“classpath:test.properties”)

```properties
# 自定义的配置项
com.sc.name=suchanginclasspath
com.sc.age=25
com.sc.desc=step by step
```

```java
// 关联配置项的bean
@Component
@ConfigurationProperties(prefix = "com.sc" locations = "classpath:test.properties")
public class SCInfo {
    private String name;
    private Integer age;
    private String desc;
    // get/set省略...
}
```

```java
// 类型安全配置的使用
@Autowired
private SCInfo scInfo;

public String test() {
    return scInfo.getName + scInfo.getAge;
}
```

#### 日志配置

SpringBoot支持多种日志框架配置, 并且都提供了自动化配置, 默认使用Logbcak, 可以在application.properties中对其进行配置.

```properties
# 配置日志文件位置
logging.file=D:/mylog/log.log
# 配置日志级别
logging.level.org.springframework.web=DEBUG
```

#### Profile配置

SpringBoot提供对不同环境调整配置的支持. 比如在classpath中提供application-dev.properties 和 application-prod.properties多种配置文件, 在application.properties中加入配置项`spring.profiles.active=prod` 即表明SpringBoot项目启动时会使用application-prod.properties配置. 它还支持命令行配置声明, 即使用`java -jar xxx.jar --spring.profiles.active=prod`也可以达到一样的效果.

除了配置文件的多版本切换, 还支持配置接口实现类的多版本切换, 如下, 两个类都实现了接口DBConnector, 并配置了不同的@Profile值, 接口DBConnector在自动注入的时候, 会根据application.properties中的`spring.profiles.active`值决定采用哪个实现类

```java
// DBConnector接口的两个实现类,配置了不同的Profile
@Component
@Profile("testdb")
public class TestDBConnector implements DBConnector {
    @Override
    public void configure() {
        System.out.println("testdb");
    }
}

@Component
@Profile("devdb")
public class DevDBConnector implements DBConnector {
    @Override
    public void configure() {
        System.out.println("devdb");
    }
}
```

```properties
# 配置文件决定接口注入的实现类
spring.profiles.active=testdb
```

### starter pom实战

参考: SpringBoot实战[汪云飞]-6.5.4

[这个例子也不错](https://blog.csdn.net/qq_37934687/article/details/78616079)

```
1.创建一个maven项目, pom.xml中添加基本的SpringBoot自动配置.
2.准备属性类
3.准备判断依据类(需要被自动配置的类)
4.准备自动配置类(重点),一般是在@ConditionOnClass情况下,执行自动配置类.在@ConditionMissingBean情况下,生成3中类的实例,生成时就会自动配置属性,自动配置的属性来源于2中的类.
5.注册配置.在src/main/resources下新建META-INF/spring.factories. 并且添加如下内容:
xxx.xxx.EnableAutoConfiguration=4中类的全限定名
(注册是因为SpringBoot会扫描这个目录中配置的自动注册类)
6.新建一个SpringBoot项目,依赖1-5中创建的maven项目,编写一个Controller方法,实验注入3中类.即可判断3中类是否能被自动配置
在配置文件application.properties中添加相关配置,以及debug=true,可以查看启动的自动配置
```








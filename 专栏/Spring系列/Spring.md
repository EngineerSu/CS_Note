## Spring

### 什么是Spring框架

![1561797385568](d:/resource/notePic/1561797385568.png)

Spring 是一种轻量级开发框架，旨在提高开发人员的开发效率以及系统的可维护性。Spring Framework是很多模块的集合, 包括核心容器, AOP, Web, 数据访问集成模块等.

Core Container 中的 Core 组件是Spring 所有组件的核心，Beans 组件和 Context 组件是实现IOC(控制反转)和DI(依赖注入)的基础，AOP组件用来实现面向切面编程。 

### 谈谈对Spring IOC和AOP的理解

[Spring-IOC源码导读-Javadoop](https://javadoop.com/post/spring-ioc)

[IOC 和 AOP 的思想理解](https://juejin.cn/post/6844903973715968007)

IOC即控制反转, 它是一种设计思想, 就是将在程序中手动创建对象的控制权, 交给了Spring的IOC容器. IOC容器就像一个工厂一样, 我们需要什么对象, 只需要配置xml文件或添加注解, 即可完成对对象的管理和注入. 这样做的好处有:

1. 不用硬编码, 比如当某个接口引用的实现类发生了改变, 只用修改配置文件即可. 假设采用硬编码方式, 所有用到这个接口引用的代码都需要改变, 对于大工程这是难以想象的.
2. 将类之间的依赖关系抽象出来, 便于项目维护. 通过查看配置文件, 即可知道类之间有哪些依赖关系.

Spring IOC的初始化过程

![1561799024776](d:/resource/notePic/1561799024776.png)

AOP即面向切面编程, 它将一些通用的功能(比如事务管理/日志记录/权限管理等)抽离出来, 通过动态代理可以方便的切入使用. 提高了代码的重用性, 降低了模块间的耦合度, 利于未来的维护和拓展

AOP的动态代理一般有两种实现方式, 基于JDK的动态代理和CGlib技术, 前者需要被代理类有实现接口, 后者则不需要. 一般对于有实现接口的类, SpringAOP会采用JDK动态代理技术

### Spring AOP 和 AspectJ AOP 有什么区别

Spring AOP是编译时增强, AspectJ AOP是运行时增强, 前者基于代理, 后者基于字节码操作, 当切面较少时, 可以使用Spring AOP, 其使用简单, 性能差别不大; 当切面较多时, 建议使用AspectJ AOP, 其是 Java 生态系统中最完整的 AOP 框架, 性能更优

- AOP有哪些实现方法？[参考](https://juejin.im/post/5c01533de51d451b80257752)
  - JDK动态代理：要求被代理类至少实现一个接口，其在运行时生成一个实现相同接口的代理类对象，并拦截所有被代理对象方法，实现切面逻辑
  - Javassist：直接操作修改编译后的字节码，加入切面逻辑
  - Cglib：在运行期生成子类，拦截方法。因此要求代理类和代理类对象执行的方法不能被final修饰

### Spring 中的 bean 的作用域(scope)有哪些

- singleton: 单例bean实例, Spring中bean默认作用域都是单例
- prototype: 每次请求都会创建一个bean实例
- request:  每一次HTTP请求都会产生一个新的bean，该bean仅在当前HTTP request内有效
- session : 每一次HTTP请求都会产生一个新的 bean，该bean仅在当前 HTTP session 内有效

### Spring中单例bean多线程安全问题怎么处理

对于单例bean实例, 在多线程写操作时会存在线程安全问题, 可以通过在单例bean实例中引入一个ThreadLocal变量, 将需要写的可变变量保存在ThreadLocal属性中.

### 讲一下Spring中bean的生命周期

对于单例bean, 其配置文件的lazy-init属性决定了spring启动时, 是否加载该bean对象, 若lazy-init="true", 则会利用反射创建一个bean实例, 并设置对象属性, 然后依次检查这个bean类是否实现某些接口, 如果实现就会执行相应的方法, 并且会检查bean是否有配置init-method属性, 如果有配置, 也会执行相应的初始化方法, 然后会注册销毁时候的回调接口. 销毁时, 会检查是否实现DisposableBean接口, 如果实现就会执行其destroy方法, 也会检查bean是否有配置destroy-method属性, 若有则执行相应方法.

对于多例bean, 其在发送请求时创建, 创建过程和单例bean类似, 但是创建成功后, 这个实例就交给开发人员了, Spring IOC容器不会处理其销毁过程(因为这个对象有多个)

![1561807914051](d:/resource/notePic/1561807914051.png)



### Spring如何解决循环依赖？

[参考](https://juejin.im/post/5c98a7b4f265da60ee12e9b2)

spring对循环依赖的处理有三种情况： 

- 构造器的循环依赖：这种依赖spring是处理不了的，直接抛出BeanCurrentlylnCreationException异常。因为 new 对象的时候就会一直循环
- 多例setter循环依赖：无法处理，每次都是创建新的对象，会 OOM
- 单例setter循环依赖：通过“三级缓存”处理循环依赖。 

Spring是自动创建对象场景的典型，它采用三级缓存的方式解决循环引用对象的创建。

一级缓存：已经完全创建好的对象的缓存； 二级缓存：正在创建中，某些成员还未装载的对象的缓存； 三级缓存：存放创建对象方法的缓存(即存放工厂，而非对象的缓存)。

假定类A对象引用类B对象，类B对象引用类A对象，在创建类A对象的过程中，需要装载B对象，这时首先会在一级缓存中寻找B对象，若没有，则在二级缓存在找，若依然没有，则会从三级缓存找到创建B的方法，并创建一个"裸"bean(未装载成员对象的bean)，放进二级缓存，然后将这个对象装载给A对象，同时还会将三级缓存中创建B的方法移除，防止重复创建，最后将A对象放入一级缓存。创建B对象时，直接在一级缓存中即可找到A对象进行装载，最后再将自己放入一级缓存中。

实际整个过程中，二级缓存承担的是解决循环引用问题的角色，个人理解三级缓存主要是为了实现上的优雅而存在的，没有也不影响循环引用问题的解决。



### SpringMVC的工作原理(流程)

![1561808498885](d:/resource/notePic/1561808498885.png)

1. 客户端（浏览器）发送请求，直接请求到 DispatcherServlet。
2. DispatcherServlet 根据请求信息调用 HandlerMapping，解析请求对应的 Handler。
3. 解析到对应的 Handler（Controller 控制器）后，开始由 HandlerAdapter 适配器处理。
4. HandlerAdapter 会根据 Handler来调用真正的处理器开处理请求，并处理相应的业务逻辑。
5. 处理器处理完业务后，会返回一个 ModelAndView 对象，Model 是返回的数据对象，View 是个逻辑上的 View。
6. ViewResolver 会根据逻辑 View 查找实际的 View。
7. DispaterServlet 把返回的 Model 传给 View（视图渲染）。
8. 把 View 返回给请求者（浏览器）

### Spring中声明bean的注解

@Component 通用注解, 添加了此注解的类会被IOC容器管理

@Repository dao层注解, 用于注解数据库相关操作类

@Service service层注解, 用于注解服务层,类 主要处理复杂的业务逻辑

@Controller web层注解, 接收请求, 调用service层返回数据给前端页面

@Autowired 依赖注入注解, 被IOC管理的bean实例可以通过此注解进行属性注入

### Spring事务的隔离级别

Spring中事务的使用, 建议使用基于注解的声明式事务, 比较简单。注意`@Transactional`声明事务只针对public方法有效，并且因为事务的实现是基于IOC的，类内部方法之间的调用时(比如A内b方法调用声明了事务的a方法)，事务也是不起作用的。

TransactionDefinition 接口中定义了五个表示隔离级别的常量, 其实和传统的事务隔离级别一致, 只是多了一个default, 采用数据库默认隔离级别

- **TransactionDefinition.ISOLATION_DEFAULT:** 使用后端数据库默认的隔离级别，Mysql 默认采用的 REPEATABLE_READ隔离级别 Oracle 默认采用的 READ_COMMITTED隔离级别.
- **TransactionDefinition.ISOLATION_READ_UNCOMMITTED:** 最低的隔离级别，允许读取尚未提交的数据变更，可能会导致脏读、幻读或不可重复读
- **TransactionDefinition.ISOLATION_READ_COMMITTED:** 允许读取并发事务已经提交的数据，可以阻止脏读，但是幻读或不可重复读仍有可能发生
- **TransactionDefinition.ISOLATION_REPEATABLE_READ:** 对同一字段的多次读取结果都是一致的，除非数据是被本身事务自己所修改，可以阻止脏读和不可重复读**，**但幻读仍有可能发生**。**
- **TransactionDefinition.ISOLATION_SERIALIZABLE:** 最高的隔离级别，完全服从ACID的隔离级别。所有的事务依次逐个执行，这样事务之间就完全不可能产生干扰，也就是说，该级别可以防止脏读、不可重复读以及幻读。但是这将严重影响程序的性能。通常情况下也不会用到该级别。

### Spring事务的传播行为

[举例分析spring事务的传播行为](https://segmentfault.com/a/1190000013341344?utm_source=tag-newest#articleHeader5)

事务传播行为是Spring框架独有的事务增强特性, 不属于的事务实际提供方数据库行为. 其为开发过程中需要业务方法的事务嵌套处理, 提供了便利的支持.

| 事务传播行为类型          | 说明                                                         |
| :------------------------ | :----------------------------------------------------------- |
| PROPAGATION_REQUIRED      | 如果当前没有事务，就新建一个事务，如果已经存在一个事务中，加入到这个事务中。这是最常见的选择。 |
| PROPAGATION_SUPPORTS      | 支持当前事务，如果当前没有事务，就以非事务方式执行。         |
| PROPAGATION_MANDATORY     | 使用当前的事务，如果当前没有事务，就抛出异常。               |
| PROPAGATION_REQUIRES_NEW  | 新建事务，如果当前存在事务，把当前事务挂起。                 |
| PROPAGATION_NOT_SUPPORTED | 以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。   |
| PROPAGATION_NEVER         | 以非事务方式执行，如果当前存在事务，则抛出异常。             |
| PROPAGATION_NESTED        | 如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则执行与PROPAGATION_REQUIRED类似的操作。 |

三个常用的事务传播行为总结

- 当外围方法未开启事务时, REQUIRED / REQUIRED_NEW / NESTED是一样的, 它们都会开启内部方法的独立事务, 且独立事务之间互不干扰.
- 当外围方法开启事务时, 三者表现不一样. REQUIRED 会加入当前事务, 即此时只有一个事务, 任何异常即使catch都会使所有回滚. REQUIRED_NEW 则挂起外部事务，并开启内部独立事务, 独立即内部事务之间, 内部与外部事务之间都不会受对方影响. NESTED 会开启外部事务的嵌套子事务, 子事务可以独立回滚, 即子事务内部异常时catch, 则外部事务不感知不回滚, 但是外部事务一旦异常, 则所有子事务需要回滚(不同于独立事务)

### Spring中用了哪些设计模式

- **工厂设计模式** : Spring使用工厂模式通过 BeanFactory、ApplicationContext 创建 bean 对象。ApplicationContext 有三个实现类: 最常用的是ClassPathXmlApplication, 其读取classpath路径下的配置文件, 进行bean实例的加载.

- **单例设计模式** : Spring 中的 Bean 默认都是单例的。在我们的系统中，有一些对象其实我们只需要一个，比如说：线程池、缓存、对话框、注册表、日志对象、充当打印机、显卡等设备驱动程序的对象。事实上，这一类对象只能有一个实例，如果制造出多个实例就可能会导致一些问题的产生，比如：程序的行为异常、资源使用过量、或者不一致性的结果。使用单例模式可以节省创建对象的开销 / 减轻GC压力 

- **代理设计模式** : Spring AOP 功能的实现。动态代理可以方便的增强类的功能, 在开发中实现模块的解耦和复用, 便于未来的维护和升级.

- **模板方法模式** : Spring 中 jdbcTemplate、hibernateTemplate 等以 Template 结尾的对数据库操作的类，它们就使用到了模板模式。

- **适配器模式** :Spring AOP 的增强或通知(Advice)使用到了适配器模式、Spring MVC 中也是用到了适配器模式适配Controller

- **观察者模式:** 观察者模式是一种对象行为型模式。它表示的是一种对象与对象之间具有依赖关系，当一个对象发生改变的时候，这个对象所依赖的对象也会做出反应。Spring 事件驱动模型就是观察者模式很经典的一个应用。Spring 事件驱动模型非常有用，在很多场景都可以解耦我们的代码。比如我们每次添加商品的时候都需要重新更新商品索引，这个时候就可以利用观察者模式来解决这个问题。

    Spring 事件驱动模型有三种角色: 事件角色, 事件发布者角色, 事件监听者角色. 通过ApplicationEventPublisher 类的publishEvent()方法发布事件对象, 相应的事件监听者类的onApplicationEvent() 方法就会被触发. Spring中事件流程如下:

    1. 定义一个事件: 实现一个继承自 ApplicationEvent，并且写相应的构造函数；
    2. 定义一个事件监听者：实现 ApplicationListener 接口，重写 onApplicationEvent() 方法；
    3. 使用事件发布者发布消息: 可以通过 ApplicationEventPublisher 的 publishEvent() 方法发布消息。
    
- 责任链模式：请求进来，会经过各种 Filter，它们各自处理自己复杂的内容

- 依赖倒置设计模式：IOC 本身就是这种思想



### 无事务方法调用@Transactionnal方法

同一类中a()方法没有@Transactional 注解，在其内部调用有@Transactional 注解的方法，有@Transactional 注解的方法b()的事务被忽略，不会发生回滚。

因为：声明式事务基于Spring AOP实现,将具体业务逻辑与事务处理解耦，在 Spring 的 AOP 代理下，只有目标方法由外部调用，目标方法才由 Spring 生成的代理对象来管理，这会造成自调用问题。
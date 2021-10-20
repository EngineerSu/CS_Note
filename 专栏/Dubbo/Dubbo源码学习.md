## Dubbo源码学习

Dubbo的动态配置中心(基于zookeeper实现)的节点结构，不同应用具有不同的application名称，其配置存储在其应用名节点下；所有服务治理相关的配置都是全局配置，即存在`/dubbo/config/dubbo/`下，服务治理配置主要包括：覆盖规则 / 标签路由 / 条件路由

![1566466621029](d:/resource/notePic/1566466621029.png)



Dubbo SPI 的相关逻辑被封装在了 ExtensionLoader 类中，通过 ExtensionLoader，我们可以加载指定的实现类。Dubbo SPI 所需的配置文件需放置在 META-INF/dubbo 路径下，配置内容如下。

```
optimusPrime = org.apache.spi.OptimusPrime
bumblebee = org.apache.spi.Bumblebee
```

测试SPI

```java
public class DubboSPITest {

    @Test
    public void sayHello() throws Exception {
        ExtensionLoader<Robot> extensionLoader = 
            ExtensionLoader.getExtensionLoader(Robot.class);
        Robot optimusPrime = extensionLoader.getExtension("optimusPrime");
        optimusPrime.sayHello();
        Robot bumblebee = extensionLoader.getExtension("bumblebee");
        bumblebee.sayHello();
    }
}
```



### Dubbo SPI

SPI 全称为 Service Provider Interface，是一种服务发现机制。SPI 的本质是将接口实现类的全限定名配置在文件中，并由服务加载器读取配置文件，加载实现类。这样可以在运行时，动态为接口替换实现类。正因此特性，我们可以很容易的通过 SPI 机制为我们的程序提供拓展功能。SPI 机制在第三方框架中也有所应用，比如 Dubbo 就是通过 SPI 机制加载所有的组件。不过，Dubbo 并未使用 Java 原生的 SPI 机制，而是对其进行了增强，使其能够更好的满足需求。在 Dubbo 中，SPI 是一个非常重要的模块。

#### Dubbo SPI示例

Dubbo SPI 的相关逻辑被封装在了 ExtensionLoader 类中，通过 ExtensionLoader，我们可以加载指定的实现类。Dubbo SPI 所需的配置文件需放置在 META-INF/dubbo 路径下，配置内容如下，这里配置了一个接口的两个实现类，每个实现类都有对应的一个name

```
optimusPrime = org.apache.spi.OptimusPrime
bumblebee = org.apache.spi.Bumblebee
```

要求：拓展类的接口必须都有`@SPI`注解

获取过程首先是从`ExtensionLoader`类的缓存中获取`ExtensionLoader<T>`实例，如果没有就创建该实例，然后调用实例的`getExtension(String name)`方法：从`cachedInstance`缓存中获取对象实例，缓存没有命中就使用`createExtension(String name)`方法创建实例对象，创建过程主要有以下：

- 从配置文件中加载所有的拓展类，可得到“配置项名称”到“配置类，加载Class”的映射关系表，加载过程中会更新一些缓存
- 通过Class反射创建对象
- 分析对象的setter方法，使用`objectFactory.getExtension(Class clazz, String name)`为其注入属性对象(IOC)
- 将拓展对象包裹在相应的 Wrapper 对象中(AOP有关)



### 自适应拓展

自适应拓展类的核心实现：在拓展接口的方法被调用时(通过代理对象调用)，通过 SPI 加载具体的拓展对象，并调用拓展对象的同名方法。

`@Adaptive`注解一般加在接口方法上，表示拓展的加载逻辑需由框架自动生成。标明了`@Adaptive`注解的接口方法一般都会有一个URL方法参数，通过这个URL参数，使用`ExtensionLoader<T>`的`getExtension(String name)`方法获取指定接口实现类对象(name从URL参数中获取)，然后调用该对象的接口方法，实现了在运行时根据URL选择不同接口实现类执行接口方法。

Dubbo中提供了一个`AdaptiveExtensionLoader<T>`类， 通过它的`getAdaptiveExtension()`方法，可以生成动态自适应拓展类Class(字节码生成技术)，这个动态拓展类的方法逻辑就是上面说的三步：

- 从 URL 中获取 接口实现类 名称
- 通过 SPI 加载具体的 接口实现类 对象
- 调用目标方法

比如有一个Protocol 接口，它有一个`@Adaptive`修饰的refer方法

```java
public interface Protocol {
    public Invoker refer(Class clazz, URL url);
}
```

`AdaptiveExtensionLoader<T>`的`getAdaptiveExtension()`方法生成的动态拓展类代码如下

```java
public com.alibaba.dubbo.rpc.Invoker refer(java.lang.Class arg0, com.alibaba.dubbo.common.URL arg1) {
    // 方法体
    com.alibaba.dubbo.rpc.Protocol extension = (com.alibaba.dubbo.rpc.Protocol) ExtensionLoader
    .getExtensionLoader(com.alibaba.dubbo.rpc.Protocol.class).getExtension(extName);
return extension.refer(arg0, arg1);
}
```

#### Question

- 自适应拓展机制生成的拓展对象，本质也是通过SPI机制调用真实接口实现类对象去执行方法，那既然这样为什么不直接在代理类里通过SPI机制获取对象去执行方法，还要生成拓展对象呢？

    因为自适应拓展机制 = @Adaptive + SPI，@Adaptive类可以正常使用，也可以根据URL参数在运行时通过SPI机制动态替换成其他对象



### 服务导出

Dubbo 服务导出过程始于 Spring 容器发布刷新事件，Dubbo 在接收到事件后，会立即执行服务导出逻辑。整个逻辑大致可分为三个部分，第一部分是前置工作，主要用于检查参数，组装 URL。第二部分是导出服务，包含导出服务到本地 (JVM)，和导出服务到远程两个过程。第三部分是向注册中心注册服务，用于服务发现。



### 服务引用原理

Dubbo 服务引用的时机有两个，第一个是在 Spring 容器调用 ReferenceBean 的 afterPropertiesSet 方法时引用服务，第二个是在 ReferenceBean 对应的服务被注入到其他类中时引用。这两个引用服务的时机区别在于，第一个是饿汉式的，第二个是懒汉式的。默认情况下，Dubbo 使用懒汉式引用服务。如果需要使用饿汉式，可通过配置 <dubbo:reference> 的 init 属性开启。下面我们按照 Dubbo 默认配置进行分析，整个分析过程从 ReferenceBean 的 getObject 方法开始。当我们的服务被注入到其他类中时，Spring 会第一时间调用 getObject 方法，并由该方法执行服务引用逻辑。按照惯例，在进行具体工作之前，需先进行配置检查与收集工作。接着根据收集到的信息决定服务用的方式，有三种，第一种是引用本地 (JVM) 服务，第二是通过直连方式引用远程服务，第三是通过注册中心引用远程服务。不管是哪种引用方式，最后都会得到一个 Invoker 实例。如果有多个注册中心，多个服务提供者，这个时候会得到一组 Invoker 实例，此时需要通过集群管理类 Cluster 将多个 Invoker 合并成一个实例。合并后的 Invoker 实例已经具备调用本地或远程服务的能力了，但并不能将此实例暴露给用户使用，这会对用户业务代码造成侵入。此时框架还需要通过代理工厂类 (ProxyFactory) 为服务接口生成代理类，并让代理类去调用 Invoker 逻辑。避免了 Dubbo 框架代码对业务代码的侵入，同时也让框架更容易使用。



### 服务目录

服务目录中存储了一些和服务提供者有关的信息，通过服务目录，服务消费者可获取到服务提供者的信息，比如 ip、端口、服务协议等。通过这些信息，服务消费者就可通过 Netty 等客户端进行远程调用。实际上服务目录在获取注册中心的服务配置信息后，会为每条配置信息生成一个 Invoker 对象，并把这个 Invoker 对象存储起来，这个 Invoker 才是服务目录最终持有的对象。Invoker 有什么用呢？看名字就知道了，这是一个具有远程调用功能的对象。

所以服务目录可以看做是 Invoker 集合，且这个集合中的元素会随注册中心的变化而进行动态调整。

服务目录目前内置的实现有两个，分别为 StaticDirectory 和 RegistryDirectory，它们均是 AbstractDirectory 的子类。AbstractDirectory 实现了 Directory 接口，这个接口包含了一个重要的方法定义，即 list(Invocation)，用于列举 Invoker。下面我们来看一下他们的继承体系图。

![1566543527260](d:/resource/notePic/1566543527260.png)

如上，Directory 继承自 Node 接口，Node 这个接口继承者比较多，像 Registry、Monitor、Invoker 等均继承了这个接口。这个接口包含了一个获取配置信息的方法 getUrl，实现该接口的类可以向外提供配置信息。另外，大家注意看 RegistryDirectory 实现了 NotifyListener 接口，当注册中心节点信息发生变化后，RegistryDirectory 可以通过此接口方法得到变更信息，并根据变更信息动态调整内部 Invoker 列表。



### 服务路由

服务目录在刷新 Invoker 列表的过程中，会通过 Router 进行服务路由，筛选出符合路由规则的服务提供者。服务路由包含一条路由规则，路由规则决定了服务消费者的调用目标，即规定了服务消费者可调用哪些服务提供者。Dubbo 目前提供了三种服务路由实现，分别为条件路由 ConditionRouter、脚本路由 ScriptRouter 和标签路由 TagRouter。其中条件路由是我们最常使用的。

条件路由规则由两个条件组成，分别用于对服务消费者和提供者进行匹配。比如有这样一条规则：

```
host = 10.20.153.10 => host = 10.20.153.11
```

该条规则表示 IP 为 10.20.153.10 的服务消费者**只可**调用 IP 为 10.20.153.11 机器上的服务，不可调用其他机器上的服务。条件路由规则的格式如下：

```
[服务消费者匹配条件] => [服务提供者匹配条件]
```

如果服务消费者匹配条件为空，表示不对服务消费者进行限制。如果服务提供者匹配条件为空，表示对某些服务消费者禁用服务。官方文档中对条件路由进行了比较详细的介绍，大家可以参考下，这里就不过多说明了。



### 集群容错

集群模块处于服务提供者和消费者之间，对于服务消费者来说，集群可向其屏蔽服务提供者集群的情况，使其能够专心进行远程调用。

![1566545738056](d:/resource/notePic/1566545738056.png)

集群工作过程可分为两个阶段，第一个阶段是在服务消费者初始化期间，集群 Cluster 实现类为服务消费者创建 Cluster Invoker 实例，即上图中的 merge 操作。第二个阶段是在服务消费者进行远程调用时。以 FailoverClusterInvoker 为例，该类型 Cluster Invoker 首先会调用 Directory 的 list 方法列举 Invoker 列表（可将 Invoker 简单理解为服务提供者）。Directory 的用途是保存 Invoker，可简单类比为 List<Invoker>。其实现类 RegistryDirectory 是一个动态服务目录，可感知注册中心配置的变化，它所持有的 Invoker 列表会随着注册中心内容的变化而变化。每次变化后，RegistryDirectory 会动态增删 Invoker，并调用 Router 的 route 方法进行路由，过滤掉不符合路由规则的 Invoker。当 FailoverClusterInvoker 拿到 Directory 返回的 Invoker 列表后，它会通过 LoadBalance 从 Invoker 列表中选择一个 Invoker。最后 FailoverClusterInvoker 会将参数传给 LoadBalance 选择出的 Invoker 实例的 invoker 方法，进行真正的远程调用。

**Failover Cluster**：调用失败时，会自动切换 Invoker 进行重试，是Dubbo的默认容错策略

**Failback Cluster**：调用失败返回空结果，会定时重试，适合执行消息通知等操作

**Failfast Cluster**： 只会进行一次调用，失败后立即抛出异常。适用于幂等操作，比如新增记录。

**Failsafe Cluster**：调用出现异常时，仅打印异常，不抛出，适用于写入审计日志等操作

**Forking Cluster**：通过线程池创建多个线程，并发调用多个服务提供者，只要有一个服务提供者成功返回了结果，doInvoke 方法就会立即结束运行。适用于实时读场景，并行写可能不安全

**Broadcast Cluster**：逐个调用每个服务提供者，如果其中一台报错，在循环调用结束后抛出异常，适用于通知所有提供者更新缓存或日志等本地资源信息



### 负载均衡

**LoadBalance**： 

- **Random** ：权值随机，比如三个服务的权值分别是5 3 2，那么就建立一个一维槽，长度为10，权值2 3 5分别代表0 ~ 2，2 ~ 5，5 ~ 10三个区间。生成一个0 ~ 10之间的随机数，随机数掉落在哪个槽就选哪个服务

- **RoundRobin** ：平滑加权轮询算法，每个服务有两个权值，weight和currentWeight，初始currentWeight都为0

    每次选择前，currentWeight += weight，然后选择最大的currentWeight 作为当前被选中的服务，被选中的服务的currentWeight 需要在选择完成后(下一次选择前)减去所有服务的currentWeight 总和。这种算法保证了权值轮询时，低权值的节点会穿插被选择，而不是短期内所有服务都跑到高权值节点

    [平滑加权轮询算法的严格证明](./平滑加权轮询算法的严格证明.md)

- **LeastActive** ：最小活跃数优先，活跃数越小，表明该机器效率越高，其收到请求的优先级越高。起初所有服务都有一个active值为0，当它接收了一个请求，active+1，完成了一个请求active-1，发生异常时不变，所以运行后active是一个不小于0的数，同一时刻越小表明其效率越高。如果active最小值一样，选权值高的，如果还一样，随机。

- **ConsistentHash** ：一致性hash，保证了即使节点很少，请求也能均匀落在各节点上，并且当集群需要伸缩时，数据迁移量会比较小

权值计算过程有一个**降权逻辑**：当服务运行时长小于服务预热时间时，对服务进行降权，避免让服务在启动之初就处于高负载状态。服务预热可以认为是适应JVM预热，JVM预热是因为类加载需要时间，很多类不是JVM启动就加载的，而是用到的时候才加载。



### 服务调用

Dubbo 服务调用过程如下

![1566784349933](d:/resource/notePic/1566784349933.png)

Dubbo 支持同步和异步两种调用方式，其中异步调用还可细分为“有返回值”的异步调用和“无返回值”的异步调用。所谓“无返回值”异步调用是指服务消费方只管调用，但不关心调用结果，此时 Dubbo 会直接返回一个空的 RpcResult。若要使用异步特性，需要服务消费方手动进行配置。默认情况下，Dubbo 使用同步调用方式。

Dubbo 实现同步和异步调用比较关键的一点就在于由谁调用 ResponseFuture 的 get 方法。同步调用模式下，由框架自身调用 ResponseFuture 的 get 方法。异步调用模式下，框架将ResponseFuture 添加到上下文中，由用户调用该方法。

Dubbo 默认使用 Javassist 框架为服务接口生成动态代理类

#### Dubbo线程派发模型

Dubbo 将底层通信框架中接收请求的线程称为 IO 线程。如果一些事件处理逻辑可以很快执行完，比如只在内存打一个标记，此时直接在 IO 线程上执行该段逻辑即可。但如果事件的处理逻辑比较耗时，比如该段逻辑会发起数据库查询或者 HTTP 请求。此时我们就不应该让事件处理逻辑在 IO 线程上执行，而是应该派发到线程池中去执行。原因也很简单，IO 线程主要用于接收请求，如果 IO 线程被占满，将导致它不能接收新的请求。

Dispatcher 真实的职责创建具有线程派发能力的 ChannelHandler，比如 AllChannelHandler、MessageOnlyChannelHandler 和 ExecutionChannelHandler 等，其本身并不具备线程派发能力。Dubbo 支持 5 种不同的线程派发策略，下面通过一个表格列举一下。

|    策略    |                             用途                             |
| :--------: | :----------------------------------------------------------: |
|    all     | 所有消息都派发到线程池，包括请求，响应，连接事件，断开事件等 |
|   direct   |      所有消息都不派发到线程池，全部在 IO 线程上直接执行      |
|  message   | 只有**请求**和**响应**消息派发到线程池，其它消息均在 IO 线程上执行 |
| execution  | 只有**请求**消息派发到线程池，不含响应。其它消息均在 IO 线程上执行 |
| connection | 在 IO 线程上，将连接断开事件放入队列，有序逐个执行，其它消息派发到线程池 |

对于双向通信，HeaderExchangeHandler 首先向后进行调用，得到调用结果。然后将调用结果封装到 Response 对象中，最后再将该对象返回给服务消费方。如果请求不合法，或者调用失败，则将错误信息封装到 Response 对象中，并返回给服务消费方。

返回给消费方的Future对象通过编号，返回给相应的用户线程池。



### 服务治理和配置管理

路由规则调整(标签路由 /  条件路由(黑白名单) )

动态配置：在不重启服务的情况下，动态改变调用行为。权重调节 / 负载均衡

优先级：服务配置 > 应用配置 > 全局配置


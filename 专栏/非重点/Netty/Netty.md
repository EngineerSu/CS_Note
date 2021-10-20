## Netty

### 定义

> Netty 是一个广泛使用的 Java 网络编程框架, 它为客户端/服务端的网络编程提供了强大便携的API, 隐藏了复杂的细节.

### 概念性知识

[操作系统的5种IO模型](https://note.youdao.com/ynoteshare1/index.html?id=1674bf550774ef96fd4a89c0f60a2f05&type=note)

**BIO通信线程阻塞的根本原因**

传统的BIO通信中, 服务端阻塞在accept方法, 客户端每发送一个请求, 服务端通过accept线程去开启一个新的线程处理这个请求, 如果客户端并发量超大, 开启的新线程数就会暴增, 线程对于JVM来说是宝贵的资源, 过多的线程数会使系统崩溃.

通过伪异步IO可以轻微缓解上述问题. 即服务端的accept线程在接收到客户端请求时, 不再开启新线程, 而是统一扔进线程池去执行, 线程池的线程数量固定, 通过消息队列来控制进入线程池的请求. 这样就避免了线程数暴增带来的系统崩溃问题.

但是以上并没有解决根本问题: 输入流和输出流的所有读和写操作都是同步阻塞的, 比如在读操作时, 如果数据没读取完, 那么线程就一直阻塞, 写操作也一样, 而网络通信中数据的读和写依赖于客户端IO性能和网络IO状况, 这都是不可靠的因素. 假设目前线程池所有线程对应的客户端IO性能很差, 那么就会阻塞很久, 而其他用户的请求在消息队列中迟迟不能被消费, 就会让用户体验为系统不可用, 并且消息队列容量是有限的, 高阻塞存在消息丢失的风险.

**同步和异步 阻塞和非阻塞**

同步是指进行方法调用时, 该方法如果等到执行完毕才返回结果, 异步则是立马返回, 但是等执行完毕再通知结果

阻塞是指线程在执行某个方法时, 未拿到返回值就被挂起, 拿到返回值后才被唤醒继续执行任务, 非阻塞则是相反, 即使未拿到返回值, 线程也会继续执行其他的任务.

同步异步的对象是调用方法, 阻塞非阻塞的对象是线程. 所以同步不等于阻塞, 即使要等执行完毕才返回结果, 在这个等待过程中, 线程仍然可以执行其他任务.

**NIO编程的优点**

1. 客户端发起的连接都是非阻塞的, 如果没有连接成功就会在Selector中注册OP_CONNECT状态等待后续结果, 不会像之前客户端那样被同步阻塞
2. SocketChannel的读写操作都是非阻塞的, 没有可读写数据时不会同步等待结果, 而是直接返回, 这样IO通信线程可以去处理其他链路, 而不是同步等待这个链路可用("不会在一棵树上吊死")
3. 线程模型的优化: 由于Selector在Linux等主流操作系统中是通过epoll实现, 没有连接数量限制, 因此一个Seletor线程可以处理成千上万的客户端, 并且其性能不会随着连接数增加而线性下降. Selector的轮询是通过系统调用实现的, 因此当其发现某个注册的Channel状态变化之后, 就会通知IO线程来处理, 而不会让IO线程阻塞在某个Channel.

需要注意的是, NIO是非阻塞IO(运用了多路复用IO技术), 并不是异步IO, 传统的IO线程在读写时, 若读不到数据则会阻塞, NIO通过多路复用器(Selector)做轮询, 有新连接时就会注册, 连接读写状态就位时才开启读写, 不会阻塞在状态未就位的连接上, 实现了一个IO线程处理多个客户端连接.

**AIO:NIO2.0**

NIO中讲的异步并非真的异步, 而是使用IO多路复用技术实现了非阻塞, AIO才是真正的异步IO(非阻塞), 其通过回调机制实现了异步. (回调机制将读写作为回调函数, 被动回调)

**回调机制**

回调机制就是相互调用, 比如A调用B的某个方法, 在这个方法中B又会调用A的某个方法. 其作用是实现通知, 即在异步的时候让A知道它调用B的方法已经执行完毕.

实现形式为, B的方法参数中有一个接口CallBackHandler, B的方法在执行完自己的逻辑后, 会执行CallBackHandler对象的callBack()方法, 实现回调. 而这个方法往往由调用方A在调用时, 使用匿名类实现. 其作用即A想要的通知效果, 并且还可以将依赖于B方法执行逻辑结果的步骤, 放在这个callBack方法中, 这样就可以实现异步调用B方法了.

形象例子可以参考: https://www.cnblogs.com/heshuchao/p/5376298.html

### Netty入门开发

**传统的NIO服务端开发过程**

![1561098421181](d:/resource/notePic/1561098421181.png)

使用Java原生NIO的API进行开发很繁琐, 而且有bug. Netty开发则简单很多

#### Netty服务端开发-TimeServer

服务端会有一个快速启动类ServerBootstrap, 配置这个类对象的group, channel, port等属性. 即可快速开启服务

启动类里配置的childHandler里提供了实际的服务, TimeServerHandler继承了ChannelInboundHandlerAdapter类, 重写它的channelRead等方法, 即相当于传统NIO的Seletor轮询时发生了特定事件的执行逻辑.

```java
package com.jacksu.learn.server;
import ...

public class TimeServer {

    public void bind (int port) throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // NIO服务端的辅助启动类
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new TimeServerHandler());
                        }
                    });

            // 绑定端口同步等待成功
            ChannelFuture future = sb.bind(port).sync();

            // 等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } finally {
            // 释放线程资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        Integer port = CommonUtils.getPort(args);

        System.out.println("TimeServer start");
        new TimeServer().bind(port);
        System.out.println("TimeServer end");
    }
}

// TimeServerHandler类
class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private static final String TIME_ORDER = "QUERY TIME ORDER";

    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
//        String body = CommonUtils.getMsg(msg);
        String body = (String)msg;
        System.out.println("TimeServer receive order: " + body + ", counter is " + ++counter);
        String currentTime = TIME_ORDER.equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf response = Unpooled.copiedBuffer(currentTime.getBytes());
        context.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
```

#### Netty客户端开发-TimeClient

客户端的开发和服务端形式很像, 只是启动类变成了Bootstrap, 并且绑定的Channel是NioSocketChannel(服务端绑定的channel是NioServerSocketChannel)

```java
package com.jacksu.learn.client;

import ...

public class TimeClient {

    public void connect(String host, int port) throws Exception {

        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bs = new Bootstrap();
            bs.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new TimeClientHandler());
                        }
                    });
            // 发起异步连接操作
            ChannelFuture future = bs.connect(host, port).sync();

            // 等待客户端连接关闭
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        Integer port = CommonUtils.getPort(args);

        System.out.println("TimeClient start");
        new TimeClient().connect("127.0.0.1", port);
        System.out.println("TimeClient end");
    }


}

// TimeClientHandler类
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private static final String TIME_ORDER = "QUERY TIME ORDER" + System.getProperty("line.separator");

    private int counter;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte[] bytes = TIME_ORDER.getBytes();
        ByteBuf buffer = null;
        for (int i = 0; i < 10; i++) {
            buffer = Unpooled.buffer(bytes.length);
            buffer.writeBytes(bytes);
            ctx.writeAndFlush(buffer);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String time = (String)msg;
//        String time = CommonUtils.getMsg(msg);
        System.out.println("TimeClient receive response: " + time + ", counter is " + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
```

#### 分隔符和定长解码器

NIO采用的是TCP连接, TCP数据传输时是采用流的形式, 如果不作任何限制时, 会根据TCP协议以及套接口缓冲区大小等实际情况, 对发送的数据流进行粘包和拆包, 而程序往往需要根据程序指定的规则进行数据接收和发送, 因此可以使用解码器, 解码器在重写initChannel方法中通过socketChannel.pipeline().addLast()方法加入

LineBasedFrameDecoder: 数据流遇到"/r"或"/r/n"时, 就"拆包"一次

DelimiterBasedFrameDecoder: 数据流遇到指定字符串时, 就"拆包"一次

FixedLengthFrameDecoder: 数据流达到指定长度时, 就"拆包"一次

StringDecoder: 将读取的字节流变成字符串对象

### netty中使用编解码

序列化和反序列化需要编解码的支持, Java原生的序列化和反序列化技术很少被采用, 尽管使用很方便(实现Serializable接口, 提供serialVersionUID即可), 但是它有三个问题导致不适用于生产环境: 不支持跨语言 / 性能差 / 占用容量大

主要学习了谷歌的protocolbuffer(简称protobuf)在netty中的使用, 大致步骤是编写符合protobuf风格的.proto文件, 通过工具生成类, 然后使用这个类即可. 这个类的特点是, 所有的msg实例都是通过其Builder生成, 并且生成后只有getter, 没有setter. 生成类可以高效的序列化和反序列化, 但是在netty中其序列化和反序列化的步骤由netty提供的编解码器处理, 不用手动处理. 用户使用时, 可以直接write或read生成类对象

```
// 根据.proto文件生成代码命令
protoc.exe --java_out=.\src .\netty\SubscribeResp.proto
```

`--java_out=.\src`指定了生成类的位置, `.\netty\SubscribeResp.proto`指定了.proto文件位置

```
// .proto文件示例
package netty;
option java_package = "com.jacksu.learn.pojo";
option java_outer_classname = "SubscribeRespProto";

message SubscribeResp{
	required int32 subReqID = 1;
	required int32 respCode = 2;
	required string desc = 3;
}
```

其中package指定的是.proto文件存在的文件夹名称, 而java_package才是生成类的实际包结构,  java_outer_classname是生成类的名称, 这个类有若干个内部类, 每个message都是一个内部类. 每个内部类都有一个Builder.

详情可以参考: [protocol在java中的使用](https://www.jianshu.com/p/1bf426a9f8f4)

**使用protobuf编解码器的netty服务端示例**

```java
public class TestServer {

    public void bind(int port) throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // NIO服务端辅助启动类
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 解决半包问题
                            socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            // 解码时需要提供待解码的对象
                            socketChannel.pipeline().addLast(new ProtobufDecoder(SubscribeRespProto.SubscribeResp.getDefaultInstance()));
                            socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            // 提供了protobuf编码器,可以直接write生成类
                            socketChannel.pipeline().addLast(new ProtobufEncoder());
                            socketChannel.pipeline().addLast(new TestServerHandler());
                        }
                    });

            // 绑定端口同步等待成功
            ChannelFuture future = sb.bind(port).sync();

            // 等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } finally {
            // 释放线程资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        Integer port = CommonUtils.getPort(args);

        System.out.println("TestServer start");
        new TestServer().bind(port);
    }
}

// TestServerHandler类
public class TestServerHandler extends ChannelInboundHandlerAdapter {

    private int counter;

    // 表示从客户端读取数据后
    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        // 由于使用了解码器,所以可以直接强制转换
        SubscribeRespProto.SubscribeResp resp = (SubscribeRespProto.SubscribeResp) msg;     
        System.out.println("TestServer receive msg: " + resp);
        System.out.println("Counter is " + ++counter);
        System.out.println("==============");
        // 由于使用了编码器,所以可以直接write对象
        context.write(getResp(resp.getSubReqID()));
    }
    // 使用Builder获取message实例对象
    private SubscribeRespProto.SubscribeResp getResp(int id) {
        SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
        builder.setSubReqID(id);
        builder.setRespCode(1);
        builder.setDesc("this is rsponse from server");
        return builder.build();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}
```



### Netty和Tomcat有什么区别？

Netty和Tomcat最大的区别就在于通信协议，Tomcat的实质是一个基于http协议的web容器，但是Netty不一样，他能通过编程自定义各种协议，因为netty能够自定义编解码器

另外, 它们的角色不同. netty作为网络编程框架, 用于客户端和服务端的开发, 开发好的客户端可以向服务端发送自定义协议的请求, 因此netty可以认为是一个半成品(工具). 而tomcat本身就是一个web容器, 其内置了servlet容器和jsp容器, 能够响应http协议的请求, 它可以认为是一个成品.

### Netty的三大优势

**封装好**

Java原生支持NIO编程, 但是原生的NIO编程非常繁琐复杂, 并且基于epoll存在空轮询的bug. netty解决了这个bug, 并且优雅的封装, 易于使用.

**并发高**

netty是基于NIO的网络通信框架, NIO使用一个selector线程可以同时接收多个请求, (**基于epoll的轮询不受请求个数限制**), 提高了线程使用率, 天生支持高并发

**传输快**

传输快主要有两个原因: NIO的优势 / netty的零拷贝

netty的零拷贝体现有三个地方: 

1. Netty的接收和发送ByteBuffer采用DIRECT BUFFERS，使用堆外直接内存进行Socket读写，不需要进行字节缓冲区的二次拷贝。如果使用传统的堆内存（HEAP BUFFERS）进行Socket读写，JVM会将堆内存Buffer拷贝一份到直接内存中，然后才写入Socket中。相比于堆外直接内存，消息在发送过程中多了一次缓冲区的内存拷贝。
2. Netty提供了组合Buffer对象，可以聚合多个ByteBuffer对象，用户可以像操作一个Buffer那样方便的对组合Buffer进行操作，避免了传统通过内存拷贝的方式将几个小Buffer合并成一个大的Buffer。
3.  Netty的文件传输采用了transferTo方法，它可以直接将文件缓冲区的数据发送到目标Channel，避免了传统通过循环write方式导致的内存拷贝问题。

### Netty的重要组件

**Channel**

![1562676834410](d:/resource/notePic/1562676834410.png)

Channel，表示一个连接，可以理解为每一个请求，就是一个Channel。

 **ChannelHandler**，核心处理业务就在这里，用于处理业务请求。

ChannelHandlerContext，用于封装传输的业务数据。

ChannelPipeline，用于保存处理过程需要用到的ChannelHandler和ChannelHandlerContext。

**ByteBuf**

netty自用存储字节的容器, 弥补了Java自带ByteBuffer的一些不足. 

比如上面讲到netty零拷贝的两个实现, 都是使用不同模式ByteBuf做到的.

**Codec**

Netty中的编码/解码器, 通过自定义编解码器可以实现自定义的协议. 当然netty本身也提供了很多编解码器的支持, 比如HttpRequestDecoder和HttpResponseEncoder

​	

### TCP的粘包拆包问题

TCP是基于流套接字的协议，它并不知道上层业务数据，因此它会根据缓冲区大小对数据包进行一定的分割和组合，从业务上来看就出现了粘包拆包问题。Netty提供了定长解码器等Decoder解决这个问题。
# Java Web 程序在 Tomcat 上是如何运行的

一个 JVM 是一个进程，JVM 上跑 Tomcat，Tomcat 上可以部署多个应用。这样的话，每个跑在 Tomcat 上的应用是一个线程吗？如果一个应用 crash 了，其他应用也会crash吗？

理解程序运行时的执行环境，直观感受程序是如何运行的，对我们开发和维护软件很有意义。我们以上面这个问题为例，看下 Java Web 程序的运行时环境是什么样的，来梳理下进程、线程、应用、Web 容器、Java 虚拟机和操作系统之间的关系。

我们用 Java 开发 Web 应用，开发完成，编译打包以后得到的是一个 war 包，这个 war 包放入 Tomcat 的应用程序路径下，启动 Tomcat 就可以通过 HTTP 请求访问这个 Web 应用了。

在这个场景下，进程是哪个？线程有哪些？Web 程序的 war 包是如何启动的？HTTP 请求如何被处理？Tomcat 在这里扮演的是什么角色？JVM 又扮演什么角色？

首先，我们是通过执行 Tomcat 的 Shell 脚本启动 Tomcat 的，而在 Shell 脚本里，其实启动的是 Java 虚拟机，大概是这样一个 Shell 命令：

```bash
java org.apache.catalina.startup.Bootstrap "$@" start
```

所以我们在 Linux 操作系统执行 Tomcat 的 Shell 启动脚本，Tomcat 启动以后，其实在操作系统里看到的是一个JVM 虚拟机进程。这个虚拟机进程启动以后，加载 class 进来执行，首先加载的就这个org.apache.catalina.startup.Bootstrap类，这个类里面有一个main()函数，是整个 Tomcat 的入口函数，JVM 虚拟机会启动一个主线程从这个入口函数开始执行。

主线程从 Bootstrap 的 main() 函数开始执行，初始化 Tomcat 的运行环境，这时候就需要创建一些线程，比如负责监听80端口的线程，处理客户端连接请求的线程，以及执行用户请求的线程。创建这些线程的代码是 Tomcat 代码的一部分。

初始化运行环境之后，Tomcat 就会扫描 Web 程序路径，扫描到开发的 war 包后，再加载 war 包里的类到 JVM。因为 Web 应用是被 Tomcat 加载运行的，所以我们也称 Tomcat 为 Web 容器。

如果有外部请求发送到 Tomcat，也就是外部程序通过 80 端口和 Tomcat 进行 HTTP 通信的时候，Tomcat 会根据 war 包中的 web.xml 配置，决定这个请求 URL 应该由哪个 Servlet 处理，然后 Tomcat 就会分配一个线程去处理这个请求，实际上，就是这个线程执行相应的 Servlet 代码。如何分配线程呢？Tomcat使用了线程池，在用户发起的一个访问web资源(servlet或者jsp页面)的请求过来时，如果线程池里面有空闲的线程，那么会在线程池里面取一个线程来处理该请求，一旦该线程当前在处理请求(该线程中调用service方法或者doGet/doPost方法)，其他请求就不会被分配到该线程上，直到该请求处理完成。请求处理完成后，会将该线程重新加入线程池。

Tomcat启动之初，会根据配置server.xml等xml及webapps下部署的app，在内存中建立起一个全局的host->context->servlet的映射关系，Tomcat有一个connector组件，负责接收socket，并进行HTTP协议的解析，然后从进程全局的线程池中随机获取一个线程，根据解析出的URL，在前面的映射关系中定位到相应的servlet进行业务处理

Tomcat 启动的时候，启动的是 JVM 进程，这个进程首先是执行 JVM 的代码，而 JVM 会加载 Tomcat 的 class 执行，并分配一个主线程，这个主线程会从 main 函数开始执行。在主线程执行过程中，Tomcat 的代码还会启动其他一些线程，包括处理 HTTP 请求的线程。

而我们开发的应用是一些类， 这些类被Tomcat 加载到这个JVM 里执行，所以，即使这里有多个应用被加载，也只是加载了一些类，我们的应用被加载进来以后，并没有增加 JVM 进程中的线程数，也就是Web应用本身和线程是没有关系的。

而 Tomcat 会根据 HTTP请求的URL 执行应用中的代码，这个时候，可以理解成每个请求分配一个线程，每个线程执行的都是我们开发的 Web 代码。如果 Web 代码中包含了创建新线程的代码，Tomcat 的线程在执行代码时，就会创建出新的线程，这些线程也会被操作系统调度执行。

如果 Tomcat 的线程在执行代码时，代码抛出未处理的异常，那么当前线程就会结束执行，这时控制台看到的异常信息，其实就是线程堆栈信息，线程会把异常信息以及当前堆栈的方法都打印出来。事实上，这个异常最后还是会被 Tomcat 捕获，然后 Tomcat 会给客户端返回一个 500 错误。单个线程的异常不影响其他线程执行，也就是不影响其他请求的处理。

但是如果线程在执行代码的时候，抛出的是JVM 错误，比如OutOfMemoryError，这个时候看起来是应用 crash，事实上是整个进程都无法继续执行了，也就是进程 crash 了，进程内所有应用都不会被继续执行了。

从 JVM 的角度看，Tomcat 和我们的 Web 应用是一样的，都是一些 Java 代码，但是 Tomcat 却可以加载执行 Web 代码，而我们的代码又不依赖 Tomcat。
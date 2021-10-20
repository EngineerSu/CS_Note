## Docker

参考how2J的教程: [how2j-Docker](<http://how2j.cn/k/docker/docker-docker/2005.html#nowhere>)

### 为什么需要Docker？

- 将一整套环境打包封装成镜像，**无需重复配置环境**，解决环境带来的种种问题。
- Docker容器间是进程隔离的，谁也不会影响谁。

### 启动docker服务

```
systemctl start docker.service  启动服务  
docker version  启动服务后可以查看版本号
systemctl status docker.service
systemctl stop docker.service
systemctl restart docker.service
```

```bash
service docker start  启动服务
service docker status  查看docker服务状态
service docker stop  重启服务
```



### image文件

> Docker 把应用程序及其依赖，打包在 image 文件里面。只有通过这个文件，才能生成 Docker 容器。image 文件可以看作是容器的模板。Docker 根据 image 文件生成容器的实例。同一个 image 文件，可以生成多个同时运行的容器实例。

`docker image pull [library/]hello-world` : 从Docker官方提供的image文件中, 拉取指定image文件到本地. 因为Docker官方提供的文件都在library组中, 所以library/可以省略

`docker image ls` : 列出本机所有的 image 文件

`docker image rm [name]` : 删除 image 文件



### Container文件

> **image 文件生成的容器实例，本身也是一个文件，称为容器文件。**也就是说，一旦容器生成，就会同时存在两个文件： image 文件和容器文件。而且关闭容器并不会删除容器文件，只是容器停止运行而已。

`docker container run hello-world` : 根据image文件, 生成一个运行的容器实例. 如果本地没有image文件, 会默认从官方仓库中抓取. 部分生成的image容器会自动停止, 对于服务类的容器, 不会自动停止, 手动kill用`docker container kill [containID]` 杀掉

`docker container run --rm hello-world` : 在容器停止后自动删除容器文件

`docker container ls ` : 列出本机正在运行的容器

`docker container ls -all` : 列出本机的所有容器

`docker container rm [containerID]` : 删除容器文件



### Dockfile文件

> 它是一个文本文件，用来配置 image。Docker 根据 该文件生成二进制的 image 文件。

创建一个Dockfile文件: [参考阮一峰的网络日志](http://www.ruanyifeng.com/blog/2018/02/docker-tutorial.html)

创建语句关键字有`FROM COPY WORKDIR RUN EXPOSE CMD`等, 用这些关键字来指定image文件的相关配置



### 创建image文件

根据创建的Dockfile文件, 创建image文件, 使用`docker image build`命令

```bash
$ docker image build -t koa-demo .
# 或者
$ docker image build -t koa-demo:0.0.1 .
```

`-t` : 参数, 指定image名字

`:0.0.1` : 指定image的标签, 默认标签是latest

`.` : 最后一个点指定用于生成image文件的Dockfile文件存放目录为当前目录



### 自定义image文件生成容器

和前面一样, 使用`docker container run`命令生成容器

```bash
$ docker container run -p 8000:3000 -it koa-demo /bin/bash
# 或者
$ docker container run -p 8000:3000 -it koa-demo:0.0.1 /bin/bash
```

`-p` : 参数, 容器的3000端口映射到本机的8000端口
进程运行在 Docker 容器的虚拟环境里面，进程接触到的文件系统和网络接口都是虚拟的，与本机的文件系统和网络接口是隔离的，因此需要定义容器与物理机的端口映射（map）。

`-it` : 参数, 容器的shell映射到当前的shell, 本机窗口输入的命令, 会传入容器

`koa-demo:0.0.1` : image文件:标签, 标签为空时默认是`latest`

`/bin/bash` : 容器启动后的第一个执行命令, 启动bash, 保证用户可以使用shell



### 发布image文件

首先, 需要在[hub.docker.com](hub.docker.com)或[cloud.docker.com](cloud.docker.com)上注册一个账户

然后, 用`docker login`命令登录

接着, 为本地的image文件标注用户名和版本 或者 通过重构image文件指定用户名和版本

```bash
# 为本地image标注用户名和版本
$ docker image tag [imageName] [username]/[repository]:[tag]
# 实例
$ docker image tag koa-demos:0.0.1 ruanyf/koa-demos:0.0.1

# 重构image指定用户名和版本
$ docker image build -t [username]/[repository]:[tag] .
```

最后, 发布image文件到个人的docker网站上

```bash
$ docker image push [username]/[repository]:[tag]
```



### 其他命令

`docker container start [containerID]` : 不同于`docker container run [ImageName]`命令, 每run一次就会生成一个container文件, 如果希望重复使用容器, 用start命令

`docker container stop [containerID]` : kill命令是强制停止容器, 接收到stop命令的容器会先继续进行一些收尾工作, 然后停止.

`docker container logs [containerID]` :  查看容器的shell输出, 如果run命令时没有-it参数, 就要用 logs命令查看输出

`docker container exec [containerID]` : 进入正在运行的docker容器, 如果docker run命令运行容器的时候，没有使用-it参数，就要用这个命令进入容器。一旦进入了容器，就可以在容器的 Shell 执行命令了。

```bash
$ docker container exec -it [containerID] /bin/bash
```

`docker container cp [containID:/containPath] [/localPath]` : 将正在运行的容器中文件拷贝到本机目录
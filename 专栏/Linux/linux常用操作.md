## linux常用命令

##### 0.最常用

````
clear 
	清空命令窗口
ll -a
	查看当前路径下的目录(-a：表示查看隐藏文件)
cd ...
	切换目录
	[cd ~] [cd ..] [cd /home/]
cat ...
	查看文件
halt 
	关机
rm -rf 
	不带询问的递归删除
jps 
	查看当前进程,注意不是线程,一个进程可以包含多个线程
echo $HIVE_HOME
	查看HIVE_HOME环境变量是否设置
ps -ef | grep 3306 
	查找进程信息中包含3306的进程
kill ...
	杀进程
touch ...
	在当前目录创建文件
chmod 600 ...
	修改文件的权限:rwx:421(读写可执行)
````

##### 使普通用户具有执行sudo命令权限

```
1.切换到管理员账户
	su (输入管理员密码)
2.vi 修改 /etc/sudoers,在 root ALL=(ALL)	ALL 下加入一行
	jack ALL=(ALL)	ALL
    备注: jack是普通用户名
```

##### 使CentOS启动默认为非图形界面

```
1.sudo vi /etc/inittab
    将inittab中的启动级别设置为3;即可默认启动就是非图形界面
    注意普通用户第一次执行sudo命令,需要用管理员账户将普通用户加入到可执行sudo名单中
    
init3
	直接使用该命令,关闭当前图形界面,切换到非图形界面(不影响下次重启界面)
```

##### 启动图形界面

```
init 5
	以图形界面重启
startx
	切换到图形界面(不重启)
```

##### 修改主机名

```
1.sudo vi /etc/sysconfig/network
	修改HOSTNAME=newhostname
2.修改主机名后不会立即生效,重启生效
	也可以使用 sudo hostname newhostname 指令,让其立即生效(需要重新登录)
```

##### 配置主机名与ip地址的映射

> 使得集群中其他节点可以通过主机名访问

```
1.sudo vi /etc/hosts 指令,添加一行
	192.168.216.129  newhostname
	前面为ip地址,后面为主机名
	保存配置后,可以通过 ping newhostname 指令测试主机名通信是否生效
```

##### 查看linux是32位还是64位

```
getconf LONG_BIT
	指令结果为32或64
```

##### 远程拷贝

```
scp 本地文件路径 远程目标IP地址:/home/test
	# scp的含义就是ssh的cp
```

##### 远程登录

```
ssh 远程目标ip地址
	也可以在 /etc/hosts 文件中配置远程目标ip地址的主机名称
```


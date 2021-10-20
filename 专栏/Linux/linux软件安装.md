#### linux软件安装

> 注意linux位数与安装的软件位数一定要相同

---

##### 1.安装JDK

```
0.卸载已有的JDK
	rpm -qa | grep java		查看所有软件包中与java有关的(找JDK)
	rpm -e --nodeps rpm包全名	  卸载软件包(先卸高版本)
1.解压 tar -zxvf 文件名 -C app/
	表示将JDK压缩包解压到当前目录的app目录下
2.将安装的bin目录设置到系统的环境变量中 sudo vi /etc/profile 
	# 在文件最后添加
	export JAVA_HOME=/home/jack/app/jdk1.7.0_65
	export PATH=$PATH:$JAVA_HOME/bin
3.修改后,需要重新加载配置文件,使配置文件立马生效
	source /etc/profile
```

##### 2.安装Hadoop

##### a.解压

```
1.解压 tar -zxvf 文件名 -C app/
	表示将Hadoop压缩包解压到当前目录的app目录下
	解压完成后,可以删除/share/doc目录(参考文档)
	share/hadoop中是开发使用的jar包存放位置
```

##### b.修改hadoop安装目录的配置文件

> 修改顺序:hadoop环境=>核心=>hdfs=>mapreduce=>yarn

```
2.修改安装目录下的配置文件,当前目录切换到安装目录/etc/hadoop/下
	vi hadoop-env.sh 
		#27行;因为会偶发性的不能自动获取JAVA_HOME路径,需要手动提供
		export JAVA_HOME=/home/jack/app/jdk1.7.0_65
		
	vi core-site.xml
		<!-- 指定HADOOP所使用的文件系统schema（URI），HDFS的老大（NameNode）的地址 -->
		<property>
			<name>fs.defaultFS</name>
			<!-- centos1是主机名,相当于一个ip地址 -->
			<value>hdfs://centos1:9000</value>
		</property>
		<!-- 指定hadoop运行时产生文件的存储目录 -->
		<property>
			<name>hadoop.tmp.dir</name>
			<value>/home/jack/hadoop-2.4.1/tmp</value>
    	</property>
    	
    vi hdfs-site.xml  hdfs-default.xml 
    	<!-- 指定HDFS副本的数量 -->
    	<property>
			<name>dfs.replication</name>
			<!-- 默认3个最好,如果是伪分布式,只有1台物理机,3会报错 -->
			<value>1</value>
    	</property>
    	
    mv mapred-site.xml.template mapred-site.xml # 先改名
	vi mapred-site.xml
		<!-- 指定mapreduce运行在yarn上 -->
		<property>
			<name>mapreduce.framework.name</name>
			<value>yarn</value>
   		</property>
   		
   	vi yarn-site.xml
   		<!-- 指定YARN的老大（ResourceManager）的地址 -->
		<property>
			<name>yarn.resourcemanager.hostname</name>
			<value>centos1</value>
    	</property>
		<!-- reducer获取数据的方式 -->
   		<property>
			<name>yarn.nodemanager.aux-services</name>
			<value>mapreduce_shuffle</value>
     	</property>
     	
     vi salves # 设置Datanodes的ip地址(或主机名)
     	默认为localhost,可以改为centos1(当前主机名)
```

##### c.关闭防火墙

```
因为hadoop集群间的电脑需要相互访问,有许多端口被防火墙拦截,而相互访问属于内网,所以直接关掉防火墙也不影响安全.

sudo service iptables stop # 关闭防火墙
sudo chkconfig iptables off # 关闭防火墙所有级别的开机自启
```

##### d.将Hadoop加入环境变量

```
vim /etc/proflie
		# 在最后添加(如果安装JDK时已添加过,则修改)
		export JAVA_HOME=/home/jack/app/jdk1.7.0_65
		export HADOOP_HOME=/home/jack/app/hadoop-2.4.1
		export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

source /etc/profile # 重新加载配置文件,使其立马生效
```

##### f.格式化namenode

```
hdfs namenode -format(hadoop namenode -format)
```

##### Hadoop的启动测试

```
1.切换到/hadoop-2.4.1/sbin/目录下
	在 app/hadoop-2.4.1/sbin/中有很多sh脚本命令
2.测试依次单独启动dfs,yarn
	start-dfs.sh
	start-yarn.sh
3.查看已启动的进程 jps
```


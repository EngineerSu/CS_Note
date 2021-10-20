# MySQL的安装和使用教程（Mac OS）超详细

## 下载MySQL

进入 [MySQL官网](https://dev.mysql.com/downloads/mysql/) 下载，考虑到新版本可能与客户端不兼容的问题，推荐下载 5.x 版本，点击 Archives 寻找历史版本。

![image-20201206163655658](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206163655658.png)

选择 5.7.31 版本，推荐下载 dmg 文件可以直接安装。

![image-20201206163616557](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206163616557.png)

点击 Download 后，会提示你登录或注册，点击下方小字，直接下载

![image-20201206154625898](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206154625898.png)

下载完成后双击 dmg 文件进入安装向导



## 安装

安装一直 Next 即可，最后一步生成 root 账户时，会自动生成一个随机密码，记住要复制该密码！

![image-20201206171445295](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206171445295.png)

如果没有复制密码，重设密码比较麻烦，这个步骤在后面的章节介绍。

安装完成后，进入电脑的系统偏好设置，可以看到 MySQL，点击进入。

![image-20201206164034491](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206164034491.png)

打开 MySQL，点击 Start MySQL Server

![image-20201206164105433](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206164105433.png)



### 修改密码

如果安装时候生成的随机密码记住了，则可以按照以下步骤修改密码（如果密码忘记参考后面的「重设密码」）。注意，密码没修改前客户端是无法连接 MySQL 的，所以这一步是必要的。

打开终端，进入 mysql 的 bin 目录下：

```shell
cd /usr/local/mysql/bin
```

执行登录命令：

```shell
./mysql -u root -p
```

按提示输入之前复制的密码，可以看到终端的命令输入头变成了 `mysql>`

执行修改密码命令（末尾的分号也要需要输入）：

```shell
set PASSWORD=PASSWORD('123456');
```



## 完全卸载 MySQL

如果安装了错误的版本，需要卸载 MySQL，首先在系统偏好设置中关闭 MySQL 服务，然后进入终端，执行以下命令即可：

```shell
sudo rm /usr/local/mysql

sudo rm -rf /usr/local/mysql*

sudo rm -rf /Library/StartupItems/MySQLCOM

sudo rm -rf /Library/PreferencePanes/My*

rm -rf ~/Library/PreferencePanes/My*

sudo rm -rf /Library/Receipts/mysql*

sudo rm -rf /Library/Receipts/MySQL*

sudo rm -rf /private/var/db/receipts/*mysql*
```

以上命令作用是删除与 MySQL 相关的文件，如果不删除干净后续安装不同版本可能会失败。

命令执行的过程中可能会报错：目录或文件不存在，那说明已经被删除或未创建过，无视就好。



## 重设密码

如果安装时候生成的随机密码没有记住，则按下面步骤进行重设密码

进入系统偏好设置 - MySQL，关闭 MySQL 服务：

![image-20201206164635901](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206164635901.png)

进入终端输入

```bash
cd /usr/local/mysql/bin/
```

回车后 登录管理员权限

```undefined
 sudo su
```

回车后输入以下命令来禁止mysql验证功能

```undefined
./mysqld_safe --skip-grant-tables &
```

回车后mysql会自动重启（偏好设置中mysql的状态会变成running）

输入命令

```undefined
./mysql
```

回车后，输入命令

```undefined
FLUSH PRIVILEGES
```

回车后，输入命令重设密码

```bash
SET PASSWORD FOR 'root'@'localhost' = PASSWORD('123456');
```



## Sequel Pro 客户端

### 登录

Sequel Pro 提供对 MySQL 的客户端操作，界面非常简洁，重点是免费！[下载 Sequel Pro](http://www.sequelpro.com/) 

下载后直接将 Sequel Pro.APP 移动到应用程序即可使用。

打开 Sequel Pro 会进入以下界面，点击 FAVORITES 的 ➕（收藏登录选项，便于后续登录）

![image-20201206165033503](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206165033503.png)

输入 Host、UserName 和 Password后，点击 Save Changes，然后点击 Connect 即可登录

![image-20201206165148965](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206165148965.png)

### 新建数据库

登录后界面如下，点击 Choose Database，选择 Add Database 即可新增数据库

![image-20201206160201445](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206160201445.png)



### 新建表

点击左下角的加号，即可新建表

![image-20201206165352165](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206165352165.png)

设置表名、字符集和引擎，推荐字符集选择 utf8mb4：

![image-20201206165447120](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206165447120.png)

### 新建字段和索引

![image-20201206165545039](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206165545039.png)

### 插入和更新

表内容的插入和更新可以直接操作，如下选择 Content 后，点击左下角加号即可插入，行内容直接直接编辑更新

![image-20201206165933735](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206165933735.png)

### 执行SQL

菜单栏选 Query，即可进行 SQL 的编辑和执行

![image-20201206170106582](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206170106582.png)

### 信息查看

#### 查看客户端执行脚本

点击右上角的 Console，即可查看客户端的所有执行脚本

![image-20201206170218711](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206170218711.png)

如下：

![image-20201206170240468](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206170240468.png)

#### 查看表信息

菜单栏选择 Tabel Info 即可查看表信息

![image-20201206170318553](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201206170318553.png)


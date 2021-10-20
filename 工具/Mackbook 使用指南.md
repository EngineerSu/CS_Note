# Macbook 使用指南

## 设置

做一些个性化设置，可以使mac更得心应手。

### 调度

可以在设置-调度中心中设置：调度中心、应用程序窗口和显示桌面的快捷键，以下是我的设置。

![image-20200913141832296](https://i.loli.net/2020/09/13/mXKaqpSFY5kPR3w.png)

特点是左边ctl键用于调度任务，因为右边ctl键使用频率低，所以用于显示桌面。



## 快捷键

### 窗口

关闭当前窗口：cmd + W

最小化窗口到程序坞：cmd + M，只会隐藏当前应用的当前窗口，并且最小化后需要在程序坞右键才能重新打开，不建议使用

隐藏应用：cmd + H，会隐藏当前应用的所有窗口

只显示当前窗口：cmd  + option + H，会隐藏除当前窗口外的所有窗口

### 显示

文件管理中文件预览：backspace

全屏/退出全屏：ctl + cmd + F

应用列表：cmd + shift + Q

### 查找

查找：cmd + F

替换：cmd + option + F

查找下一个：cmd + G

查找上一个：cmd + option + G

### 控制

锁屏：ctl + cmd + Q

打开强制退出应用程序窗口：cmd + option + Esc

退出应用：cmd + Q

删除：cmd + backspace

打开应用的偏好设置：cmd + `，`



## 应用

### Finder

返回上一层目录：cmd + ⬆️

重命名：选择文件，直接点击Enter

新建文件夹：cmd + Shift + N

开启Finder显示路径名：终端执行如下命令：`defaults write com.apple.finder _FXShowPosixPathInTitle -bool TRUE;killall Finder`

关闭Finder显示路径名：终端执行如下命令：`defaults delete com.apple.finder _FXShowPosixPathInTitle;killall Finder`

在Finder中设置“复制路径”的服务：[参考这里](https://www.jianshu.com/p/757f9ffc5acf)

隐藏文件可见：`defaults write com.apple.finder AppleShowAllFiles -boolean true ; killall Finder`

恢复隐藏文件不可见：`defaults write com.apple.finder AppleShowAllFiles -boolean false ; killall Finder`

显示根目录的Library目录：`chflags nohidden ~/Library/`

复制文件路径：option + cmd + C

复制粘贴文件：cmd + C 结合 cmd + V

剪切文件：cmd + option + V

### Chrome

删除缓存：cmd + shift + backspace

查看cookie：在地址栏搜索`chrome://settings/content/cookies`

### Alfred

破解版已存，配置参考[参考这里配置](https://www.jianshu.com/p/e9f3352c785f)

### Homebrew

[下载地址](https://brew.sh/)，使用homebrew可以安装的一些预览插件如下：

- qlmarkdown：`brew cask install qlmarkdown`，预览markdown
- qlstephen：`brew cask install qlstephen`，预览文本
- qlcolorcode：`brew cask install qlcolorcode`，预览代码高亮
- qlimagesize：`brew cask install qlimagesize`，预览图片显示大小



### Sublime Text3

一些常用的快捷键如下：

`cmd + f`：搜索

`cmd + opt + f`：替换

`cmd + k + k`：从光标处删除至行尾

`cmd + k + delete`：从光标处删除至行首

`cmd + 单击`：出现新的光标用于编辑

`cmd + opt +g`：寻找下一个当前选中的内容

`cmd + shift + g`：寻找上一个当前选中的内容

`cmd + ctl + g`：寻找所有当前选中的内容（并出现光标可以批量编辑）

`cmd + enter`：在当前行后插入新行

`cmd + shift + enter`：在当前行前插入新行

`cmd + shift + l`：对选中部分进行批量编辑

`option + enter`：cmd + F 搜索后，这个快捷键可以快速找到所有的关键词位置，并可以采取批量编辑



### 其它优秀软件

INNA：全能的视频播放器，[下载](https://iina.io/)

Iterm2：更友好的终端工具，[下载](https://iterm2.com/downloads.html)

iterm2安装完成后，使用`sh -c "$(curl -fsSL https://raw.github.com/robbyrussell/oh-my-zsh/master/tools/install.sh)"`可以安装zsh，则以后打开iterm输入zsh，即可使用zsh，它的标志是一个小箭头

zsh的一些插件：zsh-autosuggestions（自动提示）、zsh-syntax-highlighting（命令高亮）、auto-jump（自动跳转，使用`j [keyword]`就可以跳转到包含keyword的历史路径）、extract（简化压缩命令，使用`x [filepath]即可解压`



## 外设

### 显示器

设置镜像：双屏一样

设置分屏：多个拓展屏幕，并且可以设置屏幕之间的排列关系



### 键盘

当外接键盘时，一般的windows键盘的win键会被认为是cmd，alt会被认为是option，而这两个键在windows键盘中的位置与mac键盘中位置相反，因此对于习惯快捷键的人来说，使用windows键盘会非常别扭，因此可以通过以下配置交换它们的键位：

![image-20200913131125454](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200913131125454.png)

注意，设置的时候，要选择你的键盘：

![image-20200913131149053](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200913131149053.png)


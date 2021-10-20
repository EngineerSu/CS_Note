## IDEA技巧 for Mac

### 快捷键

#### 窗口

打开当前项目配置：cmd + ；

显示/隐藏Project目录树：cmd + 1

显示/隐藏底部Run窗口：cmd + 4

显示当前类的结构（属性+方法）：cmd + 7

显示收藏窗口：cmd + 2（可显示F3设置的所有收藏点 和 所有设置的断点）



#### 显示

查看类的继承层次：ctrl + H

查看方法参数列表：cmd + P

折叠 / 展开当前代码块：cmd + 减号/加号（等同于 cmd + 小数点）

折叠 / 展开所有方法：cmd + shift + 减号/加号

显示当前类的结构（属性+方法）：cmd + F12（等同于cmd + 7，但是显示位置不一样）



#### 编辑

复制当前行到下一行：cmd + D

新增一个空行：cmd + Enter（若在行首则上一行新增空行，若在行末则下一行新增空行）

下方插入一行：shift + Enter

剪切当前行：cmd + X

移动当前行：cmd + shift + ↕️

批量重命名：shift + F6

快速生成getset方法：ctrl + enter

快速try catch（或if等）：option + cmd + T

字符串全部变成大/小写：cmd + shift + U

抽取局部变量：cmd + option + V

抽取成员变量：cmd + option + F

抽取静态变量：cmd + option + C

抽取方法入参：cmd + option + P

抽取方法：cmd + option + M





#### 跳转

进入该类/方法/变量：cmd + B

返回上次编辑的地方：cmd + option + 左右

返回上次光标所在的地方：cmd + [ 或 ]

查看接口实现类：cmd + option + B

跳转到下一个提示的地方：F2



#### 搜索

搜索：cmd + O / cmd + shift + O

搜索/：cmd + F / cmd + shift + F

查看最近打开文件：cmd + E

查看方法/变量在哪些地方被调用：option + F7

显示该类的所有方法：cmd + F12（弹窗出现后，可直接输入搜索）



#### 提示

代码提示：option + enter



#### 自动化

格式化代码：cmd + option + L

优化import包：ctl + option + O

自动结束代码：cmd + shift + enter

main方法：psvm

输出：sout

静态字符串：psfs



#### 标记

标记bookmark：F3

查看当前类所有的bookmark：cmd + F3



### 插件

#### String Manipulation

可以对字符串（大小写，驼峰切换等）和文本操作，选中内容option+m

#### GenerateAllSetter

快捷键：cmd + enter

作用1：快速生成DO对象的所有set方法

作用2：避免BeanUtil.copy的坑，快速生成对象转换代码，如下图

写一个转换方法，cmd + enter，按提示选择即可

![image-20201118143818898](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201118143818898.png)

自动生成的代码效果如下：

![image-20201118143851295](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201118143851295.png)

此时只要对其中存在问题的set进行微调修改即可

### 设置

#### import相关

设置超过指定 import 个数，改为`\*`

![image-20201115164212247](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201115164212247.png)

设置自动import和优化import

![image-20201115164721683](/Users/jacksu/Desktop/File/resource/image/notePics/image-20201115164721683.png)





### 其它技巧

#### Live Templates

日常编码中有很多内容是重复劳动，比如类的注释信息，比如logger的引入

![image-20200830135457150](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200830135457150.png)

选中重复输入的代码，在状态栏Tools中选择Save as Live Template，即可打开一个编辑框（在偏好设置-Editor-Live Template）中一样可以打开，如下：

![image-20200830135708940](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200830135708940.png)

其中1即我们自定义的模板，2是模板内容，其中`$date$`是变量，在5中可以编辑变量值为某些函数，3是模板快捷单词，即在IDEA中输入`author`就会出现模板内容，4是备注。5 中有很多Expression可以选择，如下：

![image-20200830135953040](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200830135953040.png)

我选择的是`date()`，该函数会取当前时间（精确到day），设置完后，效果如下：

![idea_live_template](/Users/jacksu/Desktop/File/resource/image/notePics/idea_live_template.gif)

如此，对于重复性代码都可以自定义到IDEA中，快速的得到。




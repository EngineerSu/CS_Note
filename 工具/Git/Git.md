Git

### 文件的git状态

刚创建时，属于untracked状态，没有被git管理。使用`git add`命令后，这个文件就进入了暂存区，commit后，暂存区的文件会进入工作区，等待push。

注意，add后的文件如果发生修改，commit前需要再次add，否则第一次add后修改的内容不会被commit，因为commit只提交暂存区的文件。

比如，a.txt的内容是`a = 1`，进行了add后，又进行了修改为`a = 1; b = 2`，此时未再次add，直接commit，则commit中a.txt内容是`a = 1`

### Git仓库

#### 下载Git bash

下载网址：https://git-scm.com/download
安装完成后，打开git bash，设置用户名和邮箱

````shell
git config --global user.name "JackSu"
git config --global user.email "jacksu1024@qq.com"
````

然后使用`ssh-keygen -t rsa`创建密钥，并在远程仓库配置 SSH 密钥（密钥内容即 ***.pub 部分）

#### 创建远程仓库

创建一个远程仓库，复制clone地址。使用git bash切换到放仓库的根目录下，执行以下命令（假设仓库名是test）

```shell
git clone git@gitlab.alibaba-inc.com:yangsu.sc/test.git
cd test
touch README.md
git add README.md
git commit -m "add README"
git push -u origin master
```

#### 本地项目关联到远程仓库

首先，在本地项目中增加 .gitignore 文件！！！

本地已经有了项目，首先使用git bash切换到项目的根目录下，执行以下命令（假设仓库名是test）

```shell
git init
git remote add origin git@gitlab.alibaba-inc.com:yangsu.sc/test.git
git add .
git commit
git push -u origin master
```

#### .gitignore

一般放置于项目根目录下（也可以在项目的某个文件夹下创建，那么它就会以该文件夹为根目录去屏蔽一些不需要git管理的文件），git相关命令会忽略这些文件。

如果已经在分支A上push了不需要的文件，直接在分支A中删除这些不需要的文件，并添加.gitignore文件，然后再push一次覆盖。

.gitignore文件示例（建议编码为utf-8）

```
# Eclipse project files
.project
.classpath
.settings

# IntelliJ IDEA project files and directories
*.iml
*.ipr
*.iws
.idea/

# Geany project file
.geany

# KDevelop project file and directory
.kdev4/
*.kdev4

# Build targets
/target
*/target

# Report directories
/reports
*/reports

# Mac-specific directory that no other operating system needs.
.DS_Store
```



### Git命令

#### 状态

```shell
git status：查看仓库的文件跟踪状态(包括工作区和暂存区)
git log：查看commit的版本信息
git log --pretty=oneline：版本信息简洁版
git reflog：查看HEAD变化历史(可以查看回退之前的版本号)
git log --graph --pretty=oneline --abbrev-commit：查看分支历史图形
git diff [fileName]：显示文件工作区的改变
```



#### 回退

![1554294659901](/Users/jacksu/Desktop/File/resource/image/notePics/1554294659901.png)

回退分为两种：

- **撤回工作区的修改**：使用`git checkout [fileName]`可以将文件在工作区的修改全部撤回（撤回到该文件上次add时的状态），还可以使用`git reset HEAD [fileName]`将文件由暂存区转入工作区（文件的修改仍会保留，若要撤回修改，可继续使用`git checkout [fileName]`）

  ```shell
  # 1.修改a.txt：加入一行：a = 1;
  git add a.txt; # 将a.txt在工作区的所有修改都存入暂存区
  # 2.修改a.txt：再加入一行：b = 2;
  git checkout a.txt; # 将a.txt在工作区的所有修改撤回，a.txt中 b=2 这一行会消失，但是a = 1还在
  # 3.修改a.txt：再加入一行：c = 3;
  git add a.txt; # 将a.txt在工作区的所有修改都存入暂存区
  git reset HEAD a.txt; # 将a.txt在暂存区保存的所有修改重新带回到工作区
  git checkout a.txt; # 将a.txt在工作区的所有修改撤回，a.txt中 a=1 与 c=3 都会消失
  ```

- **commit回退**：使用以下命令可以进行commit回退。

  ```shell
  git reset HEAD^ 回到上一个版本的状态
  git reset [版本号] 回到指定版本的状态
  git reset --hard [版本号前几位]
  ```

  注意，回退后commit与回退前commit之间的工作痕迹会被保留，表现形式如下：

  - 所有对 文件内容的增删改 以及对 文件的删除 都会保留在工作区，使用`git checkout .`，即可恢复这些变化：增删改会恢复为原来，删除的文件也会恢复（或者使用`git restore .`也可以）
  - 所有 新增的文件 都将处于 untracked 状态，使用`git clean -f`可以删除（删除前可以使用`git clean -nf`确认要删除的文件）

  另外，回退后，使用`git log`不能再看到当前commit“未来”的commit记录，使用`git reflog`可查看

放弃merge：git merge --abort



#### 分支

**查看分支**

```shell
git branch：查看本地分支
git branch -r：查看远程分支
git branch -av：查看所有分支,包括远程分支
```

**创建分支**

````shell
git branch [newbranchName]: 创建新分支
git checkout -b [branchName]: 创建并切换到新分支
git checkout -b [branchName] origin/[branchName]: 创建并切换到远程分支
````

**切换分支**

```shell
git checkout [branchName]: 切换分支
```

注意，因为**Git的工作区和暂存区是不区分分支的**。若切换分支时，工作区和暂存区还有内容，则该内容可能会被带到新的分支。

比如在feature1分支中，做了以下操作：

```
A文件的内容有增删改(工作区，暂存区中都有)
新增了B文件(暂存区)
新增了C文件(工作区)
删除了D文件(删除操作自动进入暂存区，D文件在feature2中存在)
删除了E文件(删除操作自动进入暂存区，E文件在feature2中不存在)
```

然后，切换到了feature2分支，则feature2中以上操作的状态如下：

```
A文件的内容有增删改(工作区，暂存区中都有)
新增了B文件(暂存区)
新增了C文件(工作区)
删除了D文件(删除操作自动进入暂存区，D文件在feature2中存在)
```

可以看到，唯一没有的是被删除的E文件动作，因为feature2中本来就不存在E文件，因此对E文件的操作不会伴随进入feature2。

因此，切换分支前，要使用`git status`查看分支工作区和暂存区的状态，对于需要commit和restore的操作及时操作后，再切换分支，以免引起误解。

**删除分支**

```shell
git branch -d [branchName]: 删除分支
```

**更新分支**

```shell
git pull [branchName]：将远程分支拉下来，并与本地分支merge
```

直接使用`git pull`就是拉取当前分支对应的远程分支，使用`git pull origin/master`可以拉取其它远程分支，并与当前分支merge

**合并分支**

```shell
git merge [branchName]：将某分支与当前分支合并
```

在 feature1分支上使用`git merge feature2` ：

若 feature1 与 feature2 处于同一条链表上，如下图，则merge结果是feature1也指向了节点6。注意，merge的作用是为了让当前分支更新，因此不能在feature2上使用`git merge feature1`，这是没有意义的。

![image-20200222202937370](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222202937370.png)

若 feature1 与 feature2 不处于同一条链表上，如下图，merge会让feature1生成一个新的节点，该节点指向feature2的节点，这个过程可能需要处理冲突。

![image-20200222203150022](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222203150022.png)



**提交分支**

```shell
git commit -m "remark..."：提交（新增一个commit时间点，工作区的内容不会被commit）
git commit --amend：修改上次commit的备注信息
```



#### 查询

- 查询提交记录的修改内容：`git log -p`
- 查询某版本与当前版本的区别：`git diff [commitId]`



#### Git命令理解

##### 如何理解分支？

![image-20200222181256098](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222181256098.png)

分支保证了多人开发的便携性和开发过程中的安全性

无论master还是featuren都代表的是一个头时间节点，通过不同的头节点往回找，可以得到不同的单链表，这样的一个个链表，就可以理解为该头节点对应的分支。因此，master和featuren又称为分支。

master也是一个分支，只是大家公认其为主分支，其它分支有用的内容最终会想办法合并到主分支上。

每次commit或merge都会生成一个新的时间节点，对应的分支名称一定会指向该分支链表的头节点。

origin/xxx代表的是远程分支，其与本地分支xxx一般是对应xxx分支链表上的不同节点位置。谁对应的节点位置越上，代表谁更新。若本地分支更新，则使用`git push`将远程分支也更新；若远程分支更新，则使用`git pull`将远程分支的更新拉取到本地。两者目的都是为了让远程和本地分支同步：指向分支链表的最新节点。

HEAD与当前使用分支对应的commit时间节点保持一致。





##### 有commit不就行了，为什么还要add？

很多同学常见的开发动作就是：开发代码完成，`git add .`与`git commit -m "..."`两个命令连续执行，因此会有疑问：一个动作为什么要执行两次命令？

其实这里有两个动作：`git add .`是把工作区所有修改的文件保存到暂存区，`git commit -m "..."`则是把所有暂存区的文件提交为一个新的commit点。

那问题又来了，为啥要分工作区和暂存区再提交？直接所有文件都在工作区直接commit不行吗？

这样是可以的，但是对开发者不友好，因此Git作者并没有这样设计。可以举一个例子来说明：

小明需要在分支feature1上开发一个新需求，这个新需求工作量比较大，经过评审需要有5个开发阶段，这5个阶段可以理解为一个递进的关系。假设前4个阶段小明已经开发完成了，第5个阶段开发也接近尾声了，却发现第5个阶段的开发思路有问题（或是需求发生了变化），第5阶段的工作相当于白做了。

那么现在就需要将第5阶段已经修改的文件，都恢复到第4阶段结束时的状态。设想一下没有暂存区的概念，若第5阶段修改的文件特别多，那么这个恢复工作可以想象多么痛苦，而且很大概率会有一些遗漏问题。而有暂存区的概念时，只要第4阶段结束时，小明使用了`git add .`命令将前4阶段所有工作都保存到了暂存区，那么第5阶段的所有工作都处于工作区的状态，使用`git checkout .`就可以将工作区所有的文件修改都撤销，一步回到了阶段4。

同时还有`git reset HEAD [fileName] `可以将暂存区的指定文件恢复到工作区中，此时再使用`git checkout [fileName] `可以将该文件恢复到上次commit后的状态。

因此，`git add`也是一个非常重要的命令，它会将文件在工作区的修改保存到暂存区。工作区里所有的修改是可以随时撤回的，一旦保存到暂存区后，要撤回就会撤回暂存区所有的修改。因此add命令也要慎重使用，当确保该修改不会随便撤回时，再add到暂存区。随意add到暂存区，撤回时就会“连累”到已在暂存区中的文件



### 分支高级

![1555296966551](/Users/jacksu/Desktop/File/resource/image/notePics/1555296966551.png)

#### 创建标签

标签和分支一样,也是指向某个commit,但是标签一旦创建,就不能修改. 其一般作版本号使用

切换到目标分支,执行`git tag <tag-name>`就会得到一个标签,其对应的commit默认是当前分支最新的commit

也可以使用`git tag <tag-name> [commit-id]`来指定当前标签指向的commit

`git tag`可以查看当前分支下所有的tag

`git tag -a <tag-name> -m "mark" [commit-id]`创建标签的同时添加评论

#### 标签管理

`git tag -d <tag-name>`: 删除本地标签

`git push origin :ref/tags/<tag-name>`: 删除远程标签

`git push origin <tag-name>`: 推送一个本地标签

`git push origin --tags`: 推送所有本地标签



### Git In IDEA

https://www.cnblogs.com/wyb628/p/7243776.html

首先, 需要在IDEA中配置本地git的安装目录, 如下图所示, 并且需要将SSH连接改为native(本地git已经在远程仓库中配置了key, native模式即可push).

![1560084784975](/Users/jacksu/Desktop/File/resource/image/notePics/1560084784975.png)

从远程仓库中clone项目建议还是直接使用git bash. clone下来的项目用IDEA打开, 可以在右下角看到Git:master的标签, 即IDEA能自动识别该项目被Git管理. 然后其他所有有关Git的操作, 都在项目右键都Git选项中.

![1560085051037](/Users/jacksu/Desktop/File/resource/image/notePics/项目右键Git选项.png)

Fetch和Pull的区别

- fetch会将远程所有分支的更新拉到本地，但是不会merge
- pull会将远程指定的分支拉到本地，并与当前分支merge



### Git命令使用场景

#### commit撤销

只撤销最新的commit，让更改处于待commit的状态，更改本身都保留

git reset --soft HEAD^

#### merge后恢复(commit回退)

多人开发时，有时需要本地merge一下别人的分支，可能是为了排除冲突，可能是有什么需要查看一下别人分支的某些代码。但是，要记住一个需求一个分支，merge完别人分支后一定要恢复到merge之前的场景，不然你的开发分支就融入了别人的代码。

假设这样一个场景，你和同事张三在开发两个分支上分别开发不同的需求，张三分支有个严重的bug，你merge后未恢复，而张三的需求后来被主管砍了，它的分支也就删除掉了。而你带着这个严重bug的分支发布了，最好造成了严重的线上问题，这个锅肯定是你背。

**Reset**

首先，需要reset到merge前的分支状态，可以使用`git status`命令查看一下所有的分支状态，确定你要reset回去的分支commitId。然后使用IDEA的VCS-Git-Reset HEAD，出现以下界面。在以下界面的 To Commit 栏输入你要恢复分支的commitId前六位即可，还可以通过 Validate 判断你输入的commitId是否有效，然后选择Reset即可恢复。

![image-20200217110112433](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200217110112433.png)

**Restore**

恢复后，你会发现本地还有一些merge后的“痕迹”，使用`git status`可以查看这些“痕迹“：1.merge分支修改过的痕迹，它们还属于没有add 的状态；2.merge分支新增的文件，它们还属于untracked的状态。这些修改和新增都需要删除，不然后面使用`git add .`就又把merge分支的痕迹带到你自己分支了。

使用`git restore .`可以将merge分支修改的痕迹都撤回

**clean**

使用`git clean -f`可以将merge分支新增的文件都删除（其实是删除untracked的文件）

为了保险，删除之前最好使用`git clean -nf`查看一下待删除的文件，是否都为merge分支引入的，而非自己引入



#### 合并本地commit

本地开发时，有时多次commit是解决的同一个问题，因此最好将其合并为一个节点。

使用` git log --pretty=oneline `，简洁的查看当前分支 commit log 如下：

![image-20200222135915968](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222135915968.png)

可以看到 远程master和本地master处于的节点，feature则本地commit了5次，现在目的是合并这5次commit为一次。

使用`git rebase -i HEAD~5`，命令行会显示这5次commit的信息，并提示了操作commit的命令

![image-20200222140050595](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222140050595.png)

根据它的提示，我们现在应该选择`s`，即将分支与之前分支融合，因此使用vim编辑器，将界面编辑如下（commit从上到下是按提交时间的先后排列）。编辑完成后，使用`:wq`命令，保存退出。

![image-20200222140127829](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222140127829.png)

然后命令行会出现以下内容，作用是确定最终合并分支的commit备注信息，并将被合并的commit备注也列出来了，作为参考。修改完最终合并commit的备注后，即可使用`:wq`保存退出。

![image-20200222140308208](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222140308208.png)

如果没有冲突，则会显示如下信息，表示rebase成功

![image-20200222140526295](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222140526295.png)

再次使用`git log`查看commit历史，为下图，可以对比上面的历史，5个commit确实合并成了一个

![image-20200222140626395](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222140626395.png)



拓展：`git rebase`是一个非常强大的命令，仅上面的示例而言，可以看到它可以编辑组合多次commit。



#### 使用rebase简化git历史

正常的开发流程：

1. 远程新建一个分支假设是feature1，pull到本地后，本地切换到feature1，然后开始开发

2. 开发完成后，feature1的内容要整合到master上。首先，切换到本地master，先后`git pull`，将远程master分支更新pull下来

3. 再切换回feature1，使用`git merge master`，在feature1上进行merge，排除冲突后，feature1就会生成一个新的commit，master与这个commit进行merge是不会冲突的。

   为什么不再master上直接merge feature1？master分支大家都在用，禁止在master上直接merge！！

4. 切换到本地的master，进行`git merge feature1`，此时merge不需要排除冲突，同样也会生成一个commit节点，再使用`git push`就将feature1上开发的内容，push到远程master了。

此时，查看git的分支路径，就会是如下图的样子：

![image-20200222144151487](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222144151487.png)

黄色是master分支的节点，紫色是feature1分支的节点。可以想象一下，如果有多个人一起开发，这个分支图就会有非常多的类似紫色节点的线，“横搭”在黄色主干线上，当master主干线非常长的时候，到处都是这种“横搭”的分支，后续回顾整理分支时，就会很难看。

使用以下开发流程就可以解决这个问题。

1. 远程新建一个分支假设是feature2，pull到本地后，本地切换到feature2，然后开始开发

2. 开发完成后，feature2的内容要整合到master上。首先，切换到本地master，先后`git pull`，将远程master分支更新pull下来，可以看到pull到本地后master有更新。

   ![image-20200222145817333](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222145817333.png)

3. 再切换回feature2，使用`git rebase master`，这个命令的作用是将feature2分支所有commit都copy一份，然后“接到”本地master分支上，如下图。

   ![image-20200222145954609](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222145954609.png)

   `git rebase master`涉及的实际操作是：将feature2中所有commit的脚本重新在本地master分支上再执行一次，因此这个过程中可能会有冲突，需要排除冲突。因此commit脚本是一次次执行的，因此rebase最糟糕的情况是，feature2上有多少次commit，就要排除多少次冲突。为了减少`git rebase master`排除冲突的次数，可以在`git rebase master`之前，使用`git rebase -i HEAD~n`将feature2上多个commit合并，冲突放在这里解决。

4. 切换到本地master，使用`git merge feature2`，看上图可知此时feature2和本地master肯定没有冲突，因此这个merge就只是将本地master节点提到与feature2一致，如下图：

   ![image-20200222150835961](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222150835961.png)

5. 最后，将本地的master push到远程，即得到如下结果：

   ![image-20200222150949594](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222150949594.png)

此时，feature2上开发的内容已经合并在了远程master中，而且不同于feature1的合并：feature1的所有commit体现为“横搭”在master主干上的紫线，而feature2的所有commit直接体现在master主干上（紫色的远程feature2可以删除）。

![image-20200222151450221](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200222151450221.png)

可以想象，如果所有feature都以这样的形式与master合并，最后master只有一条主干线，这个主干线上分成了若干小段，每一段对应之前一个feature的开发，这样看起来会舒适很多。

这样的合并形式会有一个小缺点，就是本地的feature2路径会被更改



#### Alibaba：回滚后被删除的分支恢复

1、git log --graph 查看并找到aone自动回滚操作提交的那个版本（log往往是"

[scm-auto: rollback to](http://gitlab.alibaba-inc.com/platform-management/seller-center-portal/commit/a3f328f0d71123bec3c4449a54a1f239c2db4647)XXX版本"字样），例如该版本叫3d8w9q4。然后跑到gitlab上面去看你的项目的commits

![e5badde0-7868-454d-a0e3-49d2c3509881](/Users/jacksu/Desktop/File/resource/image/notePics/e5badde0-7868-454d-a0e3-49d2c3509881.png)

2、在aone上新建变更，可以选择aone帮创建分支（从master创建），或者自己手工从master创建分支再在变更中使用此分支，例如叫feature/mybranch。将本地工作拷贝切换到新分支：git checkout feature/mybranch

3、对aone回滚操作进行逆操作：git revert 3d8w9q4

4、git log --stat 查看代码是否已经回来了。

5、修改文件，并提交。在aone中使用这个变更分支进行集成、测试、发布



#### push后撤回之前版本

场景：commit了一些错误的代码，并且push到了远程，需要撤回

1.git reset --hard [commitId]

首先在本地分支强制回退到之前的版本（commitId之后的内容不会被保存）

2.git push -f -u origin [远程分支名]

将本地的分支强制push到远程覆盖，保证远程的版本与本地版本保持一致

注意：这样强制回退并push到远程，需要告知其它使用该分支的同事做一样的回退步骤，不然他再次push的时候，可能又会将错误代码的commit带到远程



#### 分批add或reset

场景：修改了多个文件，但是它们不属于一个改动点，本着”一次commit一个改动的原则“，只需要add部分文件到暂存区，因此可以使用`git add -p`命令，根据提示，决定每一个文件的修改是否保存到暂存区

反之，若不小心使用`git add .`已经将多个修改点的文件都add到暂存区，可使用`git reset -p`命令，根据提示，决定每一个文件是否从暂存区撤回



#### 单独回滚某文件

场景：有时候需要单独将某文件回滚到很多commitId之前的版本（不能整体reset，不希望影响其它文件）

1.git reset [commitId] [fileName] 去回退某文件的修改（不回退commit）

2.git status 查看该文件的状态，根据提示使用 git restore 命令回退所有修改

3.再commit，即可使该文件回退到历史版本，且commit为最新信息

好处：生成最新的commit，所有回退内容变成了新增修改，在与master merge时，这些新增的修改能保证merge成功
## IDEA

### 技能

#### 远程debug

1. Edit Configurations

   ![image-20200113193830512](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200113193830512.png)

2. ![image-20200113193955578](/Users/jacksu/Desktop/File/resource/image/notePics/image-20200113193955578.png)

   如上图，创建一个remote，ip和port保证正确

3. 设置断点，开启debug，在日常环境下run，如果到了断点处则会和本地debug一样

其中端口号的配置，与机器中tomcat的配置有关，在 /home/admin/asip/bin/ 目录下，执行`cat -n setenv.sh | grep "JPDA_ADDRESS"`即可搜索到端口号的配置
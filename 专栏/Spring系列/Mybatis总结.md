## Mybatis总结

```
常见面试题:https://segmentfault.com/a/1190000013678579
```



### Mybatis VS Hibernate

```
Hibernate:是一个标准的ORM框架,比较重量级
	高度封装,不用写sql,开发时会降低开发周期
	学习成本高
	sql语句无法优化
	应用场景:OA(办公自动化系统)ERP(企业流程系统)
		适用于用户量不大,并发量小
		
Mybatis:不是ORM框架,只是对JDBC的轻量级封装
	学习成本低
	sql语句可以优化,便于维护
	编码量大,会拖慢开发周期
	应用场景:互联网项目
		适用于用户量大,并发量高
```

#### JDBC存在的问题

```
频繁创建和释放资源
sql语句 参数 结果集 都是硬编码,不利于维护
```



### 原理

#### 代理Dao

```
MyBatis框架中,每个pojo类可以不用创建它的XxxDao类,而是创建它的XxxMapper接口类,在接口中定义所有需要用的方法. 并且,每个类还要提供一个XxxMapper.xml配置文件,它的内容是XxxMapper接口所有方法的实现sql标签

因此是MyBatis框架利用接口,创建了动态代理对象,然后读取XxxMapper.xml配置文件,即可让动态代理对象实现相应的方法.
	UserMapper mapper = sqlSession.getMapper(UserMapper.class);
```



### 配置文件

#### SqlMapConfig.xml

```
配置dataSource,事务等公共配置;
配置sqlSessionFactory
配置XxxMapper.xml读取路径
```

```xml
<!-- 使jdbc.properties中的<k,v>可以用EL表达式 ${k}-->
<properties resources="jdbc.properties"></properties>

<!-- 和spring整合后 environments配置将废除-->
<environments default="development">
    <environment id="development">
        <!-- 使用jdbc事务管理-->
        <transactionManager type="JDBC" />
        <!-- 数据库连接池-->
        <dataSource type="POOLED">
            <property name="driver" value="${jdbc.driver}" />
            <property name="url" value="${jdbc.url}" />
            <property name="username" value="${jdbc.username}" />
            <property name="password" value="${jdbc.password}" />
        </dataSource>
    </environment>
</environments>

<!-- 包扫描导入XxxMapper接口,默认XxxMapper.xml路径与接口路径相同 -->
<mappers>
	<package name="cn.itheima.mapper"/>
</mappers>
```

#### XxxMapper.xml

```
每个pojo类的所有Dao方法,声明在XxxMapper接口中,实现在XxxMapper.xml中

Mapper接口实现方式是在xml中写sql语句标签,标签会声明方法名和返回元素的类.但是不会声明方法的参数,方法的参数通过#{}或${}使用.
	${}是拼接符,使用拼接符可能造成sql注入,处理sql注入:对传入参数进行校监(不允许sql关键字和空格)
	#{}是占位符,比如参数是(Person p),使用:#{name} (而不是#{p.name})

返回结果如果是一行一列,则sql标签的resultType可以是基本类型
```

````xml
<!-- namespace:接口全类名,id:接口方法名,resultType:返回值元素的类 -->
<mapper namespace="com.how2java.tmall.mapper.CategoryMapper">
    
    <select id="total" resultType="int">
        select count(*) from category
    </select>
       
</mapper>
````



### Mybatis的动态Dao开发

```
读取核心配置SqlMapConfig.xml -> 获取SqlSessionFactory对象(线程安全) -> 获取SqlSession对象(线程不安全,在方法中获取) -> 获取动态代理Dao对象 -> 执行CURD操作
```

```java
// 通过流将核心配置文件读取进来
InputStream inputStream = Resources.getResourceAsStream("SqlMapConfig.xml");
// 通过核心配置文件输入流来创建会话工厂
factory = new SqlSessionFactoryBuilder().build(inputStream);
// 获取会话
SqlSession openSession = factory.openSession();
// 动态代理Dao(不用手动编写openSession的CURD方法)
UserMapper mapper = openSession.getMapper(UserMapper.class);
List<User> list = mapper.findUserByUserName("王");
// 提交
openSession.commit();
```

#### 特点

```
1.SqlSession自动开启事务,但是需要手动commit
2.SqlSession是线程不安全的,需要在方法体内new出来使用(SqlSessionFactory线程安全)
3.Mybatis的动态Dao是通过XxxMapper接口和XxxMapper.xml实现的,默认XxxMapper接口和XxxMapper.xml路径相同
```



### XxxMapper.xml中的sql编写

#### 关联查询

```
多表查询时,查询出来的一行是跨了多张表的,比如A类中有属性是B类,那么查询出来的一行就包含了A对象字段和B对象字段,此时不能直接用resultType=A接收,因为这种方式默认全部字段都用于拼凑A对象中的普通属性
```

##### A类属性包含B类对象

```xml
<!-- 一对一映射 -->
<!-- 
 id:resultMap的唯一标识
 type:将查询出的数据放入这个指定的对象中
 注意:手动映射需要指定数据库中表的字段名与java中pojo类的属性名称的对应关系
  -->
<resultMap type="cn.itheima.pojo.Orders" id="orderAndUserResultMap">
    <!-- id标签指定主键字段对应关系
  column:列,数据库中的字段名称
  property:属性,java中pojo中的属性名称
   -->
    <id column="id" property="id"/>

    <!-- result:标签指定非主键字段的对应关系 -->
    <result column="user_id" property="userId"/>
    <result column="number" property="number"/>
    <result column="createtime" property="createtime"/>
    <result column="note" property="note"/>

    <!-- 这个标签指定单个对象的对应关系 
  property:指定将数据放入Orders中的user属性中
  javaType:user属性的类型
  -->
    <association property="user" javaType="cn.itheima.pojo.User">
        <id column="uid" property="id"/>
        <result column="username" property="username"/>
        <result column="birthday" property="birthday"/>
        <result column="sex" property="sex"/>
        <result column="address" property="address"/>
    </association>
</resultMap>

<!-- sql配置标签的返回类型不再是resultType,而是resultMap -->
<select id="findOrdersAndUser2" resultMap="orderAndUserResultMap">
    select a.*, b.id uid, username, birthday, sex, address 
    from orders a, user b 
    where a.user_id = b.id
</select>

```

##### A类属性包含List<B类对象>

```xml
<!-- 一对多映射 -->
<resultMap type="cn.itheima.pojo.User" id="userAndOrdersResultMap">
    <id column="id" property="id"/>
    <result column="username" property="username"/>
    <result column="birthday" property="birthday"/>
    <result column="sex" property="sex"/>
    <result column="address" property="address"/>

    <!-- 指定对应的集合对象关系映射
  property:将数据放入User对象中的ordersList属性中
  ofType:指定ordersList属性的泛型类型
   -->
    <collection property="ordersList" ofType="cn.itheima.pojo.Orders">
        <id column="oid" property="id"/>
        <result column="user_id" property="userId"/>
        <result column="number" property="number"/>
        <result column="createtime" property="createtime"/>
    </collection>
</resultMap>

<select id="findUserAndOrders" resultMap="userAndOrdersResultMap">
    select a.*, b.id oid ,user_id, number, createtime 
    from user a, orders b where a.id = b.user_id
</select>
```



#### 动态sql

```
XxxMapper.xml中的sql语句,结合if where foreach 标签可以实现SQL语句拼接的高级查询效果
```

##### if标签

```xml
<select id="list" resultType="Category">
    select * from   category  order by id desc
    <if test="start!=null and count!=null">
        limit #{start},#{count}
    </if>
</select>
```

##### where标签

![](/Users/jacksu/Desktop/File/resource/image/notePics/where%E6%A0%87%E7%AD%BE.jpg)

##### foreach标签

```xml
<select id="findUserByIds" parameterType="cn.itheima.pojo.QueryVo" resultType="cn.itheima.pojo.User">
    select * from user

    <where>
        <if test="ids != null">
            <!-- 
    foreach:循环传入的集合参数
    collection:传入的集合的变量名称
    item:每次循环将循环出的数据放入这个变量中
    open:循环开始拼接的字符串
    close:循环结束拼接的字符串
    separator:循环中拼接的分隔符
     -->
            <foreach collection="ids" item="id" open="id in (" close=")" separator=",">
                #{id}
            </foreach>
        </if>
    </where>
</select>
```

##### sql封装重用

```xml
    <!-- 封装sql条件,封装后可以重用. id:是这个sql条件的唯一标识 -->
    <sql id="user_Where">
        <where>
            <if test="username != null and username != ''">
                and username like '%${username}%'
            </if>
            <if test="sex != null and sex != ''">
                and sex=#{sex}
            </if>
        </where>
    </sql>

    <select id="findUserByUserNameAndSex" parameterType="cn.itheima.pojo.User" resultType="cn.itheima.pojo.User">
        select * from user 
        <!-- 调用封装sql -->
        <include refid="user_Where"></include>
    </select>
```



### Spring整合MyBatis

```
1.数据源(连接池)交给Spring配置文件管理
2.会话工厂由Spring配置文件管理(IOC)
	XxxMapper.xml路径也在Spring配置文件中
3.整合后,SqlSession会话由Spring管理,不需要手动关闭.也不需要手动提交事务(AOP)
4.XxxMapper接口的包扫描交给Spring配置文件管理.

即SqlMapComfig.xml配置文件中内容,都可以转移到applicationContext.xml中.并且applicationContext.xml中用sqlSession(本质是会话工厂)配置代替了SqlMapComfig.xml中environment的配置
```

#### Spring配置文件中的MyBatis部分

````xml
<!-- 导入数据库配置文件 -->
<context:property-placeholder location="classpath:jdbc.properties"/>
<!-- 配置数据库连接池 -->
<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
    <!-- 基本属性 url、user、password -->
    <!--<property name="driver" value="${jdbc.driver}" />-->
    <property name="url" value="${jdbc.url}" />
    <property name="username" value="${jdbc.username}" />
    <property name="password" value="${jdbc.password}" />

    <!-- 配置初始化大小、最小、最大 -->
    <property name="initialSize" value="1" />
    <property name="minIdle" value="1" />
    <property name="maxActive" value="20" />

    <!-- 配置获取连接等待超时的时间 -->
    <property name="maxWait" value="60000" />

    <!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
    <property name="timeBetweenEvictionRunsMillis" value="60000" />

    <!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
    <property name="minEvictableIdleTimeMillis" value="300000" />

    <property name="validationQuery" value="SELECT 1" />
    <property name="testWhileIdle" value="true" />
    <property name="testOnBorrow" value="false" />
    <property name="testOnReturn" value="false" />

    <!-- 打开PSCache，并且指定每个连接上PSCache的大小 -->
    <property name="poolPreparedStatements" value="true" />
    <property name="maxPoolPreparedStatementPerConnectionSize"
              value="20" />
</bean>

<!--Mybatis的SessionFactory配置-->
<bean id="sqlSession" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="typeAliasesPackage" value="com.jacksu.tmall.pojo" />
    <property name="dataSource" ref="dataSource"/>
    <!-- XxxMapper.xml位置不再和Mapper接口一致,在这里配置 -->
    <property name="mapperLocations" value="classpath:mapper/*.xml"/>
</bean>

<!--Mybatis的Mapper文件识别-->
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="basePackage" value="com.jacksu.tmall.mapper"/>
</bean>
````



### 逆向工程

```
表 -> Pojo -> XxxMapper接口 -> XxxMapper.xml
以上即是MyBatis的数据创建过程,对于单表的CURD操作,上述过程是高度重复的,因此可以指定规则,自动根据表生成后面的pojo,接口和xml文件.这个过程称为逆向工程.

生成条件:generator.xml配置文件(指定生成规则)+StartServe.class(自动生成的执行类)
一张表自动生成:pojo类,pojo_Example(离线查询条件),Mapper接口,Mapper.xml
```

#### generatorConfig.xml

````xml
为正确根据表创建对应的pojo,mapper和xml文件,需要一些正确的配置:
	1.OverIsMergeablePlugin插件.配置它的路径,它就是一个工具类,防止重复执行自动生成代码带来的异常
	2.声明自动生成的代码不需要注释
	3.配置数据库基本链接信息
	4.指定pojo,mapper,xml的存放位置
	5.表和pojo类的对应关系
````

````xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <context id="DB2Tables"    targetRuntime="MyBatis3">
        <!--避免生成重复代码的插件-->
        <plugin type="com.how2java.tmall.util.OverIsMergeablePlugin" />
 
        <!--是否在代码中显示注释-->
        <commentGenerator>
            <property name="suppressDate" value="true" />
            <property name="suppressAllComments" value="true" />
        </commentGenerator>
 
        <!--数据库链接地址账号密码-->
        <jdbcConnection driverClass="com.mysql.jdbc.Driver" connectionURL="jdbc:mysql://localhost/tmall_ssm" userId="root" password="admin">
        </jdbcConnection>
        <!--不知道做什么用的。。。反正贴上来了~-->
        <javaTypeResolver>
            <property name="forceBigDecimals" value="false"/>
        </javaTypeResolver>
        <!--生成pojo类存放位置-->
        <javaModelGenerator targetPackage="com.how2java.tmall.pojo" targetProject="src/main/java">
            <property name="enableSubPackages" value="true"/>
            <property name="trimStrings" value="true"/>
        </javaModelGenerator>
        <!--生成xml映射文件存放位置-->
        <sqlMapGenerator targetPackage="mapper" targetProject="src/main/resources">
            <property name="enableSubPackages" value="true"/>
        </sqlMapGenerator>
        <!--生成mapper类存放位置-->
        <javaClientGenerator type="XMLMAPPER" targetPackage="com.how2java.tmall.mapper" targetProject="src/main/java">
            <property name="enableSubPackages" value="true"/>
        </javaClientGenerator>
 
        <!--生成对应表及类名-->
        <table tableName="category" domainObjectName="Category" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="true" selectByExampleQueryId="false">
            <property name="my.isgen.usekeys" value="true"/>
            <property name="useActualColumnNames" value="true"/>
            <generatedKey column="id" sqlStatement="JDBC"/>
 
        </table>
    </context>
</generatorConfiguration>
````

#### MybatisGenerator启动类

```
配置好了之后,使用启动类执行一次,即可自动生成.注意如果再执行一次,就会重复生成,但是因为配置了OverIsMergeablePlugin插件,所以重复生成后不会有bug,但是会覆盖之前所有的改动,相当于默认初始化.
```

````java
public class MybatisGenerator {

    public static void main(String[] args) throws Exception {
        String today = "2017-10-14";

        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        Date now = sdf.parse(today);
        Date d = new Date();

        if(d.getTime()>now.getTime()+1000*60*60*24){ // 避免后面不小心运行
            System.err.println("——————未成成功运行——————");
            System.err.println("——————未成成功运行——————");
            System.err.println("本程序具有破坏作用，应该只运行一次，如果必须要再运行，需要修改today变量为今天，如:" + sdf.format(new Date()));
            return;
        }

        if(false)
            return;
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        InputStream is= MybatisGenerator.class.getClassLoader().getResource("generatorConfig.xml").openStream();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(is);
        is.close();
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);

        System.out.println("生成代码成功，只能执行一次，以后执行会覆盖掉mapper,pojo,xml 等文件上做的修改");

    }
}
````

#### 自动生成的Mapper接口

```java
public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    List<Category> selectByExample(CategoryExample example);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
}

```

````
updateByPrimaryKeySelective 和 updateByPrimaryKey的区别:
	前者是选择性更新,传入对象的属性值如果不是null,就更新,是null就不变
	后者是全部更新,即根据传入对象更新数据库的记录,无论它的属性是不是null
	根据需要选择合适的方法,前者的好处是传入对象只用包含需要更新的信息和id即可
selectByExample方法,该方法的参数就是自动生成的XxxExample类
通过对XxxExample对象方法,可以实现多功能的条件查询
````

`````java
// 离线查询条件pojo_Example的使用
public void testFindUserAndSex() throws Exception{

    // 创建UserExample对象
    UserExample userExample = new UserExample();
    // 获取UserExample对象的离线查询条件
    Criteria createCriteria = userExample.createCriteria();
    // 设置离线查询条件
    createCriteria.andUsernameLike("%王%");
    createCriteria.andSexEqualTo("1");
    
	// 查询
    UserMapper userMapper =  (UserMapper)applicatonContext.getBean("userMapper");
    List<User> list = userMapper.selectByExample(userExample);
    System.out.println(list);
}
`````



### PageHelper分页插件

````
PageHelper是MyBatis的一个分页插件
自定义分页一般是,首先自定义一个Page类,然后每次在查询之前通过设置Page对象的curPage,count等参数,将Page对象作为查询方法的参数,sql语句中加入 limit start,count 实现.
而PageHelper只用在查询之前执行一些类的静态方法,就可以完成分页查询,sql语句就不用再加limit
````

````xml
使用分页插件,需要在SqlMapConfig.xml中配置插件(当然要导入jar包):
    <plugins>
        <plugin interceptor="com.github.pagehelper.PageInterceptor" />
    </plugins>

如果是整合SSM,则在applicationContext.xml中的sqlSession配置中,要新增:
    <property name="plugins">
        <array>
            <bean class="com.github.pagehelper.PageInterceptor">
                <property name="properties">
                    <value>
                    </value>
                </property>
            </bean>
        </array>
    </property>
````

````java
// 分页后的Controller类方法
@RequestMapping("admin_category_list")
public String list(Model model,Page page){
    PageHelper.offsetPage(page.getStart(),page.getCount()); // 有了这个,Page对象不用再往下传
    List<Category> cs= categoryService.list(); 
    int total = (int) new PageInfo<>(cs).getTotal(); // 通过分页插件的PageInfo获取total
    page.setTotal(total);
    model.addAttribute("cs", cs);
    model.addAttribute("page", page);
    return "admin/listCategory";
}

// 用了分页插件后的改变
	1.Page对象只作为Controller方法的参数,因为分页数据是从前端传来的.不会在service和mapper中出现.
        但是Page对象还是要作为参数传给jsp页面,因为页码的设置需要page.PageHelper只是帮助分页查询
   	2.mapper.xml中sql语句不用写limit,并且也可以通过PageInfo获取total,少写了一条sql
   	3.所以PageHelper可能是利用AOP,在下一次使用sql查询时,加上limit start,count
````




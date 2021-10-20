## SpringMVC总结

### SpringMVC VS Struts2

```
1.入口不同;Struts2的入口是一个Filter;SpringMVC入口是一个Servlet
2.Sturts2接收数据使用模型驱动,线程不安全,所以Action类是多例的;SpringMVC是用方法参数接收数据,是线程安全的,所以Controller类是单例的
	模型驱动即用Action类的属性接收参数,所有方法共用,因此线程不安全
	Struts2是基于类开发,一个类一个访问路径,通过通配符访问具体的方法;
	SpringMVC是基于方法开发,一个方法一个访问路径(与类的全限定名+方法名唯一对应)
3.Struts2返回数据用值栈,页面使用OGNL取值;SpringMVC返回数据使用Model(本质还是用Request域),页面使用jstl取值
```



### 原理

```
在web.xml中配置SpringMVC的Servlet分发器,因此所有的请求都被SpringMVC控制分发,给它的Controller类
	其中什么url分发到什么Controller类,由处理器映射器负责
	从前端传过来的数据,怎么封装到Controller类的方法参数中,由处理器适配器负责
Controller类进行一定业务逻辑,然后返回数据和页面,返回方式有两种:
	方式1:通过ModelAndView
		modelAndView.addObject("itemList", itemList); // 返回数据
		modelAndView.setViewName("itemList"); // 返回页面
		return modelAndView;
	方式2:通过model返回数据,return返回页面路径
		model.addAttribute("itemList",itemList); // 返回数据
		return "itemList"; // 返回页面		
注意到Controller返回的页面路径都是不完整的,因为SpringMVC.xml中配置了视图解析器,指定了它的前缀和后缀

处理器映射器 负责请求分发, 处理器适配器 负责解析获取/输出参数, 视图解析器 负责返回页面响应客户
```

简略版

![](/Users/jacksu/Desktop/File/resource/image/notePics/SpringMVC简略流程图.jpg)

复杂版

![](/Users/jacksu/Desktop/File/resource/image/notePics/SpringMVC流程图.jpg)



### 配置

#### SpringMVC.xml

```xml
<!-- 开启@Controller注解扫描 -->
<context:component-scan base-package="cn.itheima.controller"></context:component-scan>

<!-- 注解驱动,自动配置最新版注解的映射器和适配器 -->
<!-- 如果有配置转换器,也需要在注解驱动里配置 -->
<MVC:annotation-driven />

<!-- 视图解析器,默认是jsp解析,前缀后缀作用:Controller层的方法return值不用加前缀和后缀-->
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="viewClass" value="org.springframework.web.servlet.view.JstlView" />
    <property name="prefix" value="/WEB-INF/jsp/"></property>
    <property name="suffix" value=".jsp"></property>
</bean>

<!-- 对上传文件的解析-->
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver"/>
```

#### web.xml中SpringMVC部分

```
在web.xml中配置SpringMVC核心控制器DispatcherServlet时,要指定配置文件SpringMVC.xml所在的位置,不然默认去WEB-INF目录下找.
init-param标签中的classpath指定的位置是:WEB-INF/classes; (src目录下的所有文件在项目编译时都会复制到该文件下)
```

```xml
<!-- SpirngMVC前端控制器 -->
<servlet>
    <servlet-name>SpirngMVC</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <!-- 指定SpringMVC核心配置文件位置 -->
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:SpringMVC.xml</param-value>
    </init-param>
    <!-- tomcat启动的时候就加载这个servlet -->
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>SpirngMVC</servlet-name>
    <!-- 注意这里是 / 不是 /*,如果是/*,会拦截所有请求,包括jsp页面 -->
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

#### SSM整合配置文件

```
	1)Dao层
		pojo和映射文件以及接口使用逆向工程生成
		SqlMapConfig.xml   mybatis核心配置文件
			Mybatis核心配置文件不用引入sql配置文件,也不用包扫描Mapper接口
		ApplicationContext-dao.xml 整合后spring在dao层的配置
			数据源
			会话工厂
			扫描Mapper
	2)service层
		事务			ApplicationContext-trans.xml
		@Service注解扫描	ApplicationContext-service.xml
	3)controller层
		SpringMVC.xml 
			注解扫描:扫描@Controller注解
			注解驱动:替我们显示的配置了最新版的处理器映射器和处理器适配器
			视图解析器:显示的配置是为了在controller中不用每个方法都写页面的全路径
	4)web.xml
		spring监听
		springMVC前端控制器配置
```

#### SSM整合注解

```
@Service("userService") Service层接口实现类的注解

@Controller Controller类的注解
	@RequestMapping("/list") Controller类中方法的访问路径注解
	// method限制了这个方法请求的方式,这里只能采用get方式访问
	@RequestMapping(value="add", method=RequestMethod.GET)
	
@Autowired 属性注入的注解,适用于一个接口只有一个实现类的属性注入
@Resource("userService") 属性注入的注解,适用于一个接口多个实现类的属性注入
```

### Controller类

```
本质是Servlet,每一个Controller类都有一个@RequestMapping路径,结合它的每个方法的@RequestMapping路径
即是访问的url(省略了 协议://主机:端口号/项目名)
```

```java
@Controller
@RequestMapping("/items")
public class ItemsController {
	//指定url到请求方法的映射
	@RequestMapping("/list")
	public ModelAndView  itemsList() throws Exception{
		List<Items> itemList = new ArrayList<>();
		
		//商品列表
		Items items_1 = new Items();
		items_1.setName("联想笔记本_3");
		items_1.setPrice(6000f);
		items_1.setDetail("ThinkPad T430 联想笔记本电脑！");
		Items items_2 = new Items();
		items_2.setName("苹果手机");
		items_2.setPrice(5000f);
		items_2.setDetail("iphone6苹果手机！");
		itemList.add(items_1);
		itemList.add(items_2);
		
		//模型和视图
		//Model模型: 模型对象中存放了返回给页面的数据
		//view视图: 视图对象中指定了返回的页面的位置
		ModelAndView modelAndView = new ModelAndView();
		//将返回的模型数据
		modelAndView.addObject("itemList", itemList);
		//指定返回的页面
        modelAndView.setViewName("itemList");
		// 本质是modelAndView.setViewName("/WEB-INF/jsp/itemList.jsp");
		return modelAndView;
	}
}
```

#### 从页面获取参数

##### 普通参数获取

```java
// 方式1:本质还是使用的Servelt(Request)的获取参数方法
    在Controller的方法中,添加参数HttpServletRequest HttpServletResponse
    通过 request.getParameter("id") 即可获取从页面传递过来的参数

// 方式2:自动获取基本类型参数,页面的传递参数名称 与 Controller类的方法参数名称一致
	通过在web.xml中配置Spring提供的过滤器,解决post请求乱码问题
	
// 方式3:自动获取pojo类型,页面的传递参数名称 与 Controller类的方法参数(pojo)的各个属性名称一致
	注意是参数名=属性名 不是参数名=对象.属性名

// 方式4:对于vo类型(类的属性还是类),页面传递参数名称形式为 vo属性名.属性
	vo.属性名 类型是一个pojo; 因此vo属性名.属性 类型是基本类型
```

##### 集合参数获取

````xml
// 数组(存放基本数据类型),方法中参数的类有一个属性是数组
// 要求页面中input标签的name名称都相同,且等于 "vo类属性名"  (这个vo类型的属性就是一个基本类型数组)
// 此时页面中一个input的value就是数组中的一个数据

// List(存放对象数据类型),方法中参数的类有一个属性是List<T>
// 页面中input标签的name名称为,"vo类属性名[索引].属性"
// 此时页面中一个input的value就是List中一个元素的属性,索引是用foreach标签
<!-- 部分代码 -->
<c:forEach items="${itemList }" var="item" varStatus="status">
<tr>
	<!-- name属性名称要等于vo中的接收的属性名 -->
	<!-- 如果批量删除,可以用List<pojo>来接收,页面上input框的name属性值= vo中接收的集合属性名称+[list的下标]+.+list泛型的属性名称 -->
	<td>
		<input type="checkbox" name="ids" value="${item.id }"/>
		<input type="hidden" name="itemsList[${status.index }].id" value="${item.id }"/>
	</td>
	<td><input type="text" name="itemsList[${status.index }].name" value="${item.name }"/></td>
	<td><input type="text" name="itemsList[${status.index }].price" value="${item.price }"/></td>
	<td><input type="text" name="itemsList[${status.index }].createtime" 
			   value="<fmt:formatDate value="${item.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>"/></td>
	<td><input type="text" name="itemsList[${status.index }].detail" value="${item.detail }"/></td>
	
	<td><a href="${pageContext.request.contextPath }/items/itemEdit/${item.id}">修改</a></td>

</tr>
</c:forEach>
````

##### 自定义转换器:格式转换问题

> 页面中的参数值都是String,Strng转到基本类型,都可以自动完成,但是要转成Date日期类型,则需要指定转换器类型,该转换器实现Spring核心包的Converter<S,T>接口
>
> 并在SpringMVC.xml中配置自定义转换器

```java
// 转换器:String=>Date
public class CustomGlobalStrToDateConverter implements Converter<String, Date> {
	@Override
	public Date convert(String source) {
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(source);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
```

```xml
<!-- 转换器配置:在SpringMVC中配置了转化器类后,还要在注解驱动里声明这个配置
	因为格式转换是交给处理器适配器来做,而注解驱动则是配置最新的处理器映射器和适配器
 -->

	<!-- 配置自定义转换器 注意: 一定要将自定义的转换器配置到注解驱动上-->
	<bean id="conversionService"
class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
		<property name="converters">
			<set>
				<!-- 指定自定义转换器的全路径名称 -->
				<bean class="cn.itheima.controller.converter.CustomGlobalStrToDateConverter"/>
			</set>
		</property>
	</bean>

<!-- 注解驱动:替我们显示的配置了最新版的注解的处理器映射器和处理器适配器 -->
    <MVC:annotation-driven conversion-service="conversionService"></MVC:annotation-driven>
```

#### 输出参数到页面

```java
// 方式1:通过ModelAndView
// 方式2:model作为方法的参数,通过model.addAttribute("user",user)传递数据
	model是对request的加强,此时即使是重定向,也可以将数据带到新的页面中
	因为在SpringMVC中配置了视图解析器,这个路径省略了前缀和后缀
	// 请求转发
	return "forward:userEdit"; // 相对路径,不以"/"开头
	return "forward:/items/itemEdit"; // 绝对路径,以"/"开头
	// 重定向
	return "redirect:userEdit"; // 相对路径
	请求转发的网址url不变,所以适用于return的指向是某个路径的jsp时
	重定向的url会变,所以适用于return的指向也是个url时(即Controller类的某个方法)

	
页面传递参数给后端时,对于类的属性值,页面中name是和属性名一致.
    如页面: ?start=20   类Page有一个int的属性start
后端输出参数到页面时,页面使用还是用${名称.属性}使用
	如方法 model.set("page",page)  页面: ${page.curPage}
		页面使用数据使用el表达式,页面绑定数据到后端,只用注意name的名称即可
```

#### json数据交互

```
1.导入jar包,如果springMvc.xml中配置了注解驱动,则不用配置其他
2.页面中使用ajax,发送数据
3.接收数据:Controller类方法参数中使用@RequestBody注解(自动将json
格式数据封装成对象),页面中json的key要与java中pojo的属性名一样
4.返回数据:Controller类方法加上@RequestBody注解(自动将java对象转换成json格式返回),返回的json的key是属性名,value是属性值
```

````java
@RequestMapping("/sendJson")
@ResponseBody // 输出json格式
public Items json(@RequestBody Items items) throws Exception{ // json格式适配
    Syst em.out.println(items);
    return items;
}
````

### 拦截器

```
拦截器类实现HandlerInterceptor接口,重写preHandle postHandle afterHandle 三个方法
	方法返回值为true时放行,为false拦截
它们分别在被拦截的Controller方法执行前,执行时和执行完return后执行
拦截器在SpringMVC.xml中配置:它的拦截范围 以及 它的类全路径
```

````xml
<!-- 配置拦截器 -->
<mvc:interceptors>		
    <mvc:interceptor>
        <!-- 拦截请求的路径    要拦截所有必需配置成/** -->
        <mvc:mapping path="/**"/>
        <!-- 指定拦截器的位置 -->
        <bean class="cn.itheima.interceptor.LoginInterceptor"></bean>
    </mvc:interceptor>
</mvc:interceptors>
````



Q：拦截器和过滤器有什么不同？

[参考](https://juejin.cn/post/6844904179958284301)

```
过滤器是 tomcat 提供的能力，它作用在 servlet 之前；拦截器是 spring 提供的能力，它作用在 controller 之前，也可以自定义一些作用范围，
```


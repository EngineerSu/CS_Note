# Spring源码阅读

阅读《Spring源码深度解析》

## 第 2 章 容器的基本实现

### Resource

spring中对所有可访问资源的抽象顶层接口。常见的一些实现类有：ClassPathResource、FileSystemResource、ByteArrayResource等

### XmlBeanFactory的创建

![image-20201128150238163](d:/resource/notePic/image-20201128150238163.png)

核心步骤是 XmlBeanDefinitionReader 对 Resource 的xml文件进行解析，进行bean的注册。

注意到 XmlBeanDefinitionReader 只是将 xml文件 统一解析成 Document，最后 bean 的解析还是交给 BeanDefinitionDocumentReader 去做，这里非常符合类的单一职责原理。

![image-20201128150809762](d:/resource/notePic/image-20201128150809762.png)



细节：

- spring对xml的不同格式模板寻找方式是不同的。DTD是在当前路径寻找，XSD是在META_INF/Spring.schemas中找
- bean标签可以指定profile属性，用于区分不同的生产环境，最常见的应用是数据库bean。



## 第 3 章 默认标签的解析

细节：

- BeanDefinition是一个接口，xml中的bean会被解析成这样的一个个对象
- 解析的BeanDefinition会存在BeanDefinitionRegistry（同样是一个接口）中，BeanDefinitionRegistry就像是Spring内置的bean数据库，存储形式主要是map
- BeanDefinition解析注册完成后，会发布一个事件（这里仅作为一个扩展，目前Spring监听该事件发生后，没有做任何事情）



## 第 4 章 自定义标签解析

详细介绍了如何创建Spring的自定义标签（Spring给的解决方案，只用做很少的工作，即可自定义属于自己的标签格式）





## 第 5 章 bean 的加载

### 单例bean的生命周期

![1561807914051](d:/resource/notePic/1561807914051.png)



### Aware

Spring中提供了一些Aware接口，实现这些接口的bean在实例化后，会获取相应的资源。比如BeanFactoryAware：

```java
public class Counter implements BeanFactoryAware {
    private BeanFactory beanFactory;
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // 实例化后可以获取BeanFactory资源
        this.beanFactory = beanFactory;
    }
}
```



### BeanPostProcessor

BeanPostProcessor的存在为AOP提供了非常便捷的扩展。

- BeanPostProcessor的实现类一样需要与普通bean一样注册到IOC容器中；并且使用ApplicationContext.getBean时会自动调用相关的BeanPostProcessor逻辑，但是使用BeanFactory.getBean则不会，需要显示的去调用（addBeanPostProcessor）



### InitializingBean 和 DisposableBean

它们可以认为是，Spring在bean生命周期中留给用户的业务扩展（init-method和destroy-method是指定类内已有方法，前者不需要修改类代码），顺序方便都在init-method和destroy-method前面



### FactoryBean

区别 FactoryBean 与 BeanFactory，前者是一种特殊的bean，后者是factory。

对于实现了FactoryBean接口的bean，当使用`ApplicationContext.getBean(beanName)`获取bean时，获取到的不是FactoryBean的对象，而是FactoryBean对象调用getObject方法返回的对象。如果要获取FactoryBean的对象，应该使用`ApplicationContext.getBean("&" + beanName)`

Q：为什么需要FactoryBean呢？

A：能更灵活地组装bean。因为FactoryBean相当于对传入的配置再调用getObject逻辑，生成需要的bean对象。当传入的配置太复杂时，可以考虑抽取逻辑到getObject中。



### 循环依赖

Spring中存在三种循环依赖：

- 单例构造器循环依赖：不允许
- 单例set循环依赖：允许
- 多例循环依赖：不允许



Q：如何检测单例构造器循环依赖？

A：对于正在创建的bean（构造方法还未执行完）的bean，有一个“当前创建bean池”会记录它们的beanName，当创建完毕后会删除beanName。如果在往这个“当前创建bean池”插入新数据时，发现该数据已存在，那么就可以判断循环依赖了

Q：如何实现允许单例set循环依赖？

A：对于已经执行完构造方法的bean，会创建并缓存它们的ObjectFactory，当set循环依赖时，取不到bean，可以取它的ObjectFactory

Q：为什么多例循环依赖不允许？

A：因为Spring不缓存多例bean

Q：是否可以禁用单例的循环依赖？

A：可以设置



## 第 6 章 容器功能的扩展

ApplicationContext和BeanFactory都可以用于bean的加载和获取，但是前者功能更丰富，它包含了更多扩展设计。ApplicationContext初始化成功后，会发布ContextRefreshedEvent事件

### ClassPathXMLApplicationContext

- 其中`initPropertySources()`留给扩展使用

### SpEL

Spring Express Language，使用 `#{}` 作为定界符，大括号中的内容都是SpEL

### PropertyEditorSupport

用于自定义属性编辑器，比如bean声明时要注入Date类型，直接输入字符串不声明属性编辑器是会报错的。但是Spring自带了很多属性编辑器，比如CustomDateEditor

### BeanFactoryPostProcessor

当Spring加载任何实现了这个接口的bean配置时，都会在bean工厂载入所有bean的配置（不是实例化bean）之后执行`postProcessbBeanFactory`方法

类似于BeanPostProcessor，Spring中留了很多类似的扩展接口设计，扩展性很好。



## 第 7 章 AOP

了解了基于注解如何使用 AOP，并输出了笔记（Spring目录下）



## 第 8 章 数据库连接 JDBC

了解了 Java 中 JDBC 的使用，以及 Spring 中 JDBC 的使用，并输出了笔记




















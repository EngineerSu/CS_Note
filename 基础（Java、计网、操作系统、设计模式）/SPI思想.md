## SPI思想

SPI全称是Service Provider Interface，即使用方定义接口，以及接口的实现规则，服务提供方按规则实现接口，这样只用简单的配置，就可以使用接口的实现类。

API全称是Application Programming Interface，即服务提供方实现服务，暴露接口，提供给使用方。

![img](img/SPI思想/API VS SPI.png)



### JDK中的SPI思想

> 在jdk6里面引进的一个新的特性ServiceLoader，从官方的文档来说，它主要是用来装载一系列的service provider。而且ServiceLoader可以通过service provider的配置文件来装载指定的service provider。当服务的提供者，提供了服务接口的一种实现之后，我们只需要在jar包的META-INF/services/目录里同时创建一个以服务接口命名的文件。该文件里就是实现该服务接口的具体实现类。而当外部程序装配这个模块的时候，就能通过该jar包META-INF/services/里的配置文件找到具体的实现类名，并装载实例化，完成模块的注入。

举例说明SPI思想的实现：

定义接口Search

```java
package com.cainiao.ys.spi.learn;

import java.util.List;

public interface Search {
    public List<String> searchDoc(String keyword);   
}
```

实现1：文件搜索

```java
package com.cainiao.ys.spi.learn;

import java.util.List;

public class FileSearch implements Search{
    @Override
    public List<String> searchDoc(String keyword) {
        System.out.println("文件搜索 "+keyword);
        return null;
    }
}
```

实现2：数据搜索

```java
package com.cainiao.ys.spi.learn;

import java.util.List;

public class DatabaseSearch implements Search{
    @Override
    public List<String> searchDoc(String keyword) {
        System.out.println("数据搜索 "+keyword);
        return null;
    }
}
```

接下来在resources下新建`META-INF/services/`目录，然后文件，名称为`com.cainiao.ys.spi.learn.Search`，文件内容是实现类的全限定名，如`com.cainiao.ys.spi.learn.DatabaseSearch`

编写测试方法

```java
package com.cainiao.ys.spi.learn;

import java.util.Iterator;
import java.util.ServiceLoader;

public class TestCase {
    public static void main(String[] args) {
        ServiceLoader<Search> s = ServiceLoader.load(Search.class);
        Iterator<Search> iterator = s.iterator();
        while (iterator.hasNext()) {
           Search search =  iterator.next();
           search.searchDoc("hello world");
        }
    }
}
```

输出结果为：数据搜索 hello world

如果更换`META-INF/services/com.cainiao.ys.spi.learn.Search`的内容为另一个实现类，则输出结果会随之改变。即这里的使用方制定了接口的实现规则(配置文件放在制定目录下，且文件名为接口全限定名，文件内容是实现类的全限定名)。通过这个规则，改动的时候只用改动少量的配置文件，即可让接口使用新的实现类。

![img](img/JDK实现SPI的思想.png)



参考：[Java SPI思想梳理](https://zhuanlan.zhihu.com/p/28909673)
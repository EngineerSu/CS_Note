# JavaNotes

## Java基础

- 计算机为什么存在补码？因为补码可以消灭减法，补码为什么可以表示负数？取反加1 其实是为了算这个正数需要跟哪个数相加变成 100...00，类比一下，10 进制里 -22 的补码是 78 ，本来是 78 - 100，因为最高位可以自动去掉，所以只保留了 78

### 数据类型

#### 基本类型

byte(8) / char(16) / short(16) / int(32) / float(32) / long(64) / double(64) / boolean(~)

boolean 只有两个值：true、false，可以使用 1 bit 来存储，但是具体大小没有明确规定。JVM 会在编译时期将
boolean 类型的数据转换为 int，使用 1 来表示 true，0 表示 false。JVM 支持 boolean 数组，但是是通过读写 byte数组来实现的。

int 32bit，范围是 -2^31^ ~ 2^31^-1

#### 包装类型

基本类型都有对应的包装类型，基本类型与其对应的包装类型之间的赋值使用自动装箱与拆箱完成。

Q：java 为什么有基础类型还需要包装类型？

A：因为 java 是面向对象的语言，基础类型不是对象，在很多使用时需要通过装箱将其转为对象类型使用，比如容器存储。

#### 缓存池

基本类型对应的缓冲池如下：
boolean values true and false
all byte values
short values between -128 and 127
int values between -128 and 127
char in the range \u0000 to \u007F

编译器会在自动装箱过程调用 valueOf() 方法(先判断值是否在缓存池中，如果在的话就直接返回缓存池的内容)，因此多个值相同且值在缓存池范围内的 Integer 实例使用自动装箱来创建，那么就会引用相同的对象。在使用这些基本类型对应的包装类型时，就可以直接使用缓冲池中的对象。

new Integer(123) 与 Integer.valueOf(123) 的区别在于：

- new Integer(123) 每次都会新建一个对象；
- Integer.valueOf(123) 会使用缓存池中的对象，多次调用会取得同一个对象的引用。

#### String

String 被声明为 final，因此它不可被继承。在 Java 8 中，String 内部使用 char 数组存储数据。在 Java 9 之后，String 类的实现改用 byte 数组存储字符串，同时使用 coder 来标识使用了哪种编码。

value 数组被声明为 final，这意味着 value 数组初始化之后就不能再引用其它数组。并且 String 内部没有改变 value数组的方法，因此可以保证 String 不可变。

**字符串常量池**(String Pool)在堆中(因为永久代空间有限, 字符串如果太多会导致OOM), 保存着所有字符串字面量值, 这些字面量在编译时期就确定, 自动加入到常量池

比如`Sting str = new String("abc");`中"abc"会被存进字符串常量池中, 并且还会在堆中创建一个对象(所有new操作都会在堆中创建对象), 这两个对象并不相等

**字符串为什么不可变?**

0. 正是由于字符串不可变，才能存在字符串常量池

1. 因为字符串经常会被用作hashmap的key, 字符串不变导致它的hash值也不变, 每个字符串创建后，它的 hash 值也是固定的
2. 安全性: 天生具有线程安全性

StringBuffer是线程安全的(synchronized实现), StringBuilder是线程不安全的

### 运算

#### 参数传递

Java 的参数是以值传递的形式传入方法中，而不是引用传递。

在将一个对象参数传入一个方法时，本质上是将对象的地址以值的方式传递到形参中。

#### 自动类型转换

1字面量是int类型, 1.1字面量是double类型, 因此以下表达式是错误的

```java
// 编译不通过
float f = 1.1;
short s = 1;
```

但是++ 或 += 会自动强制类型转换, 所以以下表达式正确的

```java
s += 1; // 相当于 s = (short)(s + 1);
```

#### switch

switch自从Java7开始支持字符串判断, 但是不支持long类型判断, 因为switch设计初衷就是判断少量值

```java
String s = "a";
switch (s) {
    case "a":
        System.out.println("aaa");
        break;
    case "b":
        System.out.println("bbb");
        break;
}
```

### 继承

#### 访问权限

Java 中有三个访问权限修饰符：private、protected 以及 public，如果不加访问修饰符，表示包级可见。

注意一个class文件中,只能有一个public修饰的类,该类名与class文件名应该保持一致!!

protected 用于修饰成员，表示在继承体系中成员对于子类可见，但是这个访问修饰符对于类没有意义。
protected修饰的成员变量, 在子类中通过super可以直接访问, 但是无论是父类的实例还是子类的实例, 都不能直接访问protected修饰的成员变量. 同样也不能直接访问protected修饰的方法. 但是如果子类重写了父类的protected方法, 并且将其访问权限改成了public, 那么子类对象是可以访问此方法的.

即protected真的是保护作用, 为子类提供可见性, 但是无论父类对象, 还是子类对象, 只要是对象都不能访问protected修饰的成员

#### 方法

方法身份证: 方法名 + 参数列表

方法重载: 方法名相同,参数列表不同(顺序,个数,类型);    返回值和权限修饰符无要求

方法重写: 方法名和参数列表都要相同. 因为要满足里式替换原则, 即所有父类引用的地方都可以使用子类对象, 所以要求重写权限修饰符 >= 父类, 返回值/异常类型是 父类返回值/异常类型的子类

#### 面向对象

封装: 隐藏实现,对外提供访问方式.
继承: 子类自动拥有父类非private的成员变量和成员方法
多态: 结合继承和重写,提高代码的复用性和拓展性.	如果子类没有重写父类方法（接口实现也认为是一种继承）,多态就没有意义.

#### 多态

父类引用指向子类对象,使用引用

​	访问成员变量时,编译看左边,运行看左边

​	访问成员方法时,编译看左边,运行看右边

多态的JVM过程：引用在执行方法时，首先会从引用所指向的对象方法表(存在方法区)中寻找方法，若找不到，再自底向上从其父类的方法表中寻找方法。[参考](https://www.cnblogs.com/kaleidoscope/p/9790766.html)

#### 抽象类与接口

**抽象类**可认为是类高度抽象出来的模板.它本身不创建对象,而是给子类继承使用
抽象类和普通类最大的区别是，抽象类不能被实例化，需要继承抽象类才能实例化其子类

**abstract修饰符**
1.修饰方法, 即抽象方法, 不能有方法体. 有抽象方法的类一定是抽象类
	但是抽象类可以不含有抽象方法,那么声明抽象类的意义就在于不让创建它的对象,而是专供其他类继承
	继承抽象类,如果不全部实现抽象方法,那么这个子类还是一个抽象类
2.abstract 不能与 private static final 关键词共存.
    因为 abstract 意义就在于提供子类继承,提供方法重写.而以上关键字修饰都是不允许继承的.

**接口**是抽象类的延伸，在 Java 8 之前，它可以看成是一个完全抽象的类，也就是说它不能有任何的方法实现。
从 Java 8 开始，接口也可以拥有默认的方法实现（方法签名增加一个 default 关键词），这是因为不支持默认方法的接口的维护成本太高了。在 Java 8 之前，如果一个接口想要添加新的方法，那么要修改所有实现了该接口的类。
接口的成员（字段 + 方法）默认都是 public 的，并且不允许定义为 private 或者 protected。
接口的字段默认都是 static 和 final 的。

**接口与抽象类的比较**

- 从设计层面上看，抽象类提供了一种 IS-A 关系，那么就必须满足里式替换原则，即子类对象必须能够替换掉所有父类对象。而接口更像是一种 LIKE-A 关系，它只是提供一种方法实现契约，并不要求接口和实现接口的类具有 IS-A 关系。
- 从使用上来看，一个类可以实现多个接口，但是不能继承多个抽象类。
- 接口的字段只能是 static 和 final 类型的，而抽象类的字段没有这种限制。
- 接口的成员只能是 public 的，而抽象类的成员可以有多种访问权限。

**使用选择**

使用接口：

- 需要让不相关的类都实现一个方法，例如不相关的类都可以实现 Compareable 接口中的 compareTo() 方法；
- 需要使用多重继承。

使用抽象类：

- 需要在几个相关的类中共享代码。
- 需要能控制继承来的成员的访问权限，而不是都为 public。
- 需要继承非静态和非常量字段。

#### 匿名内部类

Q：为什么多线程的匿名内部类中引用的参数必须用final修饰？

A：首先要理解，多线程的匿名内部类中引用的外部变量其实是个”复制品“，因为外部变量与匿名内部类中的变量所属线程不一样，因此它们的生命周期是不一样的，当外部线程执行结束后，会释放变量，而此时匿名内部类所属的线程可能还在run，因此需要一个"复制品"保证它们是不同的生命周期。同时，为了保证这个复制品与原始局部变量语义是一致的，需要增加final修饰。

复制的是引用值，加 final 是为了保证引用值永远指向某个对象。（多线程都可以修改这个对象，但是要保证是同一个对象，而且由于复制的引用值还保持对象的指向，所以对象也不会被回收）

当变量是final时,若是基本数据类型,由于其值不变,因而:其复制品与原始的量是一样.语义效果相同.(若:不是final,就无法保证:复制品与原始变量保持一致了,因为:在方法中改的是原始变量,而局部内部类中改的是复制品)
当 变量是final时,若是引用类型,由于其引用值不变(即:永远指向同一个对象),因而:其复制品与原始的引用变量一样,永远指向同一个对象(由于是 final,从而保证:只能指向这个对象,再不能指向其它对象),达到:局部内部类中访问的复制品与方法代码中访问的原始对象,永远都是同一个即:语义效 果是一样的.否则:当方法中改原始变量,而局部内部类中改复制品时,就无法保证:复制品与原始变量保持一致了(因此:它们原本就应该是同一个变量.)



### 关键字

#### instanceof

判断引用指向的对象 是否是 某种类的对象 (和引用类型无关)

```java
Person p1 = new Student();
boolean flag1 = p1 instanceof Student; // true
boolean flag2 = p1 instanceof Person; // false
```

````java
// 向上转型 和 向下转型
Person p1 = new Student(); // 自动向上转型,p1只能调用父类拥有的成员变量和方法
Student s1 = (Student)p1; // 强制向下转型可能会出错,因为p1指向的对象可能不是Student对象

因此,向下转型前,需要对引用做 instanceof 的判断
Student s2 = null;
if(p2 instanceof Student){
    s2 = (Student)p2;
}
````

#### final

1. 数据

   对于基本类型, final使数值不变

   对于引用类型, final使引用不变, 但是引用的对象本身是可以修改的

   注意声明成员变量时, 必须在对象生成前赋值(定义时,构造方法内or构造代码块内)

2. 方法

   声明方法不能被子类重写

3. 类

   不能被继承

#### static

1. 静态变量: 又称为类变量，也就是说这个变量属于类的，类所有的实例都共享静态变量，可以直接通过类名来
   访问它。静态变量在内存中只存在一份。
2. 静态方法: 静态方法在类加载的时候就存在了，它不依赖于任何实例。所以静态方法必须有实现，也就是说它不能是抽象方法。
3. 静态语句块: 在类初始化时运行一次。
4. 静态内部类: 非静态内部类依赖于外部类的实例，而静态内部类不需要。



### Object通用方法

```java
public native int hashCode()
public boolean equals(Object obj)
protected native Object clone() throws CloneNotSupportedException
public String toString()
public final native Class<?> getClass()
protected void finalize() throws Throwable {}
public final native void notify()
public final native void notifyAll()
public final native void wait(long timeout) throws InterruptedException
public final void wait(long timeout, int nanos) throws InterruptedException
public final void wait() throws InterruptedException
```

**equals()**结果为true, 表明两个对象是等价的, 等价的两个对象要求hash值一定相等(hash值相等的两个对象不一定等价, 这是由hash特性决定的), 所以重写equals方法, 必须重写hashCode方法. 因为**重写hash要使对象分布均匀, 所以hashCode方法一般要考虑到对象的每一个成员属性**.

**clone()** 是 Object 的 protected 方法，它不是 public，一个类不显式去重写 clone()，其它类就不能直接去调用该类实例的 clone() 方法。规定如果一个类没有实现 Cloneable 接口又重写了 clone() 方法，就会抛出 CloneNotSupportedException。

因此使用 clone() 方法来拷贝一个对象即复杂又有风险，它会抛出异常，并且还需要类型转换。Effective Java 书上讲到，最好不要去使用 clone()，可以使用拷贝构造函数或者拷贝工厂来拷贝一个对象。

```java
public class CloneConstructorExample {
    private int[] arr;
    public CloneConstructorExample() {
        arr = new int[10];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
    }
    public CloneConstructorExample(CloneConstructorExample original) {
        arr = new int[original.arr.length];
        for (int i = 0; i < original.arr.length; i++) {
            arr[i] = original.arr[i];
        }
    }
    public void set(int index, int value) {
        arr[index] = value;
    }
    public int get(int index) {
        return arr[index];
    }
}
```

wait notify 方法是线程间通信机制, 它们必须在同步块中执行, 但是它们本身并不保持同步, 这是要区别开的.

- 它们必须在同步块中使用，并且在同步块中只能调用锁对象的wait方法！！！
- wait会释放同步块的锁
- notify会通知wait的线程开始尝试获取锁，但是并不是说执行完notify，就释放了锁，释放锁必须等到同步块执行完释放锁，或者同步块对象开始wait释放锁

### 反射

[深入理解反射](https://www.sczyh30.com/posts/Java/java-reflection-1/)

简而言之，通过反射，我们可以在运行时获得程序或程序集中每一个类型的成员和成员的信息。程序中一般的对象的类型都是在编译期就确定下来的，而 Java 反射机制可以动态地创建对象并调用其属性，这样的对象的类型在编译期是未知的。所以我们可以通过反射机制直接创建对象，即使这个对象的类型在编译期是未知的。

反射的核心是 JVM 在运行时才动态加载类或调用方法/访问属性，它不需要事先（写代码的时候或编译期）知道运行对象是谁。

Class 对象就是类加载时生成的对象，作为获取类加载信息（静态变量、常量、类的字段、方法区）的入口。

Class 和 java.lang.reflect 一起对反射提供了支持，java.lang.reflect 类库主要包含了以下三个类：

- Field ：可以使用 get() 和 set() 方法读取和修改 Field 对象关联的字段；
- Method ：可以使用 invoke() 方法调用与 Method 对象关联的方法；
- Constructor ：可以用 Constructor 创建新的对象。

**反射的应用**

- 控制翻转的IOC容器 和 动态代理的AOP面向切面编程都有用到 
- 类浏览器和可视化开发环境 ：一个类浏览器需要可以枚举类的成员。可视化开发环境（如 IDE）可以从利用反
  射中可用的类型信息中受益，以帮助程序员编写正确的代码。

**反射的缺点**
尽管反射非常强大，但也不能滥用。如果一个功能可以不用反射完成，那么最好就不用。

- 性能开销 ：反射涉及了动态类型的解析，所以 JVM 无法对这些代码进行优化。因此，反射操作的效率要比那些非反射操作低得多。我们应该避免在经常被执行的代码或对性能要求很高的程序中使用反射。
- 安全限制 ：使用反射技术要求程序必须在一个没有安全限制的环境中运行。如果一个程序必须在有安全限制的环境中运行，如 Applet，那么这就是个问题了。
- 内部暴露 ：由于反射允许代码执行一些在正常情况下不被允许的操作（比如访问私有的属性和方法），所以使用反射可能会导致意料之外的副作用.

### 异常

![1552746819250](/d:/resource/notePic//1552746819250.png)

异常如果能被catch, 是不会影响程序正常执行的, 这也是catch的意义所在.

```java
try {
    int i = 1 / 0;
} catch (Exception e) {
    System.out.println("抓住了异常");
}
// 这里可以正常执行
System.out.println("抓住了异常,所以可以继续执行");
```

#### 触发OOM错误的原因可能有哪些? 怎么排查?

OOM是程序在运行期间申请的内存超过了JVM可提供的最大内存, 一般是堆上发生OOM. 常见的可能原因有以下:

- JVM启动参数设置的堆内存和永久代内存分配过小, 不满足程序运行期间内存的要求，启动参数设置：

    ```
    -Xmx200M  （堆内存设置）
    -XX:PermSize=50M （永久代内存设置）
    ```

- 流量 / 数据量峰值: 应用程序在设计之初均有用户量和数据量的限制，某一时刻，当用户数量或数据量突然达到一个峰值，并且这个峰值已经超过了设计之初预期的阈值，那么以前正常的功能将会停止，并触发`java.lang.OutOfMemoryError: Java heap space`异常。

    因此在编程时, 有预计的正常流量水平, 但是对于超高流量访问, 也需要有备用措施, 比如限流  / 快速失败等. 比如在创建线程时不能使用Executors创建线程池, 它在高流量请求访问时就有发生OOM的风险

- 内存泄漏: 一些编程错误会导致某些内存始终不能被回收, 久而久之, 泄漏的内存就消耗了所有的堆空间.

dump 文件生成方法：

- OOM 发生时：通过配置 jvm 启动参数：`-XX：+HeapDumpOnOutOfMemoryError`  `-XX:HeapDumpPath=d://`
- 实时：jmap 命令：`jmap -dump:format=b,file=serviceDump.dat 6214` （会导致服务暂停）

查看 dump 文件可以使用 jprofile，查看 object 数量和 size，找 biggest objects，去定位类和代码，找到 OOM 的地方，进一步看原因

### 泛型

泛型提供了编译期的类型安全，确保你只能把正确类型的对象放入集合中，避免了在运行时出现ClassCastException。但是泛型在运行时是会被擦除的

```java
public class Box<T> {
    // T stands for "Type" 
    private T t;
    public void set(T t) { this.t = t; }
    public T get() { return t; }
}
```

### 注解

Java 注解是附加在代码中的一些元信息，用于一些工具在编译、运行时进行解析和使用，起到说明、配置的功能(如@Override / @author)。注解不会也不能影响代码的实际逻辑，仅仅起到辅助性的作用。

### Lambda表达式

lambda表达式是Java8引入的新特性, 其主要形式有两种: `(arg ...) -> {body} ` 和 `目标引用::方法` , 前者是用于表示功能接口(只有一个方法)的匿名实现类, 后者是返回一个Function对象(有的方法参数是Function对象, 即将调用这个方法的每个对象都作为Function的参数计算一次)

Lambda 表达式的结构

- Lambda 表达式可以具有零个，一个或多个参数。空括号用于表示一组空的参数

- 参数用小括号括起来，逗号分隔，可以显式声明参数类型，也可以由编译器自动从上下文推断参数的类型。例如 (a, b) 或 (int a, int b) 

- 当有且仅有一个参数时，如果不显式指明类型，则不必使用小括号。例如 a -> return a*a。

- Lambda 表达式的正文可以包含零条，一条或多条语句。

- 如果 Lambda 表达式的正文只有一条语句，则大括号可不用写，return和分号也可以省略，但是return类型要和匿名类的重写方法返回类型一致

    ```java
    // 创建Person的大根堆（p2-p1是倒序）
    PriorityQueue<Person> queue = new PriorityQueue<>((p1, p2) -> p2.getAge() - p1.getAge());
    ```

- 如果 Lambda 表达式的正文有一条以上的语句必须包含在大括号（代码块）中，且每个语句需要有分号，return类型要和匿名类的重写方法返回类型一致

    ```java
    // 创建Person的小根堆（p1-p2是顺序）
    PriorityQueue<Person> queue = new PriorityQueue<>((p1, p2) -> {
        int a = p1.getAge();
        int b = p2.getAge();
        return a - b;
    });
    ```

比如, 遍历一个list, 用两种lambda表达式都非常简洁

```java
List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
list.forEach(n -> System.out.println(n)); // 方式1
list.forEach(System.out::println); // 方式2
```

Comparable和Comparator的区别：

1. 前者是重写`int compareTo(Object o)`方法，后者是重写`int compare(Object o1, Object o2)`方法，返回值都表示 `左对象 - 右对象 `的值，即可判断大小
2. 实现Comparable接口，一般表示此类是支持比较大小的，容器对象若要直接sort，需要实现该接口。即某个类对象经常需要比较大小，就实现此接口
3. Comparator接口是比较器，它一般用于作为sort方法的参数。即某个类对象偶尔需要比较大小，并且可能每次比较大小的规则不一样，则就不能通过实现Comparable接口，因为它的比较规则是一定的。而是每次在sort时，提供一个实现Comparator接口的比较器类，一般用lambda表达式



## Java容器

容器主要包括 Collection 和 Map 两种，Collection 存储着对象的集合，而 Map 存储着键值对的映射表。

注意区别数据结构和Java容器, 数据结构是一门学科, 研究了数据以不同结构存在时的特点和使用场景, Java容器是Java作为一门语言, 提供的一些内置数据结构实现.

### Collection

![1562162237366](/d:/resource/notePic/1562162237366.png)

1. Set

  - TreeSet：基于红黑树实现，支持有序性操作，例如根据一个范围查找元素的操作。但是查找效率不如
    HashSet，HashSet 查找的时间复杂度为 O(1)，TreeSet 则为 O(logN)。

  - HashSet：基于哈希表实现，支持快速查找，但不支持有序性操作。并且失去了元素的插入顺序信息，也就是说使用 Iterator 遍历 HashSet 得到的结果是不确定的。
  - LinkedHashSet：具有 HashSet 的查找效率，且内部使用双向链表维护元素的插入顺序。

2. List

  - ArrayList：基于动态数组实现，支持随机访问。删除代价大; 自动扩容时变为原大小的1.5倍
  - Vector：和 ArrayList 类似，但它是线程安全的。其有synchronized实现同步, 效率较低, 建议使用ArrayList, 不要使用Vector, 因为可以由程序员自己来同步
  - LinkedList：基于双向链表实现，只能顺序访问，但是可以快速地在链表中间插入和删除元素。不仅如此，LinkedList 还可以用作栈、队列和双向队列。

3. Queue

  - LinkedList：可以用它来实现双向队列。
  - PriorityQueue：基于堆结构实现，可以用它来实现优先队列。大根堆, 小根堆的实现类

### Map

![1562162397000](/d:/resource/notePic/1562162397000.png)

- TreeMap：基于红黑树实现。

- HashMap：基于哈希表实现。（哈希是一种散列算法，可以将任意数据，散列映射到某个固定长度的数组上的某个位置，具备相同输入相同输出，不同输入也可能碰撞的特点）

- HashTable：和 HashMap 类似，但它是线程安全的，这意味着同一时刻多个线程可以同时写入 HashTable 并且不会导致数据不一致。它是遗留类，不应该去使用它。现在可以使用 ConcurrentHashMap 来支持线程安全，并且 ConcurrentHashMap 的效率会更高，因为 ConcurrentHashMap 引入了分段锁。

- LinkedHashMap：使用双向链表来维护元素的顺序，顺序为插入顺序或者最近最少使用（LRU）顺序。

  LinkedHashMap内部维护了一个双向链表，用来维护插入顺序或者 LRU 顺序。其中有一个属性accessOrder作为LRU顺序的开关, 当它为true时, 每次get/put操作都会将这个数据移到链表的末尾, 所以靠近链表尾部的数据表明最近使用过. 去掉很少使用的数据, 就从链表首部开始去除

  ```java
  // 继承LinkedHashMap的LRU Cache使用
  // 使用 LinkedHashMap 的构造函数将 accessOrder 设置为 true，开启 LRU 顺序；
  // 重写removeEldestEntry()方法，在节点多于 MAX_ENTRIES 就会将最近最久未使用的数据移除。
  class LRUCache<K, V> extends LinkedHashMap<K, V> {
      private static final int MAX_ENTRIES = 3;
      protected boolean removeEldestEntry(Map.Entry eldest) {
          return size() > MAX_ENTRIES;
      }
      LRUCache() {
          super(MAX_ENTRIES, 0.75f, true);
      }
  }
  ```




### Hash 算法

Object 的 hashCode() 方法被native修饰，这个方法是在 jvm 中通过c/c++实现。（参考[java Object的hashCode方法的计算逻辑](https://blog.csdn.net/weixin_45244678/article/details/107256002)）

核心代码如下，默认使用变量的地址作为 hashCode

```c++
// hotspot\src\share\vm\runtime\synchronizer.cpp
static inline intptr_t get_next_hash(Thread * Self, oop obj) {
  intptr_t value = 0 ;
  // 一共支持6中生成hashCode策略, 默认策略值是5
  if (hashCode == 0) {
  // 策略1: 直接通过随机数生成
     value = os::random() ;
  } else if (hashCode == 1) {
     // 策略2: 通过object地址和随机数运算生成
     intptr_t addrBits = cast_from_oop<intptr_t>(obj) >> 3 ;
     value = addrBits ^ (addrBits >> 5) ^ GVars.stwRandom ;
  } else if (hashCode == 2) {
  // 策略3: 永远返回1, 用于测试
     value = 1 ;            // for sensitivity testing
  } else if (hashCode == 3) {
  // 策略4: 返回一个全局递增的序列数
     value = ++GVars.hcSequence ;
  } else if (hashCode == 4) {
  // 策略5: 直接采用object的地址值
     value = cast_from_oop<intptr_t>(obj) ;
  } else {
     // 策略6: 通过在每个线程中的四个变量: _hashStateX, _hashStateY, _hashStateZ, _hashStateW
     // 组合运算出hashCode值, 根据计算结果同步修改这个四个值
     unsigned t = Self->_hashStateX ;
     t ^= (t << 11) ;
     Self->_hashStateX = Self->_hashStateY ;
     Self->_hashStateY = Self->_hashStateZ ;
     Self->_hashStateZ = Self->_hashStateW ;
     unsigned v = Self->_hashStateW ;
     v = (v ^ (v >> 19)) ^ (t ^ (t >> 8)) ;
     Self->_hashStateW = v ;
     value = v ;
  }

  value &= markOopDesc::hash_mask; // 通过hashCode的mask获得最终的hashCode值
  if (value == 0) value = 0xBAD ;
  assert (value != markOopDesc::no_hash, "invariant") ;
  TEVENT (hashCode: GENERATE) ;
  return value;
}
```

一般我们重写 hashCode 方法时，会使用 `Objects.hash()` 方法，它的使用逻辑如下：

```java
public static int hashCode(Object a[]) {
    if (a == null)
        return 0;

    int result = 1;

    for (Object element : a)
        result = 31 * result + (element == null ? 0 : element.hashCode());

    return result;
}
```

简单来说，就是把传入的 a 数组当作一个 31 进制的数，这个数的每一位是元素的 hashCode 值（可能会超过 31）

对比 String 的 hashCode 方法实现，发现是类似的逻辑，只不过 String 的 hashCode 方法入参是构成字符串的字符列表：

```java
public int hashCode() {
    int h = hash;
    if (h == 0 && value.length > 0) {
        char val[] = value;

        for (int i = 0; i < value.length; i++) {
            h = 31 * h + val[i];
        }
        hash = h;
    }
    return h;
}
```

那么会好奇，为什么会使用 31 进制数这样的逻辑呢？

- 它不大不小，保证计算出来的 hashCode 在合适的范围

- 有研究者测试了超过 50000 常用单词用 31 算出来的 hashCode，冲突数不超过 7 个，所以也是一个实际应用的概率问题
- 31 刚好是 32 - 1，所以可以使用位运算加快运算速度：`31 * i = (i << 5) - i`



### 关于HashMap的问题

**HashMap的结构**

JDK1.8之前, 使用"拉链"结构, 即数组的每个元素都是一个链表头节点

JDK1.8后, 当数组中某个链表长度超过8, 即将其改成红黑树, 减少搜索时间

**HashMap是否支持null作为key?**

支持, null作为key, 会存在数组的第0个位置. 但是ConcurrentHashMap不支持null作为key

**ConcurrentHashMap为什么不支持null做键值对？**

因为它要考虑多线程的歧义：线程1使用contains方法发现key是存在的，然后调用get方法，但是在调用get方法之前，线程2删除了这个key，那么线程1调用完get方法得到null，它会以为是这个key对应的value就是null。而如果不允许null作为键值，线程1就知道在这期间key被删除了

**为什么HashMap的数组长度总是2的n次方?**

为了将hash值的取模变成位运算: `hash(key) % length = hash(key) & (length - 1)` 

**为什么HashMap桶中元素数量大于8时会变成红黑树**

因为线性链表的平均搜索时间为N/2，红黑树的平均搜索时间为log2(N)，而8是满足N/2>log2(N)的最小值

**HashMap HashTable 和 ConcurrentHashMap的区别**

- HashMap允许一个key为null,允许多个value为null; HashTable不允许key和value为null

- 底层结构:HashTable是数组+链表; ConcurrentHashMap是数组+链表/红黑树

- ConcurrentHashMap对容器的锁是分段锁(synchronized+CAS,只对链表或红黑树的头节点进行上锁),多线程如果操作的是不同分段的区域,是可以并发操作的.而HashTable是采用synchronized对整个容器进行锁,降低了多线程效率 

**HashMap怎么求key的hash值?**

使用了key的hashCode()方法, 并且对于得到的hash值, 其高16位不变, 低16位与高16位做异或运算, 这样增加了hash值的随机性, 减少了hash碰撞的可能 (Java7使用了四次扰乱, Java8只使用了一次扰乱, 但也足以提高随机性)

```java
// Form Java8
static final int hash(Object key) {
    int h;
    // 不带符号右移16位
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

为什么要在这里对 key 的 hashCode 进行扰乱？

为了减少哈希碰撞的概率。可以这么设想：如果一次大量往 map 中 put 元素，这些元素的 hashCode 彼此之间本来就是乱序无规律的，那么不做扰乱也无妨；但是如果是有规律的呢？（比如 Object 的 hashCode 方法默认是取地址，对于一个数组的元素，可能是连续地址），那此时哈希碰撞的概率也就是有规律的，显然这不符合哈希无序散列的思想，所以这里加一个扰乱函数是为了强保证乱序。

参考：[JDK 源码中 HashMap 的 hash 方法原理是什么？](https://www.zhihu.com/question/20733617/answer/111577937)

**HashMap为什么线程不安全？**

[参考](https://www.jianshu.com/p/e2f75c8cce01)

1. **put操作可能存在覆盖**：比如线程1在put (keyA, valueA) 时，计算的桶位置是2，并且发现2位置没有元素，所以准备直接设置2位置的元素为(keyA, valueA)，但是在set之前，线程1时间片用完了，线程2开始执行，线程2在put (keyB, valueB)时，计算的桶位置也是2，线程2完成了(keyB, valueB)的set操作；然后线程A又开始执行，它不知道此时2位置已经有元素了，直接set，导致线程2 put的元素丢失。

2. **resize可能存在死循环**：如下图，线程1在resize过程中，执行到e为key3，next为key7时，时间片用完，线程2完成了resize，此时key7.next = key3，线程1不知道继续将key3重新分配到桶3的头位置，所以有key3.next=key7，如此形成了一个环形链表，后面会无限循环。这个问题是在1.7中存在，因为1.7插入数据是在头部插入(为了保证热度数据快速被取到)，1.8中改用插入数据在尾部插入，解决了这个问题。

   ![1567476663612](/d:/resource/notePic/1567476663612.png)



## Java并发

### 线程状态

![1567733954330](/d:/resource/notePic/1567733954330.png)

线程有5种状态: 新建，就绪，运行，阻塞，死亡

### 创建线程

创建线程有三种方法:

- 实现 Runnable 接口: 作为new Thread(runnable)的参数, 重写`void run()`方法
- 实现 Callable 接口: 作为new Thread(runnable)的参数, 支持异步返回值, 重写`Object call()`方法
- 继承 Thread 类: 重写run方法

### 守护线程

> 在 Java 语言中线程分为用户线程和守护线程，守护线程是用来为用户线程服务的，当一个程序中的所有用户线程都结束之后，无论守护线程是否在工作都会跟随用户线程一起结束。守护线程从业务逻辑层面来看权重比较低，但对于线程调度器来说无论是守护线程还是用户线程，在优先级相同的情况下被执行的概率都是相同的。守护线程的经典使用场景是垃圾回收线程，守护线程中创建的线程默认情况下也都是守护线程。

### 线程协作

**Daemon**: 守护线程是程序运行时在后台提供服务的线程，不属于程序中不可或缺的部分。

当所有非守护线程结束时，程序也就终止，同时会杀死所有守护线程。使用 setDaemon() 方法将一个线程设置为守护线程。

**yield()**: 对静态方法 Thread.yield() 的调用声明了当前线程已经完成了生命周期中最重要的部分，可以切换给其它线程来执行。该方法只是对线程调度器的一个建议，而且也只是建议具有相同优先级的其它线程可以运行。

**join():** 在线程中调用另一个线程的 join() 方法，会将当前线程挂起，直到目标线程结束。

**wait():** 是Object方法, 不是Thread的方法. 只能用在同步方法或者同步控制块中使用，否则会在运行时抛出 IllegalMonitorStateException。使用 wait() 挂起期间，线程会释放锁。这是因为，如果没有释放锁，那么其它线程就无法进入对象的同步方法或者同步控制块中，那么就无法执行 notify() 或者 notifyAll() 来唤醒挂起的线程，造成死锁。

wait() 与 sleep() 的区别:

- wait是Objec方法（只能由持有锁的对象调用）， sleep是Thread静态方法
- wait会释放锁, sleep不会释放锁

ThreadLocal的实现原理？

 ThreadLocal 内部维护了一个 ThreadLocalMap 的静态内部类，ThreadLocal 可以理解为只是ThreadLocalMap的封装，传递了变量值。这个map的key就是 ThreadLocal 对象的弱引用

如果 ThreadLocal 没有被外部强引用的情况下，在垃圾回收的时候会被清理掉的，ThreadLocalMap中使用这个 ThreadLocal 的 key 也会被清理掉，value 是强引用，不会被清理，在调用 set()、get()、remove() 方法的时候，会清理掉 key 为 null 的记录。但是如果后续一直没有调用set()、get()、remove()方法，就会存在内存泄漏的风险

### 线程中断

**interrupt**

通过调用一个线程对象的 interrupt() 来中断该线程，如果该线程处于阻塞、限期等待或者无限期等待状态，那么就会抛出 InterruptedException，从而提前结束该线程。但是不能中断 I/O 阻塞和 synchronized 锁阻塞。

**interrupted**

如果一个线程的 run() 方法执行一个无限循环，并且没有执行 sleep() 等会抛出 InterruptedException 的操作，那么调用线程的 interrupt() 方法就无法使线程提前结束。

但是调用 interrupt() 方法会设置线程的中断标记，此时调用 isInterrupted() 方法会返回 true。因此可以在循环体中使用 isInterrupted() 方法来判断线程是否处于中断状态，从而提前结束线程。

**Executor的中断操作**

调用 Executor 的 shutdown() 方法会等待线程都执行完毕之后再关闭，但是如果调用的是 shutdownNow() 方法，则相当于调用每个线程的 interrupt() 方法。

如果只想中断 Executor 中的一个线程，可以通过使用 submit() 方法来提交一个线程，它会返回一个 Future<?> 对象，通过调用该对象的 cancel(true) 方法就可以中断线程。



### 线程池

#### 线程池的状态

5种状态: Running, SHUTDOWN, STOP, TIDYING, TERMINATED。

![1562656743674](/d:/resource/notePic/1562656743674.png)

- RUNNING：处于RUNNING状态的线程池能够接受新任务，以及对新添加的任务进行处理。
- SHUTDOWN：处于SHUTDOWN状态的线程池不可以接受新任务，但是可以对已添加的任务进行处理。
- STOP：处于STOP状态的线程池不接收新任务，不处理已添加的任务，并且会中断正在处理的任务。
- TIDYING：当所有的任务已终止，ctl记录的”任务数量”为0，线程池会变为TIDYING状态。当线程池变为TIDYING状态时，会执行钩子函数terminated()。terminated()在ThreadPoolExecutor类中是空的，若用户想在线程池变为TIDYING时，进行相应的处理；可以通过重载terminated()函数来实现。
- TERMINATED：线程池彻底终止的状态。

#### 线程池的种类

线程池广义上属于Executor, Executors常用的模板类主要有三种(参考, 不建议使用):

- **SingleThreadExecutor**：一个线程的线程池,如果有多任务, 会存储在任务队列中, 先进先出的执行. 为了控制线程的执行顺序, 避免多线程出现的情况

    ```java
    new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLSECONDS, new LinkedBlockingQueue<Runnable>())
    ```

- **FixedThreadPool**：固定线程数量的线程池, 适用于资源管理要求严格, 负载较重的服务器

    ```java
    new ThreadPoolExecutor(n, n, 0L, TimeUnit.MILLSECONDS, new LinkedBlockingQueue<Runnable>())
    ```

- **CachedThreadPool**：一个任务创建一个线程, 适用于很多短期异步的小程序, 负载较轻的服务器

    ```java
    new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>())
    ```

    SynchronousQueue是一个没有容量的阻塞队列, 每一次offer操作, 必须等待一次poll操作后, 两者一起完成. 线程执行poll操作后, 会有keepAliveTime的生存时间, 在这段时间内, 如果有任务执行offer操作, 则该任务会分配给该线程执行.

- **ScheduleThreadPoolExecutor**: 定时任务线程池, 由于其使用的DelayedQueue(内部类)是无界的, 所以maximumPoolSize没有意义, 构造方法只有corePoolSize / ThreadFactory / RejectedExecutionHandler三种参数

    它提交定时任务使用schedule方法, 该方法有三个参数:Runnable or Callable接口对象 / delay(int) / TimeUnits. 即指定任务和延期执行时间. 此时任务只会执行一次, 也可以使用scheduleAtFixedRate 或 scheduleWithFixedDelay方法, 它们可以周期重复执行任务. 两者区别: scheduleAtFixedRate 不考虑方法执行时间, 从第一次延迟时间过了开始, 每隔period时间执行一次任务(即使上一次任务还没有执行完); 而scheduleWithFixedDelay 需要考虑方法执行时间, 它是每次等当前周期任务执行完, 再过delay时间再次执行任务(不会同时存在执行两个相同周期任务的情况)

    其内部维护了一个DelayQueue, 本质是一个小根堆, 根据任务执行时间建堆, 当堆顶周期性任务的执行时间等于当前时间, 则弹出任务执行, 并根据任务信息计算这个任务的下一次执行时间, 生成一个新的任务节点插入到堆中, 如此循环.

#### 线程池的好处

- 减少资源消耗: 避免了线程的频繁创建和销毁
- 提高响应效率: 任务到达时,可以使用已经创建的线程池直接执行
- 方便线程管理: 线程池规定了线程的数量和创建销毁规则

#### 线程池的创建

阿里巴巴java开发手册不允许使用Executors创建线程池, 因为用Executors创建的模板线程池有OOM的风险.

比如FixedThreadPool的workQueue是LinkedBlockingQueue, 没有设置容量, 默认容量是Integer.MAX_VALUE, 相当于无界队列, 很可能在队列满之前就发生了OOM. CachedThreadPool的maximumPoolSize 也是Integer.MAX_VALUE, 允许无限创建线程同样可能导致OOM.

所以推荐使用[ThreadPoolExecutor](#ThreadPoolExecutor)创建自定义线程池, 规避OOM的风险. 如下, 为自定义一个类似于FixedThreadPool的线程池, 它的名字是""MyThreadPool"", 拥有10个线程, 并且可以容忍100个请求排队, 但是如果排队排满了, 新来的请求直接丢弃

```java
ExecutorService fixedThreadPool = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), new DefaultThreadFactory("MyThreadPool"), new ThreadPoolExecutor.DiscardPolicy());
```

#### ThreadPoolExecutor

[ThreadPoolExecutor](http://www.iocoder.cn/JUC/sike/ThreadPoolExecutor/) 它也是JUC中的一个常用类

作为Executor框架中最核心的类, ThreadPoolExecutor用于创建自定义参数的线程池, 相比于Executors创建的模板线程池, 可以规避OOM的风险

![1562657304468](/d:/resource/notePic/1562657304468.png)

ThreadPoolExecutor的构造方法至少要求有5个参数, 需要显示指定workQueue

![1562657396723](/d:/resource/notePic/1562657396723.png)

**corePoolSize**

线程池中核心线程的数量。当提交一个任务时，线程池会新建一个线程来执行任务，直到当前线程数等于corePoolSize。如果调用了线程池的prestartAllCoreThreads()方法，线程池会提前创建并启动所有基本线程。

**maximumPoolSize**

线程池中允许的最大线程数。线程池的阻塞队列满了之后，如果还有任务提交，如果当前的线程数小于maximumPoolSize，则会新建线程来执行任务。注意，如果使用的是无界队列(LinkedBlockingQueue不设置容量)，该参数也就没有什么效果了, 因为无界队列不会满

**corePoolSize 和 maximumPoolSize 大小的设置**：前者主要看CPU核的个数，若设置太大，需要频繁的进行上下文切换，工作效率并不高。后者需要看线程的工作类型，若线程涉及IO场景等耗时等待场景，则后者可以设置高一些，避免 corePoolSize 个线程占用CPU却在等待，浪费资源

**keepAliveTime**

线程空闲的时间。线程的创建和销毁是需要代价的。线程执行完任务后不会立即销毁，而是继续存活一段时间：keepAliveTime。默认情况下，该参数只有在线程数大于corePoolSize时才会生效。

Q：线程池如何销毁线程？

```
线程池内部会用一个 set 去维护它管理的线程，每个线程被一个 Worker 对象持有，Woker 会有创建时间，每当一个 Worker 执行完一个任务，去取下一个任务前，会判断当前维护的线程数是否大于 corePoolSize，并且当前 worker 是否已超过存活时间，若两者都满足则会用 CAS 操作将当前 worker 从自己的维护列表中去除
```

**unit**

keepAliveTime的单位。TimeUnit

**workQueue**

用来保存等待执行的任务的阻塞队列，等待的任务必须实现Runnable接口。我们可以选择如下几种：

- ArrayBlockingQueue：基于数组结构的有界阻塞队列，FIFO。[ArrayBlockingQueue](http://cmsblogs.com/?p=2381)
- LinkedBlockingQueue：基于链表结构的有界阻塞队列，FIFO。
- PriorityBlockingQueue：具有优先界别的阻塞队列。[PriorityBlockingQueue](http://cmsblogs.com/?p=2407)

为什么线程池用阻塞队列而不用普通队列？

因为阻塞队列除了起很好的缓冲作用外，其阻塞性能够为线程池带来一些其它特性：

- 阻塞队列就是为并发设计的，当多线程 offer / poll 时，阻塞队列能够提供保证线程安全
- 比如当任务队列空了之后，线程去取任务会自动阻塞，任务来了只会会自动唤醒线程
- 再比如阻塞队列允许在队列满时 poll 等待一段时间

**threadFactory**

用于设置创建线程的工厂。可以使用`new DefaultThreadFactory("ThradPoolName")`, 建议加上这个参数(因为它可以给线程池命名)

**handler**

RejectedExecutionHandler，线程池的拒绝策略。什么时候会执行拒绝策略呢?

```java
// 新任务来时,会执行以下逻辑
if (线程池中的线程数 < corePoolSize) {
    新建线程处理任务;
} else if (workQueue未满) {
    任务进入workQueue等待;
} else if (线程池中的线程数 < maximumPoolSize) {
    新建线程处理任务;
} else {
    执行拒绝策略;
}
```

当线程池中的线程数量大于corePoolSize时, 若某线程空闲时间超过keepAliveTime, 线程将被终止, 从而达到动态调整线程池中线程数量的目的.

线程池提供了四种拒绝策略：

1. AbortPolicy：直接抛出异常，默认策略；
2. CallerRunsPolicy：用调用者所在的线程来执行任务；
3. DiscardOldestPolicy：丢弃阻塞队列中靠最早入列的任务, 将当前任务入列
4. DiscardPolicy：直接丢弃任务

比如使用DiscardPolicy策略的参数:`new ThreadPoolExecutor.DiscardPolicy()` 当然我们也可以实现自己的拒绝策略，例如记录日志等等，实现RejectedExecutionHandler接口即可。

#### 线程池的提交

线程池提交方法有execute和submit两种, 方法参数可以是Runnable和Callable对象; execute提交没有返回值,无法知道任务执行是否成功; submit方法提交有返回值,返回类型为`Future<T> f`, 通过f.get(timeout)方法获取异步返回结果(get方法会阻塞当前线程, 可以用方法参数指定等待时间)

#### 线程池与生产者消费者模式

生产者与消费者模式是为了解决生产和消费速率不匹配的问题, 思想大概就是生产者生产的消息加入到阻塞队列中, 消费者从阻塞队列中获取消息消费, 如果没有消息就阻塞等待. 这样的模式解耦了生产者 与 消费者单线程模型, 能够提高效率.

而线程池其实也是一种生产者消费者模式, 不过它更高效, 因为任务来的时候直接调用线程池中的线程消费, 当线程数量不够时, 任务才会进入队列, 而普通生产者消费者模式的消息一定要经过队列.



### Fork/Join框架

Java7提供的一个用于并行执行任务的框架, 核心思想是将一个大任务切割成若干个小任务, 对小任务并行计算, 最后汇总小任务结果, 最终得到大任务结果.

#### 工作窃取算法

将大任务分成若干个小任务, 并且将小任务分组放在不同的队列中, 每个队列都有一个线程负责去执行, 当队列的任务执行完成后, 这个线程会去"窃取"其他队列的任务, 但是为了减少冲突, 队列绑定线程都是从任务队列头取任务消费, 而"窃取线程"则是从任务队列尾部取任务消费.

#### 实现

它的核心类有ForkJoinTask(常用实现类有RecursiveAction和RecursiveTask) 和 ForkJoinPool. 

**创建任务**: 常见的实现方式是创建 RecursiveAction(无返回结果) 或 RecursiveTask(有返回结果)的子类, 重写它的compute方法, 在compute方法中使用递归实现任务的切割, 以及子任务结果的合并逻辑.

重写compute方法, 如何实现任务的切割?

在compute方法中, 可以创建任务的对象(子任务) 并且调用任务对象的fork方法, 表示将该子任务加到任务队列中, 调用join方法, 则会同步等待该子任务的执行结果. 以此来完成任务的分割及结果合并.

如下, 比如打算将1 + 2 + 3 + ... + n这个大任务分成若干个子任务来执行, 每个子任务最多处理两个数的加法.

```java
public class CountTask extends RecursiveTask<Integer> {
    private static final int THRESHOLD = 2;
    private int start;
    private int end;
    public CountTask(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected Integer compute() {
        int sum = 0;
        boolean canCompute = (end - start) <= THRESHOLD;
        if (canCompute) {
            // 任务比较小,不用分
            while (start <= end) {
                sum += start;
                start++;
            }
        } else {
            // 需要分割任务
            int mid = (start + end) / 2;
            CountTask leftTask = new CountTask(start, mid);
            CountTask rightTask = new CountTask(mid + 1, end);
            // 子任务丢进任务队列
            leftTask.fork();
            rightTask.fork();
            // 同步等待子任务执行结果
            int leftRes = leftTask.join();
            int rightRes = rightTask.join();
            // 合并子任务结果
            sum = leftRes + rightRes;
        }
        return sum;
    }
}
```

**提交任务**: 使用ForkJoinPool对象的submit方法提交任务对象. 获取结果也是使用Future对象的get阻塞方法.

```java
public static void main(String[] args) {
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    // 创建两个大任务
    CountTask bigTask1 = new CountTask(1, 100);
    CountTask bigTask2 = new CountTask(1, 200);
    // 在ForkJoinPool中提交任务1(会为每个bigTask创建一个子任务队列)
    Future<Integer> res1 = forkJoinPool.submit(bigTask1);
    // 提交任务2
    Future<Integer> res2 = forkJoinPool.submit(bigTask2);
    try {
        // 同步等待打印结果1
        System.out.println(res1.get());
        // 同步等待打印结果2
        System.out.println(res2.get());
    } catch (InterruptedException e) {        
    } catch (ExecutionException e) {        
    }
}
```

#### 异常处理

因为在Fork/Join框架内发生的异常, 主线程是捕捉不到的. 所以ForkJoinTask提供了检查任务是否以及**抛出异常或取消**的方法.

`task.getException()`返回的是一个Throwable对象, 如果任务被取消, 抛出CancellationException; 如果抛出异常直接返回该异常; 如果任务没有完成或者没有抛异常, 返回null

```java
if (task.isCompletedAbnormally()) {
    // 如果任务异常关闭
    System.out.println(task.getException());
}
```

### volatile

volatile关键字主要用来修饰变量, 保证共享变量在多线程之间的可见性. 是线程同步的轻量级实现

**volatile底层是如何实现的?**

volatile修饰的变量在修改时, 它的处理器缓存值会立刻回写到内存中, 而任一处理器缓存的回写都会导致其他处理器该缓存失效, 所以其他处理器再次取值时会从主存中取.

volatile禁止指令重排, 是因为编译器在生成指令序列时, 会在volatile修饰的变量前插入内存屏障指令, 来禁止处理器重排序

volatile使用恰当的话, 它的执行成本相对synchronized会更低, 因为没有线程上下文切换和调度.

与synchronized的使用区别: volatile主要修饰变量, synchronized还可以用来修饰方法和代码块

### Synchronized

为了解决多线程访问同一个资源的同步性, 被它修饰的方法或者代码块在任意时刻只会被一个线程执行.

synchronized修饰实例方法: 锁是当前对象

synchronized修饰静态方法: 锁是当前类的Class对象(不影响实例对象的使用)

synchronized修饰代码块: 锁是Synchronized括号里配置的对象

**底层实现原理**

基于JVM实现, 编译后, 同步方法前会加入一个 monitorenter 指令，退出方法和异常处会插入 monitorexit 指令。通过进入、退出锁对象监视器( Monitor )来实现同步

其本质就是对一个对象监视器( Monitor )进行获取，而这个获取过程具有排他性从而达到了同一时刻只能一个线程访问的目的。



### CAS

CAS包含三个基本变量, V(内存地址) A(旧值) B(新值), 只有当V所在的实际值为A时, 才更新V为新值B. 因此CAS只能保证变量的原子性操作, 不能保证代码块的原子性.

CAS(Compare And Swap)是项乐观锁技术，当多个线程尝试使用CAS同时更新同一个变量时，只有其中一个线程能更新变量的值，而其它线程都失败，失败的线程并不会被挂起，而是被告知这次竞争中失败，并可以再次尝试。

java.util.concurrent 包很多功能都是建立在 CAS 之上，如 ReenterLock 内部的 AQS，各种原子类，其底层都用 CAS来实现原子操作。使用CAS实现懒汉单例的方式如下：

```java
public class Singleton {
    private static final AtomicReference<Singleton> INSTANCE = new AtomicReference<Singleton>();

    private Singleton() {}

    public static Singleton getInstance() {
        while (true) {
            Singleton singleton = INSTANCE.get();
            if (null != singleton) {
                return singleton;
            }

            singleton = new Singleton();
            if (INSTANCE.compareAndSet(null, singleton)) {
                return singleton;
            }
        }
    }
}
```

**CAS的优点**

用CAS的好处在于不需要使用传统的锁机制来保证线程安全, CAS是一种基于忙等待的算法,依赖底层硬件的实现, 相对于锁它没有线程切换和阻塞的额外消耗, 可以支持较大的并行度, 适用于同步方法执行时间非常短的情况, 此时CAS获取锁的概率大大提升

**CAS的缺点**

- 只能保证对一个共享变量的原子操作
- **ABA问题:** 如果内存地址V发生了两次变化, 先变成了B, 后又变成了A, 那么CAS是察觉不到变化的. 而实际却变化了两次状态, 解决这个ABA问题可以使用版本号机制, 只要值发生了变化, 版本号就+1.
- 不适用于耗时长的操作, 会对CPU造成较大的执行开销。

ABA问题的一个实例: 一次性杯子装满水(A), 你喝完(B)后又把它倒满(A), 那么后来的那个人看到水还是满的, 以为这个一次性杯子没有喝, 他就拿去喝掉了(B)



### 锁

#### **锁的优化方式**

**自旋:** 线程的阻塞和唤醒需要CPU在用户态和内核态之间转换, 这种转换是比较耗时的. 对于锁竞争不激烈的情况, 可以让线程通过自旋等待(一般是循环CAS), 避免阻塞进入内核态

为什么线程的阻塞与唤醒需要 CPU 在用户态和内核态之前切换：

> 重量级锁需要通过操作系统自身的互斥量（mutex lock，也称为**互斥锁**）来实现，然而这种实现方式需要通过用户态与和核心态的切换来实现，但这个切换的过程会带来很大的性能开销。
>
> 申请锁时，从用户态进入内核态，申请到后从内核态返回用户态（两次切换）；没有申请到时阻塞睡眠在内核态。使用完资源后释放锁，从用户态进入内核态，唤醒阻塞等待锁的进程，返回用户态（又两次切换）；被唤醒进程在内核态申请到锁，返回用户态（可能其他申请锁的进程又要阻塞）。所以，使用一次锁，包括申请，持有到释放，当前进程要进行四次用户态与内核态的切换。同时，其他竞争锁的进程在这个过程中也要进行一次切换。
>
> 简单来说：**因为线程的调度是在内核态运行的，而线程中的代码是在用户态运行。**

**适应自旋:** 如果锁竞争比较激烈, 可能自旋很久还是不能获取锁, 相当于CPU一直在"空转", 是对资源的一种浪费. 适应性自旋会根据对象自旋成功获取锁的概率, 自动调整自旋次数. 

线程如果自旋成功了，那么下次自旋的次数会更加多，因为虚拟机认为既然上次成功了，那么此次自旋也很有可能会再次成功。反之，如果对于某个锁，很少有自旋能够成功的，那么以后这个锁的自旋次数会减少甚至省略掉自旋过程，以免浪费处理器资源。

**锁消除:** 有些情况下，JVM检测到加锁的对象并不存在竞争，这时JVM会对这些同步锁进行锁消除。

有时我们并没有显示使用锁, 但是在使用一些JDK的内置API时，如StringBuffer、Vector、HashTable等，会存在隐形的加锁操作。这时锁消除优化就可以提升性能了

**锁粗化:** 在使用同步锁的时候, 我们一般会让同步块的作用范围尽可能小, 此时如果存在锁竞争，等待锁的线程也能尽快拿到锁. 但是如果一系列的连续加锁解锁操作，可能会导致不必要的性能损耗. 如下, vector每次add的时候都需要加锁解锁操作，JVM检测到对同一个对象（vector）连续加锁、解锁操作，会合并一个更大范围的加锁、解锁操作，即加锁解锁操作会移到for循环之外。

```java
public void vectorTest(){
    // 如果JVM检测到vector变量并没有竞争,会进行锁消除
    Vector<String> vector = new Vector<String>();
    for(int i = 0 ; i < 10 ; i++){
        // 如果JVM检测到对vector对象连续加锁解锁,会进行锁粗化
        vector.add(i + "");
    }
    System.out.println(vector);
}
```

以上基本都是针对synchronized的锁优化, 因为synchronized在JDK1.6之前, 还是比较重的, 经过了以上锁优化, 其性能有较大提升, 除了以上锁优化, 还增加了偏向锁 / 轻量级锁的状态, 尽量避免进入阻塞的内核态, 减少了同步操作的时间消耗.

#### **对象锁状态**

[java偏向锁，轻量级锁与重量级锁为什么会相互膨胀?](https://www.zhihu.com/question/53826114/answer/236363126)

锁存在四种状态: 无锁状态、偏向锁状态、轻量级锁状态、重量级锁状态，偏向所锁，轻量级锁都是乐观锁，重量级锁是悲观锁, 他们会随着竞争的激烈而逐渐升级。注意锁可以升级不可降级，这种策略是为了提高获得锁和释放锁的效率。

![1562575292379](/d:/resource/notePic/1562575292379.png)

对象头信息里的Mark Word存储了对象的锁状态, 如上图.

#### 偏向锁

一个对象刚开始实例化的时候，没有任何线程来访问它的时候。它是可偏向的，意味着，它现在认为只可能有一个线程来访问它，所以当第一个线程来访问它的时候，它会偏向这个线程，此时，对象持有偏向锁。偏向第一个线程，这个线程在修改对象头成为偏向锁的时候使用CAS操作，并将对象头中的ThreadID改成自己的ID，之后再次访问这个对象时，只需要对比ID，不需要再使用CAS进行操作。

一旦有第二个线程访问这个对象，因为偏向锁不会主动释放，所以第二个线程可以看到对象的偏向状态，这时表明在这个对象上已经存在竞争了，这时会检查原来持有该对象锁的线程, 如果该线程已经死亡, 或者该线程虽然仍存活, 但是一段时间内并不需要再持有该对象的偏向锁, 则可以将对象变为无锁状态，然后重新偏向新的线程; 如果原来线程仍需要持有该对象的偏向锁, 则偏向锁升级为轻量级锁（**偏向锁就是这个时候升级为轻量级锁的**）

因此偏向锁是适用于无竞争的情况(两个线程访问同步块间隔时间很久, 即认为无竞争)

#### 轻量级锁 

对象轻量级锁的Mark Word信息有一个"标准状态"(标志位:00,锁记录指针:null), 当线程1准备获取轻量级锁时, 它会将这个标准状态存储起来(即为线程中的锁记录), 然后执行CAS操作:如果Mark Word的锁记录指针是null, 表明此时没有线程持有该对象锁, 则将锁记录指针更改指向线程1存储的锁记录, 并开始执行同步代码块

假设此时线程2也开始获取对象的轻量级锁, 它进行CAS操作时, 会发现Mark Word的锁记录指针并不为null(执行线程1的锁记录), 因此线程2会继续进行CAS操作(自旋). 线程1在执行完同步代码块后, 开始释放轻量级锁:进行CAS操作查看对象的Mark Word是否还是轻量级锁状态, 若是则释放锁(将Mark Word的锁记录指向null). 此时线程2通过CAS操作成功获取锁(将Mark Word的锁记录指针指向线程2存储的锁记录), 也开始执行同步代码块. 

但是若线程2自旋次数超过规定还没有获取锁, 则线程2会将对象的偏向锁升级为重量级锁并开始阻塞, 此时线程1在执行完同步方法, 准备释放轻量级锁:进行CAS操作, 发现Mark Word已经升级成了重量级锁, 则线程1会释放锁的同时唤醒阻塞进程.

因此, 轻量级锁允许多个线程同时竞争, 但是竞争力度相对小. 如果竞争力度大, 总会有某个线程CAS操作次数超限, 然后升级锁为重量级锁, 不然大量等待的线程一直CAS导致CPU"空转", 还不如让它们阻塞.

![Java_synchronized原理](d:/resource/notePic/Java_synchronized原理.jpg)

综上, 重量级锁是最简单粗暴的, 但是会带来较大的时耗. 对于没有竞争 或 竞争很小的同步情况, 使用偏向锁和轻量级锁, 避免进程阻塞进入内核态, 提升了性能. 锁的升级机制, 就是对象锁对竞争力度的自适应机制.

偏向锁和轻量级锁核心是使用CAS, 对对象头的Mark Word进行判断和设置. 偏向锁由于没有竞争, 只用在第一次获取偏向锁时进行CAS, 后续直接比较ThreadID; 轻量级锁由于存在轻量的竞争, 没有获取锁的线程会一直CAS(直到获取锁或超时升级锁), 并且在释放轻量级锁时, 也需要进行CAS操作, 决定是否需要唤醒线程.

#### 乐观锁与悲观锁

**乐观锁:** 乐观态度.每次拿数据之前,都认为没冲突,所以不会上锁.而是在准备更新数据的时候,判断期间数据有没有修改,没有修改则更新,有修改则更新失败需要重试.判断策略有版本号机制和CAS算法. **适用于多读场景, 冲突较少**

**悲观锁:** 悲观态度.每次拿数据之前,都会认为有冲突,所以每次拿数据的时候会上锁.传统关系型数据库中的行锁,表锁就是悲观锁.Java中的体现是synchronized和reentrantlock. **适用于多写场景, 冲突较多**

**版本号机制**

数据拥有一个版本号属性A,每次修改数据前会获取版本号A,将其+1,修改完成准备更新时,验证A+1是否大于当前版本号,如果是则修改成功.如果不是,说明期间已经有过修改,则失败.

#### 死锁

**死锁发生的两种情况**

- 两个任务以不合理的顺序互相争夺资源, 导致最后都在等待对方释放锁
  - 完成 A 任务需要 1 和 2 两个资源，先获取资源1 然后等待资源 2；B 任务则是先获取资源 2 等待资源 1
- 一个任务因为死循环或其他故障一直不能释放锁, 导致等待锁的其他任务形成死锁

**死锁检测**

可以用JDK自带的工具: Jstack 或 JConsole. 

Jstack: 首先使用`jps`命令查看当前JVM的进程号, 然后使用 `jstack -F 进程编号`查看当前JVM所有栈的快照, 就可以看到所有栈目前阻塞在哪里, 并且Jstack也会显示死锁检测信息

JConsole: 是JDK自带的监控工具，用于连接正在运行的本地或者远程的JVM，对运行在Java应用程序的资源消耗和性能进行监控，并画出大量的图表，提供强大的可视化界面。而且本身占用的服务器内存很小，甚至可以说几乎不消耗。可以直接检测死锁

**避免死锁的四个方向**

- 每个线程同一时刻, 保持只持有一把锁, 获取其他锁前需要释放当前的锁. (实际情况可能不允许)
- 不同线程都是以相同确定的顺序获取锁
- 尽量保证锁一定能释放
- 获取锁的线程超时放弃机制, synchronized关键词提供的内置锁时，只要线程没有获得锁，那么就会永远等待下去，Lock接口提供了固定时长等待锁的方法tryLock

**银行家算法**：系统首先尝试将剩下的资源分配可以run的进程，即这些进程目前需要的所有资源，系统都能提供，然后就可以认为这些进程可以执行完毕，并回收可以run的进程的资源；则经过一次尝试后系统可分配的资源就会变多，然后重复尝试过程。如果最后所有的进程都能被尝试通过，则系统是安全的，否则系统是不安全的。[参考](#https://www.bilibili.com/video/av47308740/)

#### 可重入性

锁的一个重要特性是可重入性，它的作用是：同步方法内调用兄弟同步方法，如果锁不支持可重入，那么这种情况就会出现自己锁自己的死锁情况。

Q：synchronized 如何实现锁的可重入性？

```
监视器对象维护了持有它的线程，以及一个计数器，当同线程获取锁时，计数器 +1，当同步代码执行完毕时计数器 -1，如果计数器变为 0，这个监视器对象会被释放，即锁释放
```

Q：ReentrantLock 如何实现锁的可重入性？

```
与 JVM 实现原理一致，其内部维护了持有锁的线程，和一个计数器 state。
当有线程获取锁时，ReentrantLock 逻辑如下：
- 先查看 state 是否为 0，如果是，则通过 CAS 将其更新为 1，并将持有锁线程 thread 指向当前线程
- 如果 state 不等于 1，就会比较 thread 是否是当前线程，如果是则 state +1，反之无法获取锁
当有线程释放锁时，逻辑如下：
- 先查看 state 是否为 0，不为 0 时才能释放；
- 然后对比 thread 是否是当前线程，如果不是为抛异常；如果是则 state -1，如果释放后的 state 为 0 则会将 thread 指向 null，即当前锁完全释放
```





### J.U.C

包含一些原子操作类和并发容器, 让并发编程变得简单.  实现原理有volatile 和 CAS等

#### AQS原理

在Java并发包java.util.concurrent中可以看到，不少源码是基于AbstractQueuedSynchronizer（以下简写AQS）这个抽象类，因为它是Java并发包的基础工具类，是实现ReentrantLock、CountDownLatch、Semaphore、FutureTask 等类的基础。 

AQS的主要使用方式是继承，子类通过继承AQS并实现它的抽象方法来管理同步状态，在抽象方法中免不了要对同步状态进行更改，这时就需要使用AQS提供的3个方法（getState()、setState(int newState)和compareAndSetState(int expect,int update) 来进行操作，因为他们能够保证状态的改变是安全的 。

在 AQS 内部，通过维护一个FIFO队列来管理多线程的排队工作。在公平竞争的情况下，无法获取锁的线程将会被封装成一个节点，置于队列尾部。入队的线程将会通过自旋的方式获取锁，若在有限次的尝试后，仍未获取成功，线程则会被阻塞住。大致示意图如下：

![æ­¤å¤è¾å¥å¾ççæè¿°](/d:/resource/notePic/633531-20190115234643185-659400767.jpg)

当头结点释放锁后，等待队列的第一个节点就会获取同步状态成功, 若后继节点对应的线程被阻塞，此时头结点线程将会去唤醒后继节点线程。后继节点线程恢复运行并获取同步状态后，会将旧的头结点从队列中移除，并将自己设为头结点。大致示意图如下：

![æ­¤å¤è¾å¥å¾ççæè¿°](/d:/resource/notePic/633531-20190115234653298-673561326.jpg)

Q：线程的阻塞和唤醒是怎么实现的？

```
通过 JDK 提供的 LockSupport 的 park/unpark 方法实现的，它们又是由本地方法实现的。
```

**AQS的部分属性和方法如下**

```java
// 头结点，你直接把它当做 当前持有锁的线程 可能是最好理解的
private transient volatile Node head;
// 阻塞的尾节点，每个新的节点进来，都插入到最后，也就形成了一个隐视的链表
private transient volatile Node tail;
// 这个是最重要的，不过也是最简单的，代表当前锁的状态，0代表没有被占用，大于0代表有线程持有当前锁
// 之所以说大于0，而不是等于1，是因为锁可以重入，每次重入都加上1
private volatile int state;
// 当前持有锁的线程
private transient Thread exclusiveOwnerThread; 
```

在并发的情况下，AQS 会将未获取同步状态的线程将会封装成节点，并将其放入同步队列尾部。同步队列中的节点除了要保存线程，还要保存等待状态。不管是独占式还是共享式，在获取状态失败时都会用到节点类。Node 的数据结构其实也挺简单的，就是 thread + waitStatus + pre + next 四个属性而已。

#### 阻塞队列的实现原理

[ArrayBlockingQueue实现原理](https://juejin.im/post/5c3def86e51d450672352cea#heading-1)

阻塞队列实现使用了wait/notify机制, 不过它是使用ReentrantLock + Condition, 而不是synchronized. 

它有一个ReentrantLock对象lock, 和两个Condition对象: 一个是notFull, 表示这个队列目前不是满的, 一个是notEmpty, 表示这个队列目前不是空的. 

当进行offer操作时, 首先会获取lock锁, 如果队列已满, 就会执行notFull.await(), 表示其目前阻塞, 等待队列不是满的条件通知, 并且会释放锁

当进行poll操作时, 首先也要获取lock锁, 如果队列不是空的, 就弹出一个元素, 并执行notFull.signal(), 表示唤醒在notFull条件下等待的阻塞线程, 同样会释放锁. 如果队列是空的, 就会执行notEmpty.await()方法, 阻塞等待

Condition对象内部都维护了一个等待队列, 当Condition对象执行await()方法时, 其实就是将执行线程加入等待队列; 当Condition对象执行signal()方法时, 其实就是从等待队列中poll一个线程, poll的方式要看Condition指定的是公平锁还是非公平锁, 公平锁就会按FIFO出列, 非公平锁则随机出列. 另外, Condition对象必须绑定ReentranLock对象, 并且在执行await()或signal()方法前, 都要获取其绑定的ReentranLock对象的锁.

(Condition是条件对象, 但是并不是说在创建这个对象时, 需要指定条件. 它的"条件"特点是体现在使用者使用它时候, 比如在offer操作时, 使用notEmpty.signal(), 就体现了notEmpty这个Condition对象的条件: 它是在队列非空时唤醒对象)

**阻塞队列的构造方法**

```java
public ArrayBlockingQueue(int capacity, boolean fair) {
    this.capacity = capacity;
    items = new Object[this.capacity];
    lock = new ReentrantLock(fair);
    notFull = lock.newCondition();
    notEmpty = lock.newCondition();
}
```

**入列方法**

```java
public void offer(E e) throws InterruptedException {
    final ReentrantLock lock = this.lock;
    // 获取锁
    lock.lockInterruptibly();
    try {
        // while是保证竞争失败继续等待
        while (this.size == capacity) {
            // 队列已满,需要阻塞等待
            notFull.await();
        }
        items[offerIndex++] = e;
        size++;
        // 循环数组实现队列
        offerIndex = offerIndex == capacity ? 0 : offerIndex;
        // 因为插入了数据,所以非空条件得到满足
        notEmpty.signal();
    } finally {
        lock.unlock();
    }
}
```

**出列方法**

```java
public E poll() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        while (this.size == 0) {
            // 队列已空,需要阻塞等待
            notEmpty.await();
        }
        E e = (E) items[pollIndex++];
        size--;
        // 循环数组实现队列
        pollIndex = pollIndex == capacity ? 0 : pollIndex;
        // 因为弹出了数据,所以非满条件得到满足
        notFull.signal();
        return e;
    } finally {
        lock.unlock();
    }
}
```

#### FutureTask

FutureTask除了实现Future接口, 还实现了Runnable接口, 因此它也可以交给Executor执行. 一般是作为Executor执行任务的返回结果对象.

它有三种状态: 未启动, 已启动, 已完成. 当未执行FutureTask.run()方法时, 它是未启动状态, 当执行了该方法后, 它变成了已启动状态, 若该方法正常执行完毕或执行时抛异常则变成已完成状态. 若在已启动状态时, 执行FutureTask.cancel(true), 则会以中断的方式试图终止任务, 也会变成已终止状态.

**FutureTask.get()方法**: 该方法在未启动或已启动状态时, 会阻塞调用方法的线程, 当变成已完成状态, 则返回任务执行结果或抛出异常.  

**实现原理**

FutureTask也是基于AQS实现的, 它有一个内部类Sync, FutureTask的所有public方法都是调用Sync的方法来完成, 而Sync方法借助于AQS方法来完成.

当执行get方法时, 若state为RAN或CANCELLED, 则立即返回结果或抛出异常. 否则执行get方法的线程会进入阻塞队列等待, 若多个线程执行get方法都阻塞, 它们会在同一个阻塞队列中等待. 当任务达到已完成的状态, 执行线程会唤醒等待阻塞队列中的第一个线程, 唤醒的线程会再唤醒队列的第一个线程, 并返回结果以此类推, 将阻塞队列中等待线程都成功唤醒.

#### ReenTrantLock

与Synchronized的区别:

- 可重入性: 两者都支持同一个线程多次加锁,每次进入锁的计数器都自增1,当其为0时释放锁
- 实现: Synchronized依赖于JVM实现,对用户透明.ReenTrantLock是JDK实现的,其实现原理是利用CAS算法,尽量避免线程进入内核的阻塞态
- 功能区别: ReenTrantLock需要手动声明加锁和释放锁, 粒度更细, 但是不如Synchronizd便利
- 性能区别: Synchronized优化后区别不大, 官方建议使用

ReenTrantLock特性:

- 可以指定公平锁or非公平锁, Synchronizd只能是非公平锁
- ReenTrantLock提供了一个Condition类,可以分组唤醒等待的线程.Synchronized只能随机唤醒或全部唤醒
- ReenTrantLock提供了一种中断等待锁的线程的机制：线程1获取锁不释放时，使用synchronized修饰的线程2会一直等待，不能中断，而如果使用ReentrantLock则可中断等待

什么时候使用ReenTrantLock? 需要它特性的时候, 适合高级开发人员

**使用经验**

[阻塞队列中ReentrantLock的使用](#阻塞队列的实现原理)

权值轮询时, 因为负载均衡采用的是单例对象, 所以它的index可能同时被多线程调用, 所以在设置index时, 会通过ReentrantLock加锁, 好处是冲突严重时, 超时未加锁则直接走最后一步:随机负载均衡, 相当于降级处理

这里做了多重保险: 加锁; 对index大于数组索引值进行检查; 最后对取到的registerMessage做了null判断

```java
public ProviderRegisterMessage select(List<ProviderRegisterMessage> providerServices) {
    ProviderRegisterMessage registerMessage = null;
    try {
        lock.tryLock(10, TimeUnit.MILLISECONDS);
        // 根据加权创建服务列表索引:加权为3,则它的索引在这个数组中出现三次
        List<Integer> indexList = LoadBalanceEngine.getIndexListByWeight(providerServices);
        // 若计数大于服务提供者个数,将计数器归0
        if (index >= indexList.size()) {
            index = 0;
        }
        registerMessage = providerServices.get(indexList.get(index));
        index++;
    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        lock.unlock();
    }
    // 兜底,保证程序健壮性,若未取到服务,则随机取一个
    return null == registerMessage ? LoadBalanceEngine.randomSelect(providerServices) : registerMessage;
}
```

#### semaphore

信号量 Semaphore 是一个控制访问多个共享资源的计数器，和 CountDownLatch 一样，其本质上是一个“**共享锁**”。Semaphore 通常用于限制可以访问某些资源（物理或逻辑的）的线程数目。本项目在NettyServerHandler用semaphore作为限流工具, NettyServerHandler中有一个静态变量`Map<String, Semaphore> serviceKeySemaphoreMap` , 其中key是rpc调用服务的实现类全限定名, value是一个固定计数量大小的Semaphore对象, 计数量大小由发布服务的标签配置. 每次调用实际服务方法时, 需要acquire一个计数量, 支持超时失败, 执行完方法一定会release. 由此实现了对每个服务的限流控制

**semaphore创建**

```java
// 根据方法名称定位到具体某一个服务提供者
String serviceKey = request.getServiceImplPath();
Semaphore semaphore = serviceKeySemaphoreMap.get(serviceKey);
// 为null时类似于单例对象的同步创建,两次检查null
if (semaphore == null) {
    synchronized (serviceKeySemaphoreMap) {
        semaphore = serviceKeySemaphoreMap.get(serviceKey);
        if (semaphore == null) {
            int workerThread = request.getWorkerThread();
            // 新建对象时,指定计数量
            semaphore = new Semaphore(workerThread);
            serviceKeySemaphoreMap.put(serviceKey, semaphore);
        }
    }
}
```

**semaphore的使用**

```java
ResponseMessage response = null;
boolean acquire = false;
try {
    // 利用semaphore实现限流,支持超时,返回boolean变量
    acquire = semaphore.tryAcquire(consumeTimeOut, TimeUnit.MILLISECONDS);
    if (acquire) {
        // 利用反射发起服务调用
        response = new ServiceProvider().execute(request);
    } else {
        logger.warn("因为服务端限流,请求超时");
    }
} catch (Exception e) {
    logger.error("服务方使用反射调用服务时发生错误", e);
} finally {
    // 一定记得release
    if (acquire) {
        semaphore.release();
    }
}
```



#### CountDownLatch

CountDownLatch是一个倒计时器, 在多线程执行任务时, 部分线程需要依赖另一部分线程的执行结果, 也就是说它们执行有先后顺序的, 此时就可以用CountDownLatch, 准备线程执行完, 倒计时器就-1, 减到0的时候, 被CountDownLatch对象await的线程就会开始执行. (就像火箭发射前需要很多准备工作一样)

**CountDownLatch原理(简版描述)**

CountDownLatch就是AQS的实现类, 它在对象初始化时需要指定count, 每次执行countDown, count就会-1, 并且是线程安全的, 当count=0时, 阻塞在await()方法的地方就会继续执行. await()方法是不断的判断count是否为0, 当然判断了一定次数之后, 会阻塞, 并将阻塞信息传给CountDownLatch对象, CountDownLatch对象在调用countDown方法会检查count是否为0, 若为0就唤醒阻塞线程.

本项目中, 在NettyChannelPoolFactory创建channel时, 需要用到CountDownLatch, 因为netty创建channel是异步的, 而channelpool的容量是一定的, 因此在while循环中, 每次创建channel都要等待创建结果, 如果没有创建成功, 需要继续创建.

**CountDownLatch的使用**

```java
ChannelFuture channelFuture = bootstrap.connect().sync();
final Channel newChannel = channelFuture.channel();
final CountDownLatch connectedLatch = new CountDownLatch(1);
final List<Boolean> isSuccessHolder = Lists.newArrayListWithCapacity(1);
// 监听Channel是否建立成功
channelFuture.addListener(new ChannelFutureListener() {
    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        // 若Channel建立成功,保存建立成功的标记
        if (future.isSuccess()) {
            isSuccessHolder.add(Boolean.TRUE);
        } else {
            // 若Channel建立失败,保存建立失败的标记
            future.cause().printStackTrace();
            isSuccessHolder.add(Boolean.FALSE);
        }
        // 表示监听线程完成,创建channel线程可以返回结果
        connectedLatch.countDown();
    }
});
// 等待监听线程完成
connectedLatch.await();
// 如果Channel建立成功,返回新建的Channel
return isSuccessHolder.get(0) ? newChannel : null;
```

**CountDownLatch 与 CyclicBarrier 的区别**

1. CountDownLatch 的作用是允许 1 或 N 个线程等待其他线程完成执行；而 CyclicBarrier 则是允许 N 个线程相互等待。
2. CountDownLatch 的计数器无法被重置；CyclicBarrier 的计数器可以被重置后使用，因此它被称为是循环的 barrier 。



#### ArrayBlockingQueue

[源码](#阻塞队列的实现原理)

阻塞队列的最常见使用场景是生产消费者模型, 即生产者不用关心消费者什么时候有时间消费, 消费者也不用关心生产者什么时候生产消息. 

ArrayBlockingQueue，一个由**数组**实现的**有界**阻塞队列。该队列采用 FIFO 的原则对元素进行排序添加的。

ArrayBlockingQueue 为**有界且固定**，其大小在构造时由构造函数来决定，确认之后就不能再改变了。

ArrayBlockingQueue支持对等待线程的可选性公平策略, 默认为非公平, 公平性会降低并发量

本项目中NettyChannelPoolFactory中每个主机地址channel的存储, 还有返回结果包装类都使用ArrayBlockingQueue, 其保证了多线程访问和支持超时失败. 如下为从结果容器中取结果

```java
ResponseReceiver responseReceiver = responseMap.get(traceId);
try {
    // 阻塞Queue在取值时会阻塞当前线程(等待),timeout时间后还未取到值,则返回null
    return responseReceiver.getResponseQueue().poll(timeout, TimeUnit.MILLISECONDS);
} catch (InterruptedException e) {
    logger.error("从结果容器中获取返回结果线程被中断!");
    throw new RuntimeException(e);
} finally {
    // 无论取没取到,本次请求已经处理过了,所以不需要再缓存它的结果
    responseMap.remove(traceId);
}
```

**入队**

- `#add(E e)` 方法：将指定的元素插入到此队列的尾部（如果立即可行且不会超过该队列的容量），在成功时返回 true ，如果此队列已满，则抛出 IllegalStateException 异常。
- `#offer(E e)` 方法：将指定的元素插入到此队列的尾部（如果立即可行且不会超过该队列的容量），在成功时返回 true ，如果此队列已满，则返回 false 。
- `#offer(E e, long timeout, TimeUnit unit)` 方法：将指定的元素插入此队列的尾部，如果该队列已满，则在到达指定的等待时间之前等待可用的空间。
- `#put(E e)` 方法：将指定的元素插入此队列的尾部，如果该队列已满，则等待可用的空间。

**出队**

- `#poll()` 方法：获取并移除此队列的头，如果此队列为空，则返回 `null` 。
- `#poll(long timeout, TimeUnit unit)` 方法：获取并移除此队列的头部，在指定的等待时间前等待可用的元素（如果有必要）。
- `#take()` 方法：获取并移除此队列的头部，在元素变得可用之前一直等待（如果有必要）。
- `#remove(Object o)` 方法：从此队列中移除指定元素的单个实例（如果存在）。



#### ConcurrentHashMap

ConcurrentHashMap**要求key、value都不允许为null**

ConcurrentHashMap和HashMap的存储结构都是数组+链表+红黑树, JDK1.7的时候, ConcurrentHashMap的实现是通过Segment+HashEntry数组方式实现, Segment在实现上继承了`ReentrantLock`，这样就自带了锁的功能, 且是更细化的锁, 相比锁住整个map, 分段锁提高了效率.

![1562637370144](/d:/resource/notePic/1562637370144.png)

1.8中放弃了`Segment`臃肿的设计，取而代之的是采用`Node` + `CAS` + `Synchronized`来保证并发安全进行实现，结构如下. 比如put时, 首先对key进行hash, 判断其存储的Node位置, 若为空, 则使用CAS创建Node, 否则则使用synchronized锁住Node, 进行put操作. 这种结构更优雅轻快

![1562637466480](/d:/resource/notePic/1562637466480.png)



### Java内存模型

#### JMM

JMM即为JAVA 内存模型（java memory model）。因为在不同的硬件生产商和不同的操作系统下，内存的访问逻辑有一定的差异，Java内存模型就是为了屏蔽系统和硬件的差异，让一套代码在不同平台下能到达相同的访问结果。

JMM规定了内存主要划分为主内存和工作内存两种。此处的主内存和工作内存跟JVM内存划分（堆、栈、方法区）是在不同的层次上进行的，如果非要对应起来，主内存对应的是Java堆中的对象实例部分，工作内存对应的是栈中的部分区域，从更底层的来说，主内存对应的是硬件的物理内存，工作内存对应的是寄存器和高速缓存。

![1567908588241](/d:/resource/notePic/1567908588241.png)



JVM在设计时候考虑到，如果JAVA线程每次读取和写入变量都直接操作主内存，对性能影响比较大，所以每条线程拥有各自的工作内存，工作内存中的变量是主内存中的一份拷贝，线程对变量的读取和写入，直接在工作内存中操作，而不能直接去操作主内存中的变量。但是这样就会出现一个问题，当一个线程修改了自己工作内存中变量，对其他线程是不可见的，会导致线程不安全的问题。因为JMM制定了一套标准来保证开发者在编写多线程程序的时候，能够控制什么时候内存会被同步给其他线程。在实际开发中，更多会根据happens-before原则，指导使用volatile和一些同步锁，保证多线程的同步问题。

#### happens-before原则

happens-before原则规定, 如果线程 1 执行的操作 A 先行发生于线程 2 中执行的操作 B，那么操作 A 产生的影响能够被操作 B 观察到。“影响” 包括修改了内存中的共享变量的值、发送了消息、调用了方法等。

多线程之间的执行在时间上具有先后执行顺序，并不意味着先执行线程的执行结果能够被后执行线程所观察到（因此存在变量的可见性问题）。Happens-before 规则的直接作用是约束指令重排序，从而保证同步，确定了线程的安全性。

#### **内存模型的三大特性**

- **原子性:** Java 内存模型保证了 read、load、use、assign、store、write、lock 和 unlock 操作具有原子性. 但是对一个变量的赋值等操作, 往往需要以上多个操作实现, 多个操作的组合并不是原子性的. 比如多线程对一个int变量n=0自增时, 线程A先取n, 读值为0, 随后线程B立即也取值, 读值为0, 然后线程A将n自增为1, 线程B也将n自增赋值为1. 看起来自增了两次, 但是n却只增加了1, 这就是多线程不同步的风险. 而JUC包里的一些原子类, 会保证多个操作的原子性. Java中通过**锁和CAS**实现原子操作

  单个操作原子性不能保证线程安全:

  ![1563203884363](/d:/resource/notePic/1563203884363.png)

  多个操作的原子性可以保证线程安全:

  ![1563203929813](/d:/resource/notePic/1563203929813.png)

- **可见性:** 指当一个线程修改了共享变量的值，其它线程能够立即得知这个修改。Java 内存模型是通过在变量修改后将新值同步回主内存，在变量读取前从主内存刷新变量值来实现可见性的。Java实现可见性主要有三种方式

  - volatile: 变量更新后会立即刷新到主存中
  - synchronized: 对一个变量执行 unlock 操作之前，必须把变量值同步回主内存。
  - final：final修饰的字段在构造器中一旦完成初始化，并且构造器没有this逸出，那么其他线程就能看到final字段的值

- **有序性:** 在本线程内观察，所有操作都是有序的。在一个线程观察另一个线程，所有操作都是无序的，无序是因为发生了指令重排序。在 Java 内存模型中，允许编译器和处理器对指令进行重排序，重排序过程不会影响到单线程程序的执行，却会影响到多线程并发执行的正确性。

  volatile 关键字通过添加内存屏障的方式来禁止指令重排，即重排序时不能把后面的指令放到内存屏障之前

  synchronized保证每个时刻只有一个线程执行同步代码，相当于是让线程顺序执行同步代码。

#### 处理器如何保证内存读操作 / 写操作的原子性?

![1565081322222](/d:/resource/notePic/1565081322222.png)



#### 为什么存在指令重排序?

![1565080158824](/d:/resource/notePic/1565080158824.png)

```java
a = 1;
x = b;
```

假设线程A执行以上代码, 可以看到写操作被分成了A1和A3两步, 这是因为CPU写入内存速度相对较慢, 为了保证CPU流水作业的高效率, 会首先将结果写进缓冲区, 然后由缓冲区写进内存, 而执行完A1后, `x=b`的A2也就可以开始执行了, 由于A3比较耗时, 所以A3步骤是最后完成的.

即按照代码的顺序`a=1`应该是A3执行完成后, A2才能执行, 然而为了提高处理器的效率, 发生了以上A2先于A3执行的情况, 这就是指令重排. 禁止指令重排, 即强制A3执行完A2才能开始执行

可以看到, 如果b变量只是线程A内部变量, 那么指令重排不会影响程序预期结果, 当b是共享变量时, 指令重排对多线程程序, 可能会改变预期结果. 



### 用户态和内核态

**用户态:** 应用程序在运行时都处于用户态, 只能访问部分内存, 对CPU也只有部分执行权, 不能访问硬件

**内核态:** 只有操作系统和相应程序才能处于内核态, 拥有CPU的完全控制权, 可以访问硬件.

应用程序通过系统调用的形式转换成内核态, 并传入一些参数, 但是内核态的代码执行还是操作系统来处理

#### 上下文切换

线程从wait状态被唤醒, 就是一次上下文切换. 单核CPU也可以进行上下文切换, 切换时需要保存当前线程的运行状态信息. 
上下文切换比较耗时, 避免上下文切换: 无锁编程(乐观锁 / CAS算法)



### Java的BIO/NIO/AIO介绍

[参考:Java的BIO NIO AIO介绍](https://juejin.cn/post/6939841279329042439)

[epoll的本质](https://maimai.cn/article/detail?fid=1251603623&efid=125MfnRzjASGAn6D6WLsiA&from=timeline)

[linux的5种IO模型](https://note.youdao.com/ynoteshare1/index.html?id=1674bf550774ef96fd4a89c0f60a2f05&type=note)

![image-20210902161122502](D:\resource\notePic\image-20210902161122502-16318770501451.png)

阻塞点有两个：内核等待 IO 设备准备数据、内核将数据拷贝到用户空间

Linux 的 5 种 IO 模型种，除了异步 IO 模型，其它四种都是同步模型，在 内核将数据拷贝到用户空间 这一步仍然是同步的。

**BIO:** 对应 Linux 的同步阻塞 IO 模型。一个线程和一个线程池，线程**接收到连接后**，把它丢给线程池中的线程，再接受下一个连接，这就是BIO, BIO是阻塞IO, 阻塞点有两个, 等待数据就绪的过程和读取数据的过程。

**NIO:** 对应 Linux 的多路复用 IO 模型。一个线程和一个线程池，线程运行selector，执行select操作，**连接就绪后**才丢给线程池中的线程，再执行下一次的select操作，这就是NIO, NIO可以认为是非阻塞IO, 但其实还是有一个阻塞点, 读取数据的过程.

NIO与IO的最主要区别有两个: NIO是非阻塞的;  NIO面向块传输,BIO面向流传输.

epoll 相对 select 优化点：

- select 每次在 IO 设备数据准备就绪后，需要遍历所有的 socket，找到有数据的 socket；而 epoll 维护了一个有数据的 socket 列表引用，在有数据进程被唤醒之后，进程只用去读这个引用即可

**AIO:** 对应 Linux 的异步 IO 模型。一个线程和一个线程池，线程注册一个accept回调，系统帮我们建立连接后，触发回调在线程池中执行，执行时再注册read回调，系统帮我们接收完数据后，才触发回调在线程池中执行，这就是AIO, AIO是真正的异步非阻塞IO, 但是其编程复杂, 实际应用时性能提升不明显, 所以一般都还是采用NIO居多, 尤其是netty框架的使用.

如果把服务器比作饭店, 请求比作客人, BIO就是一对一的VIP服务, 每个客人来了都给他配一个服务员(线程), 等他点菜; NIO就是普通服务, 有一个专门的服务员(Selector线程)负责跑腿, 查看有没有来新客人或者客人点好菜了, 如果有新客人来了就带他就坐, 如果客人点好菜了, 就再叫一个服务员(Worker线程)过来记录菜单;  AIO就是自助餐厅, 客人来了告诉他去哪拿菜就可以了, 拿菜的地方又会提示它怎么结账, 已经不需要服务员了.



### 多线程开发的建议

- 给线程起个有意义的名字，这样可以方便找 Bug。
- 缩小同步范围，从而减少锁争用。例如对于 synchronized，应该尽量使用同步块而不是同步方法。
- 多用同步工具少用 wait() 和 notify()。首先，CountDownLatch, CyclicBarrier, Semaphore 和 Exchanger 这些同步类简化了编码操作，而用 wait() 和 notify() 很难实现复杂控制流；其次，这些同步类是由最好的企业编写和维护，在后续的 JDK 中还会不断优化和完善。
- 使用 BlockingQueue 实现生产者消费者问题。
- 多用并发集合少用同步集合，例如应该使用 ConcurrentHashMap 而不是 Hashtable。
- 使用本地变量和不可变类来保证线程安全。
- 使用线程池而不是直接创建线程，这是因为创建线程代价很高，线程池可以有效地利用有限的线程来启动任务。



## JVM

Java程序经过编译会生成`.class`的字节码文件，JVM可以将字节码文件解释成机器码，即转换成机器可以执行的命令。Java的跨平台特性正是指的`.class`文件的跨平台特性，不同平台硬件支持的机器码是不一样的，因此不同平台会有不同的JVM来完成`.class`文件=>机器码的工作。

### JVM运行时数据区域

![1563206095215](/d:/resource/notePic/1563206095215.png)

**程序计数器:** 记录正在执行的虚拟机字节码指令的地址（如果正在执行的是本地方法则为空）。

**Java虚拟机栈:** 每个 Java 方法在执行的同时会创建一个栈帧用于存储局部变量表、操作数栈、常量池引用等信息。从方法调用直至执行完成的过程，就对应着一个栈帧在 Java 虚拟机栈中入栈和出栈的过程。

该区域可能抛出以下错误：

- 当线程请求的栈深度超过最大值，会抛出 StackOverflowError
- 栈进行动态扩展时如果无法申请到足够内存，会抛出 OutOfMemoryError

**本地方法栈:** 本地方法栈与 Java 虚拟机栈类似，它们之间的区别只不过是本地方法栈为本地方法服务。
本地方法一般是用其它语言（C、C++ 或汇编语言等）编写的，并且被编译为**基于本机硬件和操作系统**的程序，对待这些方法需要特别处理。

#### 堆

所有对象都在这里分配内存，是垃圾收集的主要区域（"GC 堆"）。

现代的垃圾收集器基本都是采用分代收集算法，其主要的思想是针对不同类型的对象采取不同的垃圾回收算法。可以将堆分成两块: 新生代和老年代

**堆不需要连续内存**，并且可以动态增加其内存，增加失败会抛出 OutOfMemoryError 

![1552825693968](/d:/resource/notePic/1552825693968.png)

**堆内存常见分配策略**

1.对象优先存储在eden区
2.大对象(数组,字符串)优先存储在老年代: 避免分配担保机制频繁复制
3.长期存活对象进入老年代

**分配担保机制**

发生在新生代的垃圾回收机制是MinorGC, 发生在老年代的是MajorGC
当Eden区存满后, 要存入新数据时, 会进行MinorGC(很频繁):将Eden中部分数据存入S1和S2, 如果存不下, 则直接移往老年代中

**判断对象长期存活**

虚拟机给每个对象一个年龄计数器,每经历一次MinorGC没有被清理的对象,它的年龄计数器都会增加1,增加到某个默认值,就会进入老年区.或者增加到值,年龄低于这个值的对象在S区的数量少于一半,那么也可以认为长期存活.

#### 方法区

用于存放已被加载的类信息, 静态变量

和堆一样不需要连续的内存，并且可以动态扩展，动态扩展失败一样会抛出 OutOfMemoryError 

在JDK1.6时, 方法区存在堆中, 称为永久代. 为了更容易管理方法区, 从 JDK1.8 开始, 移除永久代, 并把方法区移至元空间(本地内存中), 而不是虚拟机内存中. 但是原方法区的静态变量和常量池等则放入堆中

Q：方法区为什么从堆中移出到直接内存中？

```
1.避免因为方法区占用内存导致 OOM，启动时分配空间可能不会考虑方法区大小
2.方法区回收性价比很低，在堆中每次回收时进行扫描浪费时间
```



#### 直接内存

在 JDK 1.4 中新引入了 NIO 类，它可以使用 Native 函数库直接分配堆外内存，然后通过 Java 堆里的
DirectByteBuffer 对象作为这块内存的引用进行操作。这样能在一些场景中显著提高性能，因为避免了在堆内存和堆外内存来回拷贝数据。

#### JVM启动参数

根据JVM的启动参数分配JVM runtime data area内存空间，如根据**-Xms、-Xmx**分配Heap大小；根据**-XX:PermSize、-XX:MaxPermSize**分配Method area大小；根据**-Xss**分配JVM Stack大小。注意，Method area、Heap是所有JVM线程都共享的，在JVM启动时就会创建且分配内存空间；JVM Stack、PC Register、Native Method Stack是每个线程私有的，都是在线程创建时才分配。



### 垃圾收集

#### 如何查看 java 进程的 GC 情况

1. 可以配置 JVM 的启动参数，打印 GC 日志 `-XX:+PrintGCDetails`

2. 实时查看，使用命令

   ```
   首先使用 jps -l 命令查看进程号
   然后使用 jstat -gcutil {pid} 5000 可以每隔 5s 打印一次 GC 情况
   ```

   ![image-20210902170942104](D:\resource\notePic\image-20210902170942104-16318801667582.png)

   如上图，主要信息有各区的使用容量大小，以及 YGC、FGC 的次数和时间

#### 判断一个对象是否可被回收

**引用计数法:** 为对象添加一个引用计数器，当对象增加一个引用时计数器加 1，引用失效时计数器减 1。引用计数为 0 的对象可被回收。

但是在两个对象出现循环引用的情况下，此时引用计数器永远不为 0，导致无法对它们进行回收。正是因为循环引用的存在，因此 Java 虚拟机不使用引用计数算法。

**可达性分析算法:**  从一系列GCRoots对象作为起点,搜索它们的引用链,如果所有的引用链都找不到某个对象的引用,那么认为这个对象没有引用, 则可以被回收.

Java 虚拟机使用该算法来判断对象是否可被回收，GC Roots 一般包含以下内容：

- 虚拟机栈中局部变量表中引用的对象
- 本地方法栈中 JNI 中引用的对象
- 方法区中类静态属性和常量引用的对象

![1563289057670](/d:/resource/notePic/1563289057670.png)

#### 方法区的回收

因为方法区主要存放永久代对象，而永久代对象的回收率比新生代低很多，所以在方法区上进行回收性价比不高。
主要是对常量池的回收和对类的卸载。
为了避免内存溢出，在大量使用反射和动态代理的场景都需要虚拟机具备类卸载功能。

类的卸载条件很多，需要满足以下三个条件，并且满足了条件也不一定会被卸载：

- 该类所有的实例都已经被回收，此时堆中不存在该类的任何实例。
- 加载该类的 ClassLoader 已经被回收。
- 该类对应的 Class 对象没有在任何地方被引用，也就无法在任何地方通过反射访问该类方法。

#### 引用类型

如果没有任何引用指向对象A, 那么对象A一定会被回收. 如果有引用指向对象A, 对象A是否被回收和指向它的引用类型有关 

**强引用:** 被强引用关联的对象不会被回收。使用 new 一个新对象的方式来创建强引用。

**软引用:** 被软引用关联的对象只有在内存不够的情况下才会被回收。使用 SoftReference 类来创建软引用。

```java
Object obj = new Object();
SoftReference<Object> sf = new SoftReference<Object>(obj);
obj = null; // 使对象只被软引用指向
```

**弱引用:** 被弱引用关联的对象一定会被回收，也就是说它只能存活到下一次垃圾回收发生之前。使用 WeakReference 类来创建弱引用（ThreadLocal 是一个很好的弱引用例子）

```java
Object obj = new Object();
WeakReference<Object> wf = new WeakReference<Object>(obj);
obj = null;
```

**虚引用:** 又称为幽灵引用或者幻影引用，一个对象是否有虚引用的存在，不会对其生存时间造成影响，也无法通过虚引用得到一个对象。

为一个对象设置虚引用的唯一目的是能在这个对象被回收时收到一个系统通知。

注意，以上说的引用被回收的标准，都是指该对象仅有该类型的引用时。比如一个对象既有强引用，又有弱引用，那么它在下次 GC 时是不会被回收的。

#### 垃圾收集算法

**标记-清除**: 在标记阶段，程序会检查每个对象是否为活动对象，如果是活动对象，则程序会在对象头部打上标记。

在清除阶段，会进行对象回收并取消标志位，另外，还会判断回收后的分块与前一个空闲分块是否连续，若连续，会合并这两个分块。回收对象就是把对象作为分块，连接到被称为 “空闲链表” 的单向链表，之后进行分配时只需要遍历这个空闲链表，就可以找到分块。

在分配时，程序会搜索空闲链表寻找空间大于等于新对象大小 size 的块 block。如果它找到的块等于 size，会直接返回这个分块；如果找到的块大于 size，会将块分割成大小为 size 与 (block - size) 的两部分，返回大小为 size 的分块，并把大小为 (block - size) 的块返回给空闲链表。

优点: 快速		缺点: 产生大量不连续的碎片

![1563375833265](/d:/resource/notePic/1563375833265.png)

**标记-整理:** 让所有存活的对象都向一端移动，然后直接清理掉端边界以外的内存。

优点: 不会产生连续碎片		缺点: 耗时

![1563375899289](/d:/resource/notePic/1563375899289.png)

**复制:** 将内存划分为两块，每次只使用其中一块，当这一块内存用完了就将还存活的对象复制到另一块上面，然后再把使用过的内存空间进行一次清理。

优点: 不会产生连续碎片		缺点: 需要两个内存区

![1563375964052](/d:/resource/notePic/1563375964052.png)

现在的商业虚拟机都采用这种收集算法回收新生代，但是并不是划分为大小相等的两块，而是一块较大的 Eden 空间和两块较小的 Survivor 空间，每次使用 Eden 和其中一块 Survivor。在回收时，将 Eden 和 Survivor 中还存活着的对象全部复制到另一块 Survivor 上，最后清理 Eden 和使用过的那一块 Survivor。

HotSpot 虚拟机的 Eden 和 Survivor 大小比例默认为 8:1，保证了内存的利用率达到 90%。如果每次回收有多于10% 的对象存活，那么一块 Survivor 就不够用了，此时需要依赖于老年代进行空间分配担保，也就是借用老年代的空间存储放不下的对象。

**分代收集:** 新生代因为每次回收时只有少量对象不被回收, 使用复制算法, 老年代采用标记-清除 或 标记-整理算法



#### 垃圾收集器

在 `JVM` 中，具体实现有 `Serial`、`ParNew`、`Parallel Scavenge`、`CMS`、`Serial Old（MSC）`、`Parallel Old`、`G1` 等。图中有 `7` 种不同的 **垃圾回收器**，它们分别用于不同分代的垃圾回收。如果两个垃圾回收器之间 **存在连线**，那么表示两者可以 **配合使用**。

![1563430738119](/d:/resource/notePic/1563430738119.png)

**Serial:** 新生代单线程垃圾收集器, 采用复制算法, 垃圾收集时采用STW(Stop the World)策略, 对于单CPU而言, 效率较高, 一般作为客户端默认的垃圾收集器

**Serial Old:** Serial的老年代版本, 采用标记-整理算法

**ParNew:** Serial的多线程版本, 也是采用复制算法, 应用于新生代, 对于多CPU而言, 可以提高速度

**Parallel Scavenge:** 也是新生代的多线程垃圾收集器, 采用复制算法. 但是它更注重吞吐量的提升, 这就导致在一定程度上会提高新生代大小, 从而影响停顿时间.

**Parallel Old:** Parallel Scavenge的老年代版本, 采用标记-整理算法. 同样是吞吐量优先

**CMS:** Concurrent Mark Sweep, 也是多线程, 用于老年代的垃圾收集器, 但是采用标记-清除算法, 更关注低停顿时间指标. 它的停顿时间只发生以下的初始标记和重新标记两个阶段上, 而另外两个阶段并发标记和并发清除是最耗时的, 由于是并发操作, 所以用户感受不到停顿, 用户体验较好.

![1563431580642](/d:/resource/notePic/1563431580642.png)

因为垃圾收集进程要和用户线程并发进行, 所以不能使用标记-整理算法. 标记-整理算法更适合STW策略

- Mark Sweep算法会导致内存碎片比较多

- CMS的并发能力依赖于CPU资源，所以在CPU数少和CPU资源紧张的情况下，性能较差

- 并发清除阶段，用户线程依然在运行，会产生浮动垃圾，此阶段的垃圾等到下次GC才能回收。所以需要浪费一些内存空间给用户线程浮动垃圾预留, 吞吐量低

**G1:** G1 首先将 堆 分为 大小相等 的 Region，避免 全区域 的垃圾回收。然后追踪每个 Region 垃圾 堆积的价值大小，在后台维护一个 优先列表，根据允许的回收时间优先回收价值最大的 Region。保证 有限的时间内可以获得尽可能高的回收效率。

![1563432596825](/d:/resource/notePic/1563432596825.png)

G1 的垃圾清除阶段也是并发的，但是与 CMS 不同的是，G1 垃圾清除阶段的用户线程是 STW 模式的，所以不会产生浮动垃圾。

G1 既用于新生代垃圾回收，又用于老年代垃圾回收（新生代复制清除，老年代是标记整理），并且它的老年代会记录 out reference，避免在 young gc 的时候，扫描全部的老年代

[minor gc如何解决老新引用，避免扫描老年代？](https://www.zhihu.com/question/63785052/answer/216407946)

[G1收集器详解及调优](https://juejin.cn/post/6844904175571042312#heading-2)

#### Full GC触发条件

- 老年代空间不足, 常见场景为前文所讲的**大对象直接进入老年代**、**长期存活的对象进入老年代**等
- **分配担保失败**: 复制算法的 Minor GC 需要老年代的内存空间作担保，如果担保失败会执行一次 Full GC



### JVM中的加载机制

Java代码在编译后会变成Java字节码, 字节码被类加载器加载到JVM中, JVM执行字节码, 最终要转化为汇编指令在CPU上执行.

类是在运行期间第一次使用时动态加载的，而不是一次性加载所有类。因为如果一次性加载，那么会占用很多的内
存。**类的生命周期如下：**

![1563466057929](/d:/resource/notePic/1563466057929.png)

#### 类的加载过程

类加载发生在所有实例化操作之前，并且类加载只进行一次，实例化可以进行多次。类加载过程主要包括：加载, 验证, 准备, 解析 和 初始化5个步骤.

**加载** 

加载过程完成以下三件事：

1. 通过类的完全限定名称获取定义该类的二进制字节流。
2. 将该字节流表示的静态存储结构转换为方法区的运行时存储结构。
3. 在内存中生成一个代表该类的 Class 对象，作为方法区中该类各种数据的访问入口。

二进制字节流可以通过jar包 / 网络 / 运行时动态生成等方式获取

**验证** 

确保 Class 文件的字节流中包含的信息符合当前虚拟机的要求，并且不会危害虚拟机自身的安全。

**准备**

准备阶段为类变量分配内存并设置初始值，使用的是方法区的内存。只有被final修饰的类常量在准备阶段的初始值才等于定义值, 普通类变量初始值是零值或空值.

**解析**

将常量池的符号引用替换为直接引用的过程。

**初始化**

初始化阶段才真正开始执行类中定义的 Java 程序代码。初始化阶段是虚拟机执行类构造器 <clinit>() 方法的过程。在准备阶段，类变量已经赋过一次系统要求的初始值，而在初始化阶段，根据程序员通过程序制定的主观计划去初始化类变量和其它资源。

`<clinit>() `是由编译器自动收集类中所有**类变量的赋值动作和静态语句块**(注意类加载没有构造方法!!!)中的语句合并产生的，编译器收集的顺序由语句在源文件中出现的顺序决定。特别注意的是，静态语句块只能访问到定义在它之前的类变量，定义在它之后的类变量只能赋值，不能访问。例如以下代码：

```java
public class Test {
    static {
        i = 0; // 给变量赋值可以正常编译通过
        System.out.print(i); // 这句编译器会提示“非法向前引用”
    }
    static int i = 1;
}
```

父类优先于子类进行初始化, 但是接口初始化时不需要先将父类接口初始化.

如果多个线程同时初始化一个类，虚拟机会保证只有一个线程能执行这个类的初始化方法，其它线程都会阻塞等待. 因此如果在一个类的初始化 方法中有耗时的操作，就可能造成多个线程阻塞，在实际过程中此种阻塞很隐蔽。



#### 类的初始化时机

**主动引用**

虚拟机规定了遇到以下主动引用情形, 必须对类进行初始化

- 使用new创建类的实例对象
- 使用(get/set)类的静态变量(非常量) 或 静态方法
- 对类进行反射调用
- 初始化类的子类时, 需要先初始化父类

**被动引用**

不能触发类初始化的引用成为被动引用, 有以下情形

- 通过子类使用父类的静态变量或静态方法, 不会触发子类初始化

- 通过数组定义来引用类，不会触发此类的初始化。该过程会对数组类进行初始化，数组类是一个由虚拟机自动生成的、直接继承自 Object 的子类，其中包含了数组的属性和方法。
  
```java
    ClassA[] aArray = new ClassA[10]; // ClassA不会初始化
```

- 常量(静态属性+final)在编译阶段会存入调用类的常量池中，本质上并没有直接引用到定义常量的类，因此不会触发定义常量的类的初始化。



#### 类加载器ClassLoader

我们平常写的类文件后缀名是.java, 它是不能被JVM读取的, 需要编译成二进制字节码文件, JVM就是通过ClassLoader将二进制字节码文件读取到内存中, 生成类的Class对象.

ClassLoader总的来说有四种: 

- Bootstrap ClassLoader：启动类加载器，主要负责加载Java核心类库，%JRE_HOME%\lib下的rt.jar、resources.jar、charsets.jar和class等
- Extension ClassLoader ：标准扩展类加载器，主要负责加载目录%JRE_HOME%\lib\ext目录下的jar包和class文件
- Application ClassLoader：应用类加载器，主要负责加载当前应用的classpath下的所有类
- User ClassLoader：用户自定义类加载器，可加载指定路径的class文件

加载后的Class对象都会含有加载它的ClassLoader的引用, 而ClassLoader具有优先级关系(如下图), 在进行类加载时, 采用双亲委派模式, 避免了类的重复加载并保证了核心类的安全.

![1561479679668](/d:/resource/notePic/1561479679668.png)

`ClassLoader.getSystemClassLoader()`返回的即是Application ClassLoader. 加载类常用方式有:`Class.forName()`和`ClassLoader.loadClass()`, 两者都需要类的全限定名并且会将类的Class对象加载到内存中, 但是前者会执行类的初始化, 后者不会

**tomcat 实现 jsp 的热部署**

每个jsp文件都有一个类加载器，当jsp文件修改时，就会将这个jsp的类加载器卸载，因此下一次使用jsp时就需要重新加载。



#### 双亲委派模式

双亲委派模式的工作原理的是;如果一个类加载器收到了类加载请求，它并不会自己先去加载，而是把这个请求委托给父类的加载器去执行，如果父类加载器还存在其父类加载器，则进一步向上委托，依次递归，请求最终将到达顶层的启动类加载器，如果父类加载器可以完成类加载任务，就成功返回，倘若父类加载器无法完成此加载任务，子加载器才会尝试自己去加载，这就是双亲委派模式，即每个儿子都不愿意干活，每次有活就丢给父亲去干，直到父亲说这件事我也干不了时，儿子自己想办法去完成。总结就是：检查自底向上，加载自顶向下

我们在项目中如果自定义 ClassLoader，方式就是继承 ClassLoader 并重写 findClass 方法，需要根据类的全限定名生成一个 class 文件。但是实际在加载时，并不是说我们写的 ClassLoader 就一定会生效，双亲委派机制会将该类全限定名一层层往上寻找，找到最顶层第一个能加载它的 ClassLoader 进行加载，生成 Class 对象。

采用双亲委派模式的是好处是：

- 避免重复加载：Java类随着它的类加载器一起具备了一种带有优先级的层次关系，通过这种层级关可以避免类的重复加载，当父亲已经加载了该类时，就没有必要子ClassLoader再加载一次。
- 安全考虑：java核心api中定义类型不会被随意替换，假设通过网络传递一个名为java.lang.Integer的类，通过双亲委托模式传递到启动类加载器，而启动类加载器在核心Java API发现这个名字的类，发现该类已被加载，并不会重新加载网络传递的过来的java.lang.Integer，而直接返回已加载过的Integer.class，这样便可以防止核心API库被随意篡改。

**如何破坏双亲委派机制？**

类加载器都需要继承 ClassLoader ，它是类加载器的模板类。它其中有三个重要的方法：

- loadClass：类加载的全部步骤都在这里，双亲委派的逻辑也在这里
- findClass：根据名称或位置加载.class字节码
- definclass：把字节码转化为Class

所以破坏双亲委派机制就是，写一个类加载器继承 ClassLoader，重写它的 loadClass 方法。

如果只是想实现自定义的类加载器，但是不想破坏双亲委派机制，那么继承 ClassLoader，重写它的 findClass 方法即可。

**有哪些破坏双亲委派机制的实例，为什么？**

[参考](https://juejin.cn/post/6916314841472991239#heading-7)

1. Java 支持的 SPI 机制，比如 JDBC。因为根据 SPI 机制只提供了抽象的接口，接口实现类由第三方提供，所以在类加载时，不能向上委派，而是调用了 `Thread.currentThread().getContextClassLoader()` ，它是线程上下文类加载器，一般默认是 ApplicationClassLoader，通过它就可以加载 lib 包中的类了
2. Tomcat，因为 Tomcat 可能会运行多个实例，对于同一个类全路径名的 Class，可能有不同版本，需要加载不同的 Class。所以 Tomcat 对不同 webApp 提供了不同的 WebAppClassLoader，它们都只负责加载自己目录下的 class 文件





#### Java对象的创建过程

**1.类加载检查:** 在常量池中查找是否有这样的类,然后检查类是否被加载过,若没有,则会进行类加载

**2.分配内存空间**

**3.初始化置零:** 会将对象中所有字段的初始值设置为对应的"零值";注意这不是执行构造方法

**4.设置对象头:** 将类的hash码,锁状态等信息存放在对象头中

**5.init方法:** 此时,对象创建完成,开始执行Java程序指定的代码块/构造方法的初始化过程



#### 对象属性的初始化顺序

初始化顺序指的是站在开发人员的角度, 某个对象在生成时, 与它有关的属性(包括类属性)的加载顺序.

静态变量和静态语句块优先于实例变量和普通语句块，静态变量和静态语句块的初始化顺序取决于它们在代码中的顺序。

```java
// 初始化顺序示例
public static String staticField = "静态变量";
static {
    System.out.println("静态语句块");
}
public String field = "实例变量";
{
    System.out.println("普通语句块");
}
// 最后才是构造函数初始化
public InitialOrderTest() {
    System.out.println("构造函数");
}
```

存在继承的情况下，初始化顺序为：
父类（静态变量、静态构造语句块）
子类（静态变量、静态构造语句块）
父类（实例变量、构造语句块）
父类（构造函数）
子类（实例变量、构造语句块）
子类（构造函数）

总结为: 静态 > 实例 / 父类 > 子类 / 成员代码块看顺序 / 构造方法在最后. 其实静态成员初始化不能算在对象的创建过程中, 它们属于类加载过程.



#### 构造方法

**构造方法规范**

1. 类都有默认的隐式无参构造方法.
2. 当显式声明了构造方法时, 就失去了隐式构造方法. 所以如有需要, 需显式声明无参构造方法
3. 在构造方法中显式调用其他构造方法, 必须放在第一句
4. 所有类的所有构造方法中, 第一行都有隐式的 super(); 其调用了直系父类的无参构造方法
    不继承任何类的类,直系父类是Object
5. 4中情况有特例. 当类的构造方法中第一句显式使用了 super或this 来调用其他构造方法时, 就没有隐式 super()

```java
class A{
    int num;
    A(int num){
        this.num = num;
    }
}

class B extends A{
    String msg;
    
    B(){ // 如果没有这个构造方法,B的声明会编译失败
        super(num); // 因为默认是调用super(), 而A缺失无参构造方法
    }
}
```



#### 内部类

##### 成员内部类

成员内部类在没有使用的时候,是不会被加载的.

内部类作为外部类成员的角色,因此对它的使用和对其他成员的使用条件是一样的.
	比如成员内部类如果被private修饰,那么只能在外部类的定义中使用
	比如成员内部类如果没被static修饰,创建成员内部类就需要外部类的对象
	比如成员内部类如果被static修饰,可以用外部类类名来创建成员内部类

当然,成员内部类可以访问所有外部类成员

```java
// 非静态成员内部类
class A{
    int num;
    class B{
        int numB;
    }
}

A.B ab = new A().new B(); // 使用外部类对象 创建 非静态成员内部类对象
```

```java
// 静态成员内部类
class A{
    int num;
    static class B{
        int numB;
    }
}

A.B ab = new A.B(); // 使用外部类 直接创建 静态成员内部类对象
```

##### 局部内部类

局部内部类即在方法中定义的类, 在方法中使用局部内部类, 和使用普通类一样, 仅方法内可用

```java
class A{
	int num;
	
    void print(){
        class B{
			int numB;
        }
        B b = new B(); // 使用局部内部类,和使用正常外部类一模一样. 但是只能在这个方法中使用
        System.out.println(n.numB);
    }
}
```

```java
// 匿名内部类
interface A{
    method();
}
class B{
    int num;
    void print(){
        A a = new A(){
            @Override
            method(){
                System.out.println("匿名内部类通过直接实现接口方法");
            }
        };
        a.method(); // 打印: "匿名内部类通过直接实现接口方法"
    }
}
```



#### 常量池

所有字符串字面值都会进入常量池, 所有new创建的对象都在堆中

```java
String str0 = new String("str0"); // 常量池中有了,堆对象也创建了
String str1 = "str" + "ing"; // 常量池中新增对象
String str3 = str1 + str2; // 堆上创建的新的对象	
```

包装类中,Byte,Short,Integer,Long,Character,Boolean 实现了缓存技术, 在常量池中缓存了[-128,127]. (Double 和 Float没有缓存)

```java
Integer i1 = 40; // 引用常量池中缓存的对象
Integer i2 = new Integer(40); // 在堆上创建新对象
Integer i3 = 128; // 在堆上创建新对象
Integer i4 = 128; // 在堆上创建新对象,且和i3不是同一个对象(相当于都是new Integer(128))
```

关于字符串或包装类的等于比较: equals方法比较值相等, 如果使用"=", 比较的是它们的地址, 如果它们指向同一个对象, "="比较才为true. 但是包装类如果在比较时使用了"+"或"-", 会自动拆箱, 那么"="比较又变成了值比较.

```java
"abc" = new String("abc"); // false
"abc".equals(new String("abc")); // true
new Integer(100) = new Integer(100); // false
new Integer(100).equals(new Integer(100)); // true
new Integer(100) + 0 = new Integer(100); // true,因为两边都会自动拆箱
```



## 设计模式

### 单例模式

> 好处:1.对于只需要一个实例的对象,节约了时间开销;2.减少了内存使用,减轻了GC压力.

私有构造方法,私有静态成员变量instance,公有静态方法getInstance

````java
// 懒汉模式: instance等到第一次使用时才实例化,需要使用双重校验锁保证线程安全
public class Singleton{
    private static volatile Singleton instance; // 第二重锁volatile
    
    private Singleton(){}
    
    public static Singleton getInstance(){
        if(instance == null){
            synchronized(Sinleton.class){ // 第一重锁synchronized
                if(instance == null){ // 锁后要再进行一次判断
                    instance = new Sinegelton();
                }
            }
        }      
        return instance;
    }
}

第一重锁是保证没有多个线程同时在创建instance实例,volatile 是因为在创建instance实例时,一般有三步:
	1.给对象分配内存空间
	2.初始化对象
	3.将空间地址赋值给instance引用
而因为JVM指令重排,可能执行顺序变成132,因此A线程在执行完3后,还未初始化,B线程发现instance不为null,就返回了,而得到一个未初始化的对象.volatile关键字保证instance在创建时,禁止指令重排
````

````java
// 饿汉模式:不管用不用,将静态参数初始化的时候就创建
private static final Singleton instance = new Singleton();
````

##### 静态内部类实现懒汉模式

````java
普通懒汉模式存在反射攻击的危险.使用静态内部类.当Singleton被加载时,其内部类并未被加载,因为没有使用到.
当执行getInstance方法时,静态内部类才会被加载.并且JVM确保只会被加载一次,反射攻击无用

public class Singleton{
    private Singleton(){}
    
    private static class SingletonHolder{
        private static final Singleton INSTANCE = new Singleton();
    }
    
    public static Singleton getInstance(){
        return SingletonHolder.INSTANCE;
    }
}
````



### 工厂模式

工厂模式是将对象创建过程封装在工厂里，有以下好处：

- 解耦了对象的创建和使用
- 对于复杂对象，避免重复编码
- 对于后续对象创建过程中的变化，约束在了一个地方，易于维护

#### 简单工厂模式

简单工厂模式就是使用一个工厂类,生产一类对象.
当有新的对象需要生成时,就需要改动工厂类的具体代码

简单工厂模式的工厂类提供创建所有对象的实现

`````java
// 工厂
public class AnimalFactory{
    public static final String DOG = "dog";
    public static final String CAT = "cat";
    
    public Animal createAnimal(String race){
        switch(race){
            case DOG:
                return new Dog();
            case CAT:
                return new Cat();
        } 
    }
}

// 使用工厂
public static void main(String[] args){
    AnimaFactory factory = new AnimaFactory();
    Animal pony = factory.createAnimal(AnimalFactory.DOG);
    Animal jack = factory.createAnimal(AnimalFactory.CAT);
}
`````

#### 工厂方法模式

工厂方法模式和简单工厂模式实现的功能是一样的
区别:当客户需要创建新的类对象时,不用改变原有工厂代码,只用增加新的实现工厂类即可.

工厂方法模式的工厂只提供创建对象的规范(输入参数和输出参数), 具体怎么创建对象, 需要继承抽象工厂, 自己实现方法

````java
// 工厂抽象类
public abstract class AnimalFactory{
    public abstract Animal createAnimal();
}

// 具体工厂
public class DogFactory extends AnimalFactory{
    @Override
    public abstract Animal createAnimal(){
        return new Dog();
    }
}

public class CatFactory extends AnimalFactory{
    @Override
    public abstract Animal createAnimal(){
        return new Cat();
    }
}

// 使用工厂
public static void main(String[] args){
    Animal pony = new DogFactory().createAnimal();
    Animal jack = new CatFactory().createAnimal();
}
````

#### 抽象工厂模式

工厂方法模式:不关注生产类的行为,只关注生产类的"种类".因此它的抽象工厂只有一种方法,不同的具体工厂生产不同种类的对象
抽象工厂模式:关注类的行为和"种类",对"种类"的关注体现在抽象工厂(接口)提供了生产不同种类的方法.对行为的关注体现在,不同的具体工厂就用于生产,具有不同行为的各种"种类"对象.

显然抽象工厂模式在使用起来更加"多样化",但是缺点也是由多样化带来的,当增加了种类时,抽象工厂和具体工厂代码都需要改动

````java
// 工厂接口
public interface AnimalFactory {
    Animal createDog();
    Animal createCat();
}

// 具体工厂
public class FemaleAnimalFactory implements AnimalFactory {

    // 生产母狗和母猫
    @Override
    public Animal createDog() {
        return  new FemaleDog();
    }

    @Override
    public Animal createCat() {
        return new FemaleCat();
    }

}

public class MaleAnimalFactory implements AnimalFactory {
    
    // 生产公狗和公猫

    @Override
    public Animal createDog() {
        return new MaleDog();
    }

    @Override
    public Animal createCat() {
        return new MaleCat();
    }

}

// 使用工厂
public static void main(String[] args){
    Animal pony = new FemaleAnimalFactory().createDog(); // 生产一个公狗
    Animal jack = new MaleAnimalFactory().createCat(); // 生产一个母猫
}
````



### 代理模式

#### 静态代理

特点:1.和目标对象实现相同的接口; 2.有相同接口的属性; 3.无参构造方法里new B

```java
public class B implments A{ }

public class BProxy implements A{
    A a;
    
    public BProxy(){
        a = new B();
    }
}
```

#### 动态代理

动态代理运用字节码动态生成加载技术,在运行时生成加载类;
生成动态代理类的方法有:JDK自带的动态处理,CGlib,Javassist

````java
// JDK动态代理: Proxy.newProxyInstance()
HttpServletRequest.class requestProxy = (HttpServletRequest.class)Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader, request.getClass.getInterface(),  new InvocationHandler(){
  @Override
  public Object invoke(Object proxy, Method method, Object[] args){
		// 动态代理想要实现的效果:比如第一次使用时加载主题对象
  }
});
````



### 代理模式和装饰者模式有什么区别？

代理客户做事情，是因为代理更专业，但是本质还是为了做某件确定的事情。而装饰者模式，则是为了在做某件事情的时候，进行增强，要干点其它事情。

代理模式往往是为了帮助客户访问真实的对象, 而装饰者模式是为了增强原有对象的功能. 比如在进行RPC调用时, 客户端只声明了接口引用, 客户端执行对象方法时, 是通过动态代理去帮忙执行的, 最后的结果就是这个方法本应该返回的结果, 没有增强. 只是因为客户端方面没有接口的实现对象, 所以需要代理模式去帮它执行.



### 策略树模式

在处理多逻辑分支时，策略树模式能够将复杂的 if/else 代码分门别类，便于后续的维护和修改。

其实现也非常简单，抽象的核心接口就两个：StrategyRouter 与 StrategyHandler。

StrategyRouter 是对策略分发行为的抽象：

```java
/**
 * 通用的“策略树“框架，通过树形结构实现分发与委托，每层通过指定的参数进行向下分发委托，直到达到最终的执行者。
 * 该框架包含两个类：{@code StrategyHandler} 和 {@code StrategyRouter}
 * 实现 {@code StrategyRouter} 接口完成对策略的分发，
 * 实现 {@code StrategyHandler} 接口来对策略进行执行。
 * 像是第二层 A、B 这样的节点，既是 Root 节点的策略实现者也是策略A1、A2、B1、B2 的分发者，这样的节点只需要
 * 同时实现 {@code StrategyHandler} 和 {@code AbstractStrategyRouter} 就可以了。
 *
 * <pre>
 *           +---------+
 *           |  Root   |   ----------- 第 1 层策略入口
 *           +---------+
 *            /       \  ------------- 根据入参 P1 进行策略分发
 *           /         \
 *     +------+      +------+
 *     |  A   |      |  B   |  ------- 第 2 层不同策略的实现
 *     +------+      +------+
 *       /  \          /  \  --------- 根据入参 P2 进行策略分发
 *      /    \        /    \
 *   +---+  +---+  +---+  +---+
 *   |A1 |  |A2 |  |B1 |  |B2 |  ----- 第 3 层不同策略的实现
 *   +---+  +---+  +---+  +---+
 * </pre>
 *
 * @see StrategyHandler
 */
public interface StrategyRouter<T, R> {

    /**
     * 策略的分发逻辑
     * @return 策略执行 Handler
     */
    StrategyHandler<T, R> route(T condition);

}
```

StrategyHandler 是对策略执行行为的抽象：

```java
public interface StrategyHandler<T, R> {

    /**
     * 该方法内可以执行业务策略直接返回结果，也可以继续做路由，交给下一个 StrategyHandler 处理
     * @param condition 策略条件
     */
    R apply(T condition);
}
```



### 发布订阅模式

消息队列的概念就是发布订阅模式的优秀实践，之前开发的系统中领域事件组件是基于 Spring 观察者模式改造而成的发布订阅模式，DomainEvent 发布后，观察到的是唯一的 DomainEventDistributor，它相当于一个管道，根据领域事件的不同类型，去分发到具体的 DomainEventHandler 去响应这个领域事件。



## 计算机网络

网络是把主机连接起来,而互联网是把不同的网络连接起来,因此互联网是网络的网络

### TCP/IP五层模型

网络分层的目的就是为了一层层抽象, 对上层屏蔽复杂的实现细节. 同时为了保证消息能够在各层间传播, 会制定一定的协议.

![1553046573431](/d:/resource/notePic/1553046573431.png)

- 物理层: 考虑的是怎样在传输媒体上传输比特流,屏蔽不同传输媒体和通信手段的差异,让链路层感觉不到区别
- 数据链路层: 不同主机之间有很多链路,链路层协议是为同一链路上的主机提供数据传输服务
- 网络层: 为不同主机提供数据传输服务, 常用协议IP协议(路由器就工作在这个级别)	
- 传输层: 为主机的进程提供数据传输服务, 常用协议TCP(数据单位为报文段),UDP(用户数据报)
- 应用层: 为特定应用程序提供数据传输服务, 常用协议HTTP和DNS, 数据单位为报文

数据在各层之间的传递: 从上层往下层走,是将数据不断添加下层协议需要的头部和尾部;从下层往上层走,就是将数据不断拆开协议的头部和尾部

![1565598873576](/d:/resource/notePic/1565598873576.png)

### TCP和UDP的区别

TCP: 传输控制协议,面向可靠连接的服务.数据在传输之前会进行三次握手连接,传输完后会进行四次挥手断开连接。有了三次握手和四次挥手的连接机制，TCP协议能够让通信双方知道彼此的状态，再加上超时重传机制，保证了数据不丢失。

UDP: 用户数据协议,非面向连接的服务.其数据传输时不需要建立连接,因此消耗资源少,传输速率快,但是错误率会高

- TCP不提供广播或多播服务, UDP提供
- TCP适用于对可靠性要求高的服务,比如即时通信. UDP适用于对可靠性要求不高的服务,比如直播



### UDP 与 TCP 报文的头部信息

[参考](https://juejin.cn/post/6844903889146216456) ：特别详细

#### UDP首部信息

![1567490282558](/d:/resource/notePic/1567490282558.png)

UDP首部字段很简单，由4个字段组成，每个字段的长度都是两个字节，共8字节。

- **源端口** 原端口号，在需要对方回信时选用，不需要时可全0
- **目的端口** 目的端口号，必有
- **长度** 报文字节长度(包含头部)，最小值为8字节，仅有首部
- **检验和** 检测用户数据报在传输过程是否有错，有错就丢弃

#### TCP首部信息

![1567490894699](/d:/resource/notePic/1567490894699.png)

TCP协议作为传输层主要协议之一，是面向连接，面向字节流，端到端，可靠的全双工通信数据传输协议。UDP头部信息它都有，并且为了面向连接和保持可靠，TCP具有更多的头部信息：

- **序号** 占4个字节，范围是[0,2^32],TCP是面向字节流的，每个字节都是按顺序编号。例如一个报文段，序号字段是201，携带数据长度是100，那么第一个数据的序号就是201，最后一个就是300。当达到最大范围，又从0开始
- **确认号** 占4个字节，是期望收到对方下一个报文段的第一个字节的序号。若确认号=N,则表示序号N前所有的数据已经正确收到了。
- **数据偏移** 占4位，表示报文段的数据部分的起始位置，距离报文段的起始位置的距离。间接的指出首部的长度。
- **URG(紧急)** 当URG=1,表明紧急指针字段有效，该报文段有紧急数据，应尽快发送。
- **ACK(确认)** 仅当ACK=1时，确认号才有效，连接建立后，所有的报文段ACK都为1。
- **SYN(同步)** 在建立连接时用来同步序号。当SYN=1,ACK=0，则表明是一个连接请求报文段。SYN=1,ACK=1则表示对方同意连接。TCP建立连接用到。
- **FIN(终止)** 用来释放一个连接窗口。当FIN=1时，表明此报文段的发送方不再发送数据，请求释放单向连接。TCP断开连接用到。
- **窗口** 占2个字节，表示自己的发送方自己的接收窗口，窗口值用来告诉对方允许发送的数据量。



### TCP的三次握手和四次挥手

[参考](https://juejin.im/post/5d2757356fb9a07ef7109ecc) ：特别详细

#### 三次握手

![1567489677241](/d:/resource/notePic/1567489677241.png)

**第一次**：客户端发送连接请求报文给服务端，其中SYN=1,seq=x。发送完毕后进入SYN_END状态。

**第二次**：服务端接收到报文后，发回确认报文，其中ACK=1,ack=x+1，因为需要客户端确认，所以报文中也有SYN=1,seq=y的信息。发送完后进入SYN_RCVD状态。

**第三次**:客户端接收到报文后,发送确认报文，其中ACK=1,ack=y+1。发送完客户端进入`ESTABLISHED`状态，服务端接收到报文后，进入`ESTABLISHED`状态。到此，连接建立完成。

**为什么要三次握手？**

避免资源被浪费掉。 比如客户端第一次往服务端发送SYN,但是由于网络原因,服务端一直没收到.等待一定时间后,还未收到服务端的回执,客户端会重新发送第二次请求,第二次请求正常收到,完成任务后连接结束.此时,若第一次请求到达了服务端,如果只是两次握手,服务端便会建立连接,浪费资源.如果是三次握手,服务端会等待客户端的回执,但是客户端此时已经关闭,所以服务端不会建立连接

#### 四次挥手

![1567951292378](/d:/resource/notePic/1567951292378.png)

**第一次挥手**：客户端发送FIN=1，seq=x的包给服务端，表示自己没有数据要进行传输，单面连接传输要关闭。发送完后，客户端进入 FIN_WAIT_1 状态。

**第二次挥手**：服务端收到请求包后，发回ACK=1,ack=x+1的确认包，表示确认断开连接。服务端进`入CLOSE_WAIT`状态。客户端收到该包后，进入`FIN_WAIT_2`状态。此时客户端到服务端的数据连接已断开。

**第三次挥手**：服务端发送FIN=1,seq=y的包给客户端，表示自己没有数据要给客户端了。发送完后进入`LAST_ACK`状态，等待客户端的确认包。

**第四次挥手**：客户端收到请求包后，发送ACK=1,ack=y+1的确认包给服务端，并进入`TIME_WAIT`状态，有可能要重传确认包。服务端收到确认包后，进入`CLOSED`状态，服务端到客户端的连接已断开。客户端等到一段时间后也会进入`CLOSED`状态。

**四次挥手原因** 由于TCP的连接是全双工，双方都可以主动传输数据，一方的断开需要告知对方，让对方可以相关操作，负责任的表现。

##### 客户端突然出故障,连接怎么关闭?

服务端会为每个连接创建一个保活计时器,每当接收到客户端的请求,保活计时器都会重新开始计时;当超过默认时间(2小时)没有接收到请求,服务端就会每隔75秒,往客户端发送一次查询请求,连续十次,如果一直没有收到回执,服务端就会关闭连接.



#### TimeWait

主动发送Close信号的一方，必须进入TimeWait状态，进入TIME_WAIT状态的TCP连接需要经过2MSL才能回到初始状态，其中，MSL是指Max Segment Lifetime，即数据包在网络中的最大生存时间。有以下两个原因：

- 为了确保旧的数据包失效。若没有TimeWait，假设很快建立了新的TCP连接，旧的数据包到达时也会被传递到应用层，导致错误发生
- 主动请求关闭的一方可能发送的ACK可能会丢失，这样被动关闭方为了确认关闭要重发FIN数据包，等待2MSL可以增大被动关闭方重发FIN到达主动关闭方的概率，并且主动关闭方可以再次重发ACK，增大TCP连接正常关闭的概率

**服务器出现大量的TimeWait可能是什么原因，如何解决？**

TimeWait是TCP可靠通信机制的保障之一，系统大量出现TimeWait可能原因是服务器短时间内发送了大量的短连接请求(如爬虫服务器)，并且没有迅速回收socket资源。可以通过调整一些参数来加快资源回收的时间

**服务器出现大量的CloseWait可能是什么原因，如何解决？**

CloseWait是被动关闭方在收到主动方关闭请求后进入的状态，若此后被动方没有发送FIN，就会一直停留在CloseWait状态至超时，这一般是由于程序中没有主动关闭连接造成的影响。比如爬虫服务器A往B发送请求，爬某个资源，若B中没有此资源，可能会主动请求关闭连接，此时A中若没有释放连接，A就会处在CloseWait状态

### TCP的慢启动与拥塞避免

[参考](https://www.cnblogs.com/qxxnxxFight/p/11138576.html)

cwnd：拥塞窗口；rwnd：接收窗口

#### 慢启动

当新建连接时，cwnd初始化为1个最大报文段(MSS)大小，发送端开始按照拥塞窗口大小发送数据，每当有一个cwnd大小的报文段被确认，cwnd就增加1个MSS大小。这样cwnd的值就随着网络往返时间(Round Trip Time,RTT)呈指数级增长，事实上，慢启动的速度一点也不慢，只是它的起点比较低一点而已。

#### 拥塞避免

cwnd不能一直指数变大，TCP中有一个慢启动门限(ssthresh)来限制它，当cwnd大小超过了慢启动门限，cwnd就进入拥塞避免阶段，此时cwnd不再是指数增加，而是步长为1的加法增大，慢慢增加到网络的最佳值。

#### 拥塞检测

TCP认为网络拥塞的主要依据是它超时重传了一个报文段，当发生超时时，那么出现拥塞的可能性就很大。此时TCP反应比较“强烈”：

1.把ssthresh降低为cwnd值的一半

2.把cwnd重新设置为1

3.重新进入慢启动过程。

![1568684174574](/d:/resource/notePic/1568684174574.png)



### TCP滑动窗口

参考: https://www.jianshu.com/p/a1f6bde61053

TCP 最基本的传输可靠性来源于确认重传机制，TCP 的滑动窗口机制也是建立在确认重传基础上的。

#### 发送窗口

![1565595205544](/d:/resource/notePic/1565595205544.png)

它分成四个部分, 2和3两部分就是发送窗口的大小.

1. 得到服务器确认且已经发送的
2. 还没得到服务器确认但已经发送的
3. 未发送但服务器允许发送的
4. 未发送且因为达到了 window 的大小不允许发送的数据

在等待2区间的ACK时, 3区间的包会依次发送, **提高了吞吐量**. 同时, 如果32一直等不到ACK就会超时重传, **保证了包不丢失**.  32在未接受到ACK时, 33是不可能接收到ACK的, **包的顺序性**是由接收窗口保证的.

发送窗口只有在接收到2区间最左侧的ACK信息后, 才会往右滑动. 比如32收到了ACK, 窗口范围就变成了33~52, 52进入了待发送区.

#### 接收窗口

![1565595639844](/d:/resource/notePic/1565595639844.png)

它分成三部分:

1. 已接收并且已经发送 ACK 回执的数据
2. 未接收但可以接收状态 - 接收窗口 滑动方式一致
3. 未接收且不能接收的状态 - 达到窗口阈值

因为ACK 直接由 TCP 回复，默认没有应用延迟，因此接收窗口不存在已接收未回复 ACK 的状态. 

在服务器32未接收到的情况下, 即使接收到了33, 服务器也会丢掉, 即服务器每一个接收到的包都必须是接收窗口最左侧的包, 不然会丢掉继续等待. 接收窗口的这个机制**保证了包接收的顺序性**

#### 窗口的流量控制

网络传输时, 发送方和接收方都是存在缓冲区的, 缓冲区的存在是为了应用程序更平滑的发送和收取数据.

假设接收方应用程序高负载, 每次从缓冲区取出的数据有限, 那么缓冲区中就会堆积很多未取的数据, 用来接收发送方数据的空间自然很少, 此时发送方若还是保持高速发送, 比如每次发送100份, 但是接收方每次只能存20份, 剩余的80份需要下次重发, 这样会造成不必要的网络浪费. 

滑动窗口能解决这个问题. 接收窗口每次会根据实际接收情况, 返回本次网络传输的接收数据量, 发送窗口收到这个反馈后就可以动态调整发送窗口的大小, 达到了流量控制的目的.

#### 总结

滑动窗口分为发送窗口和接收窗口, 根据滑动窗口+重传机制可以保证在**提高吞吐量的同时, 数据包不会丢失和乱序**. 并且窗口的大小是动态变化的, 进而达到**流量控制**的目的.



### 浏览器打开url的过程

````
1.DNS解析:根据域名解析ip地址
2.TCP连接:与服务器建立TCP连接(也会使用网络层的IP协议)
3.发送HTTP请求:HTTP协议根据url生成HTTP请求报文;TCP协议将请求报文分成报文段,在IP协议的"协助"下发送给服务器
4.服务器处理请求,并返回HTTP报文:TCP协议接收报文段,将其按照HTTP协议封装成HTTP报文
5.浏览器解析渲染页面:浏览器根据得到的HTTP报文,生成页面
6.结束TCP连接
````

````
// 打开url过程中,各种协议的作用
DNS:作用在应用层,解析域名
HTTP:作用在应用层,负责将请求组成HTTP报文,返回结果也按照HTTP协议封装成报文
TCP:作用在传输层,将HTTP报文分段发送,或接收分段报文组装成HTTP报文; 并且建立连接
IP:TCP建立连接时,需要网络层的IP协议协助
	IP协议在网络层传输时,又会和路由器打交道,路由器在数据链路层和物理层又会使用一些协议
````

DNS解析过程：检查浏览器DNS缓存 => 检查操作系统DNS缓存 => 检查host文件 => 通过UDP协议发送域名解析请求，运营商将一级级查找并返回



### Http-Headers

以下为常见的Headers内容，完整内容参考[1](https://segmentfault.com/a/1190000018234763#articleHeader4) 或 [2](https://itbilu.com/other/relate/EJ3fKUwUx.html)

HTTP请求组成：请求行(请求方法/url/http版本) / 请求头(header) / 请求体

HTTP响应组成：状态行(状态码/http版本) / 响应头(header) / 响应体

#### Request Headers

```
Accept：客户端能够处理的内容
Accept-XXX：Charset / Encoding / Language 客户端能够处理的字符集/编码方式/自然语言
Cookie：通过Set-Cookie设置的值
from：用户的电子邮箱地址
host：请求资源所在的服务器名称
range：只请求资源的某个部分，断点续传需要
User-Agent：浏览器信息
```

#### Response Headers

```
Connection：值为keep-alive时，客户端通知服务器返回本次请求结果后保持连接；为close时表示不需要长连接
Set-Cookie：服务器端向客户端发送 cookie
Location：令客户端重定向至指定 URI
Public-Key-Pins：Web 服务器用来进行加密的 public key （公钥）信息
Age：消息对象在缓存代理中存贮的时长，以秒为单位
Clear-Site-Data：表示清除当前请求网站有关的浏览器数据
```



### Http状态码

| **状态码** | **类别**                         | **含义**                   |
| ---------- | -------------------------------- | -------------------------- |
| 1XX        | Informational（信息性状态码）    | 接收的请求正在处理         |
| 2XX        | Success（成功状态码）            | 请求正常处理完毕           |
| 3XX        | Redirection（重定向状态码）      | 需要进行附加操作以完成请求 |
| 4XX        | Client Error（客户端错误状态码） | 服务器无法处理请求         |
| 5XX        | Server Error（服务器错误状态码） | 服务器处理请求出错         |

#### 常见的状态码

- 200 OK
- 301 Permanently(永久重定向) / 302 Found(临时重定向)
- 400 Bad Request / 403 Forbidden / 404 Not Found
- 500 Internet Server Error 



### HTTP/2 HTTP/1.1 HTTP/1.0 的区别

首先最大的区别是1.1引入了**长连接**，1.1中默认开启`Connection = keep-alive`，即TCP连接不关闭，可以被多个请求复用。客户端在一次TCP连接时，可以发送多个请求(不用先发送的请求响应)，但是服务器仍需要按照请求顺序发送响应，所以会有**”队头阻塞“**的问题：即先发送的请求如果处理特别慢，会影响后续的请求。

还有一些小的区别：

- 1.1请求头加入了 **range** 字段，表示只请求资源的某个部分，为**断点续传**提供了支持
- 1.1请求头加入了 **host** 字段，为**虚拟主机**提供了支持。即一台物理主机上的虚拟主机共享一个ip地址，但是它们可以有不同的host，并且提供不同的服务。

http2 主要有两点优势：

- 支持多路复用：其对数据包进行二进制分帧，使得多个请求内容可以使用一个http2连接发送，解决了http1.1的队头阻塞效应
- 头部压缩：节约传输流量



### http 与 https的区别

[http于https的区别](https://blog.csdn.net/xiaoming100001/article/details/81109617)

> HTTPS是身披SSL外壳的HTTP, 其使用HTTP进行通信，使用SSL/TLS建立全信道，加密数据包。HTTPS使用的主要目的是提供对网站服务器的身份认证，同时保护交换数据的隐私与完整性。

https的加密/解密过程,解决了http的三个问题:

1. 不判断对方身份 => 通过证书,客户端确认自己访问的服务器正确
2. 传输明文,可以被直接获取信息 => 传输的都是加密的文件
3. 不能验证数据的完整性,无法判断数据是否被修改 => 通过数字签名技术,对比两次加密解密后的结果是否一致判断

**https的实现原理**

![https的实现原理](/d:/resource/notePic/5b503d10e4b0edb750e0d4f8.png)

理解上图，首先要知道这里有三对密钥：

- CA 机构的公钥1和私钥1：CA 颁发的所有证书都会用私钥加密后，发送给服务器
- CA 机构给服务器的公钥2和私钥2：服务器收到的证书是用私钥1加密的，证书内容就是公钥2和服务器的一些信息；私钥2则保存在服务器中
- 客户端与服务器通信的对称密钥：客户端拿到证书后，用公钥1解密拿到了公钥2，然后随机生成一个字符串，这个字符串的内容就是通信的对称密钥，客户端会使用公钥2将对称密钥加密后，发送给服务端，服务端用私钥2解密后得到对称密钥

通过上面的步骤，客户端和服务端拥有了一个不能被中途破解的对称密钥，它们用这个密钥可以保证通信安全

**服务器证书被掉包了怎么办?**

服务器证书包含服务器认证信息和CA颁发给服务器的公钥, 证书本身是密文形式, 其是CA私钥加密的. 证书的解析是公开的, 但是解析后, 想篡改内容掉包是不行的, 因为篡改后, 需要再加密成密文的形式发送, 但是私钥只有CA才有, 所以即使掉包篡改了证书, 客户端接收时也会出现解析异常.

服务器证书的发送应该是由CA负责, 而不是由服务器本身负责, 不然攻击人服务器就可以拿安全的证书发送给访问它们网站的客户端.

**https的混合加密**

对称加密指的是加密和解密使用相同的密钥, 非对称加密指的是, 加密和解密使用不同的密钥(公钥和私钥). 对称加密更快, 非对称加密更安全, https结合了对称加密和非对称加密.

对于 CA 机构颁发给服务器的公钥和私钥，私钥是在服务端保存，公钥在客户端保存，所以双方需要发送加密数据时，用的密钥是不一样的：

- 客户端给服务端发加密的数据：一般用于通信传输数据，是公钥加密信息, 私钥解密信息
- 服务端给客户端发送加密的数据：一般用于数字签名验证身份，私钥加密信息, 公钥解密信息

![1553908042067](/d:/resource/notePic/1553908042067.png)

如上图, 发送方和接收方互有对方的公钥, 发送方往接收方发送了三份数据, 第一份是将原文hash后再经发送方私钥加密的数字签名, 第二份是原文经过对称密钥加密后的密文, 第三份是将对称密钥用接收方公钥加密后的对称密钥密文. 为了验证发送的数据未经中间人修改, 需要将第一份和第二份信息还原, 进行比较. 第一份信息使用发送方的公钥解密, 第二份信息需要用对称密钥解密, 所以首先要用接受方的私钥将第三份数据还原成对称密钥, 然后拿去解密第二份数据, 第二份数据再hash后和第一份数据进行比较, 若一致, 则表明未经过更改, 不一致则表明被中间人更改.

**混合加密的好处**

1. 原文一般很大, 用非对称加密耗时, 所以使用对原文hash后的数字摘要进行非对称加密可以节省时间.
2. 对称密钥加密/解密原文很快, 但是对称密钥的传输是不安全的, 所以使用非对称加密将对称密钥加密后再传输, 保证了安全性

#### MD5 是加密算法吗？

MD5 是一种哈希散列算法，它可以把任意长度的文件，哈希到 128 位的数上（大致原理是分组处理，再综合处理）。碰撞的概率是 `1/(2^128)`，即碰撞可能性非常小。

但是哈希算法在哈希过程中是会丢失信息的，所以无法通过任何方式将 MD5 值还原成原信息。即 MD5 不属于加密或解密算法。

MD5 的常用场景：

- 密码存储
- 文件上传/下载完整性比较
- 存证或版权（可能要配合时间）

但是[MD5 被破解](https://zhuanlan.zhihu.com/p/131283811)后，MD5 用于密码存储就不再安全了，因为哈希碰撞如果是有规律可选的，那么拿到 MD5 值就等效于破解密码了。



### http 与 rpc 有什么区别

RPC主要用于公司内部的服务调用，性能消耗低，传输效率高，服务治理方便。HTTP主要用于对外的异构环境，浏览器接口调用，APP接口调用，第三方接口调用等。

why？http基于HTTP协议，rpc可以自定义协议；http1.1报文中有很多无效字段，且大多是基于json来序列化和反序列化，而rpc可以自定义消息格式以及序列化工具



### ipv4和port的个数

ipv4地址是4个字节，一共有40亿多个(2^32^，int占4字节，但是有一个符号位，所以最大为20亿左右)，port占2个字节，一共有65536个(2 ^16^)



### 负载均衡

#### 四层负载均衡和七层负载均衡

四层负载均衡（第四层：传输层）工作在OSI模型的传输层，主要工作是转发，它在接收到客户端的流量以后通过**修改数据包的地址信息**将流量转发到应用服务器。

![1565599679677](/d:/resource/notePic/1565599679677.png)

七层负载均衡（第七层：应用层）工作在OSI模型的应用层，因为它需要解析应用层流量，所以七层负载均衡在接到客户端的流量以后，还需要一个完整的TCP/IP协议栈。七层负载均衡会与客户端建立一条完整的连接并将应用层的请求流量解析出来，再按照调度算法选择一个应用服务器，并与应用服务器建立另外一条连接将请求发送过去，因此七层负载均衡的主要工作就是代理。

四层负载均衡实现比较简单, 七层负载均衡比较智能化, 可以根据请求内容, 进行自定义策略转发.

#### 负载均衡算法

随机  轮询 / 权值轮询 / Hash / 一致性Hash(方便集群的伸缩) / 健康度

一致性哈希是针对分布式系统而言的，假如一个请求经过 hash 算法打到 a 服务器上，那么无论服务器所在的集群如何伸缩，我们都希望下次相同的请求仍能最大概率的打到 a 服务器上。

参考：https://juejin.cn/post/6844903598694858766

#### 反向代理

传统代理服务器位于浏览器一端，代理浏览器将HTTP请求发送到互联网上。而反向代理服务器则位于网站机房一侧，代理网站web服务器接收http请求。

利用反向代理可以**实现负载均衡**. 同时反向代理还能起到 **保护网站安全** 和 **加速web请求** 的作用.

所有互联网的请求都必须经过代理服务器，相当于在web服务器和可能的网络攻击之间建立了一个屏障。当用户第一次访问静态内容的时候，静态内存就被缓存在反向代理服务器上，这样当其他用户访问该静态内容时，就可以直接从反向代理服务器返回，加速web请求响应速度，减轻web服务器负载压力。

**反向代理和正向代理的区别**

[正向代理与反向代理](https://juejin.cn/post/6844903651966713863)

正向代理（可以想一下 VPN 上网的例子，就是代理本地访问国外资源）代理的是客户端，为客户端收发请求，使真实客户端对服务器不可见

反向代理代理的则是服务端，为服务器收发请求，使真实服务器对客户端不可见



## 操作系统

### 进程、线程与协程

- 从**资源**上看，进程是系统分配资源的基本单位，线程并不拥有资源，但是它可以访问隶属进程的资源
- 从**执行任务**上看，线程是执行任务的基本单位
- 从**系统开销**上看，进程在创建/销毁时要分配或回收资源，如IO和内存设备等，因此其开销比创建/销毁线程大得多。另外进程切换时由于需要保存和设置CPU的状态，而线程切换时只需要保存少量寄存器内容，因此进程切换开销也比线程大
- 从**通信方式**上看，进程必须通过IPC通信，而线程可以通过进程间的共享内存进行通信

**浏览器多标签页采用多进程还是多线程**

google chrome采用的是多进程，其主要是从安全性和健壮性两方面考虑。多进程之间的内存和资源相互隔离，避免获取无权限的数据；同时，也避免某个标签页崩溃时影响其它标签页。 当前多进程在进程创建/销毁/切换比多线程更耗费时间和资源。

#### 什么是协程？

协程引入了挂起的概念，让函数可以在执行异步任务的时候挂起，但并不阻塞当前线程，在任务执行完后。能唤醒挂起的协程，继续执行协程作用域内的逻辑。让我们可以用同步的方式写异步代码

> 函数的调用总是一个入口，一次return，调用顺序是明确的。而协程的不同之处就在于，执行过程中函数内部是可中断的，也就是说中断之后，可以转而执行别的函数，在合适的时机再return回来继续执行没有执行完的内容。
>
> 而这种中断，叫做挂起。挂起我们当前的函数，再某个合适的时机，才反过来继续执行~这里我们再想想回调：注册一个回调函数，在合适的时机执行这个回调。
>
> - 回调采用的是一种**异步**的形式
> - 而协程则是**同步**

[参考](https://juejin.cn/post/6844903837875060750)



### 进程间的通信方式

[参考](https://www.jianshu.com/p/c1015f5ffa74)

#### 进程通信IPC概念

每个进程各自有不同的用户地址空间，任何一个进程的全局变量在另一个进程中都看不到，所以进程之间要交换数据必须通过内核，在内核中开辟一块缓冲区，进程1把数据从用户空间拷到内核缓冲区，进程2再从内核缓冲区把数据读走，内核提供的这种机制称为**进程间通信（IPC，InterProcess Communication）**

![1567478417959](/d:/resource/notePic/1567478417959.png)

#### 进程间通信方式

##### 管道

管道本质是一个队列(FIFO，不支持随机定位)，进程可以对管道进行读/写操作，管道通信的两个进程必须“同时在线”，当缺少一方时，匿名管道的另一方直接退出，有名管道的另一方会阻塞。

**匿名管道和有名管道的区别：**

- 匿名管道是半双工的，即管道的每一端都只支持读或写的一种操作，有名管道是双工的
- 匿名管道只支持具有亲缘关系的进程通信，有名管道无此限制
- 匿名管道存在内存中，有名管道存在文件系统中

##### 消息队列

消息队列是存放在内核中的消息链表，每个消息队列由消息队列标识符表示。它只有在内核重启(操作系统重启)时才会被删除，它与管道有以下区别：

- 存储在内核空间中
- 工作模式不是双工，可以支持多个进程对同一个消息队列进行读取；在写或读消息时，不需要另一个进程也保持读写(不用双方”同时在线“)
- 支持FIFO，也支持消息的随机定位

##### 信号

信号是Linux系统中用于进程间互相通信或者操作的一种机制，信号可以在任何时候发给某一进程，而无需知道该进程的状态。信号主要有两个来源：

- 硬件来源：用户按键输入`Ctrl+C`退出、硬件异常如无效的存储访问等。
- 软件终止：终止进程信号、其他进程调用kill函数、软件异常产生信号。

**信号的生命周期与处理流程**

1. 信号被某个进程产生，并设置此信号传递的对象（一般为对应进程的pid），然后传递给操作系统；
2. 操作系统根据接收进程的设置（是否阻塞）而选择性的发送给接收者，如果接收者阻塞该信号（且该信号是可以阻塞的），操作系统将暂时保留该信号，而不传递，直到该进程解除了对此信号的阻塞（如果对应进程已经退出，则丢弃此信号），如果对应进程没有阻塞，操作系统将传递此信号。
3. 目的进程接收到此信号后，将根据当前进程对此信号设置的预处理方式，暂时终止当前代码的执行，保护上下文（主要包括临时寄存器数据，当前程序位置以及当前CPU的状态）、转而执行中断服务程序，执行完成后在回复到中断的位置。当然，对于抢占式内核，在中断返回时还将引发新的调度。

##### 共享内存

为了在多个进程间高效交换信息，内核空间留有一部分空间，可以让共享内存映射，这样通过对共享内存的读写，可以实现进程间的通信，避免了内核与内存之间的拷贝。类似于进程中的线程享有一部分公共内存，使用共享内存时需要保证同步。进程间的同步使用信号量(计数器)来保证

![1567479934863](/d:/resource/notePic/1567479934863.png)

##### 套接字

套接字是支持TCP/IP的网络通信的基本操作单元，可以看做是不同主机之间的进程进行双向通信的端点，简单的说就是通信的两方的一种约定，用套接字中的相关函数来完成通信过程。

套接字使用时需要指定：域  端口  通信协议

端口号的范围是0 ~ 65535，低于256的端口号保留给操作系统使用

套接字协议类型分为：流套接字(TCP/IP)， 数据报套接字(UDP)， 原始套接字(允许对较低层次的协议直接访问，比如IP、 ICMP协议，它常用于检验新的协议实现)



### 线程的同步方式

1. 临界区

   当多个线程访问一个独占性共享资源时，可以使用临界区对象。拥有临界区的线程可以访问被保护起来的资源或代码段，其他线程若想访问，则被挂起，直到拥有临界区的线程放弃临界区为止。

2. 事件

   事件机制，则允许一个线程在处理完一个任务后，主动唤醒另外一个线程执行任务。比如在某些网络应用程序中，一个线程如A负责侦听通信端口，另外一个线程B负责更新用户数据，利用事件机制，则线程A可以通知线程B何时更新用户数据。

3. 互斥量

   互斥对象和临界区对象非常相似，只是互斥对象还允许在进程间使用，而临界区只限制与同一进程的各个线程之间使用

4. 信号量

   当需要一个计数器来限制可以使用某共享资源的线程数目时，可以使用“信号量”对象。

   

### select / epoll区别

通俗的说，可以认为select是基于轮询的，而epoll是基于回调的。

![1566319300805](/d:/resource/notePic/1566319300805.png)

调用select时，系统会将fd_set(文件句柄列表)从用户空间拷贝到内核空间，并遍历该列表，将其挂上等待进程。然后select线程开始间隙睡眠/工作，工作的时候就是**遍历该列表**(所以说是轮询)，观察有没有socket已经准备好，若有则唤醒进程并将准备好的socket拷贝到用户空间。

![1566319505657](/d:/resource/notePic/1566319505657.png)

调用epoll时，内核空间会用一颗**红黑树**去存储文件句柄的封装对象，并为它们创建回调函数，如果socket准备好，就会将socket移动到**双向链表的就绪链表**中。因此epoll线程也是睡觉/工作，但是它工作的时候**只观察就绪链表**中是否有数据，若有则拷贝到用户空间，并唤醒进程。

所以epoll对资源的消耗比select小很多，也正是因为这个原因，select默认socket数量为1024，不期望太大的轮询。



### 如何理解操作系统的虚拟内存

虚拟内存的存在使得操作系统在已有内存下，可以运行更多的程序。内存管理分为逻辑内存和物理内存，逻辑内存可以认为是虚拟内存，它能满足应用程序需要的内存，但是它真实映射的物理内存一般比其表示的虚拟内存小。逻辑内存中会分成若干页，运用一定的调度算法将应用程序需要加载的内容分在若干页中，页在使用时会真实加载到物理内存中，不使用时实际可能存在磁盘中。



### 操作系统中锁的实现原理

不同的语言都提供了各自的锁机制，其实都源于操作系统提供的锁操作，而锁本质其实就是内存中的某个变量，它的值代表了锁的状态，如1代表已有线程获取锁，为0代表锁处于可取状态。竞争锁可以分为以下几步：

- 获取锁变量值
- 判断锁变量值是否为可获取状态
- 若是则修改锁变量值，返回true； 反之，返回false

但是**中断**和**多核**会使得多线程获取锁时，出现同时能获取锁的状态，这是不允许的。因此操作系统将上述多个原子操作的步骤合并成一个原子操作，避免了异常状态。合并原子操作是通过总线机制保证的，CPU中有一个HLOCK Pin，可以通过发送指令指令(`test and set`)来控制它的电位，当其为低电位时，总线上最多只允许一个核访问内存，因此在执行多原子操作时，一直将HLOCK Pin保持为低电位，即可得到合并的原子操作。



### 文件系统

````
Linux中所有被操作系统管理的资源,都被看作是文件.
	普通文件
	目录文件
	链接文件:(快捷方式)
	设备文件:用来访问硬件设备(驱动)
	命名通道:Linux下进程间的通信是使用该文件
````



### Linux目录结构

![1553050772101](/d:/resource/notePic/1553050772101.png)

````
root:根目录
bin:二进制可执行命令文件
etc:配置文件
home:所有用户文件夹,如: /home/jack/
usr:存放系统应用程序
lib:和系统运行相关的库文件
````

![1553050884030](/d:/resource/notePic/1553050884030.png)



### 常见Linux命令

目录切换命令

```
cd usr: 切换到当前目录下的usr目录中
cd .. : 切换到上一层目录
cd /  : 切换到根目录
cd ~  : 切换到用户主目录
cd -  : 切换到上一个所在的目录
```

目录操作命令(CURD)

````
mkdir usr: 创建usr目录
rm -rf /aa : 递归且不确认的删除目录及文件 
mv aa.txt bb.txt : 重命名 或 移动文件
cp -r /aa /bb/aa : 递归的复制文件
ll : 展示当前目录下所有文件的详细信息
````

文件操作命令

```
touch a.txt : 创建文件、更新文件的时间戳
cat/more/less/tail-10 : 查看文件
	cat: 只能看最后一屏
	more/less: 可以翻页,q结束查看
	tail-10: 动态查看文件最后10行的变化
vim a.txt : 编辑文件
	Esc进入最后一行: wq保存并退出 !q:不保存强制退出
```

压缩/解压缩文件

````
tar -zcvf a.tar.gz /aa : 打包文件
	z,c: 打包
	v: 显示打包过程
	f: 指定文件名

tar -xvf a.tar.gz -C /bb : 解压文件
	x: 解压
	-C: 指定解压位置
````

其他常用命令

````
ifconfig: 查看系统网卡信息
pwd: 显示当前路径
grep java /aa -color: 查找,一般配合管道
ps -ef: 查看系统正在运行的所有进程,一般配合管道| 和 grep
	ps -ef | grep java: 查看所有java进程
kill -9 进程id: 强制杀死进程
````



## Web

#### Tomcat是什么

[tomcat架构设计](https://note.youdao.com/ynoteshare1/index.html?id=7fa73b78eb9d3e330e86517f238b6cd8&type=note)

tomcat就是一个web服务器,能够让客户端访问请求自己的资源.同时它还是servlet和jsp容器,用来处理响应和回馈资源.

![1553908403488](/d:/resource/notePic/1553908403488.png)



#### tomcat为什么要违背双亲委派机制？

[参考](https://juejin.im/post/5a59f2296fb9a01ca871eb8c#heading-6)

因为tomcat作为一个web服务器，它可以同时部署多个web服务，多个web服务可能会使用同一个jar包的不同版本，双亲委派机制类加载时往父类加载器传送的请求参数只包含类的全路径名，不包含版本参数。因此若使用双亲委派机制，多个web服务的不同版本就不能实现，每个web服务都有一个web类加载器，加载某些类时不请求父类。如何破坏双亲委派？使用线程上下文加载器，可以让父类加载器请求子类加载器去完成类加载的动作



#### tomcat如何实现jsp的热部署?

每个jsp文件都有一个类加载器，当jsp文件修改时，就会将这个jsp的类加载器卸载，因此下一次使用jsp时就需要重新加载

#### get和post的区别

[get和post的区别](https://note.youdao.com/ynoteshare1/index.html?id=b5b5ffe68d52f753d46580131da62751&type=note) 

[http的8种请求类型介绍](https://www.cnblogs.com/liangxiaofeng/p/5798607.html)

由于http协议是基于tcp/ip协议的, 所以get和post本质上来说都是tcp链接, 但是为了表示请求对资源的操作方式, http协议规定了get/post等请求方式, 导致了它们有一些不同:

1. get请求参数包含在url中, post请求参数包含在请求体中, 因此post的请求参数相对来说更安全
2. get请求参数大小有限制, 一般为2KB, 并且只接收ASCII码, 而post则没有这个限制
3. get请求在浏览器中回退是无害的, 而post会再次提交请求
4. 某些浏览器, 对于get请求只会发送一次数据, 对于post请求会发送两次数据（对于 CORS 跨域访问，第一次是预检请求，查询是否支持跨域，第二次才是真正的post提交）, 因此get请求可能会比post更快. 但是它们的应用场景不一样, 所以一般也不会因为这个原因将post请求变成get请求



#### session cookie 和 token

**session**可以认为是服务端的会话技术, 用户第一次登录网站时, 网站会将用户的登录信息及状态信息等存入一个session中, 并将sessionId传会给客户端, 存在**cookie**中, 下次用户访问时, 就会将带有sessionId的cookie发送给服务端, 服务端解析得到sessionId, 再根据其获取session, 即可恢复用户登录状态. session的过期时间一般为30min

但是session+cookie的会话技术存在**CSRF**攻击的风险, 当用户登录正规网页后, 若再访问黑客网页, 这个网页发送一个POST请求, 由于携带了用户的cookie, 所以正规网站会认为是正常用户, 而造成损失. 这是因为cookie支持跨域访问（最新的 chrome 会默认给未设置 samesite 属性的 cookie 设置为 lax，不允许跨域名，如果为 none 则可以）

**token**是为了解决CSRF而生的一种令牌机制. 用户第一次登录网站时, 服务端会为用户生成唯一的token(格式:uuid+时间+签名+特定字段)并加密发送给客户端, 客户端存储在localStorage中. 下次访问时, 会从指定的localStorage中查找token携带到服务端, 服务端解密token验证身份. token过期时间一般为7天

可以看到token与session十分相似, 但是存在以下区别:

- token是加密的, session没有
- token存储在客户端, 服务端每次收发都要加密解密, 省去了服务端存储, 但是增加了时间
- token的携带, 是需要网站开发者实现的(所以不支持跨域). 而cookie是自动携带的, 支持跨域. 因此token能够阻止CSRF攻击.



#### 分布式session解决方案

参考: https://blog.51cto.com/zhibeiwang/1965018

1. 会话保持: 通过控制负载均衡策略, 使同一客户端的请求被转发到同一台服务器上, 比如nginx使用ip_hash负载均衡策略. 但是这种方案会影响负载均衡, 并且如果服务器宕机, session还是会丢失, 容灾能力差.
2. 会话复制: tomcat支持会话复制, 其是基于IP组播完成会话复制. 即当服务器的session复制到集群中指定的其他服务器中. 但是这种方案加大了服务器的负担和网络带宽的消耗. 并且不利于扩展, 当扩展一个新服务器时, 需要复制很多数据.
3. 会话共享: 使用redis作为分布式session的存储方案. redis本身就是一个集群, 具有高并发和高可用的特性, 因此相对来说是更理想的分布式session方案



#### 有哪些常见限流算法，它们的区别是什么

常见的有令牌桶算法和漏桶算法。

令牌桶算法是说系统会以一定的速率往桶内产生令牌，当请求过来时，如果能从桶内成功拿走一块令牌，则该请求正常放行否则被限流。

漏桶算法是说系统会用一定容积的桶去承载请求，桶内的请求以一定速率放行，桶满时新请求会被限流。

它们的区别是:

- 令牌桶算法中桶的容积是考虑系统能支撑的瞬时流量，它适用于应对短暂出现的流量高峰
- 漏桶算法中桶的容积是综合考虑服务的延时与系统能支撑的持续流量，它适用于持续的大流量控制




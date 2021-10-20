## HashMap源码分析_简版

### Java7 HashMap

![1](d:/resource/notePic/1.png)

大方向上，HashMap 里面是一个**数组**，然后数组中每个元素是一个**单向链表**。

上图中，每个绿色的实体是嵌套类 Entry 的实例，Entry 包含四个属性：key, value, hash 值和用于单向链表的 next。

capacity：当前数组容量，始终保持 2^n，可以扩容，扩容后数组大小为当前的 2 倍。

loadFactor：负载因子，默认为 0.75。

threshold：扩容的阈值，等于 capacity * loadFactor

#### put操作

```java
// return值是添加key的旧value,若第一次添加此key,return null
public V put(K key, V value) {
    if (第一次添加元素) {
        数组初始化;
    }
    if (null == key) {
        键值对存放到数组的0位置;
        return 旧值;
    }
    // 求key的hash值
    hash(key); 
    // 根据hash值找到存放的数组位置,遍历链表若key已存在则覆盖,反之添加
    add(键值对); 
    return value旧值;
}
```

**数组初始化**

根据构造参数确定初始的数组大小和数组扩容阈值(数组大小 * loadFactor)

数组大小一定是 2 的 n 次方: new HashMap(20)，那么处理成初始数组大小是 32. 

**确定key的数组位置**

hash(key) % length : 保证length是2的n次方, 取模操作就变成了位运算

**添加节点到链表中**

检查size是否达到了阈值, 若达到了阈值并且新插入的元素, hash得到的数组位置已经有元素了, 则先扩容, 扩容后重新利用hash判断元素在数组的存放位置, 再插值.

**数组扩容**

扩容后, 数组大小为原来的2倍.

由于是双倍扩容，迁移过程中，会将原来 table[i] 中的链表的所有节点，**分拆**到新的数组的 newTable[i] 和 newTable[i + oldLength] 位置上。如原来数组长度是 16，那么扩容后，原来 table[2] 处的链表中的所有元素会被分配到新数组中 newTable[2] 或 newTable[18] 这两个位置中, 并且保持相对顺序不变.

至于table[2]中原链表的所有数据是会被分配在newTable[2] 还是 newTable[18] 中, 使用`e.hash & oldCap == 0`判断,  若是, 则表明hash值在oldCap的二进制位上是0, 那么重新hash分配时它仍在table[2]上(因为此时hash值对2*oldCap取余后不可能大于oldCap), 反之, 它在 newTable[18]上. 

注意这里hash值并没有重新计算, 因为节点在存储时key的hash值是存在了节点中的.  并且判断也是通过位运算进行, 非常高效!

```java
// 需要将table[2]中原链表拆成两个链表，原链表根据判断情况挂在loTail或hiTail中
// loHead即是最后newTable[2]存储值,hiHead即是最后newTable[18]存储值
Node<K,V> loHead = null, loTail = null;
Node<K,V> hiHead = null, hiTail = null;
```

#### get操作

```java
public V get(Object key) {
    if (null == key) {
        return 数组0位置的value;
    }
    hash(key);
    // 根据hash值找到数组下标
    // 遍历数组某位置的链表,找到key则返回value,反之return null
}
```



### Java7 ConcurrentHashMap

![3](d:/resource/notePic/3.png)

简单理解就是，ConcurrentHashMap 是一个 Segment 数组，Segment 通过继承 ReentrantLock 来进行加锁，所以每次需要加锁的操作锁住的是一个 segment，这样只要保证每个 Segment 是线程安全的，也就实现了全局的线程安全。

其中每个Segment 内部维护了一个类似HashMap的可扩容数组, 也是数据实际存放的地方.

#### 初始化

ConcurrentHashMap 的初始化参数最多有三个: initialCapacity / loadFactor / concurrencyLevel

**initialCapacity**：初始容量，这个值指的是整个 ConcurrentHashMap 的初始容量，实际操作的时候需要平均分给每个 Segment。

**loadFactor**：负载因子，之前我们说了，Segment 数组不可以扩容，所以这个负载因子是给每个 Segment 内部使用的。

**concurrencyLevel**：并行级别、并发数、Segment 数，怎么翻译不重要，理解它。默认是 16，也就是说 ConcurrentHashMap 有 16 个 Segments，所以理论上，这个时候，最多可以同时支持 16 个线程并发写，只要它们的操作分别分布在不同的 Segment 上。这个值可以在初始化的时候设置为其他值，但是一旦初始化以后，它是不可以扩容的。

用 new ConcurrentHashMap() 无参构造函数进行初始化，那么初始化完成后：

- Segment 数组长度为 16，不可以扩容
- Segment[i] 的默认大小为 2，负载因子是 0.75，得出初始阈值为 1.5，也就是以后插入第一个元素不会触发扩容，插入第二个会进行第一次扩容
- 这里初始化了 segment[0]，其他位置还是 null，至于为什么要初始化 segment[0]，是为了在后面初始化其他segment时, 使用segment[0]的capacity参数(capacity可能已经扩容了, 不一定还是initialCapacity)
- 当前 segmentShift 的值为 32 - 4 = 28，segmentMask 为 16 - 1 = 15，姑且把它们简单翻译为**移位数**和**掩码**，`(hash >> segmentShift) & segmentMask `通过位运算, 即可确定元素在segement内数组的存放位置

#### put操作

```java
public V put(K key, V value) {
    if (null == key) {
    	抛出异常;
        ConcurrentHashMap不支持key为null!
    } else if (null == value) {
        抛出空指针异常； // 也不支持value是null
    }
    hash(key);
    根据hash值找到s = segment[i];
    若segment[i]未初始化,则使用segement[0]参数初始化;
    // 根据hash值找到sgement[i]中数组存放当前元素的位置;
    return s.put(key, hash, value, false);
}
```

其中segment[i]的初始化过程 和 在segment[i]中put键值对时需要考虑并发的问题

**segment[i]的初始化**

很可能会有多个线程同时进来初始化同一个槽 segment[k]，不过只要有一个成功了就可以。

它对于并发操作使用 CAS 进行控制, 如果第一次CAS失败, 表明其他线程已经创建了segment[i], 那么直接返回已创建的segment[i]即可

**segment[i]中put键值对**

put之前, 需要获取segment[i]的独占锁, 首先自旋(循环)获取锁, 如果自旋次数超过MAX_SCAN_RETRIES, 进入lock()方法, 阻塞等待锁

**扩容rehash**

put 的时候，如果判断该值的插入会导致该 segment 的元素个数超过阈值，那么先进行扩容，再rehash, 再插值

该方法不需要考虑并发，因为到这里的时候，是持有该 segment 的独占锁的。

#### get操作

根据key的hash值确定segment, 再确定数组位置, 再遍历链表取value即可

ConcurrentHashMap中get操作并没有加锁和保证同步, 它的并发操作正确性是依赖put 和 remove操作中实现的

get 的时候在同一个 segment 中发生了 put 或 remove 操作如何保证并发安全呢?

**put 操作的线程安全性**

get如果先于put发生, 由于put是将新数据放在链表头, 所以get此时已不在链表头, 是天生线程安全的

get如果后于put发生, 那么要保证put的新头节点可见性, 这个依赖于 setEntryAt 方法中使用的 UNSAFE.putOrderedObject

如果get时正在扩容(迁移数据，最后将 newTable 设置给属性 table), 且也是put先发生, 那么 put 操作的可见性保证就是 table 使用了 volatile 关键字

**remove操作的线程安全性**

如果 remove 先破坏了一个节点，分两种情况考虑。 1、如果此节点是头结点，那么需要将头结点的 next 设置为数组该位置的元素，table 虽然使用了 volatile 修饰，但是 volatile 并不能提供数组内部操作的可见性保证，所以源码中使用了 UNSAFE 来操作数组，请看方法 setEntryAt。2、如果要删除的节点不是头结点，它会将要删除节点的后继节点接到前驱节点中，这里的并发保证就是 next 属性是 volatile 的。



### Java8 HashMap

![2](d:/resource/notePic/2.png)

Java7 中使用 Entry 来代表每个 HashMap 中的数据节点，Java8 中使用 **Node**，基本没有区别，都是 key，value，hash 和 next 这四个属性，不过，Node 只能用于链表的情况，红黑树的情况需要使用 **TreeNode**

#### put操作

```java
public V put(K key, V value) {
	if (第一次put操作) {
        // 扩容也是用resize()
        resize()进行数组初始化;
    }
    // 找到对应数组下标i
    hash(key);
    if (null == tab[i]) {
        tab[i] = e;
        return null;
    } else if (与数组中头节点key是不是相等) {
    	return 旧value;    
    } else if (是否是TreeNode红黑树节点类型) {
     	进行红黑树插入;   
    } else {
        // 表明是一个链表,首先判断是否要变形再进行数据插入
        // 变形判断:链表长度是否达到了阈值,若达到了阈值,则变成红黑树插入
        // 若链表中key已存在,则是覆盖旧值return 旧value
        // key不存在,Java8中新数据会插入到链表的末尾
    }
    if (size是否已经达到了阈值) {
     	// Java8中是先插值后检查扩容
        resize();   
    }
}
```

和Java7不同主要在于, 我们根据数组元素中，第一个节点数据类型是 Node 还是 TreeNode , 来判断该位置下是链表还是红黑树. 进而进行不同的插入操作.

还有两个细节不一样, Java8中新数据都是插到链表的末尾(Java7是开头). Java8是先插值后检查扩容, Java7是先检查扩容后插值

**数据扩容**

resize() 方法用于**初始化数组**或**数组扩容**，每次扩容后，容量为原来的 2 倍，并进行数据迁移。

扩容时, 链表数据迁移的过程是相似的, 仍然通过`hash & oldCap == 0`判断原数据挂在哪个节点. 红黑树的数据迁移判断依据是一样的, 只是红黑树和链表本身数据结构不一样.

#### get操作

1. 计算 key 的 hash 值，根据 hash 值找到对应数组下标: hash & (length-1)
2. 判断数组该位置处的元素是否刚好就是我们要找的，如果不是，走第三步
3. 判断该元素类型是否是 TreeNode，如果是，用红黑树的方法取数据，如果不是，走第四步
4. 遍历链表，直到找到相等(==或equals)的 key



### Java8 ConcurrentHashMap

![4](d:/resource/notePic/4.png)



#### put操作

```java
public V put(K key, V value) {
    if (第一次put操作) {
     	初始化数组;   
    }
    // 根据hash值找到数组位置tab[i]
    hash(key);
    if (null == tab[i]) {
        CAS操作放入新值;
    } else if (正在扩容) {
        比较复杂;
    } else {
		// 表明arr[i]位置有头节点,为头节点加锁
        synchronized (f) {
            if (是链表) {
                链表插入;
            } else {
                红黑树插入
            }
        }
    }
    // 插入完成判断是否需要变形或扩容
    if (节点链表长度达到了阈值) {
     	if (数组长度 < 64) {
            扩容;
        } else {
            变形成红黑树;
        }
    }
}
```

Java8中 ConcurrentHashMap 的结构和 HashMap的结构是一样的, 也加入了红黑树. 但是它的线程安全机制相对Java7发生了较大的变化, 不再使用Segment, 而是使用对头节点加锁, 因此锁的粒度更细, 并且由于synchronized的优化, 性能也会有所提升.

细节方面的不同还有, 数组某位置链表长度超过了阈值也不一定变形成红黑树, 还要看数组长度是否大于64, 大数组才有变形红黑树的意义.

初始化数组一样是使用了CAS保证线程安全, 但是扩容和数据迁移过程涉及的同步策略很复杂, 在数据迁移过程中, 将数组的分成了若干个"任务包", 比如长度为64的数组, 可能按8分成了8个任务包, 由8个线程分别执行它们的数据迁移工作, 提高了效率. 当然线程的数量还和CPU个数有关.

#### get操作

1. 计算 hash 值
2. 根据 hash 值找到数组对应位置: (n - 1) & h
3. 根据该位置处结点性质进行相应查找
    - 如果该位置为 null，那么直接返回 null 就可以了
    - 如果该位置处的节点刚好就是我们需要的，返回该节点的值即可
    - 如果该位置节点的 hash 值小于 0，说明正在扩容，或者是红黑树，后面我们再介绍 find 方法
    - 如果以上 3 条都不满足，那就是链表，进行遍历比对即可
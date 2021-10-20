## ElasticSearch

- 关系型数据库 -> Databases(库) -> Tables(表) -> Rows(行) -> Columns(列)。
- Elasticsearch -> Indeces(索引) -> Types(类型) -> Documents(文档) -> Fields(属性)。

Elasticsearch集群可以包含多个索引(indices)（数据库），每一个索引可以包含多个类型 (Types)（表），每一个类型包含多个文档(documents)（行），然后每个文档包含多个字段(Fields)（列）。

虽然这么类比，但是毕竟是两个差异化的产品，而且上面也说过在以后的版本中类型 (Types) 可能会被删除，所以一般我们创建索引都是一个种类对应一个索引。生鲜就创建商品的索引，生活用品就创建生活用品的索引，而不会说创建一个商品的索引，里面既包含生鲜的类型，又包含生活用品的类型。

分片是除法, 为了降低单索引的复杂度, 副本是乘法, 保证高可用, 也能提高并发能力.

**「索引」含义的区分**

```
索引（名词） 如上文所述，一个索引(index)就像是传统关系数据库中的数据库，它是相关文档存储的地方，index的复数是 indices 或 indexes。

索引（动词） 「索引一个文档」表示把一个文档存储到索引（名词）里，以便它可以被检索或者查询。这很像SQL中的INSERT关键字，差别是，如果文档已经存在，新的文档将覆盖旧的文档。

倒排索引 传统数据库为特定列增加一个索引，例如B-Tree索引来加速检索。Elasticsearch和Lucene使用一种叫做倒排索引(inverted index)的数据结构来达到相同目的。
```

Elasticsearch如何进行全文字段搜索且首先返回相关性性最大的结果。相关性(relevance)概念在Elasticsearch中非常重要，而这也是它与传统关系型数据库中记录只有匹配和不匹配概念最大的不同。

**映射**是定义一个文档及其包含的字段如何存储和索引的过程。

## 常见面试问题

### es存储基本单位?它的分布架构如何实现?

Q:es 的分布式架构原理能说一下么（es 是如何实现分布式的啊）？

````
es的基本存储单位是index,索引中一条记录是document,它会有一些参数field.整个存储单位的结构:index->type->mapping->document->field
可以将document类比为一条记录,index类比成一张表,type就是具有相同特点记录的集合
````

````
分布式结构:多个es进程分布在多台机器上,组成了一个集群.每个index可以拆分成多个shard,保存在不同的机器上,并且每个shard会有指定数量的副本.这种分配机制同时保障了高并发和高可用
多个es节点中,会有一个master节点.多份相同的shard中会有一个primary shard,它负责写操作,其他的shard会与它同步.如果某台机器宕机导致一些primary shard丢失,master会重新选举出primary shard.如果master宕机,一样会重新从集群的es节点中选举出master.
````

### es读写数据的原理和倒排索引的原理?

Q:es 写入数据的工作原理是什么啊？es 查询数据的工作原理是什么啊？底层的 lucene 介绍一下呗？倒排索引了解吗？

![1553740252592](d:/resource/notePic/1553740252592.png)

````
写数据的过程:
1.客户端发送请求到任意一个es节点,成为coordinate节点
2.coordinate节点根据document进行路由,将请求转发给有primary shard的节点
3.节点执行写操作,并同步到其他shard节点,同步成功后通知coordinate节点
4.coordinate节点返回响应给用户,已经成功
读数据的过程:
1.客户端发送请求到任意一个es节点,成为coordinate节点
2.coordinate节点根据did查询所有拥有相应shard的节点,并采用随机轮询的负载均衡策略,随机转发请求到某个节点
3.节点处理请求,将查询结果返回给coordinate节点
4.coordinate节点将结果返回给用户
查询的过程:
类似于读的过程,只是coordinate节点会轮询到很多节点,这些节点将查询到的did返回给coordinate节点,coordinate进行数据的合并,排序和分页等操作,最后根据各个节点上的did拉取实际数据返回给用户

总体步骤:随机找一个coordinate节点->coordinate节点进行路由->路由的节点处理请求,返回结果->coordinate返回响应给客户端
````

````
写数据的底层原理:
数据写入buffer->1s后refresh至os cache内存,产生一个segment file文件(在内存中),内存buffer每次refresh会清空->每隔5s又会将os cache内存中的数据写入translog(磁盘文件)->每隔30min或translog太大时,会触发commit操作.
内存中的segment file每秒产生一个,会定期进行merge合并成新的segment file.
commit操作会强制refresh内存buffer,然后将内存中所有的segment file文件持久化到硬盘中,并清空translog

内存buffer中的数据索引不到,所以插入数据1s后才能被搜索到,因此es搜索是准实时搜索.segment file文件在没有commit的时候是在内存中的,它的产生频率相对translog更高.但是translog在磁盘中,所以es节点如果宕机,就会损失5s的数据,这些数据在内存buffer和segment中

删除/更新数据的底层原理:删除数据,会将数据标志为delete状态.更新数据会将数据标志为delete状态,然后在插入一条数据.
被标志为delete状态的数据,在segment合并的过程中,会被物理删除.
````

![1553740842401](d:/resource/notePic/1553740842401.png)

````
底层lucene:其实就是封装了各种倒排索引的算法,我们将写入的数据建立索引,lucene会将这些索引组织成一定的数据结构.

倒排索引:就是关键词到文档id的映射,每个文档都会提取出一些关键词,根据关键词和文档的映射关系建立倒排索引.
````

### 数据量很大(数十亿级别)情况下,如何提高查询效率?

制约es搜索的关键是filesystem cache分配的内存大小,因为从内存中走会比从磁盘中走高一个数量级

````
1.分配给filesystem cache的内存大小,相比写进的索引大小不能差太多,不然会严重影响性能
2.可以配合HBase做搜索.写索引的时候不要写整行数据,而是写一些重要的搜索字段,非搜索字段不用存储.比如Person的信息可以就存name,age和pid.利用name和age搜索出来pid,再将pid扔进HBase中搜索,得到完整信息.HBase适合海量数据的在线存储,这样可以充分利用filesystem cache的内存大小.
3.数据预热:可以写一个子系统,对于热门数据,提前将其预热到filesystem cache中
4.冷热分离:热门数据和冷门数据分索引,分机器存储,不让冷门数据的搜索将热门数据flush下去
5.document模型优化:不要对es做过于复杂的关联查询,会严重影响性能,尽量在document层面就设计好插入的数据模型,尽量做简单搜索
6.分页性能优化:不要做深度搜索,越查到后面越慢.因为es分页搜索时,页码前面的结果也是会一起被搜索的.
````


## Solr

### Solr简介

Solr 是Apache下的一个顶级开源项目，采用Java开发，它是基于Lucene的全文搜索服务。Solr可以独立运行在Jetty、Tomcat等这些Servlet容器中。都是Web服务器，Servlet容器，报文 ，JSON 格式字符串， XML格式字符串。Solr是一个可以独立运行的搜索服务器，使用solr进行全文检索服务的话，只需要通过http请求访问该服务器即可。

使用Solr 进行创建索引和搜索索引的实现方法很简单，如下：

- 创建索引：客户端（可以是浏览器可以是Java程序）用 POST 方法向 Solr 服务器发送一个描述 Field 及其内容的 XML 文档，Solr服务器根据xml文档添加、删除、更新索引 。 
- 搜索索引：客户端（可以是浏览器可以是Java程序）用 GET方法向 Solr 服务器发送请求，然后对 Solr服务器返回Xml、json等格式的查询结果进行解析。Solr不提供构建页面UI的功能。Solr提供了一个管理界面，通过管理界面可以查询Solr的配置和运行情况。

### Solr术语介绍

***Collections***:SolrCloud集群中的一个完整的逻辑上的倒排索引([什么是倒排索引?](http://baike.baidu.com/link?url=Tv0F0ffW0sFmsigXdvGmI72dWLoFaspoaZQFX9D1m78-lyexm9E7bQh8HuNumHL11pCVpngmIgbSYVnwTkppXiWI7JaD8A0RofSFmV5f0NqBQ3O4SdnC_lpaqvc1as6x))，和一个独立的config set相关联，由一个或者多个shard组成，shard可以在不同的服务器上，shard对搜索接口的调用者来说是隐形的，搜索者不用考虑在搜索时如何指定shard，只需要传入Collection名即可。

***Config Set***:包含两个最根本的配置文件:solrconfig.xml和schema.xml，视这两个文件的内容而定是否需要包含其他文件。SolrCloud的config set目录会上传到zookeeper中，而传统单机Solr的config set是保存在本地文件夹中。

***Core***:一个Solr Core是一个包含索引和配置文件的运行实例，以前Solr Core是单例模式的，后来重构成了多实例的，([什么是SolrCores?](https://wiki.apache.org/solr/CoreAdmin))。一个Replica对应一个Core实例，同一个Shard对应的Replica的Core的配置和索引数据是一样的，但是是不同实例。

***Replica***:Shard的一个副本。一个Shard会在不同的服务器上保留Repicas(副本)，通过选举机制(和zookeeper的leader选举机制类似)在Replicas(副本)中选出一个leader来对外提供服务。leader连不上了就重新选其他副本作为leader，这样能保证至多(副本数-1)台服务器挂掉后仍然能正常工作。

***Shard***:Collection的一个逻辑分片。每个Shard对应一个Core，并且包含一个索引(Collection)的文档(Documents)的不相交子集，一个Shard由至少一个Replica组成，当有多个Replicas时，选举机制选出作为leader的Replica。单机Solr中，Shard指的是Solr cores.

***Zookeeper***:分布式集群的基本组件，MapReduce、HDFS、Hive等分布式系统都基于它，Leader选举也要靠它。Solr有自己的内嵌Zookeeper，但是一般不会用内嵌的。部署Zookeeper至少需要3台主机(出于节约成本，可以和Solr实例部署在相同的服务器上，目前很多Solr users都是这么用的)。

***schema***：schema是用来告诉solr如何建立索引的，他的配置围绕着一个schema配置文件，这个配置文件决定着solr如何建立索引，每个字段的数据类型，分词方式等，老版本的schema配置文件的名字叫做schema.xml他的配置方式就是手工编辑，但是现在新版本的schema配置文件的名字叫做managed-schema，他的配置方式不再是用手工编辑而是使用schemaAPI来配置，官方给出的解释是使用schemaAPI修改managed-schema内容后不需要重新加载core或者重启solr更适合在生产环境下维护，如果使用手工编辑的方式更改配置不进行重加载core有可能会造成配置丢失。

![img](img/Solr/Solr术语之间的关系.png)

### 倒排序索引

> 倒排索引源于实际应用中需要根据属性的值来查找记录。这种索引表中的每一项都包括一个属性值和具有该属性值的各记录的地址。由于不是由记录来确定属性值，而是由属性值来确定记录的位置，因而称为倒排索引(inverted index)。

Solr是基于倒排序索引进行建立索引和搜索的, doc在"入库"的时候, 会根据分词器分出很多词, 这些词如果在配置文件中配置了Field, 这个词就成了一个索引词, 这个文档中出现这个词的位置会被作为这个词的value, 存在倒排序索引文件中. 同样在搜索的时候, 也会首先分词, 然后根据去分词去查询倒排序索引文件, 再给出出现这些词的记录.

![1560739751844](img/Solr/1560739751844.png)

以下是索引和搜索的大概过程示意图, 详情参考[索引引擎理论](https://www.cnblogs.com/arli/p/6126311.html), 写的非常好.

![1560823628660](img/Solr/索引和搜索的大概过程.jpg)

### Solr时差问题

在使用Solr定时自动导入数据时, 其会使用`dataimport.properties`文件存储最近一次更新索引的时间, 由于Solr默认采用UTC时区(和GMT可以认为是一个时间), 而中国是UTC+8时区, 所以需要修改\solr-7.5.0\bin\solr.in.cmd文件(linux修改solr.in.sh文件), 找到`SOLR_TIMEZONE=UTC`, 即为其默认设置(被注释掉了), 增加一行`set SOLR_TIMEZONE=UTC+8`即成功修改了Solr的时区设置.

参考: [Solr 17 - Solr的时间为什么比本地少8小时 (附修改方法)](https://www.cnblogs.com/shoufeng/p/10618571.html)

### Field

域(Field)相当于数据库的表字段，用户存放数据，因此用户根据业务需要去定义相关的Field（域），一般来说，每一种对应着一种数据，用户对同一种数据进行相同的操作。

域的常用属性：
•	name：指定域的名称
•	type：指定域的类型
•	indexed：是否索引
•	stored：是否存储
•	required：是否必须
•	multiValued：是否多值

**1、域**

　　Solr中默认定义唯一主键key为id域，如下：

```xml
<uniqueKey>id</uniqueKey>
```

　　Solr在删除、更新索引时使用id域进行判断，也可以自定义唯一主键。
　　注意在创建索引时必须指定唯一约束。

```xml
1 <field name="item_goodsid" type="long" indexed="true" stored="true"/>
2 <field name="item_title" type="text_ik" indexed="true" stored="true"/>
3 <field name="item_price" type="double" indexed="true" stored="true"/>
4 <field name="item_image" type="string" indexed="false" stored="true" />
5 <field name="item_category" type="string" indexed="true" stored="true" />
6 <field name="item_seller" type="text_ik" indexed="true" stored="true" />
7 <field name="item_brand" type="string" indexed="true" stored="true" />
```

**2、copyField复制域**

　　copyField复制域，可以将多个Field复制到一个Field中，以便进行统一的检索：
　　比如，根据关键字只搜索item_keywords域的内容就相当于搜索item_title、item_category、item_seller、item_brand，即将item_title、item_category、item_seller、item_brand复制到item_keywords域中。

　　目标域必须是多值的。

```
1 <field name="item_keywords" type="text_ik" indexed="true" stored="false" multiValued="true"/>
2 <copyField source="item_title" dest="item_keywords"/>
3 <copyField source="item_category" dest="item_keywords"/>
4 <copyField source="item_seller" dest="item_keywords"/>
5 <copyField source="item_brand" dest="item_keywords"/>
```

 **3、dynamicField（动态字段）**

　　动态字段就是不用指定具体的名称，只要定义字段名称的规则，例如定义一个 dynamicField，name 为i，定义它的type为text，那么在使用这个字段的时候，任何以i结尾的字段都被认为是符合这个定义的，例如：name_i，gender_i，school_i等。

　　自定义Field名为：product_title_t，“product_title_t”和scheam.xml中的dynamicField规则匹配成功。

　　如：

![img](img/Solr/1184735-20180506210417292-1035778634.png)

```xml
<dynamicField name="item_spec_*" type="string" indexed="true" stored="true" />
```

### FieldType

**常用的FiledType**

```
text_general: 一般英文可用
string: 单个字符串,不会被分词
数据类型: pfloat  pint  plong
布尔类型: boolean
位置类型: location
```

**字段类型的定义**

字段类型的定义主要包含如下四个方面的信息, 定义在xxxcore/conf/managed-schema中, 如：

```xml
<fieldType name="text_general" class="solr.TextField" positionIncrementGap="100">
  <analyzer type="index">
    <tokenizer class="solr.StandardTokenizerFactory"/>
    <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt" />
    <!-- in this example, we will only use synonyms at query time
    <filter class="solr.SynonymFilterFactory" synonyms="index_synonyms.txt" ignoreCase="true" expand="false"/>
    -->
    <filter class="solr.LowerCaseFilterFactory"/>
  </analyzer>
  <analyzer type="query">
    <tokenizer class="solr.StandardTokenizerFactory"/>
    <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt" />
    <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>
    <filter class="solr.LowerCaseFilterFactory"/>
  </analyzer>
</fieldType>
```

第一行包含了字段类型的名字“text_general”，是由类”solr.TextField”实现的，这个`solr`指的是`org.apache.solr.schema`或者`org.apache.solr.analysis`，也就是说`solr.TextField`指的是`org.apache.solr.schema.TextField`。如果field type 是`solr.TextField`，则可以指定字段解析器

**字段类型属性**

- 通用属性，支持所有的字段类型
- 默认属性，一些字段类型的属性是特有的，可以通过指定来修改默认值

**通用属性**

| 属性                      | 说明                                        | 值     |
| ------------------------- | ------------------------------------------- | ------ |
| name                      | 用于定义`field`的”type”                     |        |
| class                     | 定义了使用何种方式处理该类型数据            |        |
| positionLncrementGap      | 对于多值字段，指定多值的间距                | 整数   |
| autoGeneratePhraseQueries | 适用于`text`字段，Solr是否自动生成短语查询  | 布尔值 |
| docValuesFormat           | 对于一个字段类型使用定制的`DocValuesFormat` |        |
| postingsFormat            | 对于一个字段类型使用定制的`PostingsFormat`  |        |

**默认属性**

| 属性                             | 说明                                                         | 值     | 默认值 |
| -------------------------------- | ------------------------------------------------------------ | ------ | ------ |
| indexed                          | 字段值是否用于查询                                           | 布尔值 | true   |
| stored                           | 字段真实值是否可以被查询到                                   | 布尔值 | true   |
| docValues                        | 字段值是否列式存储                                           | 布尔值 | false  |
| sortMissingFirst sortMissingLast | 没有指定排序规则时，控制文档排序                             | 布尔值 | false  |
| multiValues                      | 字段是否多值                                                 | 布尔值 | false  |
| omitNorms                        | 是否忽略不适用该字段的规范                                   | 布尔值 | true   |
| omitTermFreqAndPositions         | 是否忽略词频、位置等                                         | 布尔值 | true   |
| omitpositions                    | 类似`omitTermFreqAndPositions`但保留词频                     | 布尔值 | true   |
| termVectors                      | 是否保留term vectors                                         | 布尔值 | false  |
| termPositions                    | 是否保留term position                                        | 布尔值 | false  |
| termOffsets                      | 是否保留term offset                                          | 布尔值 | false  |
| termPayloads                     | 是否保留term payload                                         | 布尔值 | false  |
| required                         | 是否拒绝空值的字段                                           | 布尔值 | false  |
| useDocValuesAsStored             | 如果字段是`docValues`，设置为true将允许字段在fl参数匹配“*”时返回数据 | 布尔值 | false  |
| similarity                       | 对全局的相似性文档进行评分, 默认使用BM25Similarity           |        |        |

### 在Solr中引入ik中文分词器

1.在`\solr-8.1.1\server\solr-webapp\webapp\WEB-INF\lib\`中添加jar包

2.在`\solr-8.1.1\server\solr-webapp\webapp\WEB-INF\`新建classes文件夹(如果不存在), 复制IKAnalyzer.cfg.xml ext.dic stopword.dic三个文件到classes文件夹下, 该文件作用是自定义需要的分词和不需要的分词. 如果不使用该功能, 这个可以不配置

3.修改`\solr-8.1.1\server\solr\learncore\conf\managed-schema`文件, 添加对1中引入jar包的分词器声明

```xml
  <!--中文分词器-->
  <fieldType name="text_ik" class="solr.TextField">
	<!--索引的时候分词-->
    <analyzer type="index">
      <tokenizer class="org.wltea.analyzer.lucene.IKTokenizerFactory" useSmart="false" />
    </analyzer>
	<!--查询的时候分词-->
	<analyzer type="query">
      <tokenizer class="org.wltea.analyzer.lucene.IKTokenizerFactory" useSmart="true" />
    </analyzer>
  </fieldType>
```

其中useSmart属性表示分词的粒度, 其为true时是粗粒度, 为false是细粒度. 如下图:

![img](./img/Solr/细粒度和粗粒度分词.jpg)

4.重启Solr, 可以发现分词器可以被搜索到

[以后可以用有道云做分享链接啦!](http://note.youdao.com/noteshare?id=de347f6b7d6ff6fe8628299deed0ec43&sub=E4A30DD9A2FB45D4A3C4A288C2D99DB2)

**分词器的作用**

对于索引和搜索, 假设都是采用key:value的形式. 索引时会对value进行分词, 所以索引倒排文件中存储的真实属性是key:value的各个分词. 搜索的时候, 给出key:value1, 同样会对value1进行分词, value1的分词结果如果之前索引过就可以搜索到, 如果不是则搜索不到.

举例: "nameCN:妹妥宝宝", 索引的时候对"妹妥宝宝"进行了分词, 得到了两个分词结果"妹妥"和"宝宝". 搜索时, 如果搜索"nameCN:妹妥"是ok的, 搜索"nameCN:妹"会搜不到, 因为"妹"并不是分词结果之一, 搜索"nameCN:妹妥宝"可以搜索到, 因为对于搜索的"妹妥宝"也会分词, 其分词的"妹妥"是出现过的.

对于上例可以这么理解, "nameCN:妹妥宝宝"建立索引时, 建立了"nameCN:妹妥"和"nameCN:宝宝"两个索引. "nameCN:妹妥宝"在搜索时, 根据"nameCN:妹妥"和"nameCN:宝"两个索引去寻找.

### Solr的cmd命令

使用cmd命令之前需要进入solr{home}\bin\目录下

```
solr start: 启动
solr stop -all: 停止
solr restart -p 8983: 重启
solr create -c newcore: 创建core
```

### Solr导入数据库数据到Core中

1.将mysql驱动jar包拷贝到 solr\server\solr-webapp\webapp\WEB-INF\lib 下

2.将solr{home}\dist目录下的：solr-dataimporthandler-7.4.0.jar和solr-dataimporthandler-extras-7.4.0.jar这两个jar包拷贝到 solr\server\solr-webapp\webapp\WEB-INF\lib下

3.把solr{home}\example\example-DIH\solr\db下的文件复制到你创建的core里面(需要的示例配置文件)

4.打开core\conf目录在db-data-config.xml和managed-schema中进行配置: db-data-config.xml用于配置数据库连接信息/表与Field对应关系/全量导入和增量导入的sql查询语句; db-data-config.xml中凡是声明了列与Field对应关系的Field, 要在managed-schema中配置. 如果用到了ik分词器, 参考前面配置ik分词器

5.在core\conf\solrconfig.xml中配置, 声明db-data-config.xml, 如下. 这一步可能不用做, 因为如果是按步骤3, 复制的配置文件中, 已经添加了下面的配置, 而且不用做更改.

```xml
<requestHandler name="/dataimport" class="solr.DataImportHandler">
    <lst name="defaults">
        <str name="config">db-data-config.xml</str>
    </lst>
</requestHandler>
```

使用以下请求, 即可实现全量导入. 其中test_core是数据将要导入的core名, clean=false表明导入之前不清空索引库, entity=share指定了导入的表, 其是db-data-config.xml中某个Entity标签可的name属性值.

```
http://localhost:8983/solr/test_core/dataimport?command=full-import&clean=false&commit=true&entity=share
```

**自动增量更新配置**

[点击查看](https://blog.csdn.net/qq_24874939/article/details/84776847)

### Solr导入文件数据到core中建立索引

大致步骤和导入数据库是一样的, 也需要tika-data-config.xml配置文件, 配置Handler与文件的各种属性(文件名/路径/大小等), 并在solrconfig.xml中声明用到的Handler. 同样在managed-schema要建立相应的Field. 需要的jar包和导入数据库一样(除了数据库驱动jar包).

综上, 导入数据都是要配置好文件. 中间值得优化的地方有: 选择的分词器 / 自动增量导入定时器自定义




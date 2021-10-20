## Jackson使用

Jackson是一个序列/反序列化的工具, 借用它可以很方便的将对象转换成json字符串或字节数组, 也可以将json字符串或字节数组转换成对象.

在pom.xml中引入jackson

```xml
<!--jackson-->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>${jackson.version}</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>${jackson.version}</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>${jackson.version}</version>
</dependency>
```

### 对象 <=> json字符串

```java
private static ObjectMapper MAPPER = new ObjectMapper();

// 将pojo对象转换成json字符串
public static String objectToJson(Object obj) {
    try {
        String str = MAPPER.writeValueAsString(obj);
        return str;
    } catch (JsonProcessingException e) {
        e.printStackTrace();
    }
    return null;
}

// 将json数据转换成pojo对象
public static <T> T jsonToObject(String json, Class<T> beanType) {
    try {
        T t = MAPPER.readValue(json, beanType);
        return t;
    } catch (JsonParseException e) {
        e.printStackTrace();
    } catch (JsonMappingException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}
```

对象和json字符串之间的相互转换很简单, json字符串转换成对象时需要给定转换的对象Class 对象. 注意这里对象必须是非列表类型(List Map Set等), 但是对象的属性可以包含任意类型.

比如下面代码中, 第4行会报错, 因为`list.getClass()`只是得到List.class, 它的泛型是不知道的, 所以不能这样转换

```java
List<Person> list = new Arraylist<>();
list.add(new Person("sc", 18));
Srring json = objectToJson(list);
list = jsonToObject(json, list.getClass());
```

### json字符串 => 列表对象(list/map等)

```java
// 将json数据转换成pojo对象list
public static <T> List<T> jsonToList(String json, Class<T> beanType) {
    JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
    try {
        List<T> list = MAPPER.readValue(json, javaType);
        return list;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

// 将json数据转换成pojo对象map
public static <K,V> Map<K,V> jsonToList(String json, Class<T> keyType, Class<T> valueType) {
    JavaType javaType = MAPPER.getTypeFactory().constructParametricType(Map.class, keyType, valueType);
    try {
        Map<K,V> map = MAPPER.readValue(json, javaType);
        return map;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
```

如上, 就可以将json字符串转换成list对象, 但是前提是知道这个json字符串是由list对象转换而成的.

### 对象 <=> 字节数组

```java
// 将对象转换成btye数组
public static byte[] objectToBytes(Object obj) {
    try {
        return MAPPER.writeValueAsBytes(obj);
    } catch (JsonProcessingException e) {
        System.out.println("objectToBytes error: json解析异常");
        e.printStackTrace();
    }
    return null;
}

// 注意type是待转换对象的type字符串
public static Object bytesToObject(byte[] bytes, String type) {
    try {
        JavaType javaType = MAPPER.getTypeFactory().constructFromCanonical(type);
        Object o = MAPPER.readValue(bytes, javaType);
        return o;
    } catch (JsonParseException e) {
        e.printStackTrace();
        System.out.println("bytesToObject error: JsonParseException");
    } catch (JsonMappingException e) {
        e.printStackTrace();
        System.out.println("bytesToObject error: JsonMappingException");
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("bytesToObject error: IOException");
    }
    return null;
}
```

转换成json字符串和转换成字节数组看起来只是API的差别, 但是上面提供的字节数组=>对象的转换方法参数中, type并不是Class对象, 而是一个描述对象type的字符串, 对于简单对象来说,type字符串就是其类的全限定名,如java.lang.String;对于复杂列表对象(Map<String, List<Person>>)来说,type字符串例子:java.util.Map<java.lang.String, java.util.List<test.Person>>, 通过type字符串可以构建jackson反序列化需要的JavaType对象, 读取字节数组时, 传入这个JavaType对象, 再复杂的对象, 它都可以转换成功, 而且对象转换成字节数组的方法都是统一的.

type字符串主要在动态代理里的invoke方法通过method获取, 如下parameterTrueTypes就是转换JavaType需要的字符串

```java
// 设置方法参数类型
Type[] types = method.getGenericParameterTypes();
for (int i = 0; i < args.length; i++) {
    if (types[i] instanceof ParameterizedType) {
        // 比如List<Person>的Type就能获取java.util.List,用于通过反射获取方法
        requestMessage.getParameterTypes()[i] = ((ParameterizedType) types[i]).getRawType().getTypeName();
        // 比如List<Person>就能获取java.util.List<test.Person>,可以用于jackson里通过这个字符串获取JavaType,再通过JavaType反序列化复杂对象
        requestMessage.getParameterTrueTypes()[i] = types[i].toString();
    } else {
        // 比如Person都是获取test.Person: 简单对象获取类的全限定名
        requestMessage.getParameterTypes()[i] = types[i].getTypeName();
        requestMessage.getParameterTrueTypes()[i] = types[i].getTypeName();
    }
}

// 设置返回值类型
Type type = method.getGenericReturnType();
if (type instanceof ParameterizedType) {
    requestMessage.setReturnType(method.getGenericReturnType().toString());
} else {
    requestMessage.setReturnType(method.getGenericReturnType().getTypeName());
}
```






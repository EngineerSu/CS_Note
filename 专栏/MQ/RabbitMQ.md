## RabbitMQ

### 生产者发送消息流程

1. 生产者连接到RabbitMQ Broker，建立一个连接，开启一个信道(Channel)
2. 生产者声明一个交换器，并设置相关属性，比如交换器类型、是否持久化等
3. 生产者声明一个队列并设置相关属性，比如是否排它，是否持久化，是否自动删除等
4. 生产者通过路由键将交换器和队列绑定起来
5. 生产者发送消息到RabbitMQ Broker，其中包含路由键、交换器等信息
6. 相应的交换器会根据接收到的路由键查找相匹配的队列，如果找到，则将从生产者发送过来的消息存入相应的队列；如果没有找到，则根据生产者配置的属性选择丢弃还是回退给生产者
7. 关闭信道，关闭连接



### 消费者接收消息流程

1. 消费者连接到RabbitMQ Broker，建立一个连接，开启一个信道
2. 消费者向RabbitMQ Broker请求消费相应队列中的消息，可能会设置相应的回调函数，以及做一些准备工作
3. 等待RabbitMQ Broker回应并投递相应队列的消息，消费者接收消息，并发送confirm信息
4. 接收到confirm信息后，RabbitMQ 会从队列中删除已被confirm的消息
5. 关闭信道，关闭连接



### 交换机Exchange

生产者是不会直接把消息投递到队列，而是生产者将消息发送到交换器Exchange，由交换器将消息路由到一个或多个队列，如果路由不到，可能会返回给生产者，也可能直接丢弃。

多个消费者可以订阅同一个队列，这个队列的消息会被平摊，给多个消费者处理，而不是每个消费者都接收所有消息并处理

#### fanout广播模式

![1566876891316](d:/resource/notePic/1566876891316.png)

Exchange收到的消息转发与它绑定的所有Queue

#### direct发布/订阅模式

![1566876956524](d:/resource/notePic/1566876956524.png)

生产者将消息发送给交换器时，需要指定routeKey，Queue与Exchange绑定时需要指定bindKey，根据routeKey和bindKey的等值匹配，将消息转发到指定的Queue中

#### topic话题模式

![1566877180938](d:/resource/notePic/1566877180938.png)

生产者将消息发送给交换器时，需要指定routeKey，Queue与Exchange绑定时需要指定bindKey，根据routeKey和bindKey的正则匹配，将消息转发到指定的Queue中



### 支持RPC

RabbitMQ 还可以实现RPC(远程过程调用)，因为RabbitMQ中存在Callback Queue，它可以将消息的消费结果再回发给生产者。每个生产者只用绑定一个Callback Queue即可，因为每个消息都有id，返回的结果会绑定消息id。
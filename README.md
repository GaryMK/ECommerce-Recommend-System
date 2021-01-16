# ECommerce-Recommend-System
ECommerce Recommend System

## 基于Docker环境下部署

### 版本

### Spark(单节点)

1. 安装docker-compose

```python
pip install docker-compose
```

测试是否安装成功

```
docker-compose --version
```

2. 安装docker的spark镜像singularities/spark

```
docker pull singularities/spark
```

3. 创建docker-compose.yml

```
mkdir /singularitiesCR
vim /singularitiesCR/docker-compose.yml
```

4. 编辑docker-compose.yml

```
version: "2"

services:
  master:
    image: singularities/spark
    command: start-spark master
    hostname: master
    ports:
      - "6066:6066"
      - "7070:7070"
      - "8080:8080"
      - "50070:50070"
    volumes:
      - /dockerData/sigularitiesCR/master:/data
  worker:
    image: singularities/spark
    command: start-spark worker master
    environment:
      SPARK_WORKER_CORES: 1
      SPARK_WORKER_MEMORY: 2g
    links:
      - master
    volumes:
      - /dockerData/sigularitiesCR/worker:/data
```

5. 启动容器

```
docker-compose up -d
```

7. 停止容器

```
docker-compose stop
```

8. 删除容器

```
docker-compose rm
```

### ZooKeeper

1. 拉取ZooKeeper镜像

```
docker pull wurstmeister/zookeeper
```

2. ZooKeeper安装

```
docker run -p 2181:2181 --name zookeeper \
-v /dockerData/zookeeper/data:/data \
-d zookeeper:3.4.10
```

3. 查看ZooKeeper启动日志

```
docker logs -f zookeeper
```

![image-20210116185304640](README/image-20210116185304640.png)

### Kafka(单节点)

1. 拉取Kafka镜像

```
docker pull wurstmeister/kafka
```

2. Kafka安装

```
docker run -p 9092:9092 --name kafka \
--link zookeeper \
--env KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
--env KAFKA_ADVERTISED_HOST_NAME=localhost \
--env KAFKA_ADVERTISED_PORT=9092 \
--volume /dockerData/kafka/data:/data \
-d wurstmeister/kafka:latest
```

3. 修改Kafka相关参数

   3. 1进入Kafka容器

   ``` 
   docker exec -it kafka /bin/bash
   ```

   3. 2修改配置文件

   ```shell
   # 修改config下的 server.properties 文件
   vi /opt/kafka/config/server.properties
   
   # 将 
   listteners=PLAINTEXT://:9092
   # 修改成
   listteners=PLAINTEXT://ip:9092
   ```
      3.3重启Kafka

   ```
   docker restart kafka
   ```

4. 验证

   4.1 新建一个test主题，并以生产者身份进行消息生产

   ```shell
   cd /opt/kafka_2.13-2.7.0/bin
   
   # 删除一个topic 名称为 test
   ./kafka-topics.sh --delete --zookeeper zookeeper:2181 --topic test
   # 创建一个 topic 名称为 test
   ./kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic test
   # 查看当前topic列表
   ./kafka-topics.sh --list --zookeeper zookeeper:2181
   # 运行一个消息生产者，指定 topic 为刚刚创建的 test 
   ./kafka-console-producer.sh --broker-list localhost:9092 --topic test
   >hello # 发送一条消息并回车
   >world
   ```

   4.2 打开一个新的ssh连接，同样进入kafka容器，模拟消费者接收消息

   ```shell
   docker exec -it kafka /bin/bash
   cd /opt/kafka_2.13-2.7.0/bin
   
   # 以消费者身份接收消息
   ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
   >hello # 成功接收到消息
   >world
   ```

   

5. Kafka-manager安装

```
docker run -d --name kafka-manager -e ZK_HOSTS="172.17.0.8:2181" --net=host sheepkiller/kafka-manager
```

6. Kafka-manager添加cluster

### Flume

1. 拉取Flume镜像


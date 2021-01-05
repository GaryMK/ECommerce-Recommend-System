/*
 * Copyright (c) 2021-2021, GaryMK  All Rights Reserved.
 * ProjectName:  ECommerceRecommendSystem
 * FileName:  Application.java
 * Author:  GaryMK
 * Date:  2021/1/5 下午7:59
 * Version:  1.0
 * LastModified:  2021/1/5 下午7:59
 *
 */

package com.morningstar.kafkastream;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.TopologyBuilder;

import java.util.Properties;

/**
 * @author: GaryMK
 * @EMAIL: chenxingmk@gmail.com
 * @Date: 2021/1/5 19:59
 * @Version: 1.0
 * @Description:
 */
public class Application {
    public static void main(String[] args) {
        String brokers = "localhost:9092";
        String zookeepers = "localhost:2181";

        // 定义输入和输出的topic
        String from = "log";
        String to = "recommender";

        // 定义kafka stream 配置参数
        Properties settings = new Properties();
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "logFilter");
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, zookeepers);

        // 创建kafka stream 配置对象
        StreamsConfig config = new StreamsConfig(settings);

        // 定义拓扑构建器
        TopologyBuilder builder = new TopologyBuilder();
        builder.addSource("SOURCE", from)
                .addProcessor("PROCESSOR", () -> new LogProcessor(), "SOURCE")
                .addSink("SINK", to, "PROCESSOR");

        // 创建kafka stream
        KafkaStreams streams = new KafkaStreams(builder, config);

        streams.start();
        System.out.println("kafka stream started!");
    }
}

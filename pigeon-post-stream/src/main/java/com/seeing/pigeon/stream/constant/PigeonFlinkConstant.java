package com.seeing.pigeon.stream.constant;

public class PigeonFlinkConstant {
    /**
     * Kafka 配置信息
     * TODO 使用前配置kafka broker ip:port
     * (真实网络ip,这里不能用配置的hosts，看语雀文档得到真实ip)
     * （如果想要自己监听到所有的消息，改掉groupId）
     */
    public static final String GROUP_ID = "pigeonLogGroup";
    public static final String TOPIC_NAME = "pigeonTraceLog";
    public static final String BROKER = "117.50.186.19:9092";

    /**
     * redis 配置
     * TODO 使用前配置redis ip:port
     * (真实网络ip,这里不能用配置的hosts，看语雀文档得到真实ip)
     */
    public static final String REDIS_IP = "43.138.199.12";
    public static final String REDIS_PORT = "6379";
    public static final String REDIS_PASSWORD = "123456";


    /**
     * Flink流程常量
     */
    public static final String SOURCE_NAME = "pigeon_kafka_source";
    public static final String FUNCTION_NAME = "pigeon_transfer";
    public static final String SINK_NAME = "pigeon_sink";
    public static final String JOB_NAME = "PigeonBootStrap";
}

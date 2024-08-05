package com.seeing.pigeon.stream;

import com.seeing.pigeon.common.domain.AnchorInfo;
import com.seeing.pigeon.stream.constant.PigeonFlinkConstant;
import com.seeing.pigeon.stream.function.PigeonFlatMapFunction;
import com.seeing.pigeon.stream.sink.PigeonSink;
import com.seeing.pigeon.stream.utils.MessageQueueUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
/**
 * flink启动类
 *
 * @author zengxw
 */
@Slf4j
public class PigeonBootStrap {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(); //获取 Flink 的执行环境，设置它的并行度和其他配置

        /**
         * 1.获取KafkaConsumer
         */
        KafkaSource<String> kafkaConsumer = MessageQueueUtils.getKafkaConsumer(PigeonFlinkConstant.TOPIC_NAME, PigeonFlinkConstant.GROUP_ID, PigeonFlinkConstant.BROKER);
        DataStreamSource<String> kafkaSource = env.fromSource(kafkaConsumer, WatermarkStrategy.noWatermarks(), PigeonFlinkConstant.SOURCE_NAME); //使用 Flink 的 env.fromSource 方法从 Kafka 消费数据


        /**
         * 2. 数据转换处理
         */
        SingleOutputStreamOperator<AnchorInfo> dataStream = kafkaSource.flatMap(new PigeonFlatMapFunction()).name(PigeonFlinkConstant.FUNCTION_NAME); //使用 flatMap 方法将 Kafka 中的字符串数据转换为 AnchorInfo 对象，转换逻辑由 PigeonFlatMapFunction 实现

        /**
         * 3. 将实时数据多维度写入Redis(已实现)，离线数据写入hive(未实现)
         * "sink" 是一个常见的术语，指的是数据流的终点或最终的存储位置。它可以理解为数据流处理的"接收器"
         */
        dataStream.addSink(new PigeonSink()).name(PigeonFlinkConstant.SINK_NAME); //使用 addSink 方法将处理后的数据写入 Redis，具体逻辑由 PigeonSink 实现
        env.execute(PigeonFlinkConstant.JOB_NAME); //执行 Flink 作业

    }

}

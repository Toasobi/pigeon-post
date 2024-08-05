package com.seeing.pigeon.stream.function;

import com.alibaba.fastjson.JSON;
import com.seeing.pigeon.common.domain.AnchorInfo;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * process处理
 */
public class PigeonFlatMapFunction implements FlatMapFunction<String, AnchorInfo> {

    @Override
    public void flatMap(String value, Collector<AnchorInfo> collector) throws Exception { //用于收集 AnchorInfo 对象的 Collector
        AnchorInfo anchorInfo = JSON.parseObject(value, AnchorInfo.class); //将输入的 JSON 字符串 value 解析为 AnchorInfo 对象
        collector.collect(anchorInfo); //使用 Flink 的 Collector 将解析出的对象收集起来，供后续处理使用
    }
}

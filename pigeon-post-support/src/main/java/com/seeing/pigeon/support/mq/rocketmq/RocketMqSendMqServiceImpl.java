package com.seeing.pigeon.support.mq.rocketmq;


import com.seeing.pigeon.support.constant.MessageQueuePipeline;
import com.seeing.pigeon.support.mq.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.messaging.support.MessageBuilder;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Service
@ConditionalOnProperty(name = "pigeon.mq.pipeline", havingValue = MessageQueuePipeline.ROCKET_MQ)
public class RocketMqSendMqServiceImpl implements SendMqService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void send(String topic, String jsonValue, String tagId) {
        if (StringUtils.isNotBlank(tagId)) {
            topic = topic + ":" + tagId;
        }
        send(topic, jsonValue);
    }

    @Override
    public void send(String topic, String jsonValue) {
        rocketMQTemplate.send(topic, MessageBuilder.withPayload(jsonValue).build());
    }
}

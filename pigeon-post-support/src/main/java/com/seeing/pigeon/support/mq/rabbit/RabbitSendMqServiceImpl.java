package com.seeing.pigeon.support.mq.rabbit;


import com.seeing.pigeon.support.constant.MessageQueuePipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.seeing.pigeon.support.mq.SendMqService;

@Slf4j
@Service
@ConditionalOnProperty(name = "pigeon.mq.pipeline", havingValue = MessageQueuePipeline.RABBIT_MQ)
public class RabbitSendMqServiceImpl implements SendMqService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${pigeon.rabbitmq.topic.name}")
    private String confTopic;

    @Value("${pigeon.rabbitmq.exchange.name}")
    private String exchangeName;


    @Override
    public void send(String topic, String jsonValue, String tagId) {
        if (topic.equals(confTopic)) {
            rabbitTemplate.convertAndSend(exchangeName, confTopic, jsonValue);
        } else {
            log.error("RabbitSendMqServiceImpl send topic error! topic:{},confTopic:{}", topic, confTopic);
        }
    }

    @Override
    public void send(String topic, String jsonValue) {
        send(topic, jsonValue, null);
    }
}

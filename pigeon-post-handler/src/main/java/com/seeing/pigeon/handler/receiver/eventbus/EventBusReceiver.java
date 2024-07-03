package com.seeing.pigeon.handler.receiver.eventbus;

import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.Subscribe;
import com.seeing.pigeon.common.domain.TaskInfo;
import com.seeing.pigeon.handler.service.ConsumeService;
import com.seeing.pigeon.support.constant.MessageQueuePipeline;
import com.seeing.pigeon.support.domain.MessageTemplate;
import com.seeing.pigeon.support.mq.eventbus.EventBusListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "pigeon.mq.pipeline", havingValue = MessageQueuePipeline.EVENT_BUS)
@Slf4j
public class EventBusReceiver implements EventBusListener {

    @Autowired
    private ConsumeService consumeService;


    @Override
    @Subscribe
    public void consume(List<TaskInfo> lists) {
        consumeService.consume2Send(lists);
    }

    @Override
    @Subscribe
    public void recall(MessageTemplate messageTemplate){

    }
}

package com.seeing.pigeon.handler.service;

import com.seeing.pigeon.common.domain.TaskInfo;
import com.seeing.pigeon.support.domain.MessageTemplate;

import java.util.List;

/**
 * 消费消息服务
 *
 * @author zengxw
 */
public interface ConsumeService {

    /**
     * 从MQ拉到消息进行消费，发送消息
     *
     * @param taskInfoLists
     */
    void consume2Send(List<TaskInfo> taskInfoLists);


    /**
     * 从MQ拉到消息进行消费，撤回消息
     *
     * @param messageTemplate
     */
    void consume2recall(MessageTemplate messageTemplate);


}

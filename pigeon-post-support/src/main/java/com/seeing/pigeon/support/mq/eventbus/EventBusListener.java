package com.seeing.pigeon.support.mq.eventbus;




import com.seeing.pigeon.common.domain.TaskInfo;
import com.seeing.pigeon.support.domain.MessageTemplate;

import java.util.List;

/**
 * @author zengxw
 * 监听器
 */
public interface EventBusListener {


    /**
     * 消费消息
     * @param lists
     */
    void consume(List<TaskInfo> lists);

    /**
     * 撤回消息
     * @param messageTemplate
     */
    void recall(MessageTemplate messageTemplate);
}

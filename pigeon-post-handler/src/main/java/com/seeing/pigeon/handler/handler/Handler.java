package com.seeing.pigeon.handler.handler;

import com.seeing.pigeon.common.domain.TaskInfo;
import com.seeing.pigeon.support.domain.MessageTemplate;

public interface Handler {

    /**
     * 处理器
     *
     * @param taskInfo
     */
    void doHandler(TaskInfo taskInfo);

    /**
     * 撤回消息
     *
     * @param messageTemplate
     * @return
     */
    void recall(MessageTemplate messageTemplate);

}

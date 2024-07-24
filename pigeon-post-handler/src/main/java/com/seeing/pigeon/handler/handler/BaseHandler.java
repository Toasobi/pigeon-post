package com.seeing.pigeon.handler.handler;

import org.springframework.beans.factory.annotation.Autowired;
import com.seeing.pigeon.common.domain.TaskInfo;

import javax.annotation.PostConstruct;

public abstract class BaseHandler implements Handler {

    @Autowired
    private HandlerHolder handlerHolder;

    /**
     * 标识渠道的Code
     * 子类初始化的时候指定
     */
    protected Integer channelCode;

    /**
     * 初始化渠道与Handler的映射关系
     */
    @PostConstruct
    private void init() {
        handlerHolder.putHandler(channelCode, this);
    }



    @Override
    public void doHandler(TaskInfo taskInfo) {
        if (handler(taskInfo)) {
            return;
        }
    }


    /**
     * 统一处理的handler接口
     *
     * @param taskInfo
     * @return
     */
    public abstract boolean handler(TaskInfo taskInfo);


}

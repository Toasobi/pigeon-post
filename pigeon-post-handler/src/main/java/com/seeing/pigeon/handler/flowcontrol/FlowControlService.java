package com.seeing.pigeon.handler.flowcontrol;

import com.seeing.pigeon.common.domain.TaskInfo;

/**
 * @author zengxw
 * 流量控制服务
 */
public interface FlowControlService {


    /**
     * 根据渠道进行流量控制
     *
     * @param taskInfo
     * @param flowControlParam
     * @return 耗费的时间
     */
    Double flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam);

}
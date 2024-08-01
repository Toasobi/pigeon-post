package com.seeing.pigeon.handler.shield;

import com.seeing.pigeon.common.domain.TaskInfo;

/**
 * 屏蔽服务
 */
public interface ShieldService {


    /**
     * 屏蔽消息
     *
     * @param taskInfo
     */
    void shield(TaskInfo taskInfo);
}

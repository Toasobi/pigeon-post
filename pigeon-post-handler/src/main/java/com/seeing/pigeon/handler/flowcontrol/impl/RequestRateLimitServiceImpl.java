package com.seeing.pigeon.handler.flowcontrol.impl;

import com.google.common.util.concurrent.RateLimiter;
import com.seeing.pigeon.common.domain.TaskInfo;
import com.seeing.pigeon.handler.enums.RateLimitStrategy;
import com.seeing.pigeon.handler.flowcontrol.FlowControlParam;
import com.seeing.pigeon.handler.flowcontrol.FlowControlService;
import com.seeing.pigeon.handler.flowcontrol.annotations.LocalRateLimit;


@LocalRateLimit(rateLimitStrategy = RateLimitStrategy.REQUEST_RATE_LIMIT)
public class RequestRateLimitServiceImpl implements FlowControlService {

    /**
     * 根据渠道进行流量控制
     *
     * @param taskInfo
     * @param flowControlParam
     */
    @Override
    public Double flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam) {
        RateLimiter rateLimiter = flowControlParam.getRateLimiter();
        return rateLimiter.acquire(1);
    }
}

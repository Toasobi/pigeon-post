package com.seeing.pigeon.handler.deduplication.service;

import cn.hutool.core.collection.CollUtil;
import com.seeing.pigeon.common.domain.TaskInfo;
import com.seeing.pigeon.handler.deduplication.DeduplicationHolder;
import com.seeing.pigeon.handler.deduplication.DeduplicationParam;
import com.seeing.pigeon.handler.deduplication.limit.LimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * @author zengxw
 * 去重服务
 */
@Slf4j
public abstract class AbstractDeduplicationService implements DeduplicationService {

    protected Integer deduplicationType;

    protected LimitService limitService; //给的是子类绑定的limitService

    @Autowired
    private DeduplicationHolder deduplicationHolder;

    @PostConstruct
    private void init() {
        deduplicationHolder.putService(deduplicationType, this); //子类构造函数已经将deduplicationType赋予了值, 有多少个子类执行多少次。这个this其实实际为该抽象类和子类的合体
    }

//    @Autowired
//    private LogUtils logUtils;


    @Override
    public void deduplication(DeduplicationParam param) {
        TaskInfo taskInfo = param.getTaskInfo();

        Set<String> filterReceiver = limitService.limitFilter(this, taskInfo, param);

        // 剔除符合去重条件的用户
        if (CollUtil.isNotEmpty(filterReceiver)) {
            taskInfo.getReceiver().removeAll(filterReceiver);
            //logUtils.print(AnchorInfo.builder().businessId(taskInfo.getBusinessId()).ids(filterReceiver).state(param.getAnchorState().getCode()).build());
        }
    }


    /**
     * 构建去重的Key
     *
     * @param taskInfo
     * @param receiver
     * @return
     */
    public abstract String deduplicationSingleKey(TaskInfo taskInfo, String receiver);


}

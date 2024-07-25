package com.seeing.pigeon.handler.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.seeing.pigeon.common.domain.AnchorInfo;
import com.seeing.pigeon.common.domain.LogParam;
import com.seeing.pigeon.common.domain.TaskInfo;
import com.seeing.pigeon.common.enums.AnchorState;
import com.seeing.pigeon.handler.pending.Task;
import com.seeing.pigeon.handler.service.ConsumeService;
import com.seeing.pigeon.handler.utils.GroupIdMappingUtils;
import com.seeing.pigeon.support.domain.MessageTemplate;
import com.seeing.pigeon.support.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import com.seeing.pigeon.handler.pending.TaskPendingHolder;

import java.util.List;

/**
 * @author zengxw
 */
@Service
public class ConsumeServiceImpl implements ConsumeService {
    private static final String LOG_BIZ_TYPE = "Receiver#consumer";
    private static final String LOG_BIZ_RECALL_TYPE = "Receiver#recall";
    @Autowired
    private ApplicationContext context;

    @Autowired
    private LogUtils logUtils;


    @Autowired
    private TaskPendingHolder taskPendingHolder;


    @Override
    public void consume2Send(List<TaskInfo> taskInfoLists) {
        String topicGroupId = GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(taskInfoLists.iterator()));
        for (TaskInfo taskInfo : taskInfoLists) {
            Task task = context.getBean(Task.class).setTaskInfo(taskInfo);
            logUtils.print(LogParam.builder().bizType(LOG_BIZ_TYPE).object(taskInfo).build(), AnchorInfo.builder().ids(taskInfo.getReceiver()).businessId(taskInfo.getBusinessId()).state(AnchorState.RECEIVE.getCode()).build());
            taskPendingHolder.route(topicGroupId).execute(task);
        }
    }

    @Override
    public void consume2recall(MessageTemplate messageTemplate) {
    }
}

package com.seeing.pigeon.handler.pending;

import com.dtp.core.thread.DtpExecutor;
import com.seeing.pigeon.handler.config.HandlerThreadPoolConfig;
import com.seeing.pigeon.support.utils.ThreadPoolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.seeing.pigeon.handler.utils.GroupIdMappingUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;


/**
 * 存储 每种消息类型 与 TaskPending 的关系
 * 启动时创建所有动态线程池，并给所有动态线程池注册好优雅关闭
 *
 * @author zengxw
 */
@Component
public class TaskPendingHolder {
    @Autowired
    private ThreadPoolUtils threadPoolUtils;

    private Map<String, ExecutorService> taskPendingHolder = new HashMap<>(32);

    /**
     * 获取得到所有的groupId
     */
    private static List<String> groupIds = GroupIdMappingUtils.getAllGroupIds();

    /**
     * 给每个渠道，每种消息类型初始化一个线程池
     */
    @PostConstruct
    public void init() {
        /**
         * example ThreadPoolName:pigeon.im.notice
         *
         * 可以通过apollo配置：dynamic-tp-apollo-dtp.yml  动态修改线程池的信息
         */
        for (String groupId : groupIds) {
            DtpExecutor executor = HandlerThreadPoolConfig.getExecutor(groupId);
            threadPoolUtils.register(executor);

            taskPendingHolder.put(groupId, executor);
        }
    }

    /**
     * 得到对应的线程池
     *
     * @param groupId
     * @return
     */
    public ExecutorService route(String groupId) {
        return taskPendingHolder.get(groupId);
    }


}

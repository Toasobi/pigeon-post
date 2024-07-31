package com.seeing.pigeon.cron.handler;

import com.dtp.core.thread.DtpExecutor;
import com.seeing.pigeon.cron.config.CronAsyncThreadPoolConfig;
import com.seeing.pigeon.cron.service.TaskHandler;
import com.seeing.pigeon.support.utils.ThreadPoolUtils;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CronTaskHandler {

    @Autowired
    private TaskHandler taskHandler;

    @Autowired
    private ThreadPoolUtils threadPoolUtils;

    private DtpExecutor dtpExecutor = CronAsyncThreadPoolConfig.getXxlCronExecutor();

    /**
     * 处理后台的pigeonJob定时任务消息
     */
    @XxlJob("pigeonJob")
    public void execute() {
        log.info("CronTaskHandler#execute messageTemplateId:{} cron exec!", XxlJobHelper.getJobParam());
        threadPoolUtils.register(dtpExecutor);

        Long messageTemplateId = Long.valueOf(XxlJobHelper.getJobParam()); //创建定时任务的时候就将模板id放入定时任务的Param中了
        dtpExecutor.execute(() -> taskHandler.handle(messageTemplateId));
    }

}

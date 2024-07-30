package com.seeing.pigeon.cron.xxl.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.seeing.pigeon.common.constant.CommonConstant;
import com.seeing.pigeon.common.enums.RespStatusEnum;
import com.seeing.pigeon.common.vo.BasicResultVO;
import com.seeing.pigeon.cron.service.CronTaskService;
import com.seeing.pigeon.cron.xxl.constants.XxlJobConstant;
import com.seeing.pigeon.cron.xxl.entity.XxlJobGroup;
import com.seeing.pigeon.cron.xxl.entity.XxlJobInfo;
import com.seeing.pigeon.cron.xxl.enums.*;
import com.seeing.pigeon.support.domain.MessageTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class XxlJobUtils {

    @Value("${xxl.job.executor.appname}")
    private String appName;

    @Value("${xxl.job.executor.jobHandlerName}")
    private String jobHandlerName;

    @Autowired
    private CronTaskService cronTaskService;

    /**
     * 构建xxlJobInfo信息
     *
     * @param messageTemplate
     * @return
     */
    public XxlJobInfo buildXxlJobInfo(MessageTemplate messageTemplate) {
        //getExpectPushTime : 0:立即发送 定时任务以及周期任务:cron表达式
        String scheduleConf = messageTemplate.getExpectPushTime();
        // 如果没有指定cron表达式，说明立即执行(给到xxl-job延迟5秒的cron表达式)
        if (messageTemplate.getExpectPushTime().equals(String.valueOf(CommonConstant.FALSE))) {
            //假设当前时间是 2023-10-01 12:00:00, scheduleConf = 05 00 12 01 10 ? 2023-2023
            scheduleConf = DateUtil.format(DateUtil.offsetSecond(new Date(), XxlJobConstant.DELAY_TIME), CommonConstant.CRON_FORMAT); //将偏移后的时间格式化为 cron 表达式
        }

        XxlJobInfo xxlJobInfo = XxlJobInfo.builder()
                .jobGroup(queryJobGroupId()).jobDesc(messageTemplate.getName())
                .author(messageTemplate.getCreator())
                .scheduleConf(scheduleConf) //cron表达式
                .scheduleType(ScheduleTypeEnum.CRON.name())
                .misfireStrategy(MisfireStrategyEnum.DO_NOTHING.name()) //调度过期策略
                .executorRouteStrategy(ExecutorRouteStrategyEnum.CONSISTENT_HASH.name()) //指定任务在多台执行器之间的分配策略
                .executorHandler(XxlJobConstant.JOB_HANDLER_NAME) //指定具体执行任务的逻辑处理器
                .executorParam(String.valueOf(messageTemplate.getId()))
                .executorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name()) //阻塞策略
                .executorTimeout(XxlJobConstant.TIME_OUT)
                .executorFailRetryCount(XxlJobConstant.RETRY_COUNT)
                .glueType(GlueTypeEnum.BEAN.name()) //任务类型，用于指定任务的执行方式
                .triggerStatus(CommonConstant.FALSE) //用于指定任务的运行状态 这里为不会调度
                .glueRemark(StrUtil.EMPTY)
                .glueSource(StrUtil.EMPTY)
                .alarmEmail(StrUtil.EMPTY)
                .childJobId(StrUtil.EMPTY).build();

        if (Objects.nonNull(messageTemplate.getCronTaskId())) {
            xxlJobInfo.setId(messageTemplate.getCronTaskId());
        }
        return xxlJobInfo;
    }

    /**
     * 根据就配置文件的内容获取jobGroupId，没有则创建
     *
     * @return
     */
    private Integer queryJobGroupId() {
        BasicResultVO basicResultVO = cronTaskService.getGroupId(appName, jobHandlerName);
        if (Objects.isNull(basicResultVO.getData())) {
            XxlJobGroup xxlJobGroup = XxlJobGroup.builder().appname(appName).title(jobHandlerName).addressType(CommonConstant.FALSE).build();
            if (RespStatusEnum.SUCCESS.getCode().equals(cronTaskService.createGroup(xxlJobGroup).getStatus())) {
                return (int) cronTaskService.getGroupId(appName, jobHandlerName).getData();
            }
        }
        return (Integer) basicResultVO.getData();
    }

}

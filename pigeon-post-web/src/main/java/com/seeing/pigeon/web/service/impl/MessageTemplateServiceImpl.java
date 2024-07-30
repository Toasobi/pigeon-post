package com.seeing.pigeon.web.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.seeing.pigeon.common.enums.MessageStatus;
import com.seeing.pigeon.common.enums.RespStatusEnum;
import com.seeing.pigeon.common.vo.BasicResultVO;
import com.seeing.pigeon.cron.service.CronTaskService;
import com.seeing.pigeon.cron.xxl.entity.XxlJobInfo;
import com.seeing.pigeon.cron.xxl.utils.XxlJobUtils;
import com.seeing.pigeon.support.dao.MessageTemplateDao;
import com.seeing.pigeon.support.domain.MessageTemplate;
import com.seeing.pigeon.web.service.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MessageTemplateServiceImpl implements MessageTemplateService {

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Autowired
    private CronTaskService cronTaskService;

    @Autowired
    private XxlJobUtils xxlJobUtils;


    @Override
    public BasicResultVO startCronTask(Long id) {
        // 1.获取消息模板的信息
        MessageTemplate messageTemplate = messageTemplateDao.findById(id).orElse(null);
        if (Objects.isNull(messageTemplate)) {
            return BasicResultVO.fail();
        }

        // 2.动态创建或更新定时任务
        XxlJobInfo xxlJobInfo = xxlJobUtils.buildXxlJobInfo(messageTemplate);

        // 3.获取taskId(如果本身存在则复用原有任务，如果不存在则得到新建后任务ID)
        Integer taskId = messageTemplate.getCronTaskId();
        BasicResultVO basicResultVO = cronTaskService.saveCronTask(xxlJobInfo);
        if (Objects.isNull(taskId) && RespStatusEnum.SUCCESS.getCode().equals(basicResultVO.getStatus()) && Objects.nonNull(basicResultVO.getData())) {
            taskId = Integer.valueOf(String.valueOf(basicResultVO.getData()));
        }

        // 4. 启动定时任务
        if (Objects.nonNull(taskId)) {
            cronTaskService.startCronTask(taskId);
            MessageTemplate clone = ObjectUtil.clone(messageTemplate).setMsgStatus(MessageStatus.RUN.getCode()).setCronTaskId(taskId).setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
            messageTemplateDao.save(clone);
            return BasicResultVO.success();
        }
        return BasicResultVO.fail();
    }

    @Override
    public BasicResultVO stopCronTask(Long id) {
        // 1.修改模板状态
        MessageTemplate messageTemplate = messageTemplateDao.findById(id).orElse(null);
        if (Objects.isNull(messageTemplate)) {
            return BasicResultVO.fail();
        }
        MessageTemplate clone = ObjectUtil.clone(messageTemplate).setMsgStatus(MessageStatus.STOP.getCode()).setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        messageTemplateDao.save(clone);

        // 2.暂停定时任务
        return cronTaskService.stopCronTask(clone.getCronTaskId());
    }
}

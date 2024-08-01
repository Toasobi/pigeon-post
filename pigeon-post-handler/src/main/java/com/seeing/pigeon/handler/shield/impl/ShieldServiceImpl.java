package com.seeing.pigeon.handler.shield.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.seeing.pigeon.common.domain.AnchorInfo;
import com.seeing.pigeon.common.domain.TaskInfo;
import com.seeing.pigeon.common.enums.AnchorState;
import com.seeing.pigeon.common.enums.ShieldType;
import com.seeing.pigeon.handler.shield.ShieldService;
import com.seeing.pigeon.support.utils.LogUtils;
import com.seeing.pigeon.support.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * 屏蔽服务
 *
 * @author zengxw
 */
@Service
@Slf4j
public class ShieldServiceImpl implements ShieldService {

    private static final String NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY = "night_shield_send"; //redis键名

    private static final long SECONDS_OF_A_DAY = 86400L; //存储一天的秒数常量 24 * 60 * 60 = 86400秒
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private LogUtils logUtils;

    @Override
    public void shield(TaskInfo taskInfo) {

        if (ShieldType.NIGHT_NO_SHIELD.getCode().equals(taskInfo.getShieldType())) { //如果消息的屏蔽类型是“不屏蔽”（NIGHT_NO_SHIELD），直接返回，不做任何处理
            return;
        }

        /**
         * example:当消息下发至平台时，已经是凌晨1点，业务希望此类消息在次日的早上9点推送
         * (配合 分布式任务定时任务框架)
         */
        if (isNight()) {
            if (ShieldType.NIGHT_SHIELD.getCode().equals(taskInfo.getShieldType())) { //夜间屏蔽
                logUtils.print(AnchorInfo.builder().state(AnchorState.NIGHT_SHIELD.getCode())
                        .bizId(taskInfo.getBizId()).messageId(taskInfo.getMessageId()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceiver()).build()); //如果消息的屏蔽类型是“夜间屏蔽”（NIGHT_SHIELD），记录一条日志，表示消息被夜间屏蔽
            }
            if (ShieldType.NIGHT_SHIELD_BUT_NEXT_DAY_SEND.getCode().equals(taskInfo.getShieldType())) { //夜间屏蔽但是次日发送
                redisUtils.lPush(NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY, JSON.toJSONString(taskInfo,
                                SerializerFeature.WriteClassName),
                        SECONDS_OF_A_DAY);
                logUtils.print(AnchorInfo.builder().state(AnchorState.NIGHT_SHIELD_NEXT_SEND.getCode()).bizId(taskInfo.getBizId()).messageId(taskInfo.getMessageId()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceiver()).build());
            }
            taskInfo.setReceiver(new HashSet<>()); //将消息的接收者设置为空集合，表示不再发送该消息
        }
    }

    /**
     * 小时 < 8 默认就认为是凌晨(夜晚)
     *
     * @return true/false
     * true:夜间
     * false：非夜间
     */
    private boolean isNight() { //定义夜间为凌晨0点到早上8点之间
        return LocalDateTime.now().getHour() < 8;

    }

}

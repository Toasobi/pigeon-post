package com.seeing.pigeon.handler.script;

import com.seeing.pigeon.handler.handler.domain.sms.SmsParam;
import com.seeing.pigeon.support.domain.SmsRecord;

import java.util.List;

/**
 * 短信脚本 接口
 *
 * @author zengxw
 */
public interface SmsScript {

    /**
     * 发送短信
     *
     * @param smsParam
     * @return 渠道商发送接口返回值
     */
    List<SmsRecord> send(SmsParam smsParam);


    /**
     * 拉取回执
     *
     * @param id 渠道账号的ID
     * @return 渠道商回执接口返回值
     */
    List<SmsRecord> pull(Integer id);

}

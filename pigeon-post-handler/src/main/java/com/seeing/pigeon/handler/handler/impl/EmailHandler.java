package com.seeing.pigeon.handler.handler.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.seeing.pigeon.common.domain.TaskInfo;
import com.seeing.pigeon.common.dto.model.EmailContentModel;
import com.seeing.pigeon.common.enums.ChannelType;
import com.seeing.pigeon.handler.handler.Handler;
import com.seeing.pigeon.support.domain.MessageTemplate;
import com.seeing.pigeon.support.utils.AccountUtils;
import com.seeing.pigeon.support.utils.PigeonPostFileUtils;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.seeing.pigeon.handler.handler.BaseHandler;


import java.io.File;
import java.util.Objects;

@Component
@Slf4j
public class EmailHandler extends BaseHandler implements Handler {


    @Value("${pigeon.business.upload.crowd.path}")
    private String dataPath;

    @Autowired
    private AccountUtils accountUtils;

    public EmailHandler() {
        channelCode = ChannelType.EMAIL.getCode();
    }

    @Override
    public boolean handler(TaskInfo taskInfo) {
        //获取邮件内容
        EmailContentModel emailContentModel = (EmailContentModel) taskInfo.getContentModel();
        //获取账户配置
        MailAccount account = getAccountConfig(taskInfo.getSendAccount());
        try {
            //处理附件
            File file = StrUtil.isNotBlank(emailContentModel.getUrl()) ? PigeonPostFileUtils.getRemoteUrl2File(dataPath, emailContentModel.getUrl()) : null;
            //发送邮件
            String result = Objects.isNull(file) ? MailUtil.send(account, taskInfo.getReceiver(), emailContentModel.getTitle(), emailContentModel.getContent(), true) :
                    MailUtil.send(account, taskInfo.getReceiver(), emailContentModel.getTitle(), emailContentModel.getContent(), true, file);
        } catch (Exception e) {
            log.error("EmailHandler#handler fail!{},params:{}", Throwables.getStackTraceAsString(e), taskInfo);
            return false;
        }
        return true;
    }

    /**
     * 获取账号信息和配置
     *
     * @return
     */
    private MailAccount getAccountConfig(Integer sendAccount) {
        MailAccount account = accountUtils.getAccountById(sendAccount, MailAccount.class);
        try {
            //SSL配置
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            account.setAuth(account.isAuth()).setStarttlsEnable(account.isStarttlsEnable()).setSslEnable(account.isSslEnable()).setCustomProperty("mail.smtp.ssl.socketFactory", sf);
            account.setTimeout(25000).setConnectionTimeout(25000);
        } catch (Exception e) {
            log.error("EmailHandler#getAccount fail!{}", Throwables.getStackTraceAsString(e));
        }
        return account;
    }

    @Override
    public void recall(MessageTemplate messageTemplate) {

    }
}

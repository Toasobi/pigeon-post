package com.seeing.pigeon.web.controller;

import com.alibaba.fastjson.JSON;
import com.seeing.pigeon.service.api.domain.MessageParam;
import com.seeing.pigeon.service.api.domain.SendRequest;
import com.seeing.pigeon.service.api.domain.SendResponse;
import com.seeing.pigeon.service.api.enums.BusinessCode;
import com.seeing.pigeon.service.api.service.SendService;
import com.seeing.pigeon.support.domain.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;
import com.seeing.pigeon.support.dao.MessageTemplateDao;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@Slf4j
public class TestController {
    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Autowired
    private SendService sendService;

    @RequestMapping("/test")
    private String test(){
        log.info("日志测试");
        return "日志测试";
    }

    @RequestMapping("/database")
    private String testDataBase() {
        List<MessageTemplate> list = messageTemplateDao.findAllByIsDeletedEquals(0, PageRequest.of(0, 10));
        return JSON.toJSONString(list);
    }

    @RequestMapping("/send")
    private String testSend() {
        SendRequest sendRequest = SendRequest.builder()
                .code(BusinessCode.COMMON_SEND.getCode())
                .messageTemplateId(1L)
                .messageParam(MessageParam.builder().receiver("13722222222").build()).build();

        SendResponse response = sendService.send(sendRequest);
        return JSON.toJSONString(response);

    }
}

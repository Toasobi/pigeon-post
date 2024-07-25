package com.seeing.pigeon.service.api.impl.service;


import com.seeing.pigeon.common.vo.BasicResultVO;
import com.seeing.pigeon.service.api.domain.BatchSendRequest;
import com.seeing.pigeon.service.api.domain.SendRequest;
import com.seeing.pigeon.service.api.domain.SendResponse;
import com.seeing.pigeon.service.api.impl.domain.SendTaskModel;
import com.seeing.pigeon.service.api.service.SendService;
import com.seeing.pigeon.support.pipeline.ProcessContext;
import com.seeing.pigeon.support.pipeline.ProcessController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.monitor4all.logRecord.annotation.OperationLog;


import java.util.Collections;

/**
 * 发送接口
 *
 * @author zengxw
 */
@Service
public class SendServiceImpl implements SendService {

    @Autowired
    private ProcessController processController;

    @Override
    @OperationLog(bizType = "SendService#send", bizId = "#sendRequest.messageTemplateId", msg = "#sendRequest")
    public SendResponse send(SendRequest sendRequest) {

        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(sendRequest.getMessageTemplateId())
                .messageParamList(Collections.singletonList(sendRequest.getMessageParam()))
                .build();

        ProcessContext context = ProcessContext.builder()
                .code(sendRequest.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(BasicResultVO.success()).build();

        ProcessContext process = processController.process(context);

        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
    }

    @Override
    public SendResponse batchSend(BatchSendRequest batchSendRequest) {
        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(batchSendRequest.getMessageTemplateId())
                .messageParamList(batchSendRequest.getMessageParamList())
                .build();

        ProcessContext context = ProcessContext.builder()
                .code(batchSendRequest.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(BasicResultVO.success()).build();

        ProcessContext process = processController.process(context);

        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
    }

}

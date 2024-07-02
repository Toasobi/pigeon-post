package com.seeing.pigeon.service.api.service;


import com.seeing.pigeon.service.api.domain.BatchSendRequest;
import com.seeing.pigeon.service.api.domain.SendRequest;
import com.seeing.pigeon.service.api.domain.SendResponse;

public interface SendService {

    /**
     * 单文案发送接口
     *
     * @param sendRequest
     * @return
     */
    SendResponse send(SendRequest sendRequest);


    /**
     * 多文案发送接口
     *
     * @param batchSendRequest
     * @return
     */
    SendResponse batchSend(BatchSendRequest batchSendRequest);

}

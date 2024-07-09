package com.seeing.pigeon.handler.deduplication.service;

import com.seeing.pigeon.handler.deduplication.DeduplicationParam;

public interface DeduplicationService {

    /**
     * 去重
     *
     * @param param
     */
    void deduplication(DeduplicationParam param);
}

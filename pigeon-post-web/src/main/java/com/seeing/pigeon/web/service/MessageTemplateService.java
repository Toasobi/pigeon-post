package com.seeing.pigeon.web.service;

import com.seeing.pigeon.common.vo.BasicResultVO;

public interface MessageTemplateService {
    BasicResultVO startCronTask(Long id);

    BasicResultVO stopCronTask(Long id);
}

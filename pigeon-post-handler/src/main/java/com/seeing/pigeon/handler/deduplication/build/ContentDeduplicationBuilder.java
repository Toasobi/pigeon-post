package com.seeing.pigeon.handler.deduplication.build;

import com.seeing.pigeon.common.domain.TaskInfo;
import com.seeing.pigeon.common.enums.AnchorState;
import com.seeing.pigeon.common.enums.DeduplicationType;
import com.seeing.pigeon.handler.deduplication.DeduplicationParam;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 根据内容进行去重builder
 */

@Service
public class ContentDeduplicationBuilder extends AbstractDeduplicationBuilder implements Builder {

    public ContentDeduplicationBuilder() {
        deduplicationType = DeduplicationType.CONTENT.getCode();
    }

    @Override
    public DeduplicationParam build(String deduplication, TaskInfo taskInfo) {
        DeduplicationParam deduplicationParam = getParamsFromConfig(deduplicationType, deduplication, taskInfo);
        if (Objects.isNull(deduplicationParam)) {
            return null;
        }
        deduplicationParam.setAnchorState(AnchorState.CONTENT_DEDUPLICATION);
        return deduplicationParam;

    }
}

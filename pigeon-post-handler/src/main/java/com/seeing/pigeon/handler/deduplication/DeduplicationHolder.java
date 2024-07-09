package com.seeing.pigeon.handler.deduplication;

import com.seeing.pigeon.handler.deduplication.service.DeduplicationService;
import org.springframework.stereotype.Service;
import com.seeing.pigeon.handler.deduplication.build.Builder;

import java.util.HashMap;
import java.util.Map;

@Service
public class DeduplicationHolder {

    //相比与动态线程池的TaskPendingHolder来说，这里的变量初始化是通过有依赖注入该Holder的类去实现的
    private final Map<Integer, Builder> builderHolder = new HashMap<>(4);
    private final Map<Integer, DeduplicationService> serviceHolder = new HashMap<>(4);

    public Builder selectBuilder(Integer key) { //
        return builderHolder.get(key);
    }

    public DeduplicationService selectService(Integer key) {
        return serviceHolder.get(key);
    }

    public void putBuilder(Integer key, Builder builder) {
        builderHolder.put(key, builder);
    }

    public void putService(Integer key, DeduplicationService service) {
        serviceHolder.put(key, service);
    }
}

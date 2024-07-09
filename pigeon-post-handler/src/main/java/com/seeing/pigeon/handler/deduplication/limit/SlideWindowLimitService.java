package com.seeing.pigeon.handler.deduplication.limit;

import cn.hutool.core.util.IdUtil;
import com.seeing.pigeon.support.utils.RedisUtils;
import com.seeing.pigeon.common.domain.TaskInfo;
import com.seeing.pigeon.handler.deduplication.service.AbstractDeduplicationService;
import com.seeing.pigeon.handler.deduplication.DeduplicationParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;


/**
 * 滑动窗口去重器（内容去重采用基于redis中zset的滑动窗口去重，可以做到严格控制单位时间内的频次。）
 *
 * @author zengxw
 */
@Service(value = "SlideWindowLimitService")
public class SlideWindowLimitService extends AbstractLimitService {

    private static final String LIMIT_TAG = "SW_";

    @Autowired
    private RedisUtils redisUtils;


    private DefaultRedisScript<Long> redisScript;


    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit.lua")));
    }


    /**
     * @param service  去重器对象
     * @param taskInfo
     * @param param    去重参数
     * @return 返回不符合条件的手机号码
     */
    @Override
    public Set<String> limitFilter(AbstractDeduplicationService service, TaskInfo taskInfo, DeduplicationParam param) { //300s 1次

        Set<String> filterReceiver = new HashSet<>(taskInfo.getReceiver().size());
        long nowTime = System.currentTimeMillis();
        for (String receiver : taskInfo.getReceiver()) {
            String key = LIMIT_TAG + deduplicationSingleKey(service, taskInfo, receiver); //redis键值
            String scoreValue = String.valueOf(IdUtil.getSnowflake().nextId()); //生成一个唯一标识符，用于在redis有序集合中存储请求记录
            String score = String.valueOf(nowTime); //时间戳，作为分数
            if (redisUtils.execLimitLua(redisScript, Collections.singletonList(key), String.valueOf(param.getDeduplicationTime() * 1000), score, String.valueOf(param.getCountNum()), scoreValue)) {
                filterReceiver.add(receiver);
            }

        }
        return filterReceiver;
    }


}

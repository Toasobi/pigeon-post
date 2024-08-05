package com.seeing.pigeon.stream.utils;

import com.seeing.pigeon.stream.callback.RedisPipelineCallBack;
import com.seeing.pigeon.stream.constant.PigeonFlinkConstant;
import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.ByteArrayCodec;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 无Spring环境下使用Redis，基于Lettuce封装
 * 初始化一个 Redis 客户端，用于连接 Redis 服务器。
 * 提供一个 pipeline 方法，封装了 Redis Pipeline 操作，允许批量执行 Redis 命令，并等待所有操作完成
 */
public class LettuceRedisUtils {

    /**
     * 初始化 redisClient
     */
    private static RedisClient redisClient;

    static { //静态初始化块用于初始化 redisClient
        RedisURI redisUri = RedisURI.Builder.redis(PigeonFlinkConstant.REDIS_IP)
                .withPort(Integer.valueOf(PigeonFlinkConstant.REDIS_PORT))
                .withPassword(PigeonFlinkConstant.REDIS_PASSWORD.toCharArray())
                .build();
        redisClient = RedisClient.create(redisUri);
    }


    /**
     * 封装pipeline操作
     * 回调接口，用于定义具体的 Pipeline 操作
     */
    public static void pipeline(RedisPipelineCallBack pipelineCallBack) {
        StatefulRedisConnection<byte[], byte[]> connect = redisClient.connect(new ByteArrayCodec()); //使用 ByteArrayCodec 编解码器连接到 Redis 服务器
        RedisAsyncCommands<byte[], byte[]> commands = connect.async(); //获取异步的 Redis 命令接口

        List<RedisFuture<?>> futures = pipelineCallBack.invoke(commands); //调用回调接口的 invoke 方法，执行具体的 Pipeline 操作，返回一个包含各个操作结果的 RedisFuture 列表

        commands.flushCommands(); //刷新所有缓冲的命令，确保它们被发送到 Redis 服务器
        LettuceFutures.awaitAll(10, TimeUnit.SECONDS,
                futures.toArray(new RedisFuture[futures.size()]));
        connect.close();
    }

}

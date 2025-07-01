package com.zbkj.common.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 * redis工具类
 */

@Component
public class RedisDistributedLock {

    @Autowired
    private final RedisUtil redisUtil;
    private static final String LOCK_PREFIX = "dist_lock:";
    private static final long DEFAULT_EXPIRE = 3000; // 默认锁过期时间3秒
    private static final long DEFAULT_WAIT = 2000;   // 默认等待时间2秒

    @Autowired
    public RedisDistributedLock(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }


    /**
     * 尝试获取分布式锁
     * @param lockKey 锁的key
     * @return 锁标识（解锁时使用），获取失败返回null
     */
    public String tryLock(String lockKey) {
        String lockValue = UUID.randomUUID().toString();
        String fullKey = LOCK_PREFIX + lockKey;
        long endTime = System.currentTimeMillis() + DEFAULT_WAIT;

        while (System.currentTimeMillis() < endTime) {
            // 使用setIfAbsent实现原子操作
            Boolean success = redisUtil.setIfAbsent(fullKey, lockValue, DEFAULT_EXPIRE, TimeUnit.MILLISECONDS);
            if (Boolean.TRUE.equals(success)) {
                return lockValue;
            }

            // 短暂休眠避免CPU空转
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }

    /**
     * 释放分布式锁
     * @param lockKey 锁的key
     * @param lockValue 锁标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String lockValue) {
        String fullKey = LOCK_PREFIX + lockKey;

        // 使用Lua脚本保证原子性
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "   return redis.call('del', KEYS[1]) " +
                "else " +
                "   return 0 " +
                "end";

        // 执行Lua脚本
        Object result = redisUtil.executeLuaScript(luaScript, fullKey, lockValue);
        return result != null && (long) result == 1;
    }
}

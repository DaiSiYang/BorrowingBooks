package com.example.borrowingbooks.common;

import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    private final StringRedisTemplate stringRedisTemplate;

    // 锁的默认过期时间，单位秒
    private static final long TIMEOUT = 5;

    public RedisLock(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 尝试加锁
     * @param key 业务key，例如 book:lock:123
     * @param clientId 请求唯一标识
     * @return 是否加锁成功
     */
    public boolean tryLock(String key, String clientId) {
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, clientId, TIMEOUT, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 释放锁（Lua脚本保证原子性）
     * @param key 业务key
     * @param clientId 当前线程的UUID
     */
    public void unlock(String key, String clientId) {
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                "then return redis.call('del', KEYS[1]) else return 0 end";

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Long.class);

        stringRedisTemplate.execute(
                redisScript,
                Collections.singletonList(key),
                clientId
        );
    }

    /**
     * 生成唯一请求标识
     */
    public String generateClientId() {
        return UUID.randomUUID().toString();
    }
}

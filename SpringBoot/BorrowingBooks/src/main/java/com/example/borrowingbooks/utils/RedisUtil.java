package com.example.borrowingbooks.utils;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ======================= String =======================
    public boolean set(String key, Object value, long minutes) {
        try {
            redisTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public <T> T get(String key, Class<T> clazz) {
        try {
            Object obj = redisTemplate.opsForValue().get(key);
            return obj == null ? null : JSON.parseObject(obj.toString(), clazz);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    // ======================= Hash =======================
    public boolean hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    public Object hGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        try {
            Object obj = redisTemplate.opsForHash().get(key, hashKey);
            return obj == null ? null : JSON.parseObject(obj.toString(), clazz);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public boolean hDelete(String key, String hashKey) {
        try {
            redisTemplate.opsForHash().delete(key, hashKey);
            return true;
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    public boolean hHasKey(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    // ======================= List =======================
    public <T> boolean lSet(String key, List<T> values, long seconds) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values.toArray());
            redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public <T> List<T> lGetTyped(String key, long start, long end, Class<T> clazz) {
        try {
            List<Object> rawList = redisTemplate.opsForList().range(key, start, end);
            if (rawList == null) return null;
            List<T> result = new ArrayList<>();
            for (Object obj : rawList) {
                result.add(JSON.parseObject(obj.toString(), clazz));
            }
            return result;
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    // ======================= Set =======================
    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public boolean sAdd(String key, Object value) {
        try {
            redisTemplate.opsForSet().add(key, value);
            return true;
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    // ======================= ZSet =======================
    public boolean zAdd(String key, Object value, double score) {
        try {
            redisTemplate.opsForZSet().add(key, value, score);
            return true;
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    // ======================= Expire =======================
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    // ======================= JSON Support =======================
    public boolean setJson(String key, Object value, long timeMinutes) {
        try {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(value), timeMinutes, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    public <T> T getJson(String key, Class<T> clazz) {
        try {
            String json = (String) redisTemplate.opsForValue().get(key);
            return json == null ? null : JSON.parseObject(json, clazz);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public <T> List<T> getJsonList(String key, Class<T> clazz) {
        try {
            String json = (String) redisTemplate.opsForValue().get(key);
            return json == null ? null : JSON.parseArray(json, clazz);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    // ======================= Common Methods =======================
    private void handleException(Exception e) {
        e.printStackTrace(); // Log the exception
        // You can add more sophisticated error handling here, e.g. logging to a file or sending alerts
    }
}

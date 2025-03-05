package com.parking.handler.redis;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RedisUtil {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;



    // ====== String类型操作 ======
    
    /**
     * 设置字符串类型的值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置字符串类型的值，同时设置过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 获取字符串类型的值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // ====== Hash类型操作 ======
    
    /**
     * 设置Hash的属性
     */
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 获取Hash的属性值
     */
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 批量获取Hash中多个field的值
     * @param key Redis的key
     * @param fields 需要获取的field列表
     * @return field-value映射的Map
     */
    public Map<String, Object> hMultiGet(String key, List<String> fields) {
        List<Object> values = redisTemplate.opsForHash().multiGet(key, Lists.newArrayList(fields));
        Map<String, Object> result = new HashMap<>();
        int i = 0;
        for (String field : fields) {
            if (values.get(i) != null) {
                result.put(field, values.get(i));
            }
            i++;
        }
        return result;
    }

    /**
     * 设置整个Hash
     */
    public void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 获取整个Hash
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    // ====== List类型操作 ======
    
    /**
     * 从左边添加元素到List
     */
    public void lPush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从右边添加元素到List
     */
    public void rPush(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 获取List范围内的元素
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    // ====== Set类型操作 ======
    
    /**
     * 添加元素到Set
     */
    public void sAdd(String key, Object... values) {
        redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 获取Set中的所有元素
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // ====== 通用操作 ======

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 删除键
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 判断键是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}

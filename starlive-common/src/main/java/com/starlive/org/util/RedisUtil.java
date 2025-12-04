package com.starlive.org.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private  RedisTemplate<String, Object> redisTemplate;
    public void setExpire(String key, long timeout, TimeUnit unit) {
redisTemplate.expire(key, timeout, unit);
    }

    /**
     * String类型操作
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean set(String key, Object value, long time,TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value,time,timeUnit);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean setIfAbsent(String lockKey,String lockValue,long time,TimeUnit timeUnit) {
       return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, time, timeUnit));
    }

    public boolean setAndTTL(String key, Object value, long timeOut) {
        try {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, timeOut, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    // List类型操作
    public void lpush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public Object lpop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public void rpush(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }
    public void rpushAllWithExpiry(String key, List<?> values, long timeout) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public Object range(String key, long start, long end) {return redisTemplate.opsForList().range(key, start, end);}

    public Object rpop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    // Set类型操作
    public boolean sadd(String key, Object value) {
        try {
            redisTemplate.opsForSet().add(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Set<Object> smembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public boolean srem(String key, Object value) {
        return redisTemplate.opsForSet().remove(key, value) > 0;
    }

    // Hash类型操作
    public void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    public void hdel(String key, String... fields) {
        redisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    // ZSet类型操作
    public boolean zadd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    public Set<Object> zrange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    public boolean zrem(String key, Object value) {
        return redisTemplate.opsForZSet().remove(key, value) > 0;
    }



    /**
     * 事务支持
     * @param transactionLogic
     */
    public void executeTransactionWithCallback(Runnable transactionLogic) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                transactionLogic.run();
                return operations.exec();
            }
        });
    }
    public void executeLuaScript(String luaScript, String lockKey, String lockValue) {
        redisTemplate.execute((RedisCallback<Long>) connection -> connection.eval(
                luaScript.getBytes(),           // Lua 脚本
                ReturnType.INTEGER,             // 返回类型
                1,                              // 键的数量
                lockKey.getBytes(),             // 锁的 Key
                lockValue.getBytes()            // 锁的 Value
        ));
    }


    // 创建固定大小的线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * 异步操作: String 类型
     * @param key
     * @param value
     * @return
     */
    public Future<Boolean> setAsync(String key, Object value) {
        return executorService.submit(() -> {
            try {
                redisTemplate.opsForValue().set(key, value);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public Future<Object> getAsync(String key) {
        return executorService.submit(() -> redisTemplate.opsForValue().get(key));
    }

    public Future<Boolean> deleteAsync(String key) {
        return executorService.submit(() -> redisTemplate.delete(key));
    }

    /**
     * 异步操作: List 类型
     * @param key
     * @param value
     * @return
     */
    public Future<Long> lpushAsync(String key, Object value) {
        return executorService.submit(() -> redisTemplate.opsForList().leftPush(key, value));
    }

    public Future<Object> lpopAsync(String key) {
        return executorService.submit(() -> redisTemplate.opsForList().leftPop(key));
    }

    /**
     *  异步操作: Set 类型
     * @param key
     * @param value
     * @return
     */
    public Future<Boolean> saddAsync(String key, Object value) {
        return executorService.submit(() -> {
            try {
                redisTemplate.opsForSet().add(key, value);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public Future<Set<Object>> smembersAsync(String key) {
        return executorService.submit(() -> redisTemplate.opsForSet().members(key));
    }

    /**
     *  关闭线程池
     */
    public void shutdown() {
        executorService.shutdown();
    }

}

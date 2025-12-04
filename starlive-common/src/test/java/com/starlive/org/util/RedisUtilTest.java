package com.starlive.org.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisUtilTest {

	@Autowired
	private RedisUtil redisUtil;

	@Test
	public void testStringOperations() {
		redisUtil.set("testKey", 10);  // 将值初始化为整数
		redisUtil.increment("testKey", 1);
		System.out.println(redisUtil.get("testKey"));
		assertEquals("11", redisUtil.get("testKey").toString());  // 确保增量正确
		redisUtil.delete("testKey");
		assertNull(redisUtil.get("testKey"));
		System.out.println(redisUtil.get("testKey"));
	}

	@Test
	public void testListOperations() {
		redisUtil.lpush("testList", "a");
		redisUtil.rpush("testList", "b");
		assertEquals("a", redisUtil.lpop("testList"));
		assertEquals("b", redisUtil.rpop("testList"));
	}

	@Test
	public void testSetOperations() {
		redisUtil.sadd("testSet", "a");
		assertTrue(redisUtil.smembers("testSet").contains("a"));
		redisUtil.srem("testSet", "a");
		assertFalse(redisUtil.smembers("testSet").contains("a"));
	}

	@Test
	public void testHashOperations() {
		redisUtil.hset("testHash", "field1", "value1");
		assertEquals("value1", redisUtil.hget("testHash", "field1"));
		redisUtil.hdel("testHash", "field1");
		assertNull(redisUtil.hget("testHash", "field1"));
	}

	@Test
	public void testZSetOperations() {
		redisUtil.zadd("testZSet1", "a", 1);
		redisUtil.zadd("testZSet1", "b", 2);
		assertEquals(2, redisUtil.zrange("testZSet1", 0, -1).size());
		redisUtil.zrem("testZSet1", "a");
		assertEquals(1, redisUtil.zrange("testZSet1", 0, -1).size());
	}

	@Test
	public void testTransaction() {
		redisUtil.executeTransactionWithCallback(() -> {
			redisUtil.set("txKey", "txValue");
			redisUtil.hset("txHash", "field1", "value1");
		});
		assertEquals("txValue", redisUtil.get("txKey"));
		assertEquals("value1", redisUtil.hget("txHash", "field1"));
	}

	@Test
	public void testAsyncOperations() throws ExecutionException, InterruptedException {
		Future<Boolean> setResult = redisUtil.setAsync("myKey", "myValue");
		if (setResult.get()) {  // get() 方法会阻塞，直到结果返回
			System.out.println("Set operation completed successfully");
		}

		Future<Object> getResult = redisUtil.getAsync("myKey");
		System.out.println("Get result: " + getResult.get());
	}

}


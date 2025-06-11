package com.rngad33.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArkPicturesApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void redisTest() {
        // 获取redis操作对象
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();

        // 设置模拟数据
        String key = "testKey";
        String value = "testValue";

        // 新增、更新测试
        valueOps.set(key, value);   // 设置键值对
        String storedValue = valueOps.get(key);
        assertEquals(value, storedValue, "——！存储的值与预期不一致！——");

        // 修改测试
        String updateValue = "updatedValue";
        valueOps.set(key, updateValue);
        storedValue = valueOps.get(key);
        assertEquals(updateValue, storedValue, "——！更新后的值与预期不一致！——");

        // 查询测试
        storedValue = valueOps.get(key);
        assertNotNull(storedValue, "——！查询失败！——");
        assertEquals(updateValue, storedValue, "——！查询的值与预期不一致！——");

        // 删除测试
        stringRedisTemplate.delete(key);
        storedValue = valueOps.get(key);
        assertNull(storedValue, "——！删除失败！——");

        System.out.println("测试结束，运行正常>>>");
    }

}
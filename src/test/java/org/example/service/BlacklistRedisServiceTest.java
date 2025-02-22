package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class BlacklistRedisServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private BlacklistRedisService blacklistRedisService;




    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testAddBlacklist() {
        doNothing().when(valueOperations).set("testKey", "testValue", 1000, TimeUnit.SECONDS);
        blacklistRedisService.addBlacklist("testKey", "testValue");
        verify(valueOperations, times(1)).set("testKey", "testValue", 1000, TimeUnit.SECONDS);
    }

    @Test
    void testGetBlacklist() {
        when(valueOperations.get("testKey")).thenReturn("testValue");
        String result = blacklistRedisService.getBlacklist("testKey");
        assertEquals("testValue", result);
        verify(valueOperations, times(1)).get("testKey");
    }

    @Test
    void testRemoveBlacklist() {
        String key = "testKey";
        when(redisTemplate.delete(any(String.class))).thenReturn(true);

        Boolean result = blacklistRedisService.removeBlacklist(key);

        assertTrue(result);
        verify(redisTemplate, times(1)).delete(key);
    }


}

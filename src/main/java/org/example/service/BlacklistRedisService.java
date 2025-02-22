package org.example.service;

import org.example.constants.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BlacklistRedisService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public void addBlacklist(String key,String value){
        redisTemplate.opsForValue().set(key,value, AppConstants.TimeToLive, TimeUnit.SECONDS);
    }
    public String getBlacklist(String key){
        String value = redisTemplate.opsForValue().get(key);
        return value;
    }

    public boolean removeBlacklist(String key){
       return redisTemplate.delete(key);
    }


}

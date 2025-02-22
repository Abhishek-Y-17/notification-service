package org.example.utils;

import org.example.service.BlacklistRedisService;
import org.example.service.BlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class HelperBlacklist {

    @Autowired
    private BlacklistRedisService blacklistRedisService;

    @Autowired
    private BlacklistService blacklistService;

    public Boolean isBlacklisted( String phoneNo) {
        String blacklist = blacklistRedisService.getBlacklist(phoneNo);
        if (blacklist != null) {
            System.out.println("cache success");
            return true;
        }
        Boolean isPhoneNo =  blacklistService.checkByPhoneNo(phoneNo);
        if(isPhoneNo) {
            blacklistRedisService.addBlacklist(phoneNo,"1");
        }
        return isPhoneNo;
    }

}

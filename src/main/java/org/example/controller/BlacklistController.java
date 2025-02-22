package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.Exceptions.InvalidPhoneNoException;
import org.example.entity.Blacklist;
import org.example.request.BlacklistRequest;
import org.example.service.BlacklistRedisService;
import org.example.service.BlacklistService;
import org.example.utils.Helper;
import org.example.utils.HelperBlacklist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/blacklist")
public class BlacklistController {

    @Autowired
    private BlacklistService blacklistService;
    @Autowired
    private BlacklistRedisService blacklistRedisService;

    @Autowired
    private Helper helper;

    @GetMapping("")
    public String blacklist() {
        log.debug("retrieving complete blacklist");
        return blacklistService.getAll().toString();
    }


    @PostMapping("")
    public ResponseEntity<?> addBlacklist(@RequestBody BlacklistRequest blacklistRequest) {
        List<Blacklist> blacklists = new ArrayList<>();
        for (String phoneNo : blacklistRequest.getPhoneNumbers()) {
            if(!helper.isPhoneNoValid(phoneNo)) {
                log.info("Phone number not valid: {}", phoneNo);
                continue;
            }
           blacklists.add( blacklistService.create(phoneNo));
        }
        if(blacklists.isEmpty()) {
            throw new InvalidPhoneNoException("All the phone numbers are invalid");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(blacklists);
    }

    @DeleteMapping("")
    public ResponseEntity<String> removeFromBlacklist(@RequestBody BlacklistRequest blacklistRequest) {
        List<String> whitelistedNumbers = new ArrayList<>();

        for (String phoneNo : blacklistRequest.getPhoneNumbers()) {
            if (blacklistService.checkByPhoneNo(phoneNo)) {
                String blacklistedPhoneNo = blacklistRedisService.getBlacklist(phoneNo);
                if (blacklistedPhoneNo != null) {
                    blacklistRedisService.removeBlacklist(phoneNo);
                }
                blacklistService.deleteByPhoneNo(phoneNo);
                whitelistedNumbers.add(phoneNo);
            } else {
                log.info("Phone number not valid: {}", phoneNo);
            }
        }

        if (whitelistedNumbers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No valid phone numbers were whitelisted.");
        }

        return ResponseEntity.ok("Successfully whitelisted phone numbers: " + String.join(", ", whitelistedNumbers));
    }

}

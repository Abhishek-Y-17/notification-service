package org.example.service;

import org.example.entity.Blacklist;
import org.example.repository.jpa.BlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlacklistService {

    @Autowired
    private BlacklistRepository blacklistRepository;


    public List<Blacklist> getAll() {
        return blacklistRepository.findAll();
    }

    public boolean checkByPhoneNo(String phoneNo) {
        return blacklistRepository.existsByPhoneNo(phoneNo);
    }


    public Blacklist create(String phoneNo) {
        Blacklist blacklist = new Blacklist(phoneNo);
        return blacklistRepository.save(blacklist);
    }

    @Transactional
    public void deleteByPhoneNo(String phoneNo) {
         blacklistRepository.deleteByPhoneNo(phoneNo);
    }



}

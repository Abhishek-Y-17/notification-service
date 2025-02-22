package org.example.service;


import org.example.entity.Blacklist;
import org.example.repository.jpa.BlacklistRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class BlacklistServiceTest {

    @Mock
    private BlacklistRepository blacklistRepository;

    @InjectMocks
    private BlacklistService blacklistService;

    @Test
    void createBlacklistTest(){
        Blacklist blacklist = new Blacklist("9999999999");

        Mockito.when(blacklistRepository.save(blacklist)).thenReturn(blacklist);

        Blacklist result = blacklistService.create(blacklist.getPhoneNo());
        Assertions.assertEquals(blacklist.getPhoneNo(), result.getPhoneNo());
    }

    @Test
    void getAllTest(){
        Blacklist blacklist1 = new Blacklist("9999999999");
        Blacklist blacklist2 = new Blacklist("9999999999");

        Mockito.when(blacklistRepository.findAll()).thenReturn(Arrays.asList(blacklist1, blacklist2));

        List<Blacklist> result = blacklistService.getAll();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(blacklist1, result.get(0));
        Assertions.assertEquals(blacklist2, result.get(1));
    }

    @Test
    void testCheckByPhoneNo_whenPhoneNoExists(){
        String phoneNo = "9999999999";

        Mockito.when(blacklistRepository.existsByPhoneNo(phoneNo)).thenReturn(true);
        boolean result = blacklistService.checkByPhoneNo(phoneNo);
        Assertions.assertTrue(result);
        verify(blacklistRepository,times(1)).existsByPhoneNo(phoneNo);
    }

    @Test
    void testCheckByPhoneNo_whenPhoneNoDoesNotExists(){
        String phoneNo = "9999999999";
        Mockito.when(blacklistRepository.existsByPhoneNo(phoneNo)).thenReturn(false);
        boolean result = blacklistService.checkByPhoneNo(phoneNo);
        Assertions.assertFalse(result);
        verify(blacklistRepository,times(1)).existsByPhoneNo(phoneNo);
    }

    @Test
    void deleteByPhoneNo(){
        String phoneNo = "9999999999";
        blacklistService.deleteByPhoneNo(phoneNo);
        verify(blacklistRepository,times(1)).deleteByPhoneNo(phoneNo);
    }


}

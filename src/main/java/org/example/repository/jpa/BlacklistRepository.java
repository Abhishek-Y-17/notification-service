package org.example.repository.jpa;


import org.example.entity.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface BlacklistRepository extends JpaRepository<Blacklist, Integer> {
    Boolean existsByPhoneNo(String phoneNo);

    @Modifying
    @Transactional
    void deleteByPhoneNo(String phoneNo);
}

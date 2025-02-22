package org.example.repository.jpa;

import org.example.entity.SmsRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsRequestRepository extends JpaRepository<SmsRequests,Integer> {

}
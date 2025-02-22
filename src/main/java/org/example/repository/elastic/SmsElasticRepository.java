package org.example.repository.elastic;

import org.example.entity.SmsReqElastic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsElasticRepository  extends ElasticsearchRepository<SmsReqElastic,Integer> {

    Page<SmsReqElastic> findAllByPhoneNo(String phoneNo, Pageable pageable);
    Page<SmsReqElastic> findAllByMessage(String text,Pageable pageable);
//    Iterable<SmsReqElastic> findByDate(Date date);
}
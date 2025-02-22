package org.example.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Enums.Status;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Data
@Document(indexName = "smsrequest")
public class SmsReqElastic {

    @Id
    private Integer id ;
    private String phoneNo;
    private String message;
    private Status status;
    private String failure_code;
    private String failure_comments;
    @Field(type = FieldType.Date ,format = DateFormat.date_time)
    private Instant created_at;
    @Field(type = FieldType.Date ,format = DateFormat.date_time)
    private Instant updated_at;

};



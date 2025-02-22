package org.example.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Enums.Status;
import org.example.request.CreateSmsRequest;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Data
@Table(name="sms_requests")
public class SmsRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id ;

    @Column(name = "phone_no")
    private String phoneNo;
    @Column(name = "message")
    private String message;

    @Column(name="status")
    private Status status;

    @Column(name = "failure_code")
    private String failure_code;

    @Column(name = "failure_comments")
    private String failure_comments;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime  created_at;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime  updated_at;

    public SmsRequests(CreateSmsRequest createSmsRequest) {
        this.phoneNo = createSmsRequest.getPhoneNo();
        this.message = createSmsRequest.getMessage();
    }
};



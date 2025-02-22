package org.example.response;
import org.example.entity.SmsRequests;

import lombok.Data;


@Data
public class SmsRequestResponse {
    private String phone_no;
    private String message;
    public SmsRequestResponse( SmsRequests smsRequest ) {
        this.phone_no = smsRequest.getPhoneNo();
        this.message = smsRequest.getMessage();
    }

}
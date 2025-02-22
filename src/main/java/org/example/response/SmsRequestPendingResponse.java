package org.example.response;
import lombok.Data;
import org.example.Enums.Status;
import org.example.entity.SmsRequests;

@Data
public class SmsRequestPendingResponse
{
    private Integer msgId;
    private Status status;
    public SmsRequestPendingResponse(SmsRequests smsRequests){
        this.msgId = smsRequests.getId();
        this.status = smsRequests.getStatus();
    }
}

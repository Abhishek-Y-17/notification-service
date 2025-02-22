package org.example.request;

import lombok.Data;

@Data
public class DateRangeRequest {
    private String phoneNo;
    private String fromDateTime;
    private String toDateTime ;
}

package org.example.response;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Data
class Response{
    private String code;
    private String description;
    private String transid;
}
@Data
public class SmsResponse {
    private Response response;
}


package org.example.request;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CreateSmsRequest {

    @NotNull(message = "phoneNo shouldn't be null")
    @NotEmpty(message = "phoneNo shouldn't be empty")
    private String phoneNo;

    @NotNull(message = "Message shouldn't be null")
    @NotEmpty(message = "Message shouldn't be empty")
    private String message;
    public CreateSmsRequest(String phoneNo, String message) {
        this.phoneNo = phoneNo;
        this.message = message;
    }
}
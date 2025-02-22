package org.example.utils;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

@Component
public class Helper {
    public Boolean isPhoneNoValid(String phoneNo) {

        if( phoneNo == null || !(phoneNo.length()==10 || phoneNo.length()==13) ){
            return false;
        }
        String str = phoneNo.substring(1);
        for(char c : str.toCharArray()) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }
        return true;

    }
}

package com.mashreq.wealth.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;


public class Token implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8401709953679307209L;

    @Getter
    @Setter
    private static String jwtToken;

    public static boolean isThereAJwtToken() {
        return !(jwtToken == null || jwtToken.trim().isEmpty());
    }
}

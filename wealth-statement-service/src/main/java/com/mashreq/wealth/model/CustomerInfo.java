package com.mashreq.wealth.model;


import com.mashreq.wealth.enums.BusinessGroupType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.image.BufferStrategy;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerInfo {
    private String cifId;
    private String firstname;
    private String lastname;
    private String email;
    private BusinessGroupType businessGroupType;
}

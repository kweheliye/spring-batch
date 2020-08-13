package com.mashreq.wealth.client.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerSearchRequest {
    String cifId;
}

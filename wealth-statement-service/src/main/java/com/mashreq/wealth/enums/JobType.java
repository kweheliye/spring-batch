package com.mashreq.wealth.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JobType {
    EOM ("eomStatementJob"),
    ADHOC ("adhocStatementJob"),
    LOAD_CUSTOMERS(""),
    CUSTOMER_SERVICE("");
    private final String jobName ;

    public static JobType findJobTypeByName(String jobName){
        for( JobType v: values()){
            if(v.jobName.equalsIgnoreCase(jobName))
                return v;
        }
        return null;
    }
    public String getJobName() {
        return jobName;
    }
}

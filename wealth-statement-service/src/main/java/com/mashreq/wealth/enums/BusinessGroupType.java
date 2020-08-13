package com.mashreq.wealth.enums;

public enum BusinessGroupType {
    RBG,
    RETAIL,
    UNKNOWN;

    public static BusinessGroupType findBusinessGroupTypeByValue(String value) {
        for (BusinessGroupType type : values()) {
            if (type.name().equalsIgnoreCase(value))
                return type;
        }
        return UNKNOWN;
    }

}

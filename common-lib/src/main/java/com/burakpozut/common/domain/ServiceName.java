package com.burakpozut.common.domain;

public enum ServiceName {
    PAYMENT("PAYMENT"),
    PRODUCT("PRODUCT"),
    NOTIFICATION("NOTIFICATION");

    private final String value;

    ServiceName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}

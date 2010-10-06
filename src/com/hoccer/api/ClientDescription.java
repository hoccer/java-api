package com.hoccer.api;

public class ClientDescription {

    private final String mApplicationName;

    public ClientDescription(String applicatioName) {
        mApplicationName = applicatioName;
    }

    public String getApplicationName() {
        return mApplicationName;
    }
}

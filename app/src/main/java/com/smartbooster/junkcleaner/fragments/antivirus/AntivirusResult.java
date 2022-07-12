package com.smartbooster.junkcleaner.fragments.antivirus;

import com.trustlook.sdk.data.AppInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AntivirusResult implements Serializable {

    private boolean success;
    private List<AppInfo> apps;
    private String error_message;

    public AntivirusResult() {
        success = false;
        apps = new ArrayList<>();
        error_message = "";
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<AppInfo> getApps() {
        return apps;
    }

    public void setApps(List<AppInfo> apps) {
        this.apps = apps;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
}

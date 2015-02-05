package com.next.eswaraj.base;


public class BaseEvent {

    private Boolean success;
    private String error;

    public BaseEvent() {
        this.success = false;
        this.error = null;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public String getError() {
        return this.error;
    }
}

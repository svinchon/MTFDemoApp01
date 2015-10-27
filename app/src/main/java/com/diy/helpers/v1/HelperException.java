package com.diy.helpers.v1;

public class HelperException extends Exception {
    public String helper, method, message;
    public HelperException(String helper, String method, String message) {
        this.helper = helper;
        this.method = method;
        this.message = message;
    }
}

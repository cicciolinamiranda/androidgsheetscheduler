package com.google.scheduler.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by cicciolina on 5/18/18.
 */

public class ApiException extends Exception {
    int code = 0;
    String message = null;

    public ApiException() {}

    public ApiException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiException(String s, int code, Map<String, List<String>> stringListMap, String respBody) {
        this.code = code;
        this.message = s;
    }

    public ApiException(IOException e) {
        this.message = e.getMessage();
    }

    public ApiException(String s) {
        this.message = s;
    }

    public ApiException(String message, IOException e, int code, Map<String, List<String>> stringListMap) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

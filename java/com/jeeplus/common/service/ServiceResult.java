package com.jeeplus.common.service;

/**
 * @description:
 * @copyright: 福建骏华信息有限公司 (c)2018
 * @createTime: 2018/1/23 11:55
 * @author：lxb
 * @version：1.0
 */
public class ServiceResult {
    private boolean isSuccess = false;
    private String message;
    private Object data;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

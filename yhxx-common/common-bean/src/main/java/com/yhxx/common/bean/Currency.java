package com.yhxx.common.bean;

/**
 * @Author: Wanglf
 * @Date: Created in 17:25 2018/6/9
 * @modified By:
 */
public enum Currency implements TypeBean {

    CNY(1, "人民币");

    /**
     * 代码
     */
    private int code;

    /**
     * 名称
     */
    private String name;

    Currency(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }
}

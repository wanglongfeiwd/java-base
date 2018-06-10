package com.yhxx.common.bean;

/**
 * @author lingzhen on 17/9/24.
 */
public class BeanException extends RuntimeException {

    public BeanException(String message) {
        super(message);
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanException(Throwable message) {
        super(message);
    }


}

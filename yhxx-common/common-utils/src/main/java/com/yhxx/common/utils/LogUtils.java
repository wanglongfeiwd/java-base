package com.yhxx.common.utils;

/**
 * @Author: Wanglf
 * @Date: Created in 18:29 2018/6/9
 * @modified By:
 */
public class LogUtils {
    /**
     * 构建日志
     *
     * @param args
     * @return
     */
    public static String message(Object[] args) {
        return message(null, args, null);
    }

    /**
     * 构建日志
     *
     * @param message
     * @param args
     * @return
     */
    public static String message(String message, Object[] args) {
        return message(message, args, null);
    }

    /**
     * 构建日志
     *
     * @param args
     * @param result
     * @return
     */
    public static String message(Object[] args, Object result) {
        return message(null, args, result);
    }

    /**
     * 构建日志
     *
     * @param message
     * @param args
     * @param result
     * @return
     */
    public static String message(String message, Object[] args, Object result) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n----------log message begin----------\n");
        if(message != null) {
            builder.append("msg: ").append(message).append("\n");
        }
        if(args != null) {
            for(int i = 0, len = args.length; i < len; i++) {
                builder.append("arg").append(i).append(": ").append(args[i]).append("\n");
            }
        }
        if(result != null) {
            builder.append("result: ").append(result).append("\n");
        }
        builder.append("----------log message end----------\n");
        return builder.toString();
    }
}

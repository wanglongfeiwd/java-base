package com.yhxx.common.utils;

import java.io.Closeable;

/**
 * @author lingzhen on 17/11/1.
 */
public class CloseUtils {

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            // 忽略
        }
    }
}

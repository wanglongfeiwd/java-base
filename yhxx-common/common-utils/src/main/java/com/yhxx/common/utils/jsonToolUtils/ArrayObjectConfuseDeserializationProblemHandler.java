package com.yhxx.common.utils.jsonToolUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;

/**
 * @author lingzhen on 17/12/4.
 */
public class ArrayObjectConfuseDeserializationProblemHandler extends DeserializationProblemHandler {

    public Object handleUnexpectedToken(DeserializationContext ctxt, Class<?> targetType, JsonToken t, JsonParser p, String failureMsg) throws IOException {
        try {
            if (targetType.isArray()) {
                // 数组
                if (JsonToken.START_OBJECT.equals(t)) {
                    // 数组却给对象的token
                    nextToken(p, JsonToken.END_OBJECT);
                    return Array.newInstance(targetType.getComponentType(), 0);
                }
            } else if (Collection.class.isAssignableFrom(targetType)) {
                // 集合类
                if (JsonToken.START_OBJECT.equals(t)) {
                    // 数组却给对象的token
                    nextToken(p, JsonToken.END_OBJECT);
                    return targetType.newInstance();
                }
            } else {
                // 非数组或者是集合类
                if (JsonToken.START_ARRAY.equals(t)) {
                    // 对象却给数组的token
                    nextToken(p, JsonToken.END_ARRAY);
                    return null;
                }
            }
        } catch (Exception e) {
            // ignore
        }

        // 返回原始值
        return DeserializationProblemHandler.NOT_HANDLED;
    }

    private void nextToken(JsonParser p, JsonToken t) throws Exception {
        JsonToken nextToken = p.nextToken();
        if (!t.equals(nextToken)) {
            throw new JsonParseException(p, "下一个token与期待[" + t + "]不符:" + nextToken);
        }
    }

}

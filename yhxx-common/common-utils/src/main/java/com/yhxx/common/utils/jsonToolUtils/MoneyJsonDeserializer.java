package com.yhxx.common.utils.jsonToolUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.yhxx.common.bean.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @Author: Wanglf
 * @Date: Created in 17:41 2018/6/9
 * @modified By:
 */
public class MoneyJsonDeserializer extends JsonDeserializer<Money> {

    private static final Logger logger = LoggerFactory.getLogger(MoneyJsonDeserializer.class);
    @Override
    public Money deserialize(JsonParser json, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        if (JsonToken.VALUE_NUMBER_INT.equals(json.getCurrentToken())) {
            // 整型当分转换
            int value = json.getValueAsInt();
            return new Money(value);
        }

        // 字符串处理
        String value = json.getValueAsString();
        return new Money(value);
    }
}

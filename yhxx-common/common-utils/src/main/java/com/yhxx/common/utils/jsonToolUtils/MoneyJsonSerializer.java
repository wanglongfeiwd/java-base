package com.yhxx.common.utils.jsonToolUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.yhxx.common.bean.Money;

import java.io.IOException;

/**
 * @Author: Wanglf
 * @Date: Created in 17:36 2018/6/9
 * @modified By:
 */
public class MoneyJsonSerializer extends JsonSerializer<Money> {
    @Override
    public void serialize(Money value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        if (value == null) {
            jgen.writeNull();
        }else {
            //money序列化的时候序列化成 “分”
            jgen.writeNumber(value.intMinUnitValue());
        }

    }
}

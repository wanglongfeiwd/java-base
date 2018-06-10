package com.yhxx.common.utils.jsonToolUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.yhxx.common.bean.StringTypeBean;
import com.yhxx.common.bean.TypeBean;

import java.io.IOException;

/**
 * @Author: Wanglf
 * @Date: Created in 18:11 2018/6/9
 * @modified By:
 */
public class EnumJsonDeserializer<E extends Enum<E>> extends JsonDeserializer<E> implements ContextualDeserializer {

    private Class<E> clazz;

    public EnumJsonDeserializer() {
    }

    public EnumJsonDeserializer(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        Class<E> clazz = (Class<E>) deserializationContext.getContextualType().getRawClass();
        return new EnumJsonDeserializer<>(clazz);
    }


    @Override
    public E deserialize(JsonParser json, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonToken currJsonToken = json.getCurrentToken();
        if (JsonToken.VALUE_NULL.equals(currJsonToken)) {
            // null
            return null;
        }

        //处理框架内部的枚举
        if (TypeBean.class.isAssignableFrom(clazz)) {
            // TypeBean
            int value = json.getValueAsInt();
            return TypeBeanUtils.getType(clazz, value);
        }

        if (StringTypeBean.class.isAssignableFrom(clazz)) {
            // StringTypeBean
            String value = json.getValueAsString();
            return StringTypeBeanUtils.getType(clazz, value);
        }

        //处理普通类型的枚举
        if (JsonToken.VALUE_NUMBER_INT.equals(currJsonToken)) {
            int value = json.getValueAsInt();
            return (E) EnumUtils.getEnumList(clazz).get(value);
        }

        if (JsonToken.VALUE_STRING.equals(currJsonToken)) {
            // 字符作为枚举的name
            String value = json.getValueAsString();
            return EnumUtils.getEnum(clazz, value);
        }

        //异常情况
        return null;
    }

}

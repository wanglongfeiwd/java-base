package com.yhxx.common.utils.jsonToolUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.yhxx.common.bean.StringTypeBean;
import com.yhxx.common.bean.TypeBean;

import java.io.IOException;

/**
 * @Author: Wanglf
 * @Date: Created in 17:49 2018/6/9
 * @modified By:
 */
public class EnumJsonSerializer<E extends Enum<E>> extends JsonSerializer<E> implements ContextualSerializer {


    private Class<E> clazz;

    public EnumJsonSerializer() {

    }

    public EnumJsonSerializer(Class<E> clazz) {
        this.clazz = clazz;
    }


    //TODO 主要的作用是什么
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {

        Class<E> clazz = (Class<E>) beanProperty.getType().getRawClass();
        return new EnumJsonSerializer<>(clazz);
    }


    @Override
    public void serialize(E value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        if (value == null) {
            jgen.writeNull();
        } else if (TypeBean.class.isAssignableFrom(clazz)) {
            jgen.writeNumber(((TypeBean) value).getCode());
        } else if (StringTypeBean.class.isAssignableFrom(clazz)) {
            jgen.writeString(((StringTypeBean) value).getCodeString());
        } else {
            jgen.writeString(value.name());
        }

    }

}

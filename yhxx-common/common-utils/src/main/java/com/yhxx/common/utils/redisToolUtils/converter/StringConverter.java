package com.yhxx.common.utils.redisToolUtils.converter;

public interface StringConverter<T> {

	String serialize(T value);
	
	T deserialize(String value, Class<T> clazz);
	
}

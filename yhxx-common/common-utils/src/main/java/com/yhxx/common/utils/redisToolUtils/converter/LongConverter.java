package com.yhxx.common.utils.redisToolUtils.converter;

public class LongConverter implements StringConverter<Long> {

	@Override
	public String serialize(Long value) {
		if(value != null) {
			return String.valueOf(value);
		}
		return null;
	}

	@Override
	public Long deserialize(String value, Class<Long> clazz) {
		if(value != null) {
			return Long.parseLong(value);
		}
		return null;
	}

}

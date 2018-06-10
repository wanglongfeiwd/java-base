package com.yhxx.common.utils.redisToolUtils.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhxx.common.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 
 * 
 * @author zsp
 *
 * @param <T>
 */
public class BeanConverter<T> implements StringConverter<T> {

	private static final Logger logger = LoggerFactory.getLogger(BeanConverter.class);
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public String serialize(T value) {
		try {
			return mapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			logger.error(LogUtils.message("序列化错误", new Object[] {value}), e);
		}
		return null;
	}

	@Override
	public T deserialize(String value, Class<T> clazz) {
		if(value != null) {
			try {
				return mapper.readValue(value, clazz);
			} catch (IOException e) {
				logger.error(LogUtils.message("反序列化错误", new Object[] {clazz, value}), e);
			}
		}
		return null;
	}

}

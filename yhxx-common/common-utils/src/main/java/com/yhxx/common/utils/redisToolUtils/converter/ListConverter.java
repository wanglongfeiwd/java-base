package com.yhxx.common.utils.redisToolUtils.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhxx.common.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author zsp
 *
 * @param <T>
 */
public class ListConverter<T, E> implements StringConverter<T> {

	private static final Logger logger = LoggerFactory.getLogger(ListConverter.class);
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public String serialize(T value) {
		if(value != null) {
			try {
				return mapper.writeValueAsString(value);
			} catch (JsonProcessingException e) {
				logger.error(LogUtils.message("序列化错误", new Object[] {value}), e);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(String value, Class<T> clazz) {
		if(value != null) {
			try {
				JsonNode jsonNode = mapper.readTree(value);
				if(jsonNode.isArray()) {
					List<E> list = new ArrayList<E>();
					for(JsonNode element : jsonNode) {
						list.add(mapper.readValue(element.toString(), (Class<E>)clazz.getComponentType()));
					}
					return (T)list;
				} else {
					throw new IllegalArgumentException("value is not an array, or incorrect length of classes");
				}
			} catch (IOException e) {
				logger.error(LogUtils.message("反序列化错误", new Object[] {clazz, value}), e);
			}
		}
		return null;
	}

}

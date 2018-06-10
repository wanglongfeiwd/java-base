package com.yhxx.common.utils.jsonToolUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Json工具
 * 
 * @author zsp
 *
 */
public class JsonUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	
	private static ObjectMapper defaultMapper = new ObjectMapper();

	public void setObjectMapper(ObjectMapper mapper) {
		defaultMapper = mapper;
	}
	
	/**
	 * 序列化成字符串
	 */
	public static String toJson(Object value) {
		return toJson(defaultMapper, value);
	}
	
	/**
	 * 序列化成字符串
	 */
	public static String toJson(final ObjectMapper mapper, Object value) {
		if(value == null) {
			return null;
		}
		ObjectMapper _mapper = mapper;
		if(_mapper == null) {
			_mapper = defaultMapper;
		}
		try {
			return _mapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			logger.error("序列化错误: value=" + value, e);
		}
		return null;
	}
	
	/**
	 * 根据类型反序列化为对象
	 */
	public static <T> T parse(String value, Class<T> clasz) {
		return parse(defaultMapper, value, clasz);
	}

	/**
	 * 根据类型反序列化为对象
	 */
	public static <T> T parse(final ObjectMapper mapper, String value, Class<T> clasz) {
		if(clasz == null) {
			throw new IllegalArgumentException("clasz is null");
		}
		if(value == null || "".equals(value.trim())) {
			return null;
		}
		ObjectMapper _mapper = mapper;
		if(_mapper == null) {
			_mapper = defaultMapper;
		}
		try {
			return _mapper.readValue(value, clasz);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("反序列化错误: class=" + clasz + ", value=" + value, e);
		}
		return null;
	}

	public static <T> T parse(String value, Type type) {
		return parse(defaultMapper, value, type);
	}

	public static <T> T parse(final ObjectMapper mapper, String value, Type type) {
		if(type == null) {
			throw new IllegalArgumentException("type is null");
		}
		if(value == null || "".equals(value.trim())) {
			return null;
		}
		ObjectMapper _mapper = mapper;
		if(_mapper == null) {
			_mapper = defaultMapper;
		}
		try {
			return _mapper.readValue(value, mapper.constructType(type));
		} catch (IOException e) {
			logger.error("反序列化错误: type=" + type + ", value=" + value, e);
		}
		return null;
	}

	public static ObjectMapper constructObjectMapperForArrayObjectConfuse() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.addHandler(new ArrayObjectConfuseDeserializationProblemHandler());
		return mapper;
	}

	public static JavaType constructParametricType(Class<?> parametrized, Class... parameterClasses) {
		return constructParametricType(defaultMapper, parametrized, parameterClasses);
	}

	public static JavaType constructParametricType(ObjectMapper mapper, Class<?> parametrized, Class... parameterClasses) {
		ObjectMapper _mapper = mapper;
		if(_mapper == null) {
			_mapper = defaultMapper;
		}
		return _mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
	}

	public static <T> T parse(String value, JavaType javaType) {
		return parse(defaultMapper, value, javaType);
	}

	public static <T> T parse(final ObjectMapper mapper, String value, JavaType javaType) {
		if(javaType == null) {
			throw new IllegalArgumentException("javaType is null");
		}
		if(value == null || "".equals(value.trim())) {
			return null;
		}
		ObjectMapper _mapper = mapper;
		if(_mapper == null) {
			_mapper = defaultMapper;
		}
		try {
			return _mapper.readValue(value, javaType);
		} catch (IOException e) {
			logger.error("反序列化错误: javaType=" + javaType + ", value=" + value, e);
		}
		return null;
	}

	/**
	 * 根据类型反序列化为对象
	 */
	public static <T> T parse(byte[] value, Class<T> clasz) {
		return parse(defaultMapper, value, clasz);
	}
	
	/**
	 * 根据类型反序列化为对象
	 */
	public static <T> T parse(final ObjectMapper mapper, byte[] value, Class<T> clasz) {
		if(clasz == null) {
			throw new IllegalArgumentException("clasz is null");
		}
		if(value == null) {
			return null;
		}
		ObjectMapper _mapper = mapper;
		if(_mapper == null) {
			_mapper = defaultMapper;
		}
		try {
			return _mapper.readValue(value, clasz);
		} catch (IOException e) {
			logger.error("反序列化错误: class=" + clasz + ", value=" + value, e);
		}
		return null;
	}
	
	/**
	 * 根据类型反序列化为对象
	 */
	public static <T> T parse(Object value, Class<T> clasz) {
		return parse(defaultMapper, value, clasz);
	}
	
	/**
	 * 根据类型反序列化为对象
	 */
	public static <T> T parse(final ObjectMapper mapper, Object value, Class<T> clasz) {
		if(clasz == null) {
			throw new IllegalArgumentException("clasz is null");
		}
		if(value == null) {
			return null;
		}
		ObjectMapper _mapper = mapper;
		if(_mapper == null) {
			_mapper = defaultMapper;
		}
		try {
			if(value instanceof String) {
				return _mapper.readValue((String)value, clasz);
			} else if(value instanceof byte[]) {
				return _mapper.readValue((byte[])value, clasz);
			} else {
				throw new IllegalArgumentException("value should be String or byte[]");
			}
		} catch (IOException e) {
			logger.error("反序列化错误: class=" + clasz + ", value=" + value, e);
		}
		return null;
	}
	
	/**
	 * 根据类型反序列化为对象数组
	 */
	public static Object[] parseArray(String value, 
			Class<?>[] classes) {
		return parseArray(defaultMapper, value, classes);
	}
	
	/**
	 * 根据类型反序列化为对象数组
	 */
	public static Object[] parseArray(final ObjectMapper mapper, String value, 
			Class<?>[] classes) {
		if(classes == null || classes.length == 0) {
			throw new IllegalArgumentException("classes is null or empty");
		}
		if(value == null || "".equals(value.trim())) {
			return null;
		}
		ObjectMapper _mapper = mapper;
		if(_mapper == null) {
			_mapper = defaultMapper;
		}
		try {
			JsonNode jsonNode = mapper.readTree(value);
			if(jsonNode.isArray() && jsonNode.size() == classes.length) {
				int index = 0;
				List<Object> list = new ArrayList<Object>();
				for(JsonNode element : jsonNode) {
					list.add(mapper.readValue(element.toString(), classes[index]));
					index++;
				}
				return list.toArray();
			} else {
				throw new IllegalArgumentException("value is not an array, or incorrect length of classes");
			}
		} catch (IOException e) {
			StringBuilder builder = new StringBuilder();
			for(Class<?> clasz : classes) {
				builder.append(clasz).append(",");
			}
			logger.error("反序列化错误: classes=" + builder.toString() + " value=" + value, e);
		}
		return null;
	}

	public static <T> T[] parseArrayByElementClass(String value, Class<T> clazz) {
		return parseArrayByElementClass(defaultMapper, value, clazz);
	}

	/**
	 * 根据类型反序列化为对象数组
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] parseArrayByElementClass(final ObjectMapper mapper, String value,
									  Class<T> clazz) {
		if(clazz == null) {
			throw new IllegalArgumentException("classes is null or empty");
		}
		if(value == null || "".equals(value.trim())) {
			return null;
		}
		ObjectMapper _mapper = mapper;
		if(_mapper == null) {
			_mapper = defaultMapper;
		}
		try {
			JsonNode jsonNode = mapper.readTree(value);
			if(jsonNode.isArray()) {
				List<T> list = new ArrayList<>();
				for(JsonNode element : jsonNode) {
					list.add(mapper.readValue(element.toString(), clazz));
				}
				return (T[]) list.toArray();
			} else {
				throw new IllegalArgumentException("value is not an array, or incorrect length of classes");
			}
		} catch (IOException e) {
			StringBuilder builder = new StringBuilder();
			builder.append(clazz);
			logger.error("反序列化错误: classes=" + builder.toString() + " value=" + value, e);
		}
		return null;
	}

	
}

package com.yhxx.common.utils.redisToolUtils.util;

/**
 * 键的构建工具
 * 
 * @author zsp
 *
 */
public class KeyBuilder {
	
	public final static char KEY_SEPARATOR = ':';
	
	private final StringBuilder builder;
	private char separator = KEY_SEPARATOR;
	
	public KeyBuilder() {
		builder = new StringBuilder();
	}
	
	public KeyBuilder(char separator) {
		builder = new StringBuilder();
		this.separator = separator;
	}
	
	/**
	 * 构建键
	 * 
	 * @param parts 构成键的多个部分
	 * @return
	 */
	public KeyBuilder build(String... parts) {
		int keyLength = builder.length();
		if(parts != null && parts.length > 0) {
			final char sep = separator;
			if(keyLength > 0) {
				builder.append(sep);
			}
			String part;
			for(int i = 0, len = parts.length, comparand = len - 1; i < len; i++) {
				part = parts[i];
				if(!isNullOrEmpty(part)) {
					builder.append(part);
					if(i < comparand) {
						builder.append(sep);
					}
				}
			}
		}
		return this;
	}
	
	/**
	 * 构建键，追加
	 * 
	 * @param part 往键后面追加的部分
	 * @return
	 */
	public KeyBuilder append(String part) {
		if(!isNullOrEmpty(part)) {
			int keyLength = builder.length();
			if(keyLength > 0) {
				builder.append(separator);
			}
			builder.append(part);
		}
		return this;
	}
	
	/**
	 * 构建键，插入
	 * 
	 * @param part 往键前面插入的部分
	 * @return
	 */
	public KeyBuilder insert(String part) {
		if(!isNullOrEmpty(part)) {
			builder.insert(0, separator);
			builder.insert(0, part);
		}
		return this;
	}
	
	/**
	 * 返回redis键
	 */
	@Override
	public String toString() {
		return builder.toString();
	}
	
	private boolean isNullOrEmpty(String part) {
		return part == null || part.length() == 0;
	}

}

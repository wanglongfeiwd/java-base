package com.yhxx.common.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommonLogger {

	public static final Logger WARN = LoggerFactory.getLogger("warn");

	public static final Logger ERROR = LoggerFactory.getLogger("error");

	public static final Logger BIZ = LoggerFactory.getLogger("biz");

	public static final Logger ACCESS = LoggerFactory.getLogger("access");

	public static final Logger CLIENT = LoggerFactory.getLogger("client");

	public static final Logger SCHEDULE = LoggerFactory.getLogger("schedule");

	public static final Logger MESSAGE = LoggerFactory.getLogger("message");

	public static final Logger DAO = LoggerFactory.getLogger("dao");

	public static final Logger PERFORMANCE = LoggerFactory.getLogger("performance");

}

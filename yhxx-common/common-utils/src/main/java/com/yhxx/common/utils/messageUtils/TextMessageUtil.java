package com.yhxx.common.utils.messageUtils;

import com.thoughtworks.xstream.XStream;
import com.yhxx.common.bean.messageBean.MessageText;
import com.yhxx.common.utils.BaseMessageUtil;

import java.util.Date;


public class TextMessageUtil implements BaseMessageUtil<MessageText> {

	public String messageToxml(MessageText message) {
		XStream xstream  = new XStream();
		xstream.alias("xml", message.getClass());
		return xstream.toXML(message);
	}

	@Override
	public String initMessage(String FromUserName, String ToUserName) {
		MessageText text = new MessageText();
		text.setToUserName(FromUserName);
		text.setFromUserName(ToUserName);
		text.setContent("欢迎关注党支部");
		text.setCreateTime(new Date().getTime());
		text.setMsgType("text");
	    return  messageToxml(text);
	}
	
	public String initMessage(String FromUserName, String ToUserName,String Content) {
		MessageText text = new MessageText();
		text.setToUserName(FromUserName);
		text.setFromUserName(ToUserName);
		text.setContent("您输入的内容是："+Content);
		text.setCreateTime(new Date().getTime());
		text.setMsgType("text");
	    return  messageToxml(text);
	}
	
	
}

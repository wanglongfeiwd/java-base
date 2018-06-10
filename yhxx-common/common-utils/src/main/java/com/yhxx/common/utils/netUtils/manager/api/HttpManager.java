package com.yhxx.common.utils.netUtils.manager.api;

import org.apache.http.Header;
import org.apache.http.client.ResponseHandler;

import java.io.File;

/**
 * @Author: Wanglf
 * @Date: Created in 17:30 2018/6/10
 * @modified By:
 */
public interface HttpManager {

    void download(String url, File file, Header... header);

    <T> T get(String url, ResponseHandler<T> handler,
              Header... header);

    <T> T get(String url, Object parameter, ResponseHandler<T> handler,
              Header... header);

    <T> T postForm(String url, Object parameter,
                   ResponseHandler<T> handler, Header... header);

    <T> T postJson(String url, ResponseHandler<T> handler);

    <T> T postJson(String url, String jsonString,
                   ResponseHandler<T> handler);

    <T> T postJson(String url, ResponseHandler<T> handler,
                   Header... header);

    <T> T postJsonBean(String url, Object param, final Class<T> resultClazz,
                       Header... header);

    <T> T postJson(String url, String jsonString,
                   ResponseHandler<T> handler, Header... header);

    <T> T putJson(String url, String jsonString,
                  ResponseHandler<T> handler, Header... header);

    <T> T postXml(String url, String xmlString, String encoding,
                  ResponseHandler<T> handler);

    <T> T postXml(String url, String xmlString, ResponseHandler<T> handler);

    <T> T postXml(String url, String xmlString, String encoding,
                  ResponseHandler<T> handler, Header... header);

}

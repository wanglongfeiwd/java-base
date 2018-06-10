package com.yhxx.common.utils.netUtils;


import com.yhxx.common.utils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.lang.reflect.Field;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author: Wanglf
 * @Date: Created in 14:39 2018/5/8
 * @modified By:
 */
public class HttpUtils {

    public static final String DEFAULT_ENCODING = "utf8";
    public static final String JSON_CONTENT_TYPE = "application/json;chatset=utf8";
    public static final String XML_CONTENT_TYPE = "text/xml";

    private static HttpClient client;

    public void setHttpClient(HttpClient client) {
        this.client = client;
    }

    public static <T> T get(String url, ResponseHandler<T> handler,
                            Header... header) {
        return get(url, null, handler, header);
    }

    public static <T> T get(String url, Object parameter, ResponseHandler<T> handler,
                            Header... header) {
        T res = null;

        HttpGet get = null;
        try {
            List<NameValuePair> pairList = convertToNameValuePairList(parameter);
            if (!CollectionUtils.isEmpty(pairList)) {
                url = url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairList, Consts.UTF_8));
            }

            HttpClient client = createClient(url);
            get = new HttpGet(url);
            if (header != null) {
                for (Header each : header) {
                    get.setHeader(each);
                }
            }
            res = client.execute(get, handler);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            if (get != null) {
                get.releaseConnection();
            }
        }
        return res;
    }

    private static List<NameValuePair> convertToNameValuePairList(Object parameter) {
        List<NameValuePair> pairList = new ArrayList<>();
        if (parameter != null) {
            Field[] fields = FieldUtils.getAllFields(parameter.getClass());
            for (Field field : fields) {
                if ("serialVersionUID".equals(field.getName())) {
                    continue;
                }

                // 嵌入在url里面的参数（含有UrlEmbedParameter注解的）不拼接在请求体里
                EmbedUrlParameter embedUrlParameter = field.getAnnotation(EmbedUrlParameter.class);
                if (embedUrlParameter == null) {
                    Object value = PropertyUtils.getProperty(parameter, field.getName());
                    if (value != null) {
                        pairList.add(new BasicNameValuePair(field.getName(), String.valueOf(value)));
                    }
                }
            }
        }
        return pairList;
    }

    private static HttpClient createClient(String url) {
        if (client != null) {
            // 采用环境配置的httpClient实例
            return client;
        }

        // 创建HttpClient实例，注意这里创建的是非线程安全的
        HttpClient _client = null;
        if (url.startsWith("https")) {
            _client = createSSLClientDefault();
        } else if (url.startsWith("http")) {
            _client = HttpClients.createDefault();
        }
        return _client;
    }

    private static HttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
                    null, new TrustStrategy() {
                        // 信任所有
                        public boolean isTrusted(X509Certificate[] chain,
                                                 String authType) throws CertificateException {
                            return true;
                        }
                    }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}

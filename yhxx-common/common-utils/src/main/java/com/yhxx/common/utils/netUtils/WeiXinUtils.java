package com.yhxx.common.utils.netUtils;


import com.yhxx.common.utils.jsonToolUtils.JsonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: Wanglf
 * @Date: Created in 14:51 2018/5/7
 * @modified By:
 */
public class WeiXinUtils {

    private static final String WEIXIN_APP_ID = "wx0513408521c17edf";
    private static final String WEIXIN_SECRET = "420145f710e55495bf272d98b1f3fba6";

    public String refreshAccessToken(){
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                + WEIXIN_APP_ID+ "&secret=" + WEIXIN_SECRET;

        String accessToken = HttpUtils.get(url, new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity httpEntity = response.getEntity();
                    String json = EntityUtils.toString(httpEntity);
                    Map obj = JsonUtils.parse(json,Map.class);
                    handleError("getAccessToken", obj);
                    return (String) obj.get("access_token");
                }else {
                    handleError("getAccessToken", response);
                    return null;
                }
            }
        });

        return accessToken;

    }

    private void handleError(String prefix, Map result) {
        Integer errcode = (Integer) result.get("errcode");
        if (errcode != null&&!errcode.equals(0)) {
            String errmsg = (String) result.get("errmsg");
            throw new IllegalStateException(prefix + " error[" + errcode +"]:" + errmsg);
        }
    }

    private void handleError(String prefix, HttpResponse response) {
        throw new IllegalStateException(prefix + " unknown error[" + response.getStatusLine().getStatusCode() + "]");
    }

}

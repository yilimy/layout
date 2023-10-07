package com.gomain.layout.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @author caimeng
 * @date 2023/10/7 17:25
 */
public class HttpClientUtils {

    public static <T> T sendPostJsonRequest(String url, Object obj, Class<T> mClass) {
        String str = sendPostJson(url, obj);
        return JSON.parseObject(str, mClass);
    }

    public static <T> T sendPostJsonByTypeReference(String url, Object obj, TypeReference<T> typeReference) {
        String str = sendPostJson(url, obj);
        return JSON.parseObject(str, typeReference);
    }

    public static String sendPostJson(String url, Object obj) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(obj));
        Request request = (new Request.Builder()).url(url).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        try {
            ResponseBody body = call.execute().body();
            if (body == null) {
                throw new RuntimeException("返回结果body为空");
            }
            return body.string();
        } catch (Exception e) {
            throw new RuntimeException("OkHttp Exception", e);
        }
    }
}

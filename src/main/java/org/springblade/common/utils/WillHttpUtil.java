package org.springblade.common.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.utils.OkHttpUtil;

import java.util.concurrent.TimeUnit;

import static org.springblade.core.tool.utils.OkHttpUtil.JSON;


@Slf4j
public
class WillHttpUtil{

    static final Long default_time_out = 10L;

    public static
    String postJson(String url, String data){
        System.out.println("WillHttpUtil.postJson()");
        System.out.println("url = " + url);
        System.out.println("data ========= \r\n");
        System.out.println(data);
        String res = OkHttpUtil.postJson(url, data);
        System.out.println("res ========= \r\n");
        System.out.println(res);
        if(StringUtils.isBlank(res)){
            throw new RuntimeException("调用三方接口无响应内容。接口地址：" + url);
        }
        return res;
    }

    public static
    String postJson(String url, String data, Long timeOut){
        System.out.println("WillHttpUtil.postJson() outTime:" + timeOut);
        System.out.println("url ==> " + url);
        System.out.println("data =========>");
        System.out.println(data);
        String res = postContent(url, data, JSON, timeOut);
        System.out.println("res =========>");
        System.out.println(res);
        if(StringUtils.isBlank(res)){
            throw new RuntimeException("调用三方接口无响应。接口地址：" + url);
        }
        return res;
    }


    public static
    String postContent(String url, String content, MediaType mediaType, Long timeOut){
        RequestBody     requestBody = RequestBody.create(mediaType, content);
        Request.Builder builder     = new Request.Builder();

        Request request = builder.url(url).post(requestBody).build();
        return getBody(request, timeOut);
    }

    private static
    String getBody(Request request, Long timeOut){
        String   responseBody = "";
        Response response     = null;

        String res;
        try{
            //设置连接超时时间     .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeOut, TimeUnit.SECONDS)
                    .readTimeout(timeOut+60L, TimeUnit.SECONDS)
                    .build();

            response = okHttpClient.newCall(request).execute();
            if(!response.isSuccessful()){
                return responseBody;
            }

            res = response.body().string();
        }catch(Exception exc){
            log.error("okhttp3 post error >> ex = {}", exc.getMessage());
            return responseBody;
        }finally{
            if(response != null){
                response.close();
            }
        }

        return res;
    }
}

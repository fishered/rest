package com.asset.rest.biz.core;

import com.alibaba.fastjson.JSON;
import com.asset.rest.biz.InitializingBiz;
import com.asset.rest.biz.context.ProcessParam;
import com.asset.rest.contrast.BizContrast;
import com.asset.rest.enums.ProcessType;
import com.asset.rest.enums.RestType;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author fisher
 * @date 2023-09-14: 15:40
 */
@Component
@Slf4j
public class RestCoreProcess implements CoreProcess, InitializingBean {

    @Autowired
    @Qualifier("httpsClient")
    private OkHttpClient httpsClient;

    @Autowired
    @Qualifier("httpClient")
    private OkHttpClient httpClient;

    @Override
    public String exec(ProcessParam processParam) {
        boolean https = processParam.isHttps();
        RestType restType = processParam.getRestType();

        log.info("[rest -core] 处理前所有的参数为：" + JSON.toJSONString(processParam));

        String result = StringUtils.EMPTY;
        switch (restType){
            case POST:
                result = doPost(processParam, https);
                break;
            case GET:
                result = doGet(processParam, https);
                break;
            default:
                new IllegalArgumentException("[rest] The current type is currently not supported!");
        }
        return result;
    }

    @Override
    public ProcessType getType() {
        return ProcessType.REST;
    }

    private String doGet(ProcessParam processParam, boolean isHttps){
        Request request = new Request.Builder()
                .url(processParam.getUrl())
                .build();

        return okhttpExec(request, isHttps);
    }

    private String doPost(ProcessParam processParam, boolean isHttps){
        if (CollectionUtils.isEmpty(processParam.getHeaders())
         || processParam.getHeaders().get(BizContrast.HEADER_TYPE_KEY) == null){
            throw new IllegalArgumentException(BizContrast.HEADER_TYPE_KEY + " or header is bad!");
        }
        String contentType = String.valueOf(processParam.getHeaders().get(BizContrast.HEADER_TYPE_KEY));
        MediaType mediaType = MediaType.parse(contentType);

        Map<String, Object> params = processParam.getParams();
        Request.Builder requestBuilder = null;
        if (contentType.toLowerCase().contains("application/json".toLowerCase())){
            log.info("[rest] - post 请求url：" + processParam.getUrl());
            log.info("[rest] - post 请求body：" + JSON.toJSONString(processParam.getParams()));
            //json类型的数据
            requestBuilder = new Request.Builder()
                    .url(processParam.getUrl())
                    .post(RequestBody.create(mediaType, JSON.toJSONString(processParam.getParams())));
//            for (Map.Entry<String, Object> entry : processParam.getHeaders().entrySet()) {
//                requestBuilder.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
//            }
        } else if (contentType.toLowerCase().contains("multipart/form-data".toLowerCase())) {
            //文本或二进制
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            if (!CollectionUtils.isEmpty(params)){
                for (Map.Entry<String, Object> entry : processParam.getParams().entrySet()) {
                    builder.addFormDataPart(entry.getKey(), String.valueOf(processParam.getParams()));
                }
            }
            // 添加文件字段
//            File file = new File("path/to/file");
//            builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
            // 构建MultipartBody对象
            MultipartBody requestBody = builder.build();
            requestBuilder = new Request.Builder()
                    .url(processParam.getUrl())
                    .post(requestBody)
                    .addHeader("Content-Type", "multipart/form-data");
        } else {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();

            if (!CollectionUtils.isEmpty(params)){
                for (Map.Entry<String, Object> entry : processParam.getParams().entrySet()) {
                    formBodyBuilder.add(entry.getKey(), String.valueOf(processParam.getParams()));
                }
            }
            RequestBody requestBody = formBodyBuilder.build();
            requestBuilder = new Request.Builder()
                    .url(processParam.getUrl())
                    .post(requestBody);
        }
        for (Map.Entry<String, Object> entry : processParam.getHeaders().entrySet()) {
            log.info(String.format("[rest] - post 请求header key:{%s},value:{%s}", entry.getKey(), String.valueOf(entry.getValue())));
            requestBuilder.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
        }

        Request request = requestBuilder.build();
        return okhttpExec(request, isHttps);
    }

    private String okhttpExec(Request request, boolean isHttps){
        Response response = null;
        try {
            response = isHttps?
                    httpsClient.newCall(request).execute():httpClient.newCall(request).execute();
            if (response.isSuccessful()){
                ResponseBody body = response.body();
                if (body != null){
                    return body.string();
                }
                return StringUtils.EMPTY;
            }else {
                log.info("[rest] response body is null!");
            }
        } catch (IOException e) {
            log.info("[rest] client exec has a error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (response != null){
                response.close();
            }
        }
        return StringUtils.EMPTY;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        InitializingBiz.addCore(ProcessType.REST, this);
    }
}

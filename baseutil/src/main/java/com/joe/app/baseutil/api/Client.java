package com.joe.app.baseutil.api;

import android.text.TextUtils;

import com.squareup.okhttp.OkHttpClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joe on 2016/6/5.
 * Email-joe_zong@163.com
 */
public class Client {
    public static Client mClient;
    private String host;
    private String tokenKey = "token";
    private String token = "1.www.nuorui.site";
    public static Client getInstance(String host){
        if(mClient == null){
            mClient = new Client(host);
        }
        mClient.refreshHost(host);
        return mClient;
    }


    private Client(){}

    private Client(String host){
        this.host = host;
        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        client.setConnectTimeout(20000, TimeUnit.MILLISECONDS);
        client.setReadTimeout(20000, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(20000, TimeUnit.MILLISECONDS);
    }

    public void refreshAuthToken(String tokenKey, String token){
        this.tokenKey = tokenKey;
        this.token = token;
    }

    public void refreshHost(String host){
        this.host = host;
    }

    /**
     * get 请求
     * @param method
     * @param params
     * @param callback
     */
    public void get(String method, Map<String, String> params, OnRequestCallback callback){
        GetBuilder getBuilder = OkHttpUtils.get();
        getBuilder.url(host + method);
        if(!TextUtils.isEmpty(token)){
            getBuilder.addHeader(tokenKey, token);
        }
        if(params!=null){
            getBuilder.params(params);
        }
       getBuilder.build()
                .execute(callback);
    }

    /**
     * get 请求指定url
     * @param url
     * @param method
     * @param params
     * @param callback
     */
    public void get(String url, String method, Map<String, String> params, OnRequestCallback callback){
        OkHttpUtils.get()
                .url(url + method)
                .params(params)
                .build()
                .execute(callback);
    }

    /**
     * post 请求
     * @param method
     * @param params
     * @param callback
     */
    public void post(String method, Map<String, String> params, OnRequestCallback callback){
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        postFormBuilder.url(host + method);
        if(params!=null){
            postFormBuilder.params(params);
        }
        if(!TextUtils.isEmpty(token)){
            postFormBuilder.addHeader(tokenKey, token);
        }
        postFormBuilder.build().execute(callback);
    }

    /**
     * post请求指定url
     * @param url
     * @param method
     * @param params
     * @param callback
     */
    public void post(String url, String method, Map<String, String> params, OnRequestCallback callback){
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        postFormBuilder.url(url + method);
        if(params!=null&&params.size()>0){
            postFormBuilder.params(params);
        }
        postFormBuilder.build()
                .execute(callback);
    }

    /**
     * 将file作为请求体传入到服务端
     * @param method
     * @param file
     * @param callback
     */
    public void postFile(String method, File file, OnDownloadCallback callback){
        OkHttpUtils.postFile()
                .url(host + method)
                .file(file)
                .addHeader(TextUtils.isEmpty(token) ? "" : tokenKey, token)
                .build()
                .execute(callback);
    }

    /**
     * 基于POST的文件上传（类似web上的表单）
     * @param method
     * @param params
     * @param fileParams
     * @param callback
     */
    public void postFile(String method, Map<String, String> params, Map<String, String> fileParams, OnDownloadCallback callback){
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        for(String key : fileParams.keySet()){
            String filePath = fileParams.get(key);
            String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
            File file = new File(filePath);
            postFormBuilder.addFile(key,fileName,file);
        }
        postFormBuilder
                .url(host+method)
                .params(params)
                .addHeader(TextUtils.isEmpty(token) ? "" : tokenKey, token)
                .build()
                .execute(callback);
    }

    /**
     * 文件下载
     * @param fileUrl
     * @param callback
     */
    public void downLoadFile(String fileUrl, OnDownloadCallback callback){
        OkHttpUtils.get()
                .url(fileUrl)
                .build()
                .execute(callback);
    }
}

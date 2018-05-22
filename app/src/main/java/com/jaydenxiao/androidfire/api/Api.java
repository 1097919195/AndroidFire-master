package com.jaydenxiao.androidfire.api;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaydenxiao.common.baseapp.BaseApplication;
import com.jaydenxiao.common.commonutils.NetWorkUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * des:retorfit api
 * Created by xsf
 * on 2016.06.15:47
 */
public class Api {
    //读超时长，单位：毫秒
    public static final int READ_TIME_OUT = 7676;
    //连接时长，单位：毫秒
    public static final int CONNECT_TIME_OUT = 7676;
    public Retrofit retrofit;
    public ApiService apiService;
    public OkHttpClient okHttpClient;
    private static SparseArray<Api> sRetrofitManager = new SparseArray<>(HostType.TYPE_COUNT);

    /*************************缓存设置*********************/
/*
   1. noCache 不使用缓存，全部走网络

    2. noStore 不使用缓存，也不存储缓存

    3. onlyIfCached 只使用缓存

    4. maxAge 设置最大失效时间，失效则不使用 需要服务器配合

    5. maxStale 设置最大失效时间，失效则不使用 需要服务器配合 感觉这两个类似 还没怎么弄清楚，清楚的同学欢迎留言

    6. minFresh 设置有效时间，依旧如上

    7. FORCE_NETWORK 只走网络

    8. FORCE_CACHE 只走缓存*/

    /**
     * 设缓存有效期为两天
     */
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    /**
     * 查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
     * max-stale 指示客户机可以接收超出超时期间的响应消息。如果指定max-stale消息的值，那么客户机可接收超出超时期指定值之内的响应消息。
     */
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    /**
     * 查询网络的Cache-Control设置，头部Cache-Control设为max-age=0
     * (假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)时则不会使用缓存而请求服务器
     */
    private static final String CACHE_CONTROL_AGE = "max-age=0";


    //构造方法私有
    private Api(int hostType) {
        //添加拦截器，获取日志信息
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("okhttp",message);
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//BODY:请求/响应行 + 头 + 体
        //缓存(getCacheDir()方法用于获取/data/data//cache目录)
        File cacheFile = new File(BaseApplication.getAppContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb
        //添加请求头，增加头部信息
        Interceptor headerInterceptor =new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //将请求体设置给请求方法内
                Request build = chain.request().newBuilder()
                        //使用 addHeader(name, value) 方法来为 HTTP 头添加新的值
                        .addHeader("Content-Type", "application/json")//Content-Type向接收方指示实体的介质类型，指定HEAD方法送到接收方的实体介质类型，或GET方法发送的请求介质类型
                        .build();
                return chain.proceed(build);
            }
        };

        //设置OkHttpClient客户端（在trofit自己是不支持缓存的。要做缓存用的是okhttp的功能，主要利用的是拦截器。这里一定要看清楚okhtt添加拦截器有两种。addNetworkInterceptor添加的是网络拦截器，他会在在request和resposne是分别被调用一次，addinterceptor添加的是application拦截器，他只会在response被调用一次）
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                .addInterceptor(mRewriteCacheControlInterceptor)
                .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                .addInterceptor(headerInterceptor)
                .addInterceptor(logInterceptor)
                .cache(cache)
                .build();

        //序列化日期（使用GsonBuilder来创建Gson对象指定固定格式，防止不同环境格式不同）
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls().create();

        //适配器
        retrofit = new Retrofit.Builder()
                //手动设置OkHttpClient客户端
                .client(okHttpClient)
                //Josn转换器（把服务端返回的json数据解析成实体）
                .addConverterFactory(GsonConverterFactory.create(gson))
                //为了支持Observable，Call类型是DefaultCallAdapterFactory默认支持的
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(ApiConstants.getHost(hostType))
                .build();

        //服务
        apiService = retrofit.create(ApiService.class);
    }

    /**
     * @param hostType NETEASE_NEWS_VIDEO：   1 （新闻，视频），
     *                 GANK_GIRL_PHOTO：      2（图片新闻）;
     *                 EWS_DETAIL_HTML_PHOTO: 3新闻详情html图片)
     */
    public static ApiService getDefault(int hostType) {
        Api retrofitManager = sRetrofitManager.get(hostType);
        if (retrofitManager == null) {
            retrofitManager = new Api(hostType);
            sRetrofitManager.put(hostType, retrofitManager);
        }
        return retrofitManager.apiService;
    }

    /**
     * OkHttpClient
     * @return
     */
    public static OkHttpClient getOkHttpClient(){
        Api retrofitManager = sRetrofitManager.get(HostType.NETEASE_NEWS_VIDEO);
        if (retrofitManager == null) {
            retrofitManager = new Api(HostType.NETEASE_NEWS_VIDEO);
            sRetrofitManager.put(HostType.NETEASE_NEWS_VIDEO, retrofitManager);
        }
        return retrofitManager.okHttpClient;
    }


    /**
     * 根据网络状况获取缓存的策略
     */
    @NonNull
    public static String getCacheControl() {
        return NetWorkUtils.isNetConnected(BaseApplication.getAppContext()) ? CACHE_CONTROL_AGE : CACHE_CONTROL_CACHE;
    }

    /**
     * 云端响应头拦截器，用来配置缓存策略（无论有无网路都添加缓存）
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String cacheControl = request.cacheControl().toString();

            //要是没有网络就去缓存里面取数据
            if (!NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {
                request = request.newBuilder()
                        .cacheControl(TextUtils.isEmpty(cacheControl)?CacheControl.FORCE_NETWORK:CacheControl.FORCE_CACHE)
                        .build();
            }

            Response originalResponse = chain.proceed(request);

            //要是有网络的话就直接获取网络上面的数据
            if (NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {

                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                return originalResponse.newBuilder()
                        //缓存的时间
                        .header("Cache-Control", cacheControl)
                        //清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };
}
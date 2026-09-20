package cn.net.yunlou.douyin.impl;

import cn.net.yunlou.douyin.DouyinConfigStorage;
import cn.net.yunlou.douyin.DouyinConstants;
import cn.net.yunlou.douyin.DouyinRequestExecutor;
import cn.net.yunlou.douyin.error.DouyinErrorException;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基于 OkHttp 的请求执行器实现。
 *
 * <p>连接池（{@link ConnectionPool}）与调度器（{@link Dispatcher}）在进程内共享，
 * 因此即使创建多个 {@code DouyinClient} 实例，也不会产生多套独立连接池，
 * 避免连接/线程资源堆积。超时等参数可按配置逐实例设置。</p>
 *
 * <p>对担保支付这类非幂等写入，显式关闭 {@code retryOnConnectionFailure}，
 * 避免连接层静默重试导致重复下单/退款（业务层已通过 out_order_no / out_refund_no 保证幂等）。</p>
 */
public class OkHttpDouyinRequestExecutor implements DouyinRequestExecutor, AutoCloseable {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /** 进程内共享连接池：最多 5 个空闲连接，空闲保活 5 分钟。 */
    private static final ConnectionPool SHARED_POOL = new ConnectionPool(5, 5, TimeUnit.MINUTES);
    /** 进程内共享调度器，限制整体并发调用数（默认 64 / 单 host 5）。 */
    private static final Dispatcher SHARED_DISPATCHER = new Dispatcher();

    private final OkHttpClient client;

    public OkHttpDouyinRequestExecutor(DouyinConfigStorage config) {
        this(buildClient(config.getHttpConnectionTimeout(), config.getHttpReadTimeout()));
    }

    public OkHttpDouyinRequestExecutor() {
        this(buildClient(DouyinConstants.DEFAULT_TIMEOUT_MILLIS, DouyinConstants.DEFAULT_TIMEOUT_MILLIS));
    }

    /**
     * 注入共享的 {@link OkHttpClient}（例如复用宿主应用的连接池/拦截器）。
     * 该构造函数下 {@link #close()} 不会关闭传入的 client，由调用方负责其生命周期。
     */
    public OkHttpDouyinRequestExecutor(OkHttpClient client) {
        this.client = client;
    }

    private static OkHttpClient buildClient(int connectTimeout, int readTimeout) {
        return new OkHttpClient.Builder()
                .connectionPool(SHARED_POOL)
                .dispatcher(SHARED_DISPATCHER)
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .build();
    }

    @Override
    public String postForm(String url, Map<String, String> formParams) {
        FormBody.Builder builder = new FormBody.Builder(StandardCharsets.UTF_8);
        if (formParams != null) {
            for (Map.Entry<String, String> e : formParams.entrySet()) {
                builder.add(e.getKey(), e.getValue());
            }
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        return execute(request);
    }

    @Override
    public String postJson(String url, String jsonBody) {
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder().url(url).post(body).build();
        return execute(request);
    }

    @Override
    public String get(String url, Map<String, String> queryParams) {
        HttpUrl.Builder urlBuilder = HttpUrl.get(url).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> e : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(e.getKey(), e.getValue());
            }
        }
        Request request = new Request.Builder().url(urlBuilder.build()).get().build();
        return execute(request);
    }

    private String execute(Request request) {
        try (Response response = client.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            int code = response.code();
            if (code >= 200 && code < 300) {
                return body;
            }
            throw new DouyinErrorException("HTTP 状态码异常: " + code + ", body=" + body);
        } catch (IOException e) {
            throw new DouyinErrorException("HTTP 请求失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        // 连接池/调度器为进程内共享，不随单个执行器关闭而释放；
        // 若使用注入式构造函数传入自有 client，亦由调用方负责关闭。
    }
}

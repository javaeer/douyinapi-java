package cn.net.yunlou.douyin.impl;

import cn.net.yunlou.douyin.DouyinConfigStorage;
import cn.net.yunlou.douyin.DouyinRequestExecutor;
import cn.net.yunlou.douyin.error.DouyinErrorException;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于 Apache HttpClient 4.x 的请求执行器实现。
 */
public class ApacheHttpClientRequestExecutor implements DouyinRequestExecutor, AutoCloseable {

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String JSON_CONTENT_TYPE = "application/json";

    private final CloseableHttpClient httpClient;
    private final RequestConfig requestConfig;

    public ApacheHttpClientRequestExecutor(DouyinConfigStorage config) {
        this.httpClient = buildClient();
        this.requestConfig = RequestConfig.custom()
                .setConnectTimeout(config.getHttpConnectionTimeout())
                .setSocketTimeout(config.getHttpReadTimeout())
                .setConnectionRequestTimeout(config.getHttpConnectionTimeout())
                .build();
    }

    public ApacheHttpClientRequestExecutor() {
        this.httpClient = buildClient();
        this.requestConfig = RequestConfig.custom()
                .setConnectTimeout(cn.net.yunlou.douyin.DouyinConstants.DEFAULT_TIMEOUT_MILLIS)
                .setSocketTimeout(cn.net.yunlou.douyin.DouyinConstants.DEFAULT_TIMEOUT_MILLIS)
                .setConnectionRequestTimeout(cn.net.yunlou.douyin.DouyinConstants.DEFAULT_TIMEOUT_MILLIS)
                .build();
    }

    private static CloseableHttpClient buildClient() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(50);
        return HttpClients.custom()
                .setConnectionManager(cm)
                .setConnectionManagerShared(true)
                .build();
    }

    @Override
    public String postForm(String url, Map<String, String> formParams) {
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);
        post.setHeader("Content-Type", FORM_CONTENT_TYPE);
        if (formParams != null && !formParams.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<>(formParams.size());
            for (Map.Entry<String, String> e : formParams.entrySet()) {
                pairs.add(new BasicNameValuePair(e.getKey(), e.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8));
        }
        return execute(post);
    }

    @Override
    public String postJson(String url, String jsonBody) {
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);
        post.setEntity(new StringEntity(jsonBody, ContentType.create(JSON_CONTENT_TYPE, StandardCharsets.UTF_8)));
        return execute(post);
    }

    @Override
    public String get(String url, Map<String, String> queryParams) {
        String fullUrl = url;
        if (queryParams != null && !queryParams.isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            sb.append(url.contains("?") ? '&' : '?');
            boolean first = true;
            for (Map.Entry<String, String> e : queryParams.entrySet()) {
                if (!first) {
                    sb.append('&');
                }
                sb.append(e.getKey()).append('=').append(encode(e.getValue()));
                first = false;
            }
            fullUrl = sb.toString();
        }
        HttpGet get = new HttpGet(fullUrl);
        get.setConfig(requestConfig);
        return execute(get);
    }

    private static String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return value;
        }
    }

    private String execute(org.apache.http.client.methods.HttpUriRequest request) {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                return body;
            }
            throw new DouyinErrorException("HTTP 状态码异常: " + status + ", body=" + body);
        } catch (IOException e) {
            throw new DouyinErrorException("HTTP 请求失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() throws Exception {
        httpClient.close();
    }
}

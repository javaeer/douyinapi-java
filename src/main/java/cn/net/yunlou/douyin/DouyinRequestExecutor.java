package cn.net.yunlou.douyin;

import java.util.Map;

/**
 * HTTP 请求执行器。将底层 HTTP 客户端与业务服务解耦，便于测试时注入 mock 实现。
 */
public interface DouyinRequestExecutor {

    /**
     * 以 application/x-www-form-urlencoded 提交 POST 请求。
     *
     * @return 响应体字符串
     */
    String postForm(String url, Map<String, String> formParams);

    /**
     * 以 application/json 提交 POST 请求。
     *
     * @return 响应体字符串
     */
    String postJson(String url, String jsonBody);

    /**
     * 提交 GET 请求。
     *
     * @return 响应体字符串
     */
    String get(String url, Map<String, String> queryParams);
}

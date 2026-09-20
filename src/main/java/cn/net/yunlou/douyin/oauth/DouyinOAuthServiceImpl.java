package cn.net.yunlou.douyin.oauth;

import cn.net.yunlou.douyin.DouyinClient;
import cn.net.yunlou.douyin.DouyinConstants;
import cn.net.yunlou.douyin.error.DouyinErrorException;
import cn.net.yunlou.douyin.bean.oauth.AccessTokenResult;
import cn.net.yunlou.douyin.bean.oauth.ClientTokenResult;
import cn.net.yunlou.douyin.bean.oauth.UserInfoResult;
import cn.net.yunlou.douyin.util.JsonUtils;
import cn.net.yunlou.douyin.util.RandomUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link DouyinOAuthService} 的默认实现。
 */
public class DouyinOAuthServiceImpl implements DouyinOAuthService {

    private final DouyinClient client;

    public DouyinOAuthServiceImpl(DouyinClient client) {
        this.client = client;
    }

    @Override
    public String buildAuthorizeUrl(String redirectUri, String scope) {
        return buildAuthorizeUrl(redirectUri, scope, RandomUtils.generateState());
    }

    @Override
    public String buildAuthorizeUrl(String redirectUri, String scope, String state) {
        String clientKey = client.getConfigStorage().getClientKey();
        StringBuilder sb = new StringBuilder(DouyinConstants.OAUTH_CONNECT_URL);
        sb.append("?client_key=").append(encode(clientKey));
        sb.append("&response_type=code");
        sb.append("&scope=").append(encode(scope));
        sb.append("&redirect_uri=").append(encode(redirectUri));
        sb.append("&state=").append(encode(state));
        return sb.toString();
    }

    @Override
    public AccessTokenResult getAccessToken(String code) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("client_key", client.getConfigStorage().getClientKey());
        form.put("client_secret", client.getConfigStorage().getClientSecret());
        form.put("code", code);
        form.put("grant_type", DouyinConstants.GRANT_TYPE_AUTHORIZATION_CODE);
        String resp = client.getRequestExecutor().postForm(DouyinConstants.OAUTH_ACCESS_TOKEN_URL, form);
        AccessTokenResult result = JsonUtils.fromJson(resp, AccessTokenResult.class);
        result.checkSuccess();
        return result;
    }

    @Override
    public AccessTokenResult refreshToken(String refreshToken) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("client_key", client.getConfigStorage().getClientKey());
        form.put("refresh_token", refreshToken);
        String resp = client.getRequestExecutor().postForm(DouyinConstants.OAUTH_REFRESH_TOKEN_URL, form);
        AccessTokenResult result = JsonUtils.fromJson(resp, AccessTokenResult.class);
        result.checkSuccess();
        return result;
    }

    @Override
    public ClientTokenResult getClientToken() {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("client_key", client.getConfigStorage().getClientKey());
        form.put("client_secret", client.getConfigStorage().getClientSecret());
        form.put("grant_type", DouyinConstants.GRANT_TYPE_CLIENT_CREDENTIAL);
        String resp = client.getRequestExecutor().postForm(DouyinConstants.OAUTH_CLIENT_TOKEN_URL, form);
        ClientTokenResult result = JsonUtils.fromJson(resp, ClientTokenResult.class);
        result.checkSuccess();
        return result;
    }

    @Override
    public AccessTokenResult renewRefreshToken(String refreshToken) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("client_key", client.getConfigStorage().getClientKey());
        form.put("refresh_token", refreshToken);
        String resp = client.getRequestExecutor()
                .postForm(DouyinConstants.OAUTH_RENEW_REFRESH_TOKEN_URL, form);
        AccessTokenResult result = JsonUtils.fromJson(resp, AccessTokenResult.class);
        result.checkSuccess();
        return result;
    }

    @Override
    public UserInfoResult getUserInfo(String accessToken, String openId) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("access_token", accessToken);
        form.put("open_id", openId);
        String resp = client.getRequestExecutor().postForm(DouyinConstants.OAUTH_USER_INFO_URL, form);
        UserInfoResult result = JsonUtils.fromJson(resp, UserInfoResult.class);
        result.checkSuccess();
        return result;
    }

    @Override
    public Map<String, String> parseCallback(String callbackUrl) {
        Map<String, String> params = new LinkedHashMap<>();
        int qi = callbackUrl.indexOf('?');
        if (qi < 0) {
            return params;
        }
        String query = callbackUrl.substring(qi + 1);
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq < 0) {
                continue;
            }
            try {
                params.put(decode(pair.substring(0, eq)), decode(pair.substring(eq + 1)));
            } catch (UnsupportedEncodingException e) {
                throw new DouyinErrorException("回调参数解码失败: " + e.getMessage(), e);
            }
        }
        return params;
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    private static String decode(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
    }
}

package cn.net.yunlou.douyin.oauth;

import cn.net.yunlou.douyin.DouyinClient;
import cn.net.yunlou.douyin.DouyinClientImpl;
import cn.net.yunlou.douyin.DouyinRequestExecutor;
import cn.net.yunlou.douyin.bean.oauth.AccessTokenResult;
import cn.net.yunlou.douyin.bean.oauth.UserInfoResult;
import cn.net.yunlou.douyin.error.DouyinErrorException;
import cn.net.yunlou.douyin.impl.DefaultDouyinConfigImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DouyinOAuthServiceTest {

    private DouyinRequestExecutor executor;
    private DouyinClient client;

    @BeforeEach
    void setUp() {
        executor = mock(DouyinRequestExecutor.class);
        DefaultDouyinConfigImpl config = DefaultDouyinConfigImpl.builder()
                .clientKey("client_key_demo")
                .clientSecret("client_secret_demo")
                .merchantId("merchant_demo")
                .salt("salt_demo")
                .build();
        client = new DouyinClientImpl(config, executor);
    }

    @Test
    void buildAuthorizeUrl_encodesParams() {
        String url = client.getOAuthService().buildAuthorizeUrl("https://example.com/cb", "user_info", "state123");
        assertTrue(url.startsWith("https://open.douyin.com/platform/oauth/connect/"));
        assertTrue(url.contains("client_key=client_key_demo"));
        assertTrue(url.contains("response_type=code"));
        assertTrue(url.contains("scope=user_info"));
        assertTrue(url.contains("redirect_uri=https%3A%2F%2Fexample.com%2Fcb"));
        assertTrue(url.contains("state=state123"));
    }

    @Test
    void getAccessToken_parsesResponse() {
        String json = "{\"error_code\":0,\"description\":\"success\",\"access_token\":\"at_value\","
                + "\"expires_in\":1296000,\"open_id\":\"open_id_value\",\"refresh_token\":\"rt_value\","
                + "\"scope\":\"user_info\"}";
        when(executor.postForm(eq("https://open.douyin.com/oauth/access_token/"), any()))
                .thenReturn(json);

        AccessTokenResult result = client.getOAuthService().getAccessToken("auth_code");
        assertEquals("at_value", result.getAccessToken());
        assertEquals("open_id_value", result.getOpenId());
        assertEquals("rt_value", result.getRefreshToken());
        assertEquals("user_info", result.getScope());
        assertTrue(result.isSuccess());
    }

    @Test
    void getAccessToken_sendsCorrectForm() {
        Map<String, String> captured = new java.util.HashMap<>();
        when(executor.postForm(eq("https://open.douyin.com/oauth/access_token/"), any()))
                .thenAnswer(inv -> {
                    @SuppressWarnings("unchecked")
                    Map<String, String> form = (Map<String, String>) inv.getArgument(1);
                    captured.putAll(form);
                    return "{\"error_code\":0,\"access_token\":\"at\",\"open_id\":\"o\",\"refresh_token\":\"r\"}";
                });

        client.getOAuthService().getAccessToken("auth_code");
        assertEquals("client_key_demo", captured.get("client_key"));
        assertEquals("client_secret_demo", captured.get("client_secret"));
        assertEquals("auth_code", captured.get("code"));
        assertEquals("authorization_code", captured.get("grant_type"));
    }

    @Test
    void getUserInfo_parsesResponse() {
        String json = "{\"error_code\":0,\"nickname\":\"抖音用户\",\"avatar\":\"https://avatar\","
                + "\"city\":\"北京\",\"province\":\"北京\",\"country\":\"中国\",\"gender\":1,"
                + "\"open_id\":\"o1\",\"union_id\":\"u1\"}";
        when(executor.postForm(eq("https://open.douyin.com/oauth/userinfo/"), any())).thenReturn(json);

        UserInfoResult info = client.getOAuthService().getUserInfo("at", "o1");
        assertEquals("抖音用户", info.getNickname());
        assertEquals("https://avatar", info.getAvatar());
        assertEquals(Integer.valueOf(1), info.getGender());
        assertEquals("o1", info.getOpenId());
    }

    @Test
    void getAccessToken_throwsOnBizError() {
        when(executor.postForm(any(), any()))
                .thenReturn("{\"error_code\":10008,\"description\":\"access_token 已过期\"}");
        Assertions.assertThrows(
                DouyinErrorException.class,
                () -> client.getOAuthService().getAccessToken("bad"));
    }

    @Test
    void parseCallback_extractsCodeAndState() {
        Map<String, String> cb = client.getOAuthService()
                .parseCallback("https://example.com/cb?code=CODE&state=ST");
        assertEquals("CODE", cb.get("code"));
        assertEquals("ST", cb.get("state"));
        assertNotNull(cb);
    }
}

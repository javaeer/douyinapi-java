package cn.net.yunlou.douyin.oauth;

import cn.net.yunlou.douyin.bean.oauth.AccessTokenResult;
import cn.net.yunlou.douyin.bean.oauth.ClientTokenResult;
import cn.net.yunlou.douyin.bean.oauth.UserInfoResult;

import java.util.Map;

/**
 * 抖音开放平台账号与授权服务（OAuth2 授权码模式）。
 *
 * <p>覆盖：拼接授权页地址、用授权码换取 access_token、刷新 token、获取应用级 client_token、
 * 延长 refresh_token 有效期、获取用户信息。
 */
public interface DouyinOAuthService {

    /**
     * 拼接用户授权登录页地址（自动生成 state）。
     *
     * @param redirectUri 授权回调地址（需与开放平台配置一致）
     * @param scope       申请的权限作用域，多个以英文逗号分隔
     */
    String buildAuthorizeUrl(String redirectUri, String scope);

    /**
     * 拼接用户授权登录页地址。
     *
     * @param redirectUri 授权回调地址
     * @param scope       权限作用域
     * @param state       防 CSRF 的随机串
     */
    String buildAuthorizeUrl(String redirectUri, String scope, String state);

    /**
     * 用授权码换取用户 access_token。
     *
     * @param code 用户授权后回调带回的临时票据（只能用一次）
     */
    AccessTokenResult getAccessToken(String code);

    /**
     * 刷新用户 access_token / 续期。
     */
    AccessTokenResult refreshToken(String refreshToken);

    /**
     * 获取应用级 client_token（无需用户授权）。
     */
    ClientTokenResult getClientToken();

    /**
     * 延长 refresh_token 有效期。
     */
    AccessTokenResult renewRefreshToken(String refreshToken);

    /**
     * 获取用户信息（昵称、头像等）。
     *
     * @param accessToken 用户授权凭证
     * @param openId      用户 open_id
     */
    UserInfoResult getUserInfo(String accessToken, String openId);

    /** 解析回调地址中的授权码与 state（工具方法）。 */
    Map<String, String> parseCallback(String callbackUrl);
}

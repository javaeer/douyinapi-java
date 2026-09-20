package cn.net.yunlou.douyin;

/**
 * 抖音开放平台 / 抖音支付 API 常量与端点定义。
 *
 * <p>所有地址均来自抖音开发者平台官方文档：
 * <ul>
 *   <li>账号与授权：https://open.douyin.com</li>
 *   <li>担保支付（小程序）：https://developer.toutiao.com</li>
 * </ul>
 */
public final class DouyinConstants {

    private DouyinConstants() {
    }

    /** 开放平台（账号/授权）域名。 */
    public static final String DOMAIN_API = "https://open.douyin.com";

    /** 支付（担保支付）域名。 */
    public static final String DOMAIN_PAY = "https://developer.toutiao.com";

    /* ========================= 授权 / 账号 ========================= */

    /** 用户授权登录页（拼接授权码模式地址）。 */
    public static final String OAUTH_CONNECT_URL = DOMAIN_API + "/platform/oauth/connect/";

    /** 用授权码换取 access_token。POST form-urlencoded。 */
    public static final String OAUTH_ACCESS_TOKEN_URL = DOMAIN_API + "/oauth/access_token/";

    /** 刷新 access_token。POST form-urlencoded。 */
    public static final String OAUTH_REFRESH_TOKEN_URL = DOMAIN_API + "/oauth/refresh_token/";

    /** 获取应用级 client_token（无需用户授权）。POST form-urlencoded。 */
    public static final String OAUTH_CLIENT_TOKEN_URL = DOMAIN_API + "/oauth/client_token/";

    /** 延长 refresh_token 有效期。POST form-urlencoded。 */
    public static final String OAUTH_RENEW_REFRESH_TOKEN_URL = DOMAIN_API + "/oauth/renew_refresh_token/";

    /** 获取用户信息。POST form-urlencoded（access_token + open_id）。 */
    public static final String OAUTH_USER_INFO_URL = DOMAIN_API + "/oauth/userinfo/";

    /* ========================= 担保支付 ========================= */

    /** 预下单（创建支付订单）。POST application/json。 */
    public static final String PAY_TRADE_CREATE_URL = DOMAIN_PAY + "/api/apps/trade/create";

    /** 查询订单。POST application/json。 */
    public static final String PAY_TRADE_QUERY_URL = DOMAIN_PAY + "/api/apps/trade/query";

    /** 发起退款。POST application/json。 */
    public static final String PAY_CREATE_REFUND_URL = DOMAIN_PAY + "/api/apps/trade/create_refund";

    /** 查询退款（担保支付）。POST application/json。 */
    public static final String PAY_REFUND_QUERY_URL = DOMAIN_PAY + "/api/apps/ecpay/sa/";

    /** 分账（担保支付）。POST application/json。 */
    public static final String PAY_CREATE_SETTLE_URL = DOMAIN_PAY + "/api/apps/ecpay/v1/create_settle";

    /* ========================= 通用参数 ========================= */

    /** 授权码模式。 */
    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

    /** client_token 使用的 grant_type。 */
    public static final String GRANT_TYPE_CLIENT_CREDENTIAL = "client_credential";

    /** 默认连接/读取超时（毫秒）。 */
    public static final int DEFAULT_TIMEOUT_MILLIS = 10_000;
}

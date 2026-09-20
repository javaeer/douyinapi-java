package cn.net.yunlou.douyin;

import cn.net.yunlou.douyin.oauth.DouyinOAuthService;
import cn.net.yunlou.douyin.pay.DouyinPayService;

/**
 * 抖音开放平台 SDK 统一入口。
 *
 * <pre>{@code
 * DouyinClient client = DouyinClientImpl.builder()
 *         .clientKey("xxx").clientSecret("xxx").merchantId("xxx").salt("xxx")
 *         .build();
 * AccessTokenResult token = client.getOAuthService().getAccessToken(code);
 * TradeCreateResult order = client.getPayService().createOrder(req);
 * }</pre>
 */
public interface DouyinClient {

    DouyinConfigStorage getConfigStorage();

    DouyinRequestExecutor getRequestExecutor();

    DouyinOAuthService getOAuthService();

    DouyinPayService getPayService();
}

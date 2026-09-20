package cn.net.yunlou.douyin;

import cn.net.yunlou.douyin.impl.DefaultDouyinConfigImpl;
import cn.net.yunlou.douyin.impl.OkHttpDouyinRequestExecutor;
import cn.net.yunlou.douyin.oauth.DouyinOAuthService;
import cn.net.yunlou.douyin.oauth.DouyinOAuthServiceImpl;
import cn.net.yunlou.douyin.pay.DouyinPayService;
import cn.net.yunlou.douyin.pay.DouyinPayServiceImpl;

import java.util.Objects;

/**
 * {@link DouyinClient} 的默认实现。
 */
public class DouyinClientImpl implements DouyinClient {

    private final DouyinConfigStorage configStorage;
    private final DouyinRequestExecutor requestExecutor;
    private final DouyinOAuthService oAuthService;
    private final DouyinPayService payService;

    public DouyinClientImpl(DouyinConfigStorage configStorage) {
        this(configStorage, new OkHttpDouyinRequestExecutor(configStorage));
    }

    public DouyinClientImpl(DouyinConfigStorage configStorage, DouyinRequestExecutor requestExecutor) {
        this.configStorage = Objects.requireNonNull(configStorage, "configStorage 不能为空");
        this.requestExecutor = Objects.requireNonNull(requestExecutor, "requestExecutor 不能为空");
        this.oAuthService = new DouyinOAuthServiceImpl(this);
        this.payService = new DouyinPayServiceImpl(this);
    }

    /**
     * 以配置构造客户端（使用内置 Apache HttpClient 执行器）。
     */
    public static DouyinClientImpl fromConfig(DouyinConfigStorage configStorage) {
        return new DouyinClientImpl(configStorage);
    }

    /**
     * 链式构造客户端。
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public DouyinConfigStorage getConfigStorage() {
        return configStorage;
    }

    @Override
    public DouyinRequestExecutor getRequestExecutor() {
        return requestExecutor;
    }

    @Override
    public DouyinOAuthService getOAuthService() {
        return oAuthService;
    }

    @Override
    public DouyinPayService getPayService() {
        return payService;
    }

    /**
     * 链式构造器。
     */
    public static final class Builder {
        private String clientKey;
        private String clientSecret;
        private String merchantId;
        private String salt;
        private int httpConnectionTimeout = DouyinConstants.DEFAULT_TIMEOUT_MILLIS;
        private int httpReadTimeout = DouyinConstants.DEFAULT_TIMEOUT_MILLIS;

        public Builder clientKey(String v) {
            this.clientKey = v;
            return this;
        }

        public Builder clientSecret(String v) {
            this.clientSecret = v;
            return this;
        }

        public Builder merchantId(String v) {
            this.merchantId = v;
            return this;
        }

        public Builder salt(String v) {
            this.salt = v;
            return this;
        }

        public Builder httpConnectionTimeout(int v) {
            this.httpConnectionTimeout = v;
            return this;
        }

        public Builder httpReadTimeout(int v) {
            this.httpReadTimeout = v;
            return this;
        }

        public DouyinClientImpl build() {
            DouyinConfigStorage config = DefaultDouyinConfigImpl.builder()
                    .clientKey(clientKey)
                    .clientSecret(clientSecret)
                    .merchantId(merchantId)
                    .salt(salt)
                    .httpConnectionTimeout(httpConnectionTimeout)
                    .httpReadTimeout(httpReadTimeout)
                    .build();
            return new DouyinClientImpl(config);
        }
    }
}

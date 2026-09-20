package cn.net.yunlou.douyin.impl;

import cn.net.yunlou.douyin.DouyinConfigStorage;
import cn.net.yunlou.douyin.DouyinConstants;
import java.util.Objects;

/**
 * {@link DouyinConfigStorage} 的默认实现，基于不可变配置构造。
 */
public class DefaultDouyinConfigImpl implements DouyinConfigStorage {

    private final String clientKey;
    private final String clientSecret;
    private final String merchantId;
    private final String salt;
    private final int httpConnectionTimeout;
    private final int httpReadTimeout;
    private final String apiDomain;
    private final String payDomain;

    private DefaultDouyinConfigImpl(Builder b) {
        this.clientKey = b.clientKey;
        this.clientSecret = b.clientSecret;
        this.merchantId = b.merchantId;
        this.salt = b.salt;
        this.httpConnectionTimeout = b.httpConnectionTimeout;
        this.httpReadTimeout = b.httpReadTimeout;
        this.apiDomain = b.apiDomain;
        this.payDomain = b.payDomain;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getClientKey() {
        return clientKey;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String getMerchantId() {
        return merchantId;
    }

    @Override
    public String getSalt() {
        return salt;
    }

    @Override
    public int getHttpConnectionTimeout() {
        return httpConnectionTimeout;
    }

    @Override
    public int getHttpReadTimeout() {
        return httpReadTimeout;
    }

    @Override
    public String getApiDomain() {
        return apiDomain;
    }

    @Override
    public String getPayDomain() {
        return payDomain;
    }

    /** 配置构造器。 */
    public static final class Builder {
        private String clientKey;
        private String clientSecret;
        private String merchantId;
        private String salt;
        private int httpConnectionTimeout = DouyinConstants.DEFAULT_TIMEOUT_MILLIS;
        private int httpReadTimeout = DouyinConstants.DEFAULT_TIMEOUT_MILLIS;
        private String apiDomain = DouyinConstants.DOMAIN_API;
        private String payDomain = DouyinConstants.DOMAIN_PAY;

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

        public Builder apiDomain(String v) {
            this.apiDomain = v;
            return this;
        }

        public Builder payDomain(String v) {
            this.payDomain = v;
            return this;
        }

        public DefaultDouyinConfigImpl build() {
            Objects.requireNonNull(clientKey, "clientKey 不能为空");
            Objects.requireNonNull(clientSecret, "clientSecret 不能为空");
            return new DefaultDouyinConfigImpl(this);
        }
    }
}

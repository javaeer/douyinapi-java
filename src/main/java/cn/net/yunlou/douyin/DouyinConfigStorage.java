package cn.net.yunlou.douyin;

/**
 * 抖音开放平台配置存储。
 *
 * <p>持有调用开放平台与支付接口所需的凭证信息。生产环境请将 secret / salt 等敏感信息
 * 存放在服务端配置中心或密钥系统中，切勿硬编码或下发到客户端。
 */
public interface DouyinConfigStorage {

    /** 应用唯一标识 client_key（移动/网站应用、小程序）。 */
    String getClientKey();

    /** 应用密钥 client_secret。 */
    String getClientSecret();

    /** 商户号 merchant_id（担保支付）。第三方代调用场景下可留空。 */
    String getMerchantId();

    /** 支付密钥 salt（担保支付 MD5 签名使用）。 */
    String getSalt();

    /** HTTP 连接超时（毫秒）。 */
    int getHttpConnectionTimeout();

    /** HTTP 读取超时（毫秒）。 */
    int getHttpReadTimeout();

    /** 开放平台域名，默认 {@link DouyinConstants#DOMAIN_API}。 */
    String getApiDomain();

    /** 支付域名，默认 {@link DouyinConstants#DOMAIN_PAY}。 */
    String getPayDomain();
}

package cn.net.yunlou.douyin.bean.pay;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 担保支付请求基类，承载 app_id / merchant_id / thirdparty_id 等身份字段。
 *
 * <p>子类通过 {@link #toParamMap()} 产出待签名的参数表（不含 sign）。
 */

@Getter
@Setter
public abstract class AbstractPayRequest {

    /** 应用唯一标识 app_id（来自 config，不参与签名）。 */
    private String appId;

    /** 商户号 merchant_id（来自 config）。 */
    private String merchantId;

    /** 第三方平台 ID，非代调用场景留空。 */
    private String thirdpartyId;

    /** 身份字段注入（通常由 SDK 从配置填充，避免重复手工设置）。 */
    public void injectIdentity(String appId, String merchantId, String thirdpartyId) {
        if (this.appId == null) {
            this.appId = appId;
        }
        if (this.merchantId == null) {
            this.merchantId = merchantId;
        }
        if (this.thirdpartyId == null) {
            this.thirdpartyId = thirdpartyId;
        }
    }

    /** 产出身份字段参数（不含 sign），供子类扩展。 */
    protected Map<String, Object> identityMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        putIfPresent(map, "app_id", appId);
        putIfPresent(map, "merchant_id", merchantId);
        putIfPresent(map, "thirdparty_id", thirdpartyId);
        return map;
    }

    /** 子类实现：返回待签名（不含 sign）的完整参数表。 */
    public abstract Map<String, Object> toParamMap();

    protected static void putIfPresent(Map<String, Object> map, String key, Object value) {
        if (value != null && !(value instanceof String && ((String) value).isEmpty())) {
            map.put(key, value);
        }
    }
}

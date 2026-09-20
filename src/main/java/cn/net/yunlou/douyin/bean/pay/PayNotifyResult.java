package cn.net.yunlou.douyin.bean.pay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cn.net.yunlou.douyin.util.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 担保支付异步回调结果。
 *
 * <p>平台向 {@code notify_url} 推送的报文形如：
 * <pre>{ "type": "payment", "msg": "{...订单JSON...}", "msg_signature": "xxx" }</pre>
 * 其中 {@code msg} 为订单信息的 JSON 字符串，需先解析再使用。
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayNotifyResult {

    /** 通知类型，如 payment / refund。 */
    private String type;

    /** 订单信息 JSON 字符串。 */
    private String msg;

    @JsonProperty("msg_signature")
    private String msgSignature;

    /** 验签是否通过（由 SDK 填充，非平台字段）。 */
    private transient boolean signatureValid;

    /** 将 {@code msg} 解析为通用 Map。 */
    public Map<String, Object> parseMsg() {
        if (msg == null || msg.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        return JsonUtils.toMap(msg);
    }

    /** 将 {@code msg} 解析为指定类型。 */
    public <T> T parseMsg(Class<T> type) {
        if (msg == null || msg.isEmpty()) {
            return null;
        }
        return JsonUtils.fromJson(msg, type);
    }
}

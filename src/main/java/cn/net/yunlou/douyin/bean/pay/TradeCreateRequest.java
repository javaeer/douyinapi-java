package cn.net.yunlou.douyin.bean.pay;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 担保支付预下单（创建支付订单）请求。
 *
 * @see <a href="https://developer.toutiao.com/api/apps/trade/create">创建支付订单</a>
 */
@Getter
@Setter
public class TradeCreateRequest extends AbstractPayRequest {

    /** 开发者侧的订单号，同一商户下唯一。 */
    private String outOrderNo;

    /** 订单总金额，单位：分。 */
    private Integer totalAmount;

    /** 商品标题（敏感词会被拦截）。 */
    private String subject;

    /** 商品详情。 */
    private String body;

    /** 订单有效期，单位：秒。 */
    private Integer validTime;

    /** 支付结果异步回调地址（需 HTTPS）。 */
    private String notifyUrl;

    /** 用户 open_id（直连商户下单时透传，第三方代调用可不传）。 */
    private String openId;

    /** 门店 ID。 */
    private String storeUid;

    /** 是否屏蔽支付成功后的服务通知（0 不屏蔽，1 屏蔽）。 */
    private Integer disableMsg;

    /** 支付成功页（屏蔽服务通知时跳转）。 */
    private String msgPage;

    @Override
    public Map<String, Object> toParamMap() {
        Map<String, Object> map = identityMap();
        putIfPresent(map, "out_order_no", outOrderNo);
        putIfPresent(map, "total_amount", totalAmount);
        putIfPresent(map, "subject", subject);
        putIfPresent(map, "body", body);
        putIfPresent(map, "valid_time", validTime);
        putIfPresent(map, "notify_url", notifyUrl);
        putIfPresent(map, "open_id", openId);
        putIfPresent(map, "store_uid", storeUid);
        putIfPresent(map, "disable_msg", disableMsg);
        putIfPresent(map, "msg_page", msgPage);
        return map;
    }
}

package cn.net.yunlou.douyin.bean.pay;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 担保支付查询退款请求。
 *
 * @see <a href="https://developer.toutiao.com/api/apps/ecpay/sa/">查询退款</a>
 */
@Getter
@Setter
public class RefundQueryRequest extends AbstractPayRequest {

    /** 开发者侧退款单号。 */
    private String outRefundNo;

    /** 平台退款单号。 */
    private String refundNo;

    /** 开发者侧原支付订单号。 */
    private String outOrderNo;

    /** 平台原支付订单号。 */
    private String orderId;

    @Override
    public Map<String, Object> toParamMap() {
        Map<String, Object> map = identityMap();
        putIfPresent(map, "out_refund_no", outRefundNo);
        putIfPresent(map, "refund_no", refundNo);
        putIfPresent(map, "out_order_no", outOrderNo);
        putIfPresent(map, "order_id", orderId);
        return map;
    }
}

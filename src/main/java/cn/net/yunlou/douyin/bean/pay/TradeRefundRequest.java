package cn.net.yunlou.douyin.bean.pay;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 担保支付发起退款请求。
 *
 * @see <a href="https://developer.toutiao.com/api/apps/trade/create_refund">发起退款</a>
 */
@Getter
@Setter
public class TradeRefundRequest extends AbstractPayRequest {

    /** 原支付订单的开发者侧订单号。 */
    private String outOrderNo;

    /** 开发者侧退款单号，同一商户下唯一。 */
    private String outRefundNo;

    /** 退款金额，单位：分。 */
    private Integer refundAmount;

    /** 退款原因。 */
    private String refundReason;

    /** 原支付订单的平台订单号（与 out_order_no 二选一）。 */
    private String orderId;

    @Override
    public Map<String, Object> toParamMap() {
        Map<String, Object> map = identityMap();
        putIfPresent(map, "out_order_no", outOrderNo);
        putIfPresent(map, "order_id", orderId);
        putIfPresent(map, "out_refund_no", outRefundNo);
        putIfPresent(map, "refund_amount", refundAmount);
        putIfPresent(map, "refund_reason", refundReason);
        return map;
    }
}

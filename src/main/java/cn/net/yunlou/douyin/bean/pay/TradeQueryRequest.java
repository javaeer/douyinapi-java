package cn.net.yunlou.douyin.bean.pay;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 担保支付查询订单请求。
 *
 * @see <a href="https://developer.toutiao.com/api/apps/trade/query">查询订单</a>
 */
@Getter
@Setter
public class TradeQueryRequest extends AbstractPayRequest {

    /** 开发者侧订单号。与 order_id 二选一。 */
    private String outOrderNo;

    /** 平台订单号。与 out_order_no 二选一。 */
    private String orderId;

    @Override
    public Map<String, Object> toParamMap() {
        Map<String, Object> map = identityMap();
        putIfPresent(map, "out_order_no", outOrderNo);
        putIfPresent(map, "order_id", orderId);
        return map;
    }
}

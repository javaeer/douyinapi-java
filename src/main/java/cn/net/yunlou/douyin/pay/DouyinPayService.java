package cn.net.yunlou.douyin.pay;

import cn.net.yunlou.douyin.bean.pay.PayNotifyResult;
import cn.net.yunlou.douyin.bean.pay.RefundQueryRequest;
import cn.net.yunlou.douyin.bean.pay.RefundQueryResult;
import cn.net.yunlou.douyin.bean.pay.TradeCreateRequest;
import cn.net.yunlou.douyin.bean.pay.TradeCreateResult;
import cn.net.yunlou.douyin.bean.pay.TradeQueryRequest;
import cn.net.yunlou.douyin.bean.pay.TradeQueryResult;
import cn.net.yunlou.douyin.bean.pay.TradeRefundRequest;
import cn.net.yunlou.douyin.bean.pay.TradeRefundResult;

/**
 * 抖音担保支付服务。
 *
 * <p>覆盖：预下单、查询订单、发起退款、查询退款，以及异步回调的解析与验签。
 */
public interface DouyinPayService {

    /** 预下单，返回 order_id / order_token 供前端 tt.pay 唤起收银台。 */
    TradeCreateResult createOrder(TradeCreateRequest request);

    /** 查询订单。 */
    TradeQueryResult queryOrder(TradeQueryRequest request);

    /** 发起退款。 */
    TradeRefundResult createRefund(TradeRefundRequest request);

    /** 查询退款。 */
    RefundQueryResult queryRefund(RefundQueryRequest request);

    /** 解析异步回调报文（不验签）。 */
    PayNotifyResult parseNotify(String notifyBody);

    /** 校验异步回调报文签名（MD5）。 */
    boolean verifyNotify(String notifyBody);
}

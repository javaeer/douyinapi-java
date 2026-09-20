package cn.net.yunlou.douyin.pay;

import cn.net.yunlou.douyin.DouyinClient;
import cn.net.yunlou.douyin.DouyinConstants;
import cn.net.yunlou.douyin.bean.pay.AbstractPayRequest;
import cn.net.yunlou.douyin.bean.pay.PayNotifyResult;
import cn.net.yunlou.douyin.bean.pay.RefundQueryRequest;
import cn.net.yunlou.douyin.bean.pay.RefundQueryResult;
import cn.net.yunlou.douyin.bean.pay.TradeCreateRequest;
import cn.net.yunlou.douyin.bean.pay.TradeCreateResult;
import cn.net.yunlou.douyin.bean.pay.TradeQueryRequest;
import cn.net.yunlou.douyin.bean.pay.TradeQueryResult;
import cn.net.yunlou.douyin.bean.pay.TradeRefundRequest;
import cn.net.yunlou.douyin.bean.pay.TradeRefundResult;
import cn.net.yunlou.douyin.error.DouyinErrorException;
import cn.net.yunlou.douyin.util.JsonUtils;
import cn.net.yunlou.douyin.util.SignUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link DouyinPayService} 的默认实现。
 */
public class DouyinPayServiceImpl implements DouyinPayService {

    private final DouyinClient client;

    public DouyinPayServiceImpl(DouyinClient client) {
        this.client = client;
    }

    @Override
    public TradeCreateResult createOrder(TradeCreateRequest request) {
        Map<String, Object> params = prepare(request);
        String resp = client.getRequestExecutor().postJson(DouyinConstants.PAY_TRADE_CREATE_URL, JsonUtils.toJson(params));
        TradeCreateResult result = JsonUtils.fromJson(resp, TradeCreateResult.class);
        result.checkSuccess();
        return result;
    }

    @Override
    public TradeQueryResult queryOrder(TradeQueryRequest request) {
        Map<String, Object> params = prepare(request);
        String resp = client.getRequestExecutor().postJson(DouyinConstants.PAY_TRADE_QUERY_URL, JsonUtils.toJson(params));
        TradeQueryResult result = JsonUtils.fromJson(resp, TradeQueryResult.class);
        result.checkSuccess();
        return result;
    }

    @Override
    public TradeRefundResult createRefund(TradeRefundRequest request) {
        Map<String, Object> params = prepare(request);
        String resp = client.getRequestExecutor().postJson(DouyinConstants.PAY_CREATE_REFUND_URL, JsonUtils.toJson(params));
        TradeRefundResult result = JsonUtils.fromJson(resp, TradeRefundResult.class);
        result.checkSuccess();
        return result;
    }

    @Override
    public RefundQueryResult queryRefund(RefundQueryRequest request) {
        Map<String, Object> params = prepare(request);
        String resp = client.getRequestExecutor().postJson(DouyinConstants.PAY_REFUND_QUERY_URL, JsonUtils.toJson(params));
        RefundQueryResult result = JsonUtils.fromJson(resp, RefundQueryResult.class);
        result.checkSuccess();
        return result;
    }

    @Override
    public PayNotifyResult parseNotify(String notifyBody) {
        PayNotifyResult result = JsonUtils.fromJson(notifyBody, PayNotifyResult.class);
        result.setSignatureValid(verifyNotify(notifyBody));
        return result;
    }

    @Override
    public boolean verifyNotify(String notifyBody) {
        if (notifyBody == null || notifyBody.isEmpty()) {
            return false;
        }
        Map<String, Object> map = JsonUtils.toMap(notifyBody);
        String salt = client.getConfigStorage().getSalt();
        return SignUtils.verifyNotify(map, salt);
    }

    /** 注入身份字段、计算并写入 sign，返回完整待提交参数表。 */
    private Map<String, Object> prepare(AbstractPayRequest request) {
        request.injectIdentity(client.getConfigStorage().getClientKey(),
                client.getConfigStorage().getMerchantId(), null);
        Map<String, Object> params = request.toParamMap();
        // 重新组装为有序 Map，确保 app_id / merchant_id 在前，便于排查
        Map<String, Object> ordered = new LinkedHashMap<>(params);
        String salt = client.getConfigStorage().getSalt();
        if (salt == null || salt.isEmpty()) {
            throw new DouyinErrorException("支付密钥 salt 未配置，无法完成签名");
        }
        String sign = SignUtils.createSign(ordered, salt);
        ordered.put("sign", sign);
        return ordered;
    }
}

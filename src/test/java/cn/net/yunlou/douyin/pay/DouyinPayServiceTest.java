package cn.net.yunlou.douyin.pay;


import cn.net.yunlou.douyin.DouyinClient;
import cn.net.yunlou.douyin.DouyinClientImpl;
import cn.net.yunlou.douyin.DouyinRequestExecutor;
import cn.net.yunlou.douyin.bean.pay.PayNotifyResult;
import cn.net.yunlou.douyin.bean.pay.TradeCreateRequest;
import cn.net.yunlou.douyin.bean.pay.TradeCreateResult;
import cn.net.yunlou.douyin.error.DouyinErrorException;
import cn.net.yunlou.douyin.impl.DefaultDouyinConfigImpl;
import cn.net.yunlou.douyin.util.JsonUtils;
import cn.net.yunlou.douyin.util.SignUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DouyinPayServiceTest {

    private DouyinRequestExecutor executor;
    private DouyinClient client;
    private static final String SALT = "salt_demo";

    @BeforeEach
    void setUp() {
        executor = mock(DouyinRequestExecutor.class);
        DefaultDouyinConfigImpl config = DefaultDouyinConfigImpl.builder()
                .clientKey("client_key_demo")
                .clientSecret("client_secret_demo")
                .merchantId("merchant_demo")
                .salt(SALT)
                .build();
        client = new DouyinClientImpl(config, executor);
    }

    @Test
    void createOrder_signsAndParses() {
        String json = "{\"err_no\":0,\"err_tips\":\"\",\"order_id\":\"order_123\","
                + "\"order_token\":\"token_abc\"}";
        String[] captured = new String[1];
        when(executor.postJson(eq("https://developer.toutiao.com/api/apps/trade/create"), any()))
                .thenAnswer(inv -> {
                    captured[0] = (String) inv.getArgument(1);
                    return json;
                });

        TradeCreateRequest req = new TradeCreateRequest();
        req.setOutOrderNo("NO20240901");
        req.setTotalAmount(100);
        req.setSubject("测试商品");
        req.setBody("测试描述");
        req.setValidTime(3600);
        req.setNotifyUrl("https://example.com/notify");
        req.setOpenId("open_id_demo");

        TradeCreateResult result = client.getPayService().createOrder(req);

        // 解析实际发出的 JSON，校验签名与关键字段
        Map<String, Object> sent = JsonUtils.toMap(captured[0]);
        assertEquals("client_key_demo", sent.get("app_id"));
        assertEquals("merchant_demo", sent.get("merchant_id"));
        assertEquals("NO20240901", sent.get("out_order_no"));
        assertEquals(100, sent.get("total_amount"));
        assertEquals("https://example.com/notify", sent.get("notify_url"));

        // 校验 createOrder 产出的 sign 与本地按相同规则计算一致（请求签名排除 app_id/sign）
        Map<String, Object> forSign = new java.util.LinkedHashMap<>(sent);
        forSign.remove("sign");
        assertEquals(SignUtils.createSign(forSign, SALT), sent.get("sign"));

        assertEquals("order_123", result.getOrderId());
        assertEquals("token_abc", result.getOrderToken());
        assertTrue(result.isSuccess());
    }

    @Test
    void createOrder_throwsWhenSaltMissing() {
        DefaultDouyinConfigImpl noSalt = DefaultDouyinConfigImpl.builder()
                .clientKey("ck").clientSecret("cs").merchantId("m").salt("").build();
        DouyinClient c = new DouyinClientImpl(noSalt, executor);
        TradeCreateRequest req = new TradeCreateRequest();
        req.setOutOrderNo("N");
        req.setTotalAmount(1);
        req.setSubject("s");
        req.setValidTime(60);
        req.setNotifyUrl("https://x");
        Assertions.assertThrows(
                DouyinErrorException.class,
                () -> c.getPayService().createOrder(req));
    }

    @Test
    void verifyNotify_validatesSignature() {
        // 构造担保支付异步回调报文（业务字段 + type + msg_signature）
        Map<String, Object> notify = new java.util.LinkedHashMap<>();
        notify.put("out_order_no", "NO20240901");
        notify.put("total_amount", 100);
        notify.put("order_status", 2);
        notify.put("type", "payment");
        String sig = SignUtils.notifySign(notify, SALT);
        notify.put("msg_signature", sig);
        String body = JsonUtils.toJson(notify);

        assertTrue(client.getPayService().verifyNotify(body));

        PayNotifyResult parsed = client.getPayService().parseNotify(body);
        assertEquals("payment", parsed.getType());
        assertTrue(parsed.isSignatureValid());

        // 篡改后验签失败
        Map<String, Object> tampered = new java.util.LinkedHashMap<>(notify);
        tampered.put("total_amount", 999);
        assertFalse(client.getPayService().verifyNotify(JsonUtils.toJson(tampered)));
    }
}

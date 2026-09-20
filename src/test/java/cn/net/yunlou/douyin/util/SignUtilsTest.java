package cn.net.yunlou.douyin.util;

import cn.net.yunlou.douyin.error.DouyinErrorException;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 担保支付 MD5 签名与 RSA 签名测试。
 *
 * <p>MD5 测试向量来自官方文档示例：参与签名的字段值按 ASCII 排序后用 & 连接，
 * 再追加 salt 做 MD5，结果应为 {@code 3c9421d0268a974138f4b36e9cefa1f1}。
 */
class SignUtilsTest {

    @Test
    void createSign_matchesOfficialVector() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("settle_params", "[{\"merchant_uid\":\"123345\",\"amount\":1}]");
        params.put("settle_desc", "开始结算与分账");
        params.put("out_settle_no", "mock_settle_no");
        params.put("out_order_no", "mock_settle_no");
        params.put("notify_url", "https://callback.com");
        params.put("app_id", "ttabcdefg123456"); // 身份字段，不参与签名

        String sign = SignUtils.createSign(params, "your_payment_salt");

        assertEquals("3c9421d0268a974138f4b36e9cefa1f1", sign);
    }

    @Test
    void createSign_excludesEmptyAndIdentityFields() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("app_id", "ttapp");
        params.put("thirdparty_id", "tttp");
        params.put("out_order_no", "NO2024");
        params.put("total_amount", 100);
        params.put("subject", "商品");
        params.put("body", "");           // 空串不参与
        params.put("notify_url", "https://x.com");
        params.put("sign", "shouldBeIgnored");

        // sign / app_id / thirdparty_id 排除；空串 body 排除
        // 参与排序的值：100, NO2024, https://x.com, 商品, salt
        String sign = SignUtils.createSign(params, "saltVal");
        // 反向校验：与手工构造（排除后）结果一致
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("out_order_no", "NO2024");
        expected.put("total_amount", 100);
        expected.put("subject", "商品");
        expected.put("notify_url", "https://x.com");
        assertEquals(SignUtils.createSign(expected, "saltVal"), sign);
    }

    @Test
    void verifyNotify_roundTrip() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("out_order_no", "NO2024");
        params.put("total_amount", 100);
        params.put("subject", "商品");
        params.put("notify_url", "https://x.com");
        // 平台推送还会带 type / msg_signature，此处模拟
        params.put("type", "payment");

        String sig = SignUtils.notifySign(params, "saltVal");
        params.put("msg_signature", sig);

        assertTrue(SignUtils.verifyNotify(params, "saltVal"));
        assertFalse(SignUtils.verifyNotify(params, "wrongSalt"));
    }

    @Test
    void rsaSignAndVerify_roundTrip() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair kp = gen.generateKeyPair();

        String privatePem = "-----BEGIN PRIVATE KEY-----\n"
                + java.util.Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded()).replaceAll("(.{64})", "$1\n").trim()
                + "\n-----END PRIVATE KEY-----";
        String publicPem = "-----BEGIN PUBLIC KEY-----\n"
                + java.util.Base64.getEncoder().encodeToString(kp.getPublic().getEncoded()).replaceAll("(.{64})", "$1\n").trim()
                + "\n-----END PUBLIC KEY-----";

        String content = "out_order_no=NO2024&total_amount=100";
        String signature = SignUtils.signRsa(content, privatePem);
        assertTrue(SignUtils.verifyRsa(content, signature, publicPem));
        assertFalse(SignUtils.verifyRsa(content + "x", signature, publicPem));
    }

    @Test
    void rsa_signRejectsEmptyKey() {
        try {
            SignUtils.signRsa("x", "");
            org.junit.jupiter.api.Assertions.fail("应抛出 DouyinErrorException");
        } catch (DouyinErrorException e) {
            // expected
        }
    }
}

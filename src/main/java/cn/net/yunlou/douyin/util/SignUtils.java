package cn.net.yunlou.douyin.util;

import cn.net.yunlou.douyin.error.DouyinErrorException;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 抖音开放平台签名工具。
 *
 * <h2>担保支付（MD5，旧版）</h2>
 * 官方签名算法（请求加签）：
 * <ol>
 *   <li>sign / app_id / thirdparty_id 为身份字段，<b>不参与签名</b>；</li>
 *   <li>取其余字段的<b>值</b>（不含 key），与支付密钥 salt 一起做字典序（ASCII）排序；</li>
 *   <li>用 {@code &} 连接成字符串；</li>
 *   <li>对字符串做 MD5，结果为小写十六进制。</li>
 * </ol>
 * 空字符串与省略参数不参与签名。回调验签同理，仅排除字段为 msg_signature / type（及 sign）。
 *
 * <h3>通用交易系统（RSA，新版）</h3>
 * 使用应用私钥 SHA256withRSA 加签、平台公钥验签，详见 {@link #signRsa} / {@link #verifyRsa}。
 */
public final class SignUtils {

    private SignUtils() {
    }

    /** 请求加签时排除的身份字段。 */
    private static final Set<String> REQUEST_EXCLUDE = new java.util.HashSet<>(
            Arrays.asList("sign", "app_id", "thirdparty_id"));

    /** 回调验签时排除的字段。 */
    private static final Set<String> NOTIFY_EXCLUDE = new java.util.HashSet<>(
            Arrays.asList("msg_signature", "type", "sign"));

    /**
     * 担保支付请求签名。
     *
     * @param params 请求体参数（含业务字段，不含 sign）
     * @param salt   支付密钥
     * @return 小写 MD5 签名
     */
    public static String createSign(Map<String, Object> params, String salt) {
        return sign(params, salt, REQUEST_EXCLUDE);
    }

    /**
     * 担保支付回调签名计算（不含 msg_signature/type）。
     *
     * @return 计算出的签名串，用于与平台下发的 msg_signature 比对
     */
    public static String notifySign(Map<String, Object> params, String salt) {
        return sign(params, salt, NOTIFY_EXCLUDE);
    }

    /**
     * 担保支付回调验签。
     *
     * @return 平台签名（msg_signature 或 sign）与本地计算值一致返回 true
     */
    public static boolean verifyNotify(Map<String, Object> params, String salt) {
        Object platformSig = params.get("msg_signature");
        if (platformSig == null) {
            platformSig = params.get("sign");
        }
        if (platformSig == null) {
            return false;
        }
        return notifySign(params, salt).equalsIgnoreCase(String.valueOf(platformSig));
    }

    private static String sign(Map<String, Object> params, String salt, Set<String> exclude) {
        // 注意：使用 List 而非 Set，避免相同取值（如两个 out_order_no）被去重；
        // 官方算法保留每个字段值，按字典序排序后拼接。
        List<String> values = new ArrayList<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (exclude.contains(entry.getKey())) {
                continue;
            }
            if (entry.getValue() == null) {
                continue;
            }
            String value = String.valueOf(entry.getValue()).trim();
            if (value.isEmpty()) {
                continue;
            }
            values.add(value);
        }
        if (salt != null && !salt.trim().isEmpty()) {
            values.add(salt.trim());
        }
        Collections.sort(values);
        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(s);
        }
        return DigestUtils.md5Hex(sb.toString().getBytes(StandardCharsets.UTF_8)).toLowerCase();
    }

    /* ========================= RSA（通用交易系统） ========================= */

    private static final String RSA_ALGORITHM = "RSA";
    private static final String SIGN_ALGORITHM = "SHA256withRSA";

    private static PrivateKey loadPrivateKey(String pem) {
        try {
            byte[] key = base64FromPem(pem);
            return KeyFactory.getInstance(RSA_ALGORITHM)
                    .generatePrivate(new PKCS8EncodedKeySpec(key));
        } catch (DouyinErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new DouyinErrorException("加载 RSA 私钥失败: " + e.getMessage(), e);
        }
    }

    private static PublicKey loadPublicKey(String pem) {
        try {
            byte[] key = base64FromPem(pem);
            return KeyFactory.getInstance(RSA_ALGORITHM)
                    .generatePublic(new X509EncodedKeySpec(key));
        } catch (DouyinErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new DouyinErrorException("加载 RSA 公钥失败: " + e.getMessage(), e);
        }
    }

    private static byte[] base64FromPem(String pem) {
        String cleaned = pem.replaceAll("-----BEGIN [A-Z ]*PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN [A-Z ]*PUBLIC KEY-----", "")
                .replaceAll("-----END [A-Z ]*PRIVATE KEY-----", "")
                .replaceAll("-----END [A-Z ]*PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        if (cleaned.isEmpty()) {
            throw new DouyinErrorException("RSA 密钥内容为空");
        }
        return Base64.getDecoder().decode(cleaned);
    }

    /**
     * RSA-SHA256 加签（通用交易系统）。
     *
     * @param content       待签明文（通常是按 key 升序拼接的 key=value&amp;... 串）
     * @param privateKeyPem 应用私钥 PEM
     * @return Base64 签名
     */
    public static String signRsa(String content, String privateKeyPem) {
        try {
            PrivateKey key = loadPrivateKey(privateKeyPem);
            Signature sig = Signature.getInstance(SIGN_ALGORITHM);
            sig.initSign(key);
            sig.update(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(sig.sign());
        } catch (DouyinErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new DouyinErrorException("RSA 加签失败: " + e.getMessage(), e);
        }
    }

    /**
     * RSA-SHA256 验签（通用交易系统）。
     *
     * @param content      待验证明文
     * @param signBase64   平台 Base64 签名
     * @param publicKeyPem 平台公钥 PEM
     */
    public static boolean verifyRsa(String content, String signBase64, String publicKeyPem) {
        try {
            PublicKey key = loadPublicKey(publicKeyPem);
            Signature sig = Signature.getInstance(SIGN_ALGORITHM);
            sig.initVerify(key);
            sig.update(content.getBytes(StandardCharsets.UTF_8));
            return sig.verify(Base64.getDecoder().decode(signBase64));
        } catch (DouyinErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new DouyinErrorException("RSA 验签失败: " + e.getMessage(), e);
        }
    }
}

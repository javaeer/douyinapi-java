package cn.net.yunlou.douyin.util;

import java.security.SecureRandom;

/**
 * 随机串工具，用于 nonce、state 等防重放参数。
 */
public final class RandomUtils {

    private static final char[] CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    private RandomUtils() {
    }

    /** 生成指定长度的随机字母数字串。 */
    public static String generate(int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS[RANDOM.nextInt(CHARS.length)]);
        }
        return sb.toString();
    }

    /** 生成 32 位随机串（常用作 nonce / state）。 */
    public static String generateNonce() {
        return generate(32);
    }

    /** 生成 16 位随机 state。 */
    public static String generateState() {
        return generate(16);
    }
}

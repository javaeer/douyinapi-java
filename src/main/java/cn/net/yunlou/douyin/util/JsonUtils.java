package cn.net.yunlou.douyin.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Jackson 封装工具。集中管理 {@link ObjectMapper} 配置，避免各模块重复创建。
 */
public final class JsonUtils {

    private JsonUtils() {
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 序列化失败: " + e.getMessage(), e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 反序列化失败: " + e.getMessage(), e);
        }
    }

    /** 将 JSON 解析为通用 Map（用于签名、回调校验等场景）。 */
    @SuppressWarnings("unchecked")
    public static java.util.Map<String, Object> toMap(String json) {
        try {
            return MAPPER.readValue(json, java.util.Map.class);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 解析为 Map 失败: " + e.getMessage(), e);
        }
    }
}

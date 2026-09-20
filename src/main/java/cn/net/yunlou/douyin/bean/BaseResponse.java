package cn.net.yunlou.douyin.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cn.net.yunlou.douyin.error.DouyinErrorException;
import lombok.Getter;
import lombok.Setter;

/**
 * 抖音开放平台响应基类。
 *
 * <p>开放平台（账号/授权）接口通常返回 {@code error_code} + {@code description}；
 * 支付接口通常返回 {@code err_no} + {@code err_tips}。二者在此统一承接，以兼容不同产品。
 */
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse {

    /** 开放平台错误码，0 表示成功。 */
    @JsonProperty("error_code")
    private Integer errorCode;

    /** 开放平台错误描述。 */
    private String description;

    /** 支付错误码，0 表示成功。 */
    @JsonProperty("err_no")
    private Integer errNo;

    /** 支付错误描述。 */
    @JsonProperty("err_tips")
    private String errTips;

    /** 通用消息字段。 */
    private String message;

    /** 是否调用成功（error_code 或 err_no 为 0 / 缺失）。 */
    public boolean isSuccess() {
        if (errorCode != null && errorCode != 0) {
            return false;
        }
        return !(errNo != null && errNo != 0);
    }

    /** 不成功时抛出 {@link DouyinErrorException}。 */
    public void checkSuccess() {
        if (!isSuccess()) {
            int code = errorCode != null ? errorCode : (errNo != null ? errNo : 0);
            String desc = description != null ? description : (errTips != null ? errTips : message);
            throw new DouyinErrorException(code, desc, "抖音接口返回业务错误");
        }
    }

    /** 平台实际返回的错误描述（优先 err_tips，其次 description，再次 message）。 */
    public String resolvedErrorDescription() {
        if (errTips != null) {
            return errTips;
        }
        if (description != null) {
            return description;
        }
        return message;
    }
}

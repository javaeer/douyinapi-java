package cn.net.yunlou.douyin.error;

import lombok.Getter;

/**
 * 抖音开放平台 / 支付接口调用异常。
 *
 * <p>统一封装 HTTP 失败、平台返回的业务错误码（error_code / err_no）以及签名校验失败等场景。
 */
@Getter
public class DouyinErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 平台错误码（error_code / err_no），成功时为 0。 */
    private final int errorCode;

    /** 平台错误描述（description / err_tips）。 */
    private final String errorDescription;

    public DouyinErrorException(String message) {
        this(0, null, message, null);
    }

    public DouyinErrorException(String message, Throwable cause) {
        this(0, null, message, cause);
    }

    public DouyinErrorException(int errorCode, String errorDescription, String message) {
        this(errorCode, errorDescription, message, null);
    }

    public DouyinErrorException(int errorCode, String errorDescription, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    /** 是否由平台返回的业务错误码触发。 */
    public boolean isBizError() {
        return errorCode != 0;
    }

    @Override
    public String getMessage() {
        if (errorCode != 0) {
            return "Douyin error_code=" + errorCode
                    + (errorDescription != null ? " (" + errorDescription + ")" : "")
                    + ": " + super.getMessage();
        }
        return super.getMessage();
    }
}

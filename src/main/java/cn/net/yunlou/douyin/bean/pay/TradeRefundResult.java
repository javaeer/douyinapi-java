package cn.net.yunlou.douyin.bean.pay;

import com.fasterxml.jackson.annotation.JsonProperty;
import cn.net.yunlou.douyin.bean.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 发起退款响应。
 */
@Getter
@Setter
public class TradeRefundResult extends BaseResponse {

    @JsonProperty("refund_no")
    private String refundNo;

    @JsonProperty("out_refund_no")
    private String outRefundNo;

    /** 退款状态：1 退款中；2 退款成功；3 退款失败。 */
    private Integer status;

}

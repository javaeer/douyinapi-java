package cn.net.yunlou.douyin.bean.pay;

import com.fasterxml.jackson.annotation.JsonProperty;
import cn.net.yunlou.douyin.bean.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询退款响应。
 */
@Getter
@Setter
public class RefundQueryResult extends BaseResponse {

    @JsonProperty("refund_no")
    private String refundNo;

    @JsonProperty("out_refund_no")
    private String outRefundNo;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("out_order_no")
    private String outOrderNo;

    @JsonProperty("refund_amount")
    private Integer refundAmount;

    /** 退款状态：1 退款中；2 退款成功；3 退款失败。 */
    @JsonProperty("refund_status")
    private Integer refundStatus;

    @JsonProperty("refund_succeed_time")
    private Long refundSucceedTime;

}

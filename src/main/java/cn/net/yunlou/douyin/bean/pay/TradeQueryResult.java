package cn.net.yunlou.douyin.bean.pay;

import com.fasterxml.jackson.annotation.JsonProperty;
import cn.net.yunlou.douyin.bean.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询订单响应。
 *
 * <p>order_status 取值（部分）：1 待支付；2 已支付；3 已取消；4 已风控驳回；5 已关闭；
 * 6 已超期；7 已申请退款；8 退款中；9 退款成功；10 退款失败。
 */
@Getter
@Setter
public class TradeQueryResult extends BaseResponse {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("out_order_no")
    private String outOrderNo;

    @JsonProperty("order_status")
    private Integer orderStatus;

    @JsonProperty("pay_time")
    private Long payTime;

    @JsonProperty("total_amount")
    private Integer totalAmount;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("status_msg")
    private String statusMsg;

}

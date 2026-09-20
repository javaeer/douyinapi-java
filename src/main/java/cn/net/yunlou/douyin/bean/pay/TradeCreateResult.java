package cn.net.yunlou.douyin.bean.pay;

import com.fasterxml.jackson.annotation.JsonProperty;
import cn.net.yunlou.douyin.bean.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 预下单响应。成功时返回 {@code order_id} 与 {@code order_token}，供前端唤起收银台（tt.pay）。
 */
@Getter
@Setter
public class TradeCreateResult extends BaseResponse {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("order_token")
    private String orderToken;

}

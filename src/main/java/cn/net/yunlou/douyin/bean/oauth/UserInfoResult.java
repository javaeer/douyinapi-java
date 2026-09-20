package cn.net.yunlou.douyin.bean.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import cn.net.yunlou.douyin.bean.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息响应。
 *
 * @see <a href="https://open.douyin.com/oauth/userinfo/">获取用户信息</a>
 */
@Getter
@Setter
public class UserInfoResult extends BaseResponse {

    private String nickname;

    private String avatar;

    private String city;

    private String province;

    private String country;

    /** 性别：* 0 未知；1 男；2 女。 */
    private Integer gender;

    @JsonProperty("open_id")
    private String openId;

    @JsonProperty("union_id")
    private String unionId;

    @JsonProperty("e_account_role")
    private String eAccountRole;
}

package cn.net.yunlou.douyin.bean.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import cn.net.yunlou.douyin.bean.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取应用级 client_token 的响应（无需用户授权即可调用的接口凭证）。
 *
 * @see <a href="https://open.douyin.com/oauth/client_token/">生成 client_token</a>
 */
@Getter
@Setter
public class ClientTokenResult extends BaseResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    private String scope;

}

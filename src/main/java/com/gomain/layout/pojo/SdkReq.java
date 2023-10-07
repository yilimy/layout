package com.gomain.layout.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SDK接口请求参数
 * @author caimeng
 * @date 2023/10/7 17:36
 */
@Data
@NoArgsConstructor
public class SdkReq {
    /** 用户标识 **/
    private String userId;
    /** 业务标识 **/
    private String dataType;
    /** 印章标识 **/
    private String sealId;

    public SdkReq(String userId, String dataType, String sealId) {
        this.userId = userId;
        this.dataType = dataType;
        this.sealId = sealId;
    }
}

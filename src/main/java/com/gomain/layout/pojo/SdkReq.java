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
    private String outUserId;
    /** 业务标识 **/
    private String dataType;
    /** 印章标识 **/
    private String sealId;
    /** 待签名摘要 **/
    private String digest;

    public SdkReq(String userId, String dataType, String sealId) {
        this.outUserId = userId;
        this.dataType = dataType;
        this.sealId = sealId;
    }

    public SdkReq(String outUserId, String dataType, String sealId, String digest) {
        this.outUserId = outUserId;
        this.dataType = dataType;
        this.sealId = sealId;
        this.digest = digest;
    }
}

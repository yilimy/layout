package com.gomain.layout.pojo;

import com.gomain.layout.configuration.MyNoNullStyle;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 调用SDK签章街头的返回数据结构
 * @author caimeng
 * @date 2023/10/8 16:42
 */
@Data
public class SdkStampRsp {
    /**
     * 签章结构体
     */
    private String puchSignValue;
    /**
     * 签名值
     */
    private String signValue;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(
                this, new MyNoNullStyle().setLimit("puchSignValue"));
    }
}

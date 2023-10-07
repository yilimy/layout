package com.gomain.layout.pojo;

import com.gomain.layout.configuration.MyNoNullStyle;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 印章查询结果
 * @author caimeng
 * @date 2023/9/26 13:44
 */
@Data
public class QuerySealRsp {
    /**
     * 印章数据
     */
    private String sealData;
    /**
     * 印章索引
     */
    private String index;
    /**
     * 印章类型
     */
    private String sealType;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(
                this, new MyNoNullStyle().setLimit("sealData"));
    }
}

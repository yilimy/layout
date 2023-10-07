package com.gomain.layout.pojo;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * @author caimeng
 * @date 2023/9/8 13:52
 */
@Data
@NoArgsConstructor
public class RootChainPO {
    /**
     * 根证书别名
     * 一般为跟证书文件名，使用者密钥标识符
     */
    private String alias;
    /**
     * 证书链
     */
    private List<CertificatePO> rootChain;

    public RootChainPO(List<CertificatePO> rootChain) {
        this.rootChain = rootChain;
    }

    public CertificatePO findCertBySubject(String subjectId) {
        if (Objects.nonNull(rootChain) && rootChain.size() > 0) {
            return rootChain.stream().filter(item -> StrUtil.equals(subjectId, item.getSubjectId())).findAny().orElse(null);
        }
        return null;
    }
}

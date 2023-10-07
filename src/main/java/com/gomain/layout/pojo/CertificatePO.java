package com.gomain.layout.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bouncycastle.asn1.x509.Certificate;

import java.util.HashSet;
import java.util.Set;

/**
 * @author caimeng
 * @date 2023/9/8 14:05
 */
@Data
@NoArgsConstructor
@ToString(exclude = "authorityPO")
public class CertificatePO {
    /**
     * 证书指纹
     */
    private String finger;
    /**
     * 颁发者密钥标识符
     */
    private String authorityId;
    /**
     * 使用者密钥标识符
     */
    private String subjectId;
    /**
     * 证书对象
     */
    private Certificate certificate;
    /**
     * 颁发者证书
     * 该属性可能时对象本身，为防止内存溢出，不能toString或者序列化
     */
    private CertificatePO authorityPO;

    public CertificatePO(String finger, String authorityId, String subjectId, Certificate certificate) {
        this.finger = finger;
        this.authorityId = authorityId;
        this.subjectId = subjectId;
        this.certificate = certificate;
    }

    /**
     * 获取证书的顶级颁发者
     * @return 顶级颁发者证书
     */
    public CertificatePO getRooter(){
        if (this.authorityPO == null) {
            throw new RuntimeException("证书链中没有找到根证书");
        }
        // 颁发者等于使用者，是所属证书链的顶级根证书
        if (this.authorityPO.equals(this)) {
            return this;
        }
        Set<CertificatePO> poSet = new HashSet<>();
        CertificatePO po = this.authorityPO;
        while (po != null) {
            boolean addResult = poSet.add(po);
            if (!addResult) {
                // 添加失败，说明有闭环
                throw new RuntimeException("查找根证书失败，证书链闭环");
            }
            po = po.getAuthorityPO();
        }
        return po;
    }
}

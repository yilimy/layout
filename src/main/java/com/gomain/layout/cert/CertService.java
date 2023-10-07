package com.gomain.layout.cert;

import org.bouncycastle.asn1.x509.Certificate;

import java.util.Date;

/**
 * @author caimeng
 * @date 2023/9/8 10:20
 */
public interface CertService {

    /**
     * 根证书验证
     * 1. 是否国密算法（可选）
     * 2. 证书寻根
     * 3. 证书签名验证
     * 4. 签名时证书有效性验证
     * @param x509Cert x509证书对象
     * @param date 签名时间
     * @return 验证结果
     */
    boolean certificateVerify(Certificate x509Cert, Date date);

    /**
     * 验证证书有效性
     * @param x509Cert x509证书对象
     * @param date 待验证时间，默认当前时间
     * @return 证书是否有效
     */
    boolean timeValidity(Certificate x509Cert, Date date);

    /**
     * @param x509Cert x509证书对象
     * @return 证书签名算法是否国标
     */
    boolean oidIfGm(Certificate x509Cert);
}

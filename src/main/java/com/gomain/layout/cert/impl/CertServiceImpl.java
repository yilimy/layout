package com.gomain.layout.cert.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baiwang.cloud.stamp.seseal.utils.MySm2Utils;
import com.gomain.layout.cert.CertService;
import com.gomain.layout.cert.RootChainService;
import com.gomain.layout.pojo.CertificatePO;
import com.gomain.layout.pojo.RootChainPO;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.x509.Certificate;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author caimeng
 * @date 2023/9/8 10:21
 */
@Slf4j
public class CertServiceImpl implements CertService {

    @Override
    public boolean certificateVerify(Certificate x509Cert, Date date) {
        // 是否国密
        boolean gmValidate = oidIfGm(x509Cert);
        log.info("是否国密算法: {}", gmValidate);
        // 证书寻根
        List<RootChainPO> rootChainPOList = RootChainService.getRootChainList();
        String authorityId = RootChainService.authorityKeyIdentifier(x509Cert);
        RootChainPO rootChain = null;
        for (RootChainPO rootChainPO : rootChainPOList) {
            if (authorityId.equals(rootChainPO.getAlias())) {
                rootChain = rootChainPO;
                break;
            }
        }
        Certificate rootCert = Optional.ofNullable(rootChain)
                // 使用者证书的颁发者密钥标识符，取匹配跟证书的使用者密钥标识符
                .map(po -> po.findCertBySubject(authorityId))
                .map(CertificatePO::getCertificate)
                .orElseThrow(() -> new RuntimeException("没有找到根证书"));
        log.info("找到根证书: {}", authorityId);
        // 证书签名值验证
        boolean certSignValueValidate = verifySignValueByGm(x509Cert, rootCert);
        log.info("证书签名值验证: {}", certSignValueValidate);
        // 当前证书不一定有效，但是签名时证书可能有效
        boolean signTimeValidate = timeValidity(x509Cert, date);
        log.info("签名时间验证: {}", signTimeValidate);
        return true;
    }

    /**
     * 使用国密算法进行证书签名值验证
     * 根证书公钥验证使用者证书的签名值
     *
     * @param x509Cert  需要验证的证书
     * @param rootCert 根证书
     * @return 是否验证成功
     */
    private boolean verifySignValueByGm(Certificate x509Cert, Certificate rootCert) {
        try {
            byte[] pubKey = rootCert.getSubjectPublicKeyInfo().getEncoded();
            byte[] toSign = x509Cert.getTBSCertificate().getEncoded();
            byte[] signValue = x509Cert.getSignature().getOctets();
            return MySm2Utils.sm2Verify(pubKey, toSign, signValue);
        } catch (Exception e) {
            log.error("证书签名值验证失败", e);
        }
        return false;
    }

    @Override
    public boolean timeValidity(Certificate certificate, Date date) {
        try {
            Date targetDate = Optional.ofNullable(date).orElse(new Date());
            Date notAfter = certificate.getEndDate().getDate();
            Date notBefore = certificate.getStartDate().getDate();
            boolean before = targetDate.before(notAfter);
            boolean after = targetDate.after(notBefore);
            boolean result = before && after;
            if (!result){
                log.error("证书不在有效期范围内[{}, {}]",
                        DateUtil.format(notBefore, DatePattern.NORM_DATETIME_PATTERN),
                        DateUtil.format(notAfter, DatePattern.NORM_DATETIME_PATTERN));
            }
            return result;
        } catch (Exception e){
            log.error("证书时效检查失败", e);
        }
        return false;
    }

    @Override
    public boolean oidIfGm(Certificate certificate) {
        ASN1ObjectIdentifier algorithm = certificate.getSignatureAlgorithm().getAlgorithm();
        boolean result = GMObjectIdentifiers.sm2sign_with_sm3.equals(algorithm);
        if (!result && Objects.nonNull(algorithm)){
            log.error("证书签名算法:{}", algorithm.getId());
        }
        return result;
    }

}

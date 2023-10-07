package com.gomain.layout.gm;

import com.baiwang.cloud.stamp.seseal.SealFactory;
import com.baiwang.cloud.stamp.seseal.bean.SESeal;
import com.baiwang.cloud.stamp.seseal.core.impl.SeSignImp;
import com.gomain.layout.cert.CertService;
import com.gomain.layout.cert.impl.CertServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jcajce.provider.digest.SM3;
import org.ofdrw.gm.sm2strut.VerifyInfo;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;

/**
 * @author caimeng
 * @date 2023/9/11 14:13
 */
@Slf4j
public class GBT38540Validate {
    /**
     * 验证 GBT38540 结构<br>
     * 参考自 GBT35275Validate {@link org.ofdrw.gm.sm2strut.GBT35275Validate#validate(String, byte[], byte[]) }
     * @param alg 算法
     * @param tbsContent 待签名数据原文，不需要提前计算摘要
     * @param signedValue 签名值DER编码
     * @return 验证结果
     */
    public static VerifyInfo validate(String alg, byte[] tbsContent, byte[] signedValue) {
        // 核验算法
        if (!GMObjectIdentifiers.sm2sign_with_sm3.getId().equals(alg)) {
            throw new IllegalArgumentException("非法的签名算法：" + alg);
        }
        // 计算文件保护区摘要
        MessageDigest md = new SM3.Digest();
        byte[] digestAct = md.digest(tbsContent);
        // 读取签章结构体
        final SeSignImp seSign = (SeSignImp) SealFactory.getSignInstance(signedValue);
        // 验证签章结构体
        return validateSeSign(digestAct, seSign);
    }

    /**
     * 验证 GBT38540 结构
     * @param digest 文件保护区摘要值
     * @param seSign 签章结构体
     * @return 验证结果
     */
    public static VerifyInfo validateSeSign(byte[] digest, SeSignImp seSign) {
        // 从签章结构体中解析原文摘要
        byte[] plaintext = seSign.getSesSign().getToSign().getDataHash().getOctets();
        // 比较 digestAct 与 signedValue 中的文件摘要
        if (!Arrays.equals(digest, plaintext)) {
            log.error("摘要不一致:\nfiledHashHex ={}\ndataHashHex={}", digest, plaintext);
            return VerifyInfo.Err("摘要不一致");
        }
        // 验证签名时间是否在印章有效期内
        Date signedDate = seSign.getSignedDate();
        SESeal seSeal = seSign.getSesSign().getToSign().getEseal();
        if (seSeal.notBefore().before(signedDate) && seSeal.notAfter().after(signedDate)) {
            log.info("签章时间有效");
        } else {
            log.error("签章时间无效, signedDate={}, validate=[{},{}]", signedDate, seSeal.notBefore(), seSeal.notAfter());
            throw new RuntimeException("签章时间无效");
        }
        // TODO 验证签章日期是否在签章人证书有效期内
        // 验证签章人证书链
        Certificate x509Cert = seSign.getSesSign().getCert().getCertX509();
        CertService certService = new CertServiceImpl();
        boolean certificateVerify = certService.certificateVerify(x509Cert, signedDate);
        log.info("签章人证书链验证结果: {}", certificateVerify);
        //TODO 验证制章人证书链
        /**
         * 或者通过反射注入算法进行验证
         * <a href="https://blog.csdn.net/q1009020096/article/details/103434994">PDF 国密签名验证</a>
         * 该帖子的iText版本较高
         */
        // 签章结构体自验
        boolean structSelfVerify = seSign.getSesSign().verifyToSign();
        if (!structSelfVerify) {
            return VerifyInfo.Err("签名值验证失败");
        }
        return VerifyInfo.OK();
    }

}

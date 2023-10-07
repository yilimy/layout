package com.gomain.layout.cert;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.gomain.layout.pojo.CertificatePO;
import com.gomain.layout.pojo.RootChainPO;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.util.ObjectUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 根证书服务
 * @author caimeng
 * @date 2023/9/8 10:28
 */
@Slf4j
public class RootChainService {
    /** 根证书存放路径 **/
    private static final String rootCertPath = "E:\\tmp\\rootCertChain";
    /**
     * 根证书链
     */
    private static List<RootChainPO> rootChainList = new ArrayList<>();

    /**
     * @return 读取根证书
     */
    public static List<RootChainPO> getRootChainList() {
        if (ObjectUtils.isEmpty(rootChainList)) {
            // 加载根证书
            RootChainService rootChainService = new RootChainService();
            rootChainService.loadFromPath();
        }
        return rootChainList;
    }

    /**
     * 从路径中读取根证书
     */
    private void loadFromPath() {
        log.info("加载证书链");
        List<File> files = FileUtil.loopFiles(rootCertPath);
        rootChainList = new ArrayList<>(files.size());
        for (File file : files) {
            log.info("读取文件: {}", file.getAbsolutePath());
            String rootChainStr = FileUtil.readUtf8String(file);
            List<Certificate> certificates = readRootCertChain(rootChainStr);
            if (certificates.isEmpty()) {
                continue;
            }
            RootChainPO rootChainPO = parseCertificates(certificates);
            rootChainPO.setAlias(file.getName());
            rootChainList.add(rootChainPO);
        }
        log.info("证书链加载完毕，size={}", rootChainList.size());
    }

    /**
     * 将根证书链中的证书列表转化为链表
     * @param certificates 证书列表
     * @return 证书链
     */
    private RootChainPO parseCertificates(List<Certificate> certificates) {
        List<CertificatePO> rootChain = new ArrayList<>(certificates.size());
        for (Certificate cert : certificates) {
            try {
                // 读取证书指纹
                String finger = getCertFingerprint(cert);
                // 读取证书中的颁发者密钥标识符
                String authorityId = authorityKeyIdentifier(cert);
                // 读取证书中的使用者密钥标识符
                String subjectId = subjectKeyIdentifier(cert);
                CertificatePO po = new CertificatePO(finger, authorityId, subjectId, cert);
                rootChain.add(po);
            } catch (Exception e) {
                log.error("读取证书信息失败", e);
            }
        }
        // 证书寻根
        for (CertificatePO po : rootChain) {
            String authorityId = po.getAuthorityId();
            if (!ObjectUtils.isEmpty(authorityId)) {
                // 颁发者等于使用者，是上级
                for (CertificatePO _tmpPO : rootChain) {
                    if (authorityId.equals(_tmpPO.getSubjectId())){
                        // 设置颁发者证书
                        po.setAuthorityPO(_tmpPO);
                        break;
                    }
                }
            }
        }
        return new RootChainPO(rootChain);
    }

    /**
     * 解析根证书字符串
     * e.g.
     * <code>
     *     {
     * 	    "ROOTCERTNUM": "3",
     * 	    "ROOTCERT": {
     * 		    "ROOTCERT2": "MIICo...2wDBA==",
     * 		    "ROOTCERT3": "MIICr...9fihJJU=",
     * 		    "ROOTCERT1": "MIIBs...Lwg=="
     *          }
     *     }
     * </code>
     * @param rootChainStr 根证书字符串
     * @return 证书列表
     */
    private List<Certificate> readRootCertChain(String rootChainStr){
        try {
            JSONObject jsonObject = JSON.parseObject(rootChainStr);
            JSONObject rootCerts = jsonObject.getJSONObject("ROOTCERT");
            Map<String, String> certMap = rootCerts.toJavaObject(new TypeReference<Map<String, String>>(){});
            return certMap.values()
                    .stream()
                    .map(Base64.getDecoder()::decode)
                    .map(Certificate::getInstance)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("读取根证书链失败,rootChainStr=" + rootChainStr, e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取证书指纹信息
     * @param certificate x509证书对象
     * @return
     */
    public static String getCertFingerprint(Certificate certificate) throws Exception{
        byte[] certBytes = certificate.getEncoded();
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(certBytes);
        byte[] digest = md.digest();
        String digestHex = DatatypeConverter.printHexBinary(digest);
        return digestHex.toLowerCase();
    }

    /**
     * 读取证书中的颁发者密钥标识符
     * @param certificate x509证书
     * @return 颁发者密钥标识符
     */
    public static String authorityKeyIdentifier(Certificate certificate){
        return Optional.ofNullable(certificate)
                .map(Certificate::getTBSCertificate)
                .map(TBSCertificate::getExtensions)
                .map(AuthorityKeyIdentifier::fromExtensions)
                .map(AuthorityKeyIdentifier::getKeyIdentifier)
                .map(Hex::toHexString)
                .orElseThrow(() -> new RuntimeException("获取颁发者密钥标识符失败"));
    }

    /**
     * 读取证书中的使用者密钥标识符
     * @param certificate x509证书
     * @return 使用者密钥标识符
     */
    public static String subjectKeyIdentifier(Certificate certificate){
        return Optional.ofNullable(certificate)
                .map(Certificate::getTBSCertificate)
                .map(TBSCertificate::getExtensions)
                .map(SubjectKeyIdentifier::fromExtensions)
                .map(SubjectKeyIdentifier::getKeyIdentifier)
                .map(Hex::toHexString)
                .orElseThrow(() -> new RuntimeException("获取使用者密钥标识符失败"));
    }

}

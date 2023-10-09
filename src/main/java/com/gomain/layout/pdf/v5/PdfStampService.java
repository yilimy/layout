package com.gomain.layout.pdf.v5;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.SM3;
import com.baiwang.cloud.stamp.seseal.SealFactory;
import com.baiwang.cloud.stamp.seseal.core.SealInterface;
import com.gomain.layout.integration.SealService;
import com.gomain.layout.pojo.Constant;
import com.gomain.layout.pojo.StampStrategy;
import com.gomain.layout.pojo.StrategyPosition;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.OcspClient;
import com.itextpdf.text.pdf.security.TSAClient;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 参考《digitalsignatures20130304.pdf》第29页
 * @author caimeng
 * @date 2023/10/7 17:16
 */
@Slf4j
public class PdfStampService {
    /**
     * 签章地址（测试数据）
     */
    private final SealService sealService;

    public PdfStampService(SealService sealService) {
        this.sealService = sealService;
    }

    /**
     * 对文件做一次签章
     * <p>
     *     1. 暂未实现一个章在多个页面显示，但只有一个签章结构的功能。一次签章一个PdfSignatureAppearance，指定一个page。
     *     2. 多次签章需要多次文件IO
     * </p>
     * @param path 待签章文件路径
     * @param dest 签章后文件路径
     * @param strategy 签章策略
     * @throws Exception Exception
     */
    public void stampSingle(String path, String dest, StampStrategy strategy) throws Exception{
        try {
            // Creating the reader and the stamper
            PdfReader reader = new PdfReader(path);
//            log.info("====> {}", reader.getPdfVersion());
            FileOutputStream os = new FileOutputStream(dest);
            // （第51页） Code sample 2.17: adding an extra signature（第51页）
            PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, true);
            // 如果不使用追加模式，后签章会破坏之前所有签章结构。未签署文件不受影响。
//            PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
            byte[] sealBytes = sealService.findSealByte(strategy.getUserId(), strategy.getSealId());
            int sealSize = sealBytes.length;
            SealInterface sealInterface = SealFactory.getSealInstance(sealBytes);
            // 解析印章中的签名证书
            // 注册BC，否则不识别SM2证书和算法
            Security.addProvider(new BouncyCastleProvider());
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
            java.security.cert.Certificate cert;
            try (ByteArrayInputStream bais = new ByteArrayInputStream(sealInterface.getSignerCert())){
                cert = certificateFactory.generateCertificate(bais);
            }
            List<StrategyPosition> positions = strategy.getPositions();
            if (positions.size() != 1){
                throw new RuntimeException("不支持多个签章");
            }
            StrategyPosition position = positions.get(0);
            // Creating the appearance
            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
            int page = position.getPage();
            float x = position.getX();
            float y = position.getY();
            // 印章单位是毫米，需要转磅(2.83)
            float urx = x + sealInterface.getImgWidth() * Constant.POUND_PER_MM;
            float ury = y + sealInterface.getImgHeight() * Constant.POUND_PER_MM;
            // 取页面宽高
            Rectangle cropBox = reader.getCropBox(page);
            float ph = cropBox.getHeight();
            // 坐标系转换，默认坐下顶点
            if (position.getCoordinate() == Constant.COORDINATE_LEFT_TOP) {
                y = ph - y;
                ury = ph - ury;
            }
            // 设置可视区域
            // fieldName 在本文件中唯一
            appearance.setVisibleSignature(new Rectangle(x, y, urx, ury), page, "sig_" + System.currentTimeMillis());
            /*
             * 《digitalsignatures20130304.pdf》第44页
             * renderingMode 默认值：RenderingMode.DESCRIPTION
             * 置空文本，因为默认显示：“Digitally signed by ... Date : ...”
             * appearance.setLayer2Text("");
             * appearance.setImage(Image.getInstance(sealInterface.getImage()));
             * 如果指定 renderingMode 为 RenderingMode.GRAPHIC，则将 setImage 替换为 setSignatureGraphic
             */
            appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
            appearance.setSignatureGraphic(Image.getInstance(sealInterface.getImage()));
            // 估算签章结构体大小
            int estimatedSize = sealSize + sealInterface.getSignerCert().length + 1024;
            // 设置签章结构体
            signDetached(appearance, cert, estimatedSize, strategy);
        } finally {
            // 出现错误时，删除错误文件
            if (FileUtil.exist(dest) && FileUtil.size(new File(dest)) == 0 ) {
                log.error("写入文件为空，删除文件:{}", dest);
                FileUtil.del(dest);
            }
        }

    }

    /**
     * 参考 {@link com.itextpdf.text.pdf.security.MakeSignature#signDetached(PdfSignatureAppearance, ExternalDigest, ExternalSignature, java.security.cert.Certificate[], Collection, OcspClient, TSAClient, int, MakeSignature.CryptoStandard)}
     * @param sap the PdfSignatureAppearance
     * @param cert the certificate
     * @param estimatedSize the reserved size for the signature. It will be estimated if 0
     * @throws Exception Exception
     */
    private void signDetached(PdfSignatureAppearance sap, java.security.cert.Certificate cert, int estimatedSize, StampStrategy strategy) throws Exception {
        sap.setCertificate(cert);
        // 自定义的签章结构对象
        PdfSignatureStamp dic = new PdfSignatureStamp();
        dic.setSignatureCreator(sap.getSignatureCreator());
        dic.setContact(sap.getContact());
        dic.setDate(new PdfDate(sap.getSignDate())); // time-stamp will over-rule this
        sap.setCryptoDictionary(dic);

        HashMap<PdfName, Integer> exc = new HashMap<>();
        // PdfSignatureAppearance.preClose中要求byte_size*2+2.
        exc.put(PdfName.CONTENTS, estimatedSize * 2 + 2);
        sap.preClose(exc);
        // 文件保护区摘要
        byte[] hash;
        try (InputStream data = sap.getRangeStream()) {
            hash = SM3.create().digest(data);
        }
        // 签章
        byte[] encodedSig = sealService.sdkStampByte(strategy.getUserId(), strategy.getSealId(), hash);

        if (estimatedSize < encodedSig.length)
            throw new IOException("Not enough space");

        byte[] paddedSig = new byte[estimatedSize];
        System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);

        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
        sap.close(dic2);
    }
}

package com.gomain.layout.pdf.v5;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.digest.SM3;
import com.baiwang.cloud.stamp.seseal.SealFactory;
import com.baiwang.cloud.stamp.seseal.bean.SES_Sign;
import com.baiwang.cloud.stamp.seseal.bean.SESeal;
import com.baiwang.cloud.stamp.seseal.bean.TBS_Sign;
import com.baiwang.cloud.stamp.seseal.core.impl.SeSignImp;
import com.gomain.layout.gm.GBT38540Validate;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.io.RASInputStream;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import com.itextpdf.text.pdf.security.VerificationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.DERBitString;
import org.ofdrw.gm.sm2strut.VerifyInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**
 * 该类参考了iText中AcroFields的实现进行改写
 * @see com.itextpdf.text.pdf.AcroFields
 * @author caimeng
 * @date 2023/9/6 19:37
 */
@Slf4j
@Getter
public class AcroFieldsGBT {
    private AcroFields fields;
    private PdfReader reader;

    public AcroFieldsGBT(PdfReader reader, AcroFields fields) {
        this.fields = fields;
        this.reader = reader;
    }

    /**
     * 该方法参考自itext验证方法，原方法只能返回PdfPKCS7
     * @see com.itextpdf.text.pdf.AcroFields#verifySignature(String)
     * @param name SignatureName
     */
    public void verifySignature(String name) throws VerificationException {
        /**
         * 检查是否覆盖签名域
         * @see com.itextpdf.text.pdf.security.LtvVerifier#coversWholeDocument()
         */
        if (fields.signatureCoversWholeDocument(name)) {
            log.info("The timestamp covers whole document.");
        } else {
            throw new VerificationException(null, "Signature doesn't cover whole document.");
        }
        PdfDictionary v = fields.getSignatureDictionary(name);
        // GM.sm2seal, 是否要通过Filter进行过滤
        PdfName sub = v.getAsName(PdfName.SUBFILTER);
        System.out.println("sub=" + sub);
        PdfString contents = v.getAsString(PdfName.CONTENTS);
        // 签章结构体
        byte[] bytes = contents.getOriginalBytes();
        SeSignImp signInstance = (SeSignImp) SealFactory.getSignInstance(bytes);
        // 验证签章有效期
        // 签名时间
        PdfString str = v.getAsString(PdfName.M);
        Calendar cal = null;
        if (str != null) {
            cal = PdfDate.decode(str.toString());
        }
        SESeal seSeal = Optional.of(signInstance)
                .map(SeSignImp::getSesSign)
                .map(SES_Sign::getTBS_Sign)
                .map(TBS_Sign::getEseal)
                .orElseThrow(() -> new RuntimeException("读取印章数据失败"));
        // 签章结构体里也有签名时间。pdf中的签名时间由pdf验证，签章结构体里的签名时间由签章结构体验证
        Date signDate = Optional.ofNullable(cal).map(Calendar::getTime).orElse(signInstance.getSignedDate());
        if (seSeal.notBefore().before(signDate) && seSeal.notAfter().after(signDate)) {
            log.info("签章时间有效");
        } else {
            log.error("签章时间无效, signDate={}, validate=[{},{}]", signDate, seSeal.notBefore(), seSeal.notAfter());
            throw new RuntimeException("签章时间无效");
        }
        // pdf保护区摘要值
        byte[] filedHashHex = updateByteRangeAndSm3(v);
        // 签章验证
        VerifyInfo verifyInfo = GBT38540Validate.validateSeSign(filedHashHex, signInstance);
        // 验证结果
        if (!verifyInfo.result) {
            throw new RuntimeException("签章验证失败");
        }
    }

    /**
     * 该方法参考自updateByteRange
     * @see com.itextpdf.text.pdf.AcroFields#updateByteRange(PdfPKCS7, PdfDictionary)
     * @param v PdfDictionary
     * @return 保护区SM3摘要的16进制打印
     */
    private byte[] updateByteRangeAndSm3(PdfDictionary v) {
        PdfArray b = v.getAsArray(PdfName.BYTERANGE);
        RandomAccessFileOrArray rf = reader.getSafeFile();
        InputStream rg = null;
        try {
            // 借用RASInputStream，实际不是RSA算法
            rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(rf.createSourceView(), b.asLongArray()));
            byte[] plain = IoUtil.readBytes(rg);
            return SM3.create().digest(plain);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        } finally {
            try {
                if (rg != null) rg.close();
            } catch (IOException e) {
                // this really shouldn't ever happen - the source view we use is based on a Safe view, which is a no-op anyway
                log.error("关闭RASInputStream失败", e);
            }
        }
    }
}

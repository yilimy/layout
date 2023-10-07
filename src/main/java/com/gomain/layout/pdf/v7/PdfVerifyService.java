//package com.gomain.layout.pdf.v7;
//
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfReader;
//import com.itextpdf.signatures.PdfPKCS7;
//import com.itextpdf.signatures.SignatureUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//
//import java.security.Security;
//import java.util.List;
//import java.util.Objects;
//
///**
// * @author caimeng
// * @date 2023/9/7 13:55
// */
//@Slf4j
//public class PdfVerifyService {
//
//    public static void main(String[] args) throws Exception{
//        String pdfPath = "E:\\tmp\\pdf\\blank_signed.pdf";
//        verifySignatures(pdfPath);
//    }
//
//    public static void verifySignatures(String path) throws Exception {
//        Security.addProvider(new BouncyCastleProvider());
//        PdfReader pdfReader = null;
//        PdfDocument pdfDocument = null;
//        try {
//            pdfReader = new PdfReader(path);
//            pdfDocument = new PdfDocument(pdfReader);
//            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
//            List<String> signatureNames = signatureUtil.getSignatureNames();
//            for (String fileName : signatureNames) {
//                PdfPKCS7 pkcs7 = signatureUtil.readSignatureData(fileName, "BC");
//                // 验证报错:org.bouncycastle.asn1.DLSequence cannot be cast to org.bouncycastle.asn1.ASN1ObjectIdentifier
//                // 应该是支持SM2withSM3的PKCS7签名,而不支持签章
//                boolean isValidate = pkcs7.verifySignatureIntegrityAndAuthenticity();
//                System.out.println("isValidate = " + isValidate);
//            }
//        } finally {
//            if (Objects.nonNull(pdfDocument)){
//                try {
//                    pdfDocument.close();
//                } catch (Exception e) {
//                    log.error("关闭pdfDocument失败", e);
//                }
//            }
//            if (Objects.nonNull(pdfReader)) {
//                try {
//                    pdfReader.close();
//                } catch (Exception e) {
//                    log.error("关闭pdfReader失败", e);
//                }
//            }
//        }
//
//
//    }
//
//}

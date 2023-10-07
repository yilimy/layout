package com.gomain.layout.pdf.v5;


import com.gomain.layout.business.VerifyService;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;

import java.util.ArrayList;

/**
 * PDF验签
 * 依据 itext v5.x 版本
 * @author caimeng
 * @date 2023/9/6 18:03
 */
public class PdfVerifyService implements VerifyService {

    /**
     * 使用GBT38540标准验证指定路径的PDF
     * @param path 待验证PDF路径
     * @throws Exception Exception
     */
    public void verifySignatures(String path) throws Exception {
        System.out.println(path);
        PdfReader reader = new PdfReader(path);
        AcroFields fields = reader.getAcroFields();
        AcroFieldsGBT gbtFiles = new AcroFieldsGBT(reader, fields);
        ArrayList<String> names = fields.getSignatureNames();
        for (String name : names) {
            System.out.println("===== " + name + " =====");
            verifyGBT38540(gbtFiles, name);
        }
        System.out.println();
    }

    /**
     * github上没有签章的实现,需要自定义
     * <a href="https://github.com/itext/itext7/pull/85">itext7 support sm3withsm2 signature</a>
     * @param gbtFiles AcroFieldsGBT
     * @param name SignatureName
     * @throws Exception on error
     */
    public static void verifyGBT38540(AcroFieldsGBT gbtFiles, String name) throws Exception{
        System.out.println("Signature covers whole document: "
                + gbtFiles.getFields().signatureCoversWholeDocument(name));
        System.out.println("Document revision: " + gbtFiles.getFields().getRevision(name)
                + " of " + gbtFiles.getFields().getTotalRevisions());
        gbtFiles.verifySignature(name);
    }

}

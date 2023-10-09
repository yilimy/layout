package com.gomain.layout.pdf.v5;

import com.gomain.layout.pojo.Constant;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfSignature;

/**
 * 自定义PdfSignature
 * @author caimeng
 * @date 2023/10/7 18:44
 */
public class PdfSignatureStamp extends PdfSignature {
    /**
     * Creates new PdfSignature
     */
    public PdfSignatureStamp() {
        // 重置filter和subFilter
        super(null, null);
        // Filter 和 SubFilter 为约定值
        put(PdfName.FILTER, new PdfNameStamp(Constant.STAMP_FILTER));
        put(PdfName.SUBFILTER, new PdfNameStamp(Constant.SUB_FILTER_38540));
    }
}

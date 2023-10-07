package com.gomain.layout.ofd;

import com.gomain.layout.gm.GBT38540Validate;
import org.ofdrw.core.signatures.SigType;
import org.ofdrw.gm.sm2strut.VerifyInfo;
import org.ofdrw.sign.verify.SignedDataValidateContainer;
import org.ofdrw.sign.verify.exceptions.InvalidSignedValueException;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * GBT38540签章验证
 * 参考自 GBT35275ValidateContainer
 * @see org.ofdrw.sign.verify.container.GBT35275ValidateContainer#validate(SigType, String, byte[], byte[])
 * @author caimeng
 * @date 2023/9/11 14:04
 */
public class GBT38540ValidateContainer implements SignedDataValidateContainer {

    @Override
    public void validate(SigType type, String alg, byte[] tbsContent, byte[] signedValue) throws InvalidSignedValueException, IOException, GeneralSecurityException {
        // 如果要同时支持多种验证方式，需要自定义验证容器
        // 这里只加载 GBT38540Validate 验证器
        if (type != SigType.Seal) {
            throw new IllegalArgumentException("签名类型(type)必须是 Sign，不支持电子印章验证");
        } else {
            VerifyInfo verifyInfo = GBT38540Validate.validate(alg, tbsContent, signedValue);
            if (!verifyInfo.result) {
                throw new InvalidSignedValueException(verifyInfo.hit);
            }
        }
    }
}

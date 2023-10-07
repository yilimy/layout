package com.gomain.layout.ofd;

import com.gomain.layout.business.VerifyService;
import org.ofdrw.core.signatures.SigType;
import org.ofdrw.reader.OFDReader;
import org.ofdrw.sign.verify.OFDValidator;
import org.ofdrw.sign.verify.SignedDataValidateContainer;

/**
 * OFD版式验证服务
 * <p>
 *     参考
 *      <a href="https://github.com/ofdrw/ofdrw">OFD Reader & Writer</a>
 *      或者
 *      <a href="https://gitee.com/ofdrw/ofdrw">OFD Reader & Writer</a>
 * 该模块依赖 org.ofdrw:ofdrw-sign:2.0.8
 * @author caimeng
 * @date 2023/9/11 10:13
 */
public class OfdVerifyService implements VerifyService {

    /**
     * OFD的签名签章验证过程有一下几步：
     * 1. 使用OFD解析器构造一个验证引擎。
     * 2. **实现验证容器**。
     * 3. 设置验证容器。
     * 4. 执行验签验章。
     * 5. 如果没有异常抛出说明验证成功。
     *
     * @param path 待验证文件路径
     * @throws Exception Exception
     * @see org.ofdrw.sign.verify.container.GBT35275ValidateContainer#validate(SigType, String, byte[], byte[])
     */
    public void verifySignatures(String path) throws Exception {
        // 读取OFD文件，并创建电子签名验证引擎
        try (OFDReader reader = new OFDReader(path);
             OFDValidator validator = new OFDValidator(reader)) {
            // 1. 创建验证容器。
            // 如果要同时支持多种验证方式，需要自定义验证容器
            SignedDataValidateContainer dsc = new GBT38540ValidateContainer();
            // 2. 设置验证容器。
            validator.setValidator(dsc);
            // 3. 执行验签验章，如果没有异常抛出说明验证成功。
            // 支持多个验证，该方法中对签名结构做了循环验证
            validator.exeValidate();
        }
    }
}

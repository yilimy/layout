package com.gomain.layout.business;

/**
 * 文档验证
 * @author caimeng
 * @date 2023/10/7 15:58
 */
public interface VerifyService {
    /**
     * 验证路径下的文件
     * @param path 待验证文件路径
     * @throws Exception Exception
     */
    void verifySignatures(String path) throws Exception;
}

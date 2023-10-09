package com.gomain.layout.integration;

import com.gomain.layout.pojo.QuerySealRsp;
import com.gomain.layout.pojo.SdkStampRsp;

/**
 * 印章服务
 * @author caimeng
 * @date 2023/10/7 17:19
 */
public interface SealService {
    /**
     * 查询印章数据
     * @param userId 用户标识
     * @param sealCode 印章标识
     * @return 印章数据
     */
    QuerySealRsp findSeal(String userId, String sealCode);

    /**
     * 查询印章数据
     * @see this#findSeal(String, String)
     * @param userId 用户标识
     * @param sealCode 印章标识
     * @return 印章数据
     */
    byte[] findSealByte(String userId, String sealCode);

    /**
     * 调用SDK接口签章
     * @param userId 用户标识
     * @param sealCode 印章标识
     * @param digest 文件摘要
     * @return 签章结果
     */
    SdkStampRsp sdkStamp(String userId, String sealCode, String digest);
    /**
     * 调用SDK接口签章
     * @see this#sdkStamp(String, String, String)
     * @param userId 用户标识
     * @param sealCode 印章标识
     * @param hash 文件摘要
     * @return 签章结果
     */
    byte[] sdkStampByte(String userId, String sealCode, byte[] hash);
}

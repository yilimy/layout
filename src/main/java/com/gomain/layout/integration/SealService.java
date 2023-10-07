package com.gomain.layout.integration;

import com.gomain.layout.pojo.QuerySealRsp;

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
}

package com.gomain.layout.integration.impl;

import com.alibaba.fastjson.TypeReference;
import com.gomain.layout.integration.SealService;
import com.gomain.layout.pojo.BWJsonResult;
import com.gomain.layout.pojo.QuerySealRsp;
import com.gomain.layout.pojo.SdkReq;
import com.gomain.layout.pojo.SdkStampRsp;
import com.gomain.layout.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author caimeng
 * @date 2023/10/7 17:19
 */
@Slf4j
public class SealServiceImpl implements SealService {
    private final String url;

    public SealServiceImpl(String url) {
        this.url = url;
    }

    public QuerySealRsp findSeal(String userId, String sealCode) {
        SdkReq sdkReq = new SdkReq(userId, "101", sealCode);
        TypeReference<BWJsonResult<QuerySealRsp>> typeReference = new TypeReference<BWJsonResult<QuerySealRsp>>(){};
        log.info("获取印章: url={}, sdkReq={}", url, sdkReq);
        BWJsonResult<QuerySealRsp> bwJsonResult = HttpClientUtils.sendPostJsonByTypeReference(url, sdkReq, typeReference);
        log.info("获取印章返回结果:{}", bwJsonResult);
        // 简单处理结果
        return Optional.ofNullable(bwJsonResult)
                .filter(BWJsonResult::getSuccess)
                .map(BWJsonResult::getData)
                .map(list -> list.get(0))
                .orElseThrow(() -> new RuntimeException("获取印章失败"));
    }

    public SdkStampRsp sdkStamp(String userId, String sealCode, String digest) {
        SdkReq sdkReq = new SdkReq(userId, "108", sealCode, digest);
        TypeReference<BWJsonResult<SdkStampRsp>> typeReference = new TypeReference<BWJsonResult<SdkStampRsp>>(){};
        log.info("SDK签章: url={}, sdkReq={}", url, sdkReq);
        BWJsonResult<SdkStampRsp> bwJsonResult = HttpClientUtils.sendPostJsonByTypeReference(url, sdkReq, typeReference);
        log.info("SDK签章返回结果:{}", bwJsonResult);
        // 简单处理结果
        return Optional.ofNullable(bwJsonResult)
                .filter(BWJsonResult::getSuccess)
                .map(BWJsonResult::getData)
                .map(list -> list.get(0))
                .orElseThrow(() -> new RuntimeException("SDK签章失败"));
    }
}

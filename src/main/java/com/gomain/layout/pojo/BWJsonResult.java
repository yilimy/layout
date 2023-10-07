package com.gomain.layout.pojo;

import lombok.Data;

import java.util.List;

/**
 * 老用章的返回数据结构
 * 参考自老用章的同名类
 * @author caimeng
 * @date 2023/9/25 13:58
 */
@Data
public class BWJsonResult<T> {
    /**
     * 接口请求是否成功，0：成功
     */
    private Boolean success;
    /**
     * 接口请求的响应消息
     */
    private String message;
    /**
     * 业务错误码
     */
    private String errorCode;
    /**
     * 业务错误消息
     */
    private String errorMsg;
    /**
     * 业务数据集数量
     */
    private Integer total;
    /**
     * 业务数据集
     */
    private List<T> data;

    /**
     * 定义失败的返回对象
     * @param errorCode 错误码
     * @param errorMsg 错误消息
     * @return 返回结果
     * @param <D> null
     */
    public static <D> BWJsonResult<D> failed(String errorCode, String errorMsg) {
        BWJsonResult<D> result = new BWJsonResult<>();
        result.setSuccess(false);
        result.setMessage("请求失败");
        result.setErrorCode(errorCode);
        result.setErrorMsg(errorMsg);
        result.setTotal(0);
        return result;
    }
}

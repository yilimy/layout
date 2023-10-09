package com.gomain.layout.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 签章策略
 * @author caimeng
 * @date 2023/10/9 11:22
 */
@Data
@NoArgsConstructor
public class StampStrategy {
    /**
     * 用户标识（测试数据）
     */
    private String userId;
    /**
     * 印章标识（测试数据）
     */
    private String sealId;
    /**
     * 定位签章策略
     */
    private List<StrategyPosition> positions;

    public StampStrategy(String userId, String sealId, List<StrategyPosition> positions) {
        this.userId = userId;
        this.sealId = sealId;
        this.positions = positions;
    }
}

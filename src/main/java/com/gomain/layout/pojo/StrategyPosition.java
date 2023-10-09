package com.gomain.layout.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 签章位置参数
 * @author caimeng
 * @date 2023/10/9 11:23
 */
@Data
@NoArgsConstructor
public class StrategyPosition {
    /**
     * 页码
     */
    private int page;
    /**
     * 横坐标
     */
    private float x;
    /**
     * 纵坐标
     */
    private float y;
    /**
     * 坐标系
     * <p>
     *     PDF默认左下顶点 {@link com.gomain.layout.pojo.Constant#COORDINATE_LEFT_TOP}
     *     OFD默认左上顶点 {@link com.gomain.layout.pojo.Constant#COORDINATE_LEFT_DOWN}
     * </p>
     */
    private int coordinate = Constant.COORDINATE_DEFAULT;

    public StrategyPosition(int page, float x, float y) {
        this.page = page;
        this.x = x;
        this.y = y;
    }

    public StrategyPosition(int page, float x, float y, int coordinate) {
        this.page = page;
        this.x = x;
        this.y = y;
        this.coordinate = coordinate;
    }
}

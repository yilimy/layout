package com.gomain.layout.pojo;

/**
 * 常量类
 * @author caimeng
 * @date 2023/10/7 18:29
 */
public final class Constant {
    private Constant(){}
    /**
     * GBT 38540 的 filter
     */
    public static final String STAMP_FILTER = "Gomain.GMPkiLite";
    /**
     * GBT 38540 的 subFilter
     */
    public static final String SUB_FILTER_38540 = "GM.sm2seal";
    /**
     * 换算比率：毫米转磅
     */
    public static final float POUND_PER_MM = 2.83f;

    /**
     * 坐标系，左下 (0, 0)
     */
    public static final int COORDINATE_LEFT_DOWN = 0;
    /**
     * 坐标系，左上 (0, 1)
     */
    public static final int COORDINATE_LEFT_TOP = 1;
    /**
     * 版式自身默认坐标系
     */
    public static final int COORDINATE_DEFAULT = -1;
}

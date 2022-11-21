package com.multi.domain.iot.common.param;

import lombok.extern.slf4j.Slf4j;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-17 23:30
 * @description 公共参数工厂
 */
@Slf4j
public class PublicParamsFactory {
    private static volatile PublicParams publicParams;
    private static String curvesSaveParamsPath = "a.properties";
    private static String publicSaveParamsPath = "public.properties";

    public static void setPublicSaveParamsPath(String saveParamsPath) {
        publicSaveParamsPath = saveParamsPath;
    }

    public static void setCurvesSaveParamsPath(String saveParamsPath) {
        curvesSaveParamsPath = saveParamsPath;
    }

    public static PublicParams getInstance() {
        if (publicParams == null) {
            synchronized (PublicParamsFactory.class) {
                if (publicParams == null) {
                    publicParams = new PublicParams();
                    publicParams.initialize(curvesSaveParamsPath, publicSaveParamsPath);
                }
            }
        }
        return publicParams;
    }
}

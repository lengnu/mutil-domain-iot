package com.multi.domain.iot.param;

import lombok.Data;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-17 23:04
 * @description 参数工厂
 */
@Data
public class UDParamsFactory {
    private static volatile UDParams udParams;
    public static final String PUBLIC_PARAMS_SAVE_PATH = "ud-public.properties";
    public static final String SELF_PARAMS_SAVE_PATH = "ud-self.properties";

    /**
     * 单例模型获取参数
     * @param publicParams
     * @return
     */
    public static UDParams getInstance(PublicParams publicParams){
        if (udParams == null){
            synchronized (UDParamsFactory.class){
                if (udParams == null){
                    udParams = new UDParams(publicParams,PUBLIC_PARAMS_SAVE_PATH, SELF_PARAMS_SAVE_PATH);
                }
            }
        }
        return udParams;
    }


    public static UDParams getInstance(){
        if (udParams == null){
            throw new RuntimeException("The udParams has not been initialized......");
        }
        return udParams;
    }
}

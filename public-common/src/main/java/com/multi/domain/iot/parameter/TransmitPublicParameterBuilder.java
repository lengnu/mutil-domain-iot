package com.multi.domain.iot.parameter;

import java.io.*;
import java.util.Properties;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.parameter
 * @Author: duwei
 * @Date: 2022/11/17 15:12
 * @Description: 公共参数生成器
 */
public class TransmitPublicParameterBuilder {
    public static TransmitPublicParameter build(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        TransmitPublicParameter parameter = new TransmitPublicParameter();
        parameter.setCurvesType(properties.getProperty("type","a"));
        parameter.setQ(properties.getProperty("q"));
        parameter.setH(properties.getProperty("h"));
        parameter.setR(properties.getProperty("r"));
        parameter.setExp1(properties.getProperty("exp1"));
        parameter.setExp2(properties.getProperty("exp2"));
        parameter.setSign0(properties.getProperty("sign0"));
        parameter.setSign1(properties.getProperty("sign1"));
        parameter.setSign1(properties.getProperty("sign1"));
        parameter.setGenerateElementOne(properties.getProperty("generateElementOne"));
        parameter.setGenerateElementTwo(properties.getProperty("generateElementTwo"));
        parameter.setSecureParameter(Integer.parseInt(properties.getProperty("secureParameter")));
        return parameter;
    }
}

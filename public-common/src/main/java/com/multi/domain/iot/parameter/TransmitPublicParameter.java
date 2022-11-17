package com.multi.domain.iot.parameter;

import lombok.Data;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.parameter
 * @Author: duwei
 * @Date: 2022/11/17 11:34
 * @Description: 公共参数
 */
@Data
public class TransmitPublicParameter {
        private String curvesType;
        private String q;
        private String h;
        private String r;
        private String exp2;
        private String exp1;
        private String sign1;
        private String sign0;
        private String generateElementOne;
        private String generateElementTwo;
        private int secureParameter;
}

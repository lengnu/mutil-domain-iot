package com.multi.domain.iot.param;

import com.alibaba.fastjson.annotation.JSONField;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.util.Properties;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.param
 * @Author: duwei
 * @Date: 2022/11/17 17:08
 * @Description: 公共参数
 */
@Data
@Slf4j
public class PublicParams {
    private String curvesType;
    private String q;
    private String h;
    private String r;
    private String exp2;
    private String exp1;
    private String sign1;
    private String sign0;
    private String generatorOneBase64;
    private String generatorTwoBase64;
    private int secureParameter;
    //公共参数是否已经初始化过
    private boolean initialize;

    @JSONField(serialize = false)
    private Pairing pairing;
    @JSONField(serialize = false)
    private Field G1;
    @JSONField(serialize = false)
    private Field Zp;
    @JSONField(serialize = false)
    private Element generatorOne;
    @JSONField(serialize = false)
    private Element generatorTwo;

    public void rebuildCurvesParams(){
        if (!initialize){
            throw new RuntimeException("参数未初始化，不能重构，需要先进行初始化");
        }
        String tempPath = System.getProperty("user.dir") + File.separator + "tmp.properties";
        writePublicParamsToFile(tempPath);
        File file = new File(tempPath);
        file.delete();
    }

    private static interface Constant{
        String curvesType = "type";
        String q = "q";
        String h = "h";
        String r = "r";
        String exp1 = "exp1";
        String exp2 = "exp2";
        String sign1 = "sign1";
        String sign0 = "sign0";
        String generatorOneBase64 = "generatorOneBase64";
        String generatorTwoBase64 = "generatorTwoBase64";
        String secureParameter = "secureParameter";
    }

    /**
     * 初始化椭圆曲线参数
     * @param curvesPath
     */
    private void initializeCurvesParams(String curvesPath){
        Pairing pairing = PairingFactory.getPairing(curvesPath);
        this.pairing = pairing;
        this.Zp = pairing.getZr();
        this.G1 = pairing.getG1();
        this.generatorOne = this.G1.newRandomElement();
        this.generatorTwo = this.G1.newRandomElement();
        this.secureParameter = 256;
    }

    /**
     * 初始化功公共参数，根据椭圆曲线配置
     * @param curvesPath
     */
    private void initializePublicParams(String curvesPath){
        InputStream inputStream = null;
        Properties properties = new Properties();
        try {
            //初始化公共参数
            inputStream = new FileInputStream(curvesPath);
            properties.load(inputStream);
            this.exp1 = properties.getProperty(Constant.exp1);
            this.exp2 = properties.getProperty(Constant.exp2);
            this.q = properties.getProperty(Constant.q);
            this.h = properties.getProperty(Constant.h);
            this.r = properties.getProperty(Constant.r);
            this.curvesType = properties.getProperty(Constant.curvesType);
            this.sign0 = properties.getProperty(Constant.sign0);
            this.sign1 = properties.getProperty(Constant.sign1);
            this.generatorOneBase64 = Base64.encodeBase64String(this.generatorOne.toBytes());
            this.generatorTwoBase64 = Base64.encodeBase64String(this.generatorTwo.toBytes());
        }catch (Exception e){
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 将公共参数写出到文件
     * @param publicParamsPath  公共参数保存路径
     * @return  是否成功
     */
    private boolean writePublicParamsToFile(String publicParamsPath)  {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(publicParamsPath);
            Properties properties = new Properties();
            properties.setProperty(Constant.curvesType,this.curvesType);
            properties.setProperty(Constant.q,this.q);
            properties.setProperty(Constant.r,this.r);
            properties.setProperty(Constant.h,this.h);
            properties.setProperty(Constant.sign1,this.sign1);
            properties.setProperty(Constant.sign0,this.sign0);
            properties.setProperty(Constant.exp1,this.exp1);
            properties.setProperty(Constant.generatorOneBase64,this.generatorOneBase64);
            properties.setProperty(Constant.generatorTwoBase64,this.generatorTwoBase64);
            properties.setProperty(Constant.secureParameter, String.valueOf(this.secureParameter));
            properties.store(outputStream,"store public params");
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }finally {
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    public void initialize(String curvesPath,String publicParamsPath){
        initializeCurvesParams(curvesPath);
        initializePublicParams(curvesPath);
        writePublicParamsToFile(publicParamsPath);
        this.initialize = true;
        log.info("initialize params finished......");
    }
    public void PublicParams() {
        this.initialize = false;
    }
}


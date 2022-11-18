package com.multi.domain.iot.param;

import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private AtomicBoolean initialize;

    private interface Constant{
        String CURVES_TYPE = "type";
        String Q = "q";
        String H = "h";
        String R = "r";
        String EXP_1 = "exp1";
        String EXP_2 = "exp2";
        String SIGN_1 = "sign1";
        String SIGN_0 = "sign0";
        String GENERATOR_ONE_BASE_64 = "generatorOneBase64";
        String GENERATOR_TWO_BASE_64 = "generatorTwoBase64";
        String SECURE_PARAMETER = "secureParameter";
    }

    /**
     * 初始化公共参数
     * @param curvesPath
     */
    public void initializeParams(String curvesPath){
        //保证参数只初始化一次
        if (!initialize.get()){
            Pairing pairing = PairingFactory.getPairing(curvesPath);
            Field g1 = pairing.getG1();
            this.generatorOneBase64 = Base64.encodeBase64String(g1.newRandomElement().toBytes());
            this.generatorTwoBase64 = Base64.encodeBase64String(g1.newRandomElement().toBytes());
            this.secureParameter = 256;
            autowireParams(curvesPath);
            //表示已经初始化了
            this.initialize.compareAndSet(false,true);
        }

    }

    /**
     * 为当前对象注入参数
     * @param curvesPath
     */
    private void autowireParams(String curvesPath){
        InputStream inputStream = null;
        Properties properties = new Properties();
        try {
            //初始化公共参数
            inputStream = new FileInputStream(curvesPath);
            properties.load(inputStream);
            this.exp1 = properties.getProperty(Constant.EXP_1);
            this.exp2 = properties.getProperty(Constant.EXP_2);
            this.q = properties.getProperty(Constant.Q);
            this.h = properties.getProperty(Constant.H);
            this.r = properties.getProperty(Constant.R);
            this.curvesType = properties.getProperty(Constant.CURVES_TYPE);
            this.sign0 = properties.getProperty(Constant.SIGN_0);
            this.sign1 = properties.getProperty(Constant.SIGN_1);
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
    public boolean writePublicParamsToFile(String publicParamsPath)  {
        if (!this.initialize.get()){
            throw new RuntimeException("公共参数还未初始化...");
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(publicParamsPath);
            Properties properties = new Properties();
            properties.setProperty(Constant.CURVES_TYPE,this.curvesType);
            properties.setProperty(Constant.Q,this.q);
            properties.setProperty(Constant.R,this.r);
            properties.setProperty(Constant.H,this.h);
            properties.setProperty(Constant.SIGN_1,this.sign1);
            properties.setProperty(Constant.SIGN_0,this.sign0);
            properties.setProperty(Constant.EXP_1,this.exp1);
            properties.setProperty(Constant.EXP_2,this.exp2);
            properties.setProperty(Constant.GENERATOR_ONE_BASE_64,this.generatorOneBase64);
            properties.setProperty(Constant.GENERATOR_TWO_BASE_64,this.generatorTwoBase64);
            properties.setProperty(Constant.SECURE_PARAMETER, String.valueOf(this.secureParameter));
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

    /**
     * 初始化全局公共参数，并保存到指定文件
     * @param curvesPath
     * @param publicParamsPath
     */
    public void initialize(String curvesPath,String publicParamsPath){
        initializeParams(curvesPath);
        writePublicParamsToFile(publicParamsPath);
        log.info("initialize params finished......");
    }

    public  PublicParams() {
        this.initialize = new AtomicBoolean(false);
    }


    @Override
    public String toString() {
        return "PublicParams{" +
                "curvesType='" + curvesType + '\'' +
                "\n q='" + q + '\'' +
                "\n h='" + h + '\'' +
                "\n r='" + r + '\'' +
                "\n exp2='" + exp2 + '\'' +
                "\n exp1='" + exp1 + '\'' +
                "\n sign1='" + sign1 + '\'' +
                "\n sign0='" + sign0 + '\'' +
                "\n generatorOneBase64='" + generatorOneBase64 + '\'' +
                "\n generatorTwoBase64='" + generatorTwoBase64 + '\'' +
                "\n secureParameter=" + secureParameter +
                '}';
    }
}


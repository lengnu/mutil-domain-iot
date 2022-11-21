package com.multi.domain.iot.common.param;

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
    protected String curvesType;
    protected String q;
    protected String h;
    protected String r;
    protected String exp2;
    protected String exp1;
    protected String sign1;
    protected String sign0;
    protected String generatorOneBase64;
    protected String generatorTwoBase64;
    protected int secureParameter;

    //公共参数是否已经初始化过
    protected AtomicBoolean initialize;

    protected interface Constant {
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
        String MSK = "msk";
        String XI = "xi";
        String PUBLIC_KEY = "publicKey";
        String ID = "id";
        String PHONE = "phone";
        String EMAIL = "email";
        String NAME = "name";
        String S = "s";
        String SECRET = "secret";
        String HOST = "host";
        String LISTEN_PORT = "listenPort";
        String DOMAIN = "domain";
    }

    /**
     * 初始化公共参数
     *
     * @param curvesPath
     */
    public void initializeParams(String curvesPath) {
        //保证参数只初始化一次
        if (!initialize.get()) {
            Pairing pairing = PairingFactory.getPairing(curvesPath);
            Field g1 = pairing.getG1();
            this.generatorOneBase64 = Base64.encodeBase64String(g1.newRandomElement().toBytes());
            this.generatorTwoBase64 = Base64.encodeBase64String(g1.newRandomElement().toBytes());
            this.secureParameter = 128;
            autowirePublicParams(curvesPath);
            //表示已经初始化了
            this.initialize.compareAndSet(false, true);
        }
    }


    /**
     * 为当前对象注入参数
     *
     * @param curvesPath
     */
    protected void autowirePublicParams(String curvesPath) {
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
        } catch (Exception e) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    log.error(e.getMessage());
                }
            }
        }
    }


    /**
     * 将公共参数封装为properties
     *
     * @param properties
     */
    protected final void autowirePublicParamsToProperties(Properties properties) {
        properties.setProperty(Constant.CURVES_TYPE, this.curvesType);
        properties.setProperty(Constant.Q, this.q);
        properties.setProperty(Constant.R, this.r);
        properties.setProperty(Constant.H, this.h);
        properties.setProperty(Constant.SIGN_1, this.sign1);
        properties.setProperty(Constant.SIGN_0, this.sign0);
        properties.setProperty(Constant.EXP_1, this.exp1);
        properties.setProperty(Constant.EXP_2, this.exp2);
        properties.setProperty(Constant.GENERATOR_ONE_BASE_64, this.generatorOneBase64);
        properties.setProperty(Constant.GENERATOR_TWO_BASE_64, this.generatorTwoBase64);
        properties.setProperty(Constant.SECURE_PARAMETER, String.valueOf(this.secureParameter));
    }

    /**
     * 留给子类实现，写入子类自定义参数
     *
     * @param properties
     */
    protected void autowireSelfParamsToProperties(Properties properties) {

    }

    /**
     * 模板方法，写入自定义参数
     *
     */
    public final boolean writeSelfParamsToFile(String selfParamsSavePath) {
        if (!this.initialize.get()) {
            throw new RuntimeException("公共参数还未初始化...");
        }
        Properties properties = new Properties();
        autowireSelfParamsToProperties(properties);
        return writePropertiesToFile(selfParamsSavePath, properties);
    }


    /**
     * 将参数写入到文件中
     *
     * @param filePath
     * @param properties
     * @return
     */
    protected final boolean writePropertiesToFile(String filePath, Properties properties) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            properties.store(outputStream, "store params......");
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }


    /**
     * 将公共参数写出到文件
     *
     * @param publicParamsPath 公共参数保存路径
     * @return 是否成功
     */
    public final boolean writePublicParamsToFile(String publicParamsPath) {
        if (!this.initialize.get()) {
            throw new RuntimeException("公共参数还未初始化...");
        }
        Properties properties = new Properties();
        autowirePublicParamsToProperties(properties);
        return writePropertiesToFile(publicParamsPath, properties);
    }

    /**
     * 初始化全局公共参数，并保存到指定文件
     *
     * @param curvesPath
     * @param publicParamsPath
     */
    public void initialize(String curvesPath, String publicParamsPath) {
        log.info("Start initializing public parameters......");
        initializeParams(curvesPath);
        writePublicParamsToFile(publicParamsPath);
        log.info("initialize params finished......");
    }

    public PublicParams() {
        this.initialize = new AtomicBoolean(false);
    }

}


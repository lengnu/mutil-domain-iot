package com.multi.domain.iot.auditagent.session;

import com.multi.domain.iot.common.domain.Domain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.util.*;


/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.auditagent.session
 * @Author: duwei
 * @Date: 2022/11/22 15:44
 * @Description: 管理确认消息的会话
 */
@Slf4j
public class ConfirmAuthenticationMessageSessionUtils {
    // 临时存储各个验证者的消息
    private static final Map<String, Map<Integer, byte[]>> UD_CONFIRM_AUTHENTICATION_MESSAGE_MAP = new HashMap<>();

    private static final Set<String> FINISH_AUTHENTICATION_UD_MAP = new HashSet<>();

    private static final Set<String> FAILURE_CONFIRM_MESSAGE = new HashSet<>();

    public static Collection<byte[]> getConfirmsInformation(String uid){
        return UD_CONFIRM_AUTHENTICATION_MESSAGE_MAP.get(uid).values();
    }

    public synchronized static void receiveOneConfirmMessage(String uid, Integer verifierId, byte[] confirmMessage, int totalVerifiersNumber, boolean success, Domain domain) {
        if (!success) {
            FINISH_AUTHENTICATION_UD_MAP.add(uid);
            log.info("The authentication of a verifier was not passed,confirm failed!");
            return;
        }
        Map<Integer, byte[]> map = UD_CONFIRM_AUTHENTICATION_MESSAGE_MAP.get(uid);
        map.put(verifierId, confirmMessage);
        log.info("To receive an confirm message from verifier {} in domain {}, {} more messages are required!", verifierId, domain.getDomain(),totalVerifiersNumber - map.size());
    }

    public synchronized static void bindSession(String uid) {
        UD_CONFIRM_AUTHENTICATION_MESSAGE_MAP.put(uid, new HashMap<>());
    }

    /**
     * 对应ud的确认消息是否收集完毕
     */
    public static boolean isReceiveFinish(String uid, int total) {
        //1.没有验证者认证失败
        //2.收到了完整的验证消息
        return !FAILURE_CONFIRM_MESSAGE.contains(uid)
                && UD_CONFIRM_AUTHENTICATION_MESSAGE_MAP.containsKey(uid)
                && UD_CONFIRM_AUTHENTICATION_MESSAGE_MAP.get(uid).size() == total;
    }

    /**
     * 做善后工作，清楚缓存，记录完成的ud
     */
    public static void afterMath(String uid) {
        clearCache(uid);
        joinFinishSet(uid);
    }

    /**
     * 清楚存储的临时消息
     */
    private static void clearCache(String uid) {
        UD_CONFIRM_AUTHENTICATION_MESSAGE_MAP.remove(uid);
    }

    /**
     * 将对应的ud加入确认完成集合
     * 从错误集合中消除
     */
    private static void joinFinishSet(String uid) {
        FINISH_AUTHENTICATION_UD_MAP.add(uid);
        FAILURE_CONFIRM_MESSAGE.remove(uid);
    }


    /**
     * UD是否正在确认，即等待其他IDV的消息
     */
    public static boolean isConfirming(String uid) {
        return UD_CONFIRM_AUTHENTICATION_MESSAGE_MAP.containsKey(uid) && !FINISH_AUTHENTICATION_UD_MAP.contains(uid);
    }

    /**
     * UD是否未开始确认
     */
    public static boolean isNotConfirm(String uid) {
        return !UD_CONFIRM_AUTHENTICATION_MESSAGE_MAP.containsKey(uid) && !FINISH_AUTHENTICATION_UD_MAP.contains(uid);
    }

    /**
     * UD是否完成确认
     *
     * @return
     */
    public static boolean isFinishConfirm(String uid) {
        return FINISH_AUTHENTICATION_UD_MAP.contains(uid);
    }



}

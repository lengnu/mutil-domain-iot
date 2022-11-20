package com.multi.domain.iot.session;

import com.multi.domain.iot.param.EnrollInformationOnPublicServer;
import com.multi.domain.iot.role.Role;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.session
 * @Author: duwei
 * @Date: 2022/11/18 15:30
 * @Description: 保存全局注册信息
 */
public class SessionUtils {
    /**
     * 为注册的用户分配ID
     */
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    /**
     * 维护全局的节点信息
     */
    private static final Map<Role, Map<Integer, EnrollInformationOnPublicServer>> INFORMATION_MAP = new ConcurrentHashMap<>();

   public static Map<Role, Map<Integer, EnrollInformationOnPublicServer>> getInformationMap(){
        return INFORMATION_MAP;
    }

    //返回用户的id信息
    public static int bindSession(Role role, String ip, int listenPort, byte[] publicKey) {
        EnrollInformationOnPublicServer information = new EnrollInformationOnPublicServer(ATOMIC_INTEGER.incrementAndGet(), role, publicKey, ip, listenPort);
        if (INFORMATION_MAP.get(role) == null){
            Map<Integer,EnrollInformationOnPublicServer> userInformation = new HashMap<>();
            INFORMATION_MAP.put(role,userInformation);
        }
       INFORMATION_MAP.get(role).put(information.getId(),information);
        return information.getId();
    }
}

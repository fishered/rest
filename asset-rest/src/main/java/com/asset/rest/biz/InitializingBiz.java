package com.asset.rest.biz;

import com.asset.rest.biz.core.CoreProcess;
import com.asset.rest.biz.core.HookProcess;
import com.asset.rest.enums.ProcessType;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fisher
 * @date 2023-09-14: 17:38
 */
@Component
public class InitializingBiz {

    /**
     * hook impl
     */
    public static ConcurrentHashMap<ProcessType, HookProcess> hookProcessConcurrentHashMap
            = new ConcurrentHashMap<>();

    /**
     * core impl
     */
    public static ConcurrentHashMap<ProcessType, CoreProcess> coreProcessConcurrentHashMap
            = new ConcurrentHashMap<>();

    public static void addHook(ProcessType type, HookProcess hookProcess){
        hookProcessConcurrentHashMap.putIfAbsent(type, hookProcess);
    }

    public static void addCore(ProcessType type, CoreProcess coreProcess){
        coreProcessConcurrentHashMap.putIfAbsent(type, coreProcess);
    }

    public static HookProcess getHook(ProcessType type){
        return hookProcessConcurrentHashMap.get(type);
    }

    public static CoreProcess getCore(ProcessType type){
        return coreProcessConcurrentHashMap.get(type);
    }


}

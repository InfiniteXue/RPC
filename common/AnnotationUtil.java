package rpc.common;

import java.util.HashSet;
import java.util.Set;

public final class AnnotationUtil {

    /**
     * 获取@RpcApi标注的类
     */
    public static Set<Class<?>> getRpcApiClasses(Set<Class<?>> classes) {
        Set<Class<?>> rpcApiClasses = new HashSet<>();
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(RpcApi.class)) {
                rpcApiClasses.add(clazz);
            }
        }
        return rpcApiClasses;
    }

    /**
     * 获取@RpcProvider标注的类
     */
    public static Set<Class<?>> getRpcProviderClasses(Set<Class<?>> classes) {
        Set<Class<?>> rpcProviderClasses = new HashSet<>();
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(RpcProvider.class)) {
                rpcProviderClasses.add(clazz);
            }
        }
        return rpcProviderClasses;
    }

    /**
     * 获取@RpcConsumer标注的类
     */
    public static Set<Class<?>> getRpcConsumerClasses(Set<Class<?>> classes) {
        Set<Class<?>> rpcConsumerClasses = new HashSet<>();
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(RpcConsumer.class)) {
                rpcConsumerClasses.add(clazz);
            }
        }
        return rpcConsumerClasses;
    }

}

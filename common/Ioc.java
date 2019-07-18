package rpc.common;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * provider端注入Class对象；consumer端注入代理对象
 */
@Slf4j
public final class Ioc {

    private static final Map<Class<?>, Object> BEAN_MAP = new HashMap<>();

    private static final Map<String, Object> RESOURCE_MAP = new HashMap<>();

    static {
        try {
            // 扫描指定包路径
            String packageName = ConfigBean.getInstance().getPackageName();
            Set<Class<?>> classes = ClassUtil.getClasses(packageName);
            // @RpcApi
            Set<Class<?>> rpcApiClasses = AnnotationUtil.getRpcApiClasses(classes);

            // @RpcConsumer
            Set<Class<?>> rpcConsumerClasses = AnnotationUtil.getRpcConsumerClasses(classes);
            for (Class<?> clazz : rpcConsumerClasses) {
                // 实例化
                Object obj = clazz.newInstance();
                // 注入@RpcResource
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(RpcResource.class) && rpcApiClasses.contains(field.getType())) {
                        field.setAccessible(true);
                        // 获取代理对象
                        field.set(obj, ProxyFactory.getProxy(field.getType()));
                    }
                }
                BEAN_MAP.put(clazz, obj);
            }

            // @RpcProvider
            Set<Class<?>> rpcProviderClasses = AnnotationUtil.getRpcProviderClasses(classes);
            for (Class<?> clazz : rpcProviderClasses) {
                // 实例化
                Object obj = clazz.newInstance();
                // 注入@RpcResource
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(RpcResource.class) && rpcApiClasses.contains(field.getType())) {
                        // 获取实现类
                        Object fieldObj = null;
                        for (Class<?> cls : classes) {
                            if (field.getType().isAssignableFrom(cls) && !field.getType().equals(cls)) {
                                field.setAccessible(true);
                                fieldObj = cls.newInstance();
                                field.set(obj, fieldObj);
                                break;
                            }
                        }
                        if (fieldObj != null) {
                            RESOURCE_MAP.put(field.getType().getName(), fieldObj);
                        }
                    }
                }
                BEAN_MAP.put(clazz, obj);
            }
        } catch (Exception e) {
            log.error("An exception occurred when init ioc container.", e);
            throw new RuntimeException("failed to init", e);
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        return (T) BEAN_MAP.get(clazz);
    }

    public static Object getResource(String name) {
        return RESOURCE_MAP.get(name);
    }

}

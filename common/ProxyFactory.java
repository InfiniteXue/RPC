package rpc.common;

import lombok.extern.slf4j.Slf4j;
import rpc.transport.ClassInfo;
import rpc.transport.NettyClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ProxyFactory {

    private static final Map<Class<?>, Object> PROXY_MAP = new HashMap<>();

    /**
     * 根据Class对象获取代理对象
     */
    public static <T> T getProxy(Class<T> clazz) {
        Object proxy = PROXY_MAP.get(clazz);
        if (proxy == null) {
            // JDK动态代理
            proxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    NettyClient client = null;
                    try {
                        ClassInfo classInfo = new ClassInfo();
                        classInfo.setClassName(clazz.getName());
                        classInfo.setMethodName(method.getName());
                        classInfo.setArgTypes(method.getParameterTypes());
                        classInfo.setArgs(args);

                        client = new NettyClient("127.0.0.1", 9090);
                        client.connect();
                        client.getChannel().writeAndFlush(classInfo).sync();
                        return client.getResult();
                    } finally {
                        if (client != null) {
                            client.destroy();
                        }
                    }
                }
            });
            PROXY_MAP.put(clazz, proxy);
        }
        return (T) proxy;
    }

}

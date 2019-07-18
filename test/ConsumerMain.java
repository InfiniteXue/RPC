package rpc.test;

import lombok.extern.slf4j.Slf4j;
import rpc.api.Api;
import rpc.common.ConfigBean;
import rpc.common.Ioc;
import rpc.common.RpcConsumer;
import rpc.common.RpcResource;

import java.util.List;

@Slf4j
@RpcConsumer
public class ConsumerMain {

    @RpcResource
    private Api api;

    public void rpc() {
        String str = api.get("rpc");
        System.out.println(str);
        List<Integer> list = api.list();
        System.out.println(list);
    }

    public static void main(String[] args) {
        ConfigBean.getInstance().setPackageName("rpc");
        ConsumerMain consumer = Ioc.getBean(ConsumerMain.class);
        consumer.rpc();
    }

}



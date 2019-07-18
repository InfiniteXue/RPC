package rpc.test;

import rpc.api.Api;
import rpc.common.ConfigBean;
import rpc.common.RpcProvider;
import rpc.common.RpcResource;
import rpc.transport.NettyServer;

@RpcProvider
public class ProviderMain {

    @RpcResource
    private Api api;

    public static void main(String[] args) {
        ConfigBean.getInstance().setPackageName("rpc");
        new NettyServer(9090).bind();
    }

}

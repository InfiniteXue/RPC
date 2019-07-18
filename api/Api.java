package rpc.api;

import rpc.common.RpcApi;

import java.util.List;

@RpcApi
public interface Api {

    List<Integer> list();

    String get(String name);

}

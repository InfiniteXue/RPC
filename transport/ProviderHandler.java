package rpc.transport;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import rpc.common.Ioc;

import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
public class ProviderHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Active to {}.", ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ClassInfo classInfo = (ClassInfo) msg;
        log.info("Read {} from {}.", classInfo, ctx.channel());
        Object obj = Ioc.getResource(classInfo.getClassName());
        if (obj == null) {
            ctx.writeAndFlush(new RpcException());
        } else {
            Method method = obj.getClass().getMethod(classInfo.getMethodName(), classInfo.getArgTypes());
            Object result = method.invoke(obj, classInfo.getArgs());
            ctx.writeAndFlush(result);
        }
        ReferenceCountUtil.release(classInfo);
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("An exception occurred in provider handler.", cause);
        ctx.writeAndFlush(new RpcException());
        ctx.channel().close();
    }

}

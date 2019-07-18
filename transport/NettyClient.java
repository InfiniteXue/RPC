package rpc.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class NettyClient {

    private final String ip;
    private final Integer port;

    private NioEventLoopGroup workerGroup;

    private CountDownLatch connectLatch = new CountDownLatch(1);
    private Channel channel;

    private final Object lock = new Object();
    private Object result;

    public NettyClient(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public Channel getChannel() throws InterruptedException {
        connectLatch.await();
        return channel;
    }

    public Object getResult() throws InterruptedException {
        while (result == null) {
            synchronized (lock) {
                lock.wait();
            }
        }
        return result;
    }

    public void connect() {
        workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("consumer-encoder", new ObjectEncoder());
                        pipeline.addLast("consumer-decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast("consumer-business", new ConsumerHandler());
                    }
                });
        bootstrap.connect(ip, port);
        log.info("Client start, the connect ip is {} and port is {}.", ip, port);
    }

    public void destroy() {
        workerGroup.shutdownGracefully();
        log.info("Client end, the connect ip is {} and port is {}.", ip, port);
    }

    class ConsumerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("Active to {}.", ctx.channel());
            channel = ctx.channel();
            connectLatch.countDown();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("Read {} from {}.", msg, ctx.channel());
            result = msg;
            synchronized (lock) {
                lock.notify();
            }
            ctx.channel().close();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            log.info("Inactive to {}.", ctx.channel());
            channel = null;
        }

    }

}

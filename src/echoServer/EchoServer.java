package echoServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public class EchoServer {
    private int port;
    EchoServer(int port){
        this.port = port;
    }

    public void start() throws InterruptedException {
        // 接受和处理新连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            // 引导绑定和启动服务器
            ServerBootstrap b = new ServerBootstrap();
            //  指定通道类型为      NioServerSocketChannel
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // childHandler执行所有的连接请求
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            // 绑定服务器
            ChannelFuture f = b.bind(port).sync();
            // System.out.println(EchoServer.class.getName() + "start and listen on " + f.channel().localAddress());
            // program will be closed if lacking the follow code
            f.channel().closeFuture().sync();
        } finally {
            // 关闭EventLoopGroup和释放所有资源，包括创建的线程。
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new EchoServer(58888).start();
    }

}

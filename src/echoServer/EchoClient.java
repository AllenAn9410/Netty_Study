package echoServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClient {
    private int port;
    private String host;
    public EchoClient(int port,String host){
        this.port = port;
        this.host = host;
    }

    public void start() throws InterruptedException {
        // 处理连接、接受数据、发送数据
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            // 引导启动客户端
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture f = b.connect(host,port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        new EchoClient(58888,"localhost").start();
    }

}

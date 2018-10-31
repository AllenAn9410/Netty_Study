package http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

public class HttpSnoopServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;

    public HttpSnoopServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast("encoder",new HttpResponseEncoder());
        p.addLast("decoder",new HttpRequestDecoder());
        p.addLast("decoder11",new HttpContentCompressor());
        p.addLast("aggregator", new HttpObjectAggregator(10*1024*1024));
        p.addLast(new HttpSnoopServerHandler());
    }
}

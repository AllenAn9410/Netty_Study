package reqAndResp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpResponseEncoder());
        // Uncomment the following line if you don't want to handle HttpContents.
        pipeline.addLast(new HttpObjectAggregator(1048576));
        // Remove the following line if you don't want automatic content compression.
        pipeline.addLast(new HttpContentCompressor());
        pipeline.addLast(new ServerHandler());
    }
}

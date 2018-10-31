package reqAndResp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.stream.ChunkedWriteHandler;


public class ClientInitalizer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpClientCodec());
        // Remove the following line if you don't want automatic content decompression.
        p.addLast(new HttpContentDecompressor());
        // Uncomment the following line if you don't want to handle HttpContents.
        p.addLast(new HttpObjectAggregator(1048576));
        // to be used since huge file transfer
        p.addLast("chunkedWriter", new ChunkedWriteHandler());
        p.addLast(new ClientHandler());
    }
}

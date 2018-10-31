package reqAndResp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class Client {
    private String host = "localhost";
    private int port;
    public Client(int port){
        this.port = port;
    }

    public void start() throws Exception{
        URI uri = new URI("allen/123");

        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ClientInitalizer());
            Channel channel = b.connect(host,port).sync().channel();

            FullHttpRequest request = setHeader(uri);
            setValue(request,channel);
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    private void setValue(FullHttpRequest request,Channel channel){
        ByteBuf bbuf = Unpooled.copiedBuffer("abababa/abababes/erertwagf/qrwqerwerweqrew" +
                "/f34f3f4/4fagt4tt/f4343f",CharsetUtil.UTF_8);
        request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, bbuf.readableBytes());
        request.content().clear().writeBytes(bbuf);
        channel.write(request);
        channel.flush();
    }

    private FullHttpRequest setHeader(URI uri) throws Exception {
        FullHttpRequest  request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.toASCIIString());
        HttpHeaders headers = request.headers();
        // Specifies the domain name and port number of the server requested
        headers.set(HttpHeaderNames.HOST, host);
        // Indicates whether a persistent connection is required.
        headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        // Specify the web server type of content compression encoding that the browser can support.
        headers.set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP + "," + HttpHeaderValues.DEFLATE);
        // A set of character encodings acceptable to the browser
            headers.set(HttpHeaderNames.ACCEPT_CHARSET, "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        // Browser acceptable language
        headers.set(HttpHeaderNames.ACCEPT_LANGUAGE, "en");
        // The address of the previous page, the current request page is followed by the incoming
        headers.set(HttpHeaderNames.REFERER, uri.toString());
        // Specifies the type of content that the client can receive
        headers.set(HttpHeaderNames.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        //connection will not close but needed
        // headers.set("Connection","keep-alive");
        // headers.set("Keep-Alive","300");
        return  request;

    }
}

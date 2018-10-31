package http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;

public class HttpClient {
    static final String URL = System.getProperty("url", "http://127.0.0.1:8081/");

    public static void main(String[] args) throws Exception {
        URI uri = new URI(URL);
        String scheme = uri.getScheme() == null? "http" : uri.getScheme();
        String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 81;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        // Configure SSL context if necessary.
        final boolean ssl = "https".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpSnoopClientInitializer(sslCtx));

            Channel ch = b.connect(host, port).sync().channel();
            // Prepare the HTTP request.
            HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
            // headers.set(HttpHeaderNames.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            request.headers().set(HttpHeaderNames.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            request.headers().set(HttpHeaderNames.ACCEPT_CHARSET, "ISO-8859-1,utf-8;q=0.7,*;q=0.7");

            // Set some example cookies.
            request.headers().set(
                    HttpHeaderNames.COOKIE,
                    io.netty.handler.codec.http.cookie.ClientCookieEncoder.STRICT.encode(
                            new io.netty.handler.codec.http.cookie.DefaultCookie("my-cookie", "foo"),
                            new DefaultCookie("another-cookie", "bar")));
            String params="EE_LIB/EE_LIB/com.cs.swmtch.jar";
//            ChannelBuffer cb = ChannelBuffers.copiedBuffer(params,Charset.defaultCharset());
            HttpPostRequestEncoder bodyRequestEncoder =
                    new HttpPostRequestEncoder(factory, request, false);
            // bodyRequestEncoder.addBodyAttribute("getform", "POST");
            bodyRequestEncoder.addBodyAttribute("msg", params);
            request = bodyRequestEncoder.finalizeRequest();

            // Send the HTTP request.
            ch.writeAndFlush(request);

            // Wait for the server to close the connection.
            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}

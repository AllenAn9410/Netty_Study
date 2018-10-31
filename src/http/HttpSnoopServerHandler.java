package http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class HttpSnoopServerHandler extends ChannelInboundHandlerAdapter {
    private HttpRequest request;
    /** Buffer that stores the response content */
    private final StringBuilder buf = new StringBuilder();

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest httpRequest = null;
        if (msg instanceof HttpRequest) {
            httpRequest = (FullHttpRequest)msg;
        }
        System.out.println();
        System.out.println( httpRequest.uri());
        String body = getBody(httpRequest);
        System.out.println(body);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(body, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    private String getBody(FullHttpRequest request){
        ByteBuf buf = request.content();
        return buf.toString(CharsetUtil.UTF_8);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
